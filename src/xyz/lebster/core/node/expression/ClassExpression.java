package xyz.lebster.core.node.expression;

import xyz.lebster.core.NonCompliant;
import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.exception.NotImplemented;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.Intrinsics;
import xyz.lebster.core.interpreter.environment.Environment;
import xyz.lebster.core.interpreter.environment.ExecutionContext;
import xyz.lebster.core.interpreter.environment.FunctionEnvironment;
import xyz.lebster.core.node.FunctionNode;
import xyz.lebster.core.node.FunctionParameters;
import xyz.lebster.core.node.SourceRange;
import xyz.lebster.core.node.expression.ObjectExpression.MethodNode;
import xyz.lebster.core.node.expression.literal.PrimitiveLiteral;
import xyz.lebster.core.node.statement.BlockStatement;
import xyz.lebster.core.value.Names;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.error.type.TypeError;
import xyz.lebster.core.value.function.Constructor;
import xyz.lebster.core.value.function.OrdinaryFunction;
import xyz.lebster.core.value.globals.Null;
import xyz.lebster.core.value.object.Key;
import xyz.lebster.core.value.object.ObjectValue;
import xyz.lebster.core.value.primitive.string.StringValue;

import java.util.List;

import static xyz.lebster.core.interpreter.AbruptCompletion.error;

public record ClassExpression(
	String className,
	Expression heritage,
	ClassConstructorNode constructor,
	MethodNode[] methods,
	ClassFieldNode[] fields,
	SourceRange range
) implements Expression {
	public ClassExpression(String className, Expression heritage, ClassConstructorNode constructor, List<MethodNode> methods, List<ClassFieldNode> fields, SourceRange range) {
		this(className, heritage, constructor, methods.toArray(new MethodNode[0]), fields.toArray(new ClassFieldNode[0]), range);
	}

	@Override
	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-runtime-semantics-classdefinitionevaluation")
	public Value<?> execute(Interpreter interpreter) throws AbruptCompletion {
		if (constructor == null) throw new NotImplemented("Creating classes without constructors");

		ObjectValue protoParent = interpreter.intrinsics.objectPrototype;
		ObjectValue constructorParent = interpreter.intrinsics.functionPrototype;

		if (heritage != null) {
			final Value<?> executedParentClass = heritage.execute(interpreter);
			if (executedParentClass == Null.instance) {
				protoParent = null;
			} else {
				constructorParent = executedParentClass.toObjectValue(interpreter);
				protoParent = constructorParent.get(interpreter, Names.prototype).toObjectValue(interpreter);
			}
		}

		final ClassConstructor constructorFunction = constructor.execute(interpreter);
		final ObjectValue prototypeProperty = constructorFunction.get(interpreter, Names.prototype).toObjectValue(interpreter);
		prototypeProperty.setPrototype(protoParent);
		constructorFunction.setPrototype(constructorParent);

		for (final MethodNode method : methods) {
			// TODO: Reduce duplication between this and MethodNode#insertInto
			final Key<?> propKey = method.name().execute(interpreter).toPropertyKey(interpreter);
			final OrdinaryFunction function = method.execute(interpreter);
			function.updateName(propKey.toFunctionName());
			prototypeProperty.put(propKey, function);
		}

		return constructorFunction;
	}

	public record ClassConstructorNode(String className, FunctionParameters parameters, BlockStatement body, boolean isDerived, SourceRange range) implements FunctionNode {
		// FIXME
		private static final PrimitiveLiteral<StringValue> name = new PrimitiveLiteral<>(null, Names.constructor);

		@Override
		public ClassConstructor execute(Interpreter interpreter) {
			return new ClassConstructor(interpreter.intrinsics, interpreter.environment(), this, isDerived, className);
		}

		@Override
		public Expression name() {
			return name;
		}
	}

	// FIXME: Currently unused
	@NonCompliant
	public record ClassFieldNode(Expression name, Expression initializer, SourceRange range) {
	}

	// A wrapper of core.value.function.ConstructorFunction, without the call method - class constructors cannot be called without 'new'
	private static final class ClassConstructor extends Constructor {
		private final Environment environment;
		private final ClassConstructorNode code;
		private final boolean isDerived;

		public ClassConstructor(
			Intrinsics intrinsics,
			Environment environment,
			ClassConstructorNode code,
			boolean isDerived,
			String name
		) {
			super(intrinsics, name == null ? Names.EMPTY : new StringValue(name), code.parameters.expectedArgumentCount(), false);
			this.environment = environment;
			this.code = code;
			this.isDerived = isDerived;
		}

		@Override
		public StringValue toStringMethod() {
			return new StringValue(code.range.getText());
		}

		@NonCompliant
		// FIXME
		public ObjectValue construct(Interpreter interpreter, Value<?>[] arguments, ObjectValue newTarget) throws AbruptCompletion {
			// FIXME: Don't do the next 3 lines if derived. Currently they are just ignored if derived
			final Value<?> prop = this.get(interpreter, Names.prototype);
			final ObjectValue prototype = prop instanceof ObjectValue proto ? proto : interpreter.intrinsics.objectPrototype;
			final ObjectValue newInstance = new ObjectValue(prototype);

			// Calling when `this` is bound: The LexicalEnvironment of this.code; The bound `this` value
			Value<?> thisValue = isDerived ? null : newInstance;
			final ExecutionContext pushedThisValue = interpreter.pushContextWithEnvironment(new FunctionEnvironment(environment, thisValue, this, this));

			try {
				final Value<?> returnValue = code.executeBody(interpreter, arguments);
				if (returnValue instanceof final ObjectValue asObject) return asObject;
				if (!isDerived) return newInstance;
				final Value<?> thisFromParent = interpreter.thisValue();
				if (!(thisFromParent instanceof final ObjectValue thisFromParentObject)) {
					throw error(new TypeError(interpreter, "Not an object!"));
				}
				// FIXME: Follow spec
				final ObjectValue original_prototype = thisFromParentObject.getPrototype();
				prototype.setPrototype(original_prototype);
				thisFromParentObject.setPrototype(prototype);
				return thisFromParentObject;
			} finally {
				interpreter.exitExecutionContext(pushedThisValue);
			}
		}

		@Override
		public Value<?> internalCall(Interpreter interpreter, Value<?>... arguments) throws AbruptCompletion {
			final String message = this.name.value.isEmpty() ?
				"Class constructors cannot be invoked without 'new'" :
				"Class constructor %s cannot be invoked without 'new'".formatted(this.name.value);
			throw error(new TypeError(interpreter, message));
		}
	}
}

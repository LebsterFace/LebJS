package xyz.lebster.core.node.expression;

import xyz.lebster.core.DumpBuilder;
import xyz.lebster.core.exception.NotImplemented;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Environment;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.node.FunctionArguments;
import xyz.lebster.core.node.FunctionNode;
import xyz.lebster.core.node.SourceRange;
import xyz.lebster.core.node.statement.BlockStatement;
import xyz.lebster.core.value.Names;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.error.TypeError;
import xyz.lebster.core.value.function.Constructor;
import xyz.lebster.core.value.function.Executable;
import xyz.lebster.core.value.function.Function;
import xyz.lebster.core.value.globals.Null;
import xyz.lebster.core.value.object.ObjectValue;
import xyz.lebster.core.value.string.StringValue;

import java.util.HashSet;
import java.util.List;

public record ClassExpression(
	String className,
	Expression heritage,
	ClassConstructorNode constructor,
	ClassMethodNode[] methods,
	ClassFieldNode[] fields,
	SourceRange range
) implements Expression {
	public ClassExpression(String className, Expression heritage, ClassConstructorNode constructor, List<ClassMethodNode> methods, List<ClassFieldNode> fields, SourceRange range) {
		this(className, heritage, constructor, methods.toArray(new ClassMethodNode[0]), fields.toArray(new ClassFieldNode[0]), range);
	}

	@Override
	public Value<?> execute(Interpreter interpreter) throws AbruptCompletion {
		if (constructor == null) throw new NotImplemented("Creating classes without constructors");

		final ClassConstructor constructorFunction = constructor.execute(interpreter);
		final ObjectValue prototypeProperty = constructorFunction.get(interpreter, Names.prototype).toObjectValue(interpreter);

		if (heritage != null) {
			Value<?> execute = heritage.execute(interpreter);
			if (execute == Null.instance) {
				constructorFunction.setPrototype(Null.instance);
				prototypeProperty.setPrototype(Null.instance);
			} else {
				// FIXME: No implicit super
				final ObjectValue parentClass = execute.toObjectValue(interpreter);
				constructorFunction.setPrototype(parentClass);
				final ObjectValue parentClassPrototypeProperty = parentClass.get(interpreter, Names.prototype).toObjectValue(interpreter);
				prototypeProperty.setPrototype(parentClassPrototypeProperty);
			}
		}

		for (final ClassMethodNode method : methods) {
			prototypeProperty.put(new StringValue(method.name), method.execute(interpreter));
		}

		return constructorFunction;
	}

	@Override
	public void dump(int indent) {
		DumpBuilder.begin(indent)
			.self(this)
			.optional("Constructor", constructor)
			.children("Methods", methods);
	}

	@Override
	public void represent(StringRepresentation representation) {
		representation.append("class ");
		if (this.className != null) {
			representation.append(className);
			representation.append(' ');
		}
		representation.append('{');
		representation.indent();
		representation.appendLine();
		constructor.represent(representation);
		for (ClassMethodNode method : methods) {
			representation.appendLine();
			method.represent(representation);
		}
		representation.unindent();
		representation.appendLine();
		representation.append('}');
	}

	private interface ClassFunctionNode extends FunctionNode {
		@Override
		default void represent(StringRepresentation representation) {
			representation.append(name());
			representation.append(toCallString());
			representation.append(' ');
			body().represent(representation);
		}
	}


	public record ClassMethodNode(String className, String name, FunctionArguments arguments, BlockStatement body, SourceRange range) implements ClassFunctionNode {
		@Override
		public ClassMethod execute(Interpreter interpreter) {
			return new ClassMethod(interpreter, interpreter.lexicalEnvironment(), this);
		}
	}

	public record ClassConstructorNode(String className, FunctionArguments arguments, BlockStatement body, SourceRange range) implements ClassFunctionNode {
		@Override
		public ClassConstructor execute(Interpreter interpreter) throws AbruptCompletion {
			return new ClassConstructor(interpreter, interpreter.lexicalEnvironment(), this, className);
		}

		@Override
		public String name() {
			return "constructor";
		}
	}

	public record ClassFieldNode(String name, Expression initializer, SourceRange range) {

	}

	// A wrapper of core.value.function.Function, without the call method - class constructors cannot be called without 'new'
	private static final class ClassConstructor extends Constructor {
		private final Function wrappedFunction;

		public ClassConstructor(Interpreter interpreter, Environment environment, ClassConstructorNode code, String name) throws AbruptCompletion {
			super(interpreter.intrinsics.objectPrototype, interpreter.intrinsics.functionPrototype, name == null ? Names.EMPTY : new StringValue(name));
			this.wrappedFunction = new Function(interpreter, environment, code);
		}

		@Override
		public void displayRecursive(StringRepresentation representation, HashSet<ObjectValue> parents, boolean singleLine) {
			wrappedFunction.displayRecursive(representation, parents, singleLine);
		}

		@Override
		public StringValue toStringMethod() {
			return wrappedFunction.toStringMethod();
		}

		public ObjectValue construct(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
			final Value<?> prop = this.get(interpreter, Names.prototype);
			final ObjectValue prototype = prop instanceof ObjectValue proto ? proto : interpreter.intrinsics.objectPrototype;
			final ObjectValue newInstance = new ObjectValue(prototype);

			final Value<?> returnValue = wrappedFunction.call(interpreter, newInstance, arguments);
			if (returnValue instanceof final ObjectValue asObject) return asObject;
			return newInstance;
		}

		@Override
		public Value<?> call(Interpreter interpreter, Value<?>... arguments) throws AbruptCompletion {
			final String message = this.name.value.isEmpty() ?
				"Class constructors cannot be invoked without 'new'" :
				"Class constructor %s cannot be invoked without 'new'".formatted(this.name.value);
			throw AbruptCompletion.error(new TypeError(interpreter, message));
		}

		// call(Interpreter interpreter, Value<?> newThisValue, Value<?>... arguments) is handled by Executable
	}

	// A wrapper of core.value.function.Function, without the construct method - class methods cannot be constructed
	private static final class ClassMethod extends Executable {
		private final Function wrappedFunction;

		public ClassMethod(Interpreter interpreter, Environment environment, ClassExpression.ClassMethodNode code) {
			super(interpreter.intrinsics.functionPrototype, new StringValue(code.name));
			this.wrappedFunction = new Function(interpreter, environment, code);
		}

		@Override
		public void displayRecursive(StringRepresentation representation, HashSet<ObjectValue> parents, boolean singleLine) {
			wrappedFunction.displayRecursive(representation, parents, singleLine);
		}

		@Override
		public StringValue toStringMethod() {
			return wrappedFunction.toStringMethod();
		}

		@Override
		public Value<?> call(Interpreter interpreter, Value<?>... arguments) throws AbruptCompletion {
			return wrappedFunction.call(interpreter, arguments);
		}

		@Override
		public Value<?> call(Interpreter interpreter, Value<?> newThisValue, Value<?>... arguments) throws AbruptCompletion {
			return wrappedFunction.call(interpreter, newThisValue, arguments);
		}
	}
}

package xyz.lebster.core.node.expression;

import xyz.lebster.core.DumpBuilder;
import xyz.lebster.core.exception.NotImplemented;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Environment;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.node.FunctionNode;
import xyz.lebster.core.node.SourceRange;
import xyz.lebster.core.node.statement.BlockStatement;
import xyz.lebster.core.value.Names;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.error.TypeError;
import xyz.lebster.core.value.function.Constructor;
import xyz.lebster.core.value.function.Executable;
import xyz.lebster.core.value.function.Function;
import xyz.lebster.core.value.object.ObjectValue;
import xyz.lebster.core.value.string.StringValue;

import java.util.HashSet;

public record ClassExpression(String className, ClassConstructorNode constructor, ClassMethodNode[] methods, SourceRange range) implements Expression {
	@Override
	public Value<?> execute(Interpreter interpreter) throws AbruptCompletion {
		if (constructor == null) throw new NotImplemented("Creating classes without constructors");

		final ClassConstructor constructorFunction = constructor.execute(interpreter);
		final ObjectValue prototypeProperty = constructorFunction.prototypeProperty;

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


	public record ClassMethodNode(String className, String name, xyz.lebster.core.node.FunctionArguments arguments, BlockStatement body, SourceRange range) implements ClassFunctionNode {
		@Override
		public ClassMethod execute(Interpreter interpreter) {
			return new ClassMethod(interpreter, interpreter.lexicalEnvironment(), this);
		}
	}

	public record ClassConstructorNode(String className, String name, xyz.lebster.core.node.FunctionArguments arguments, BlockStatement body, SourceRange range) implements ClassFunctionNode {
		@Override
		public ClassConstructor execute(Interpreter interpreter) throws AbruptCompletion {
			return new ClassConstructor(interpreter, interpreter.lexicalEnvironment(), this, className);
		}
	}

	private static final class ClassConstructor extends Constructor {
		private final Environment environment;
		private final ClassConstructorNode code;
		private final ObjectValue prototypeProperty;

		public ClassConstructor(Interpreter interpreter, Environment environment, ClassConstructorNode code, String name) throws AbruptCompletion {
			super(interpreter.intrinsics.objectPrototype, interpreter.intrinsics.functionPrototype, name == null ? Names.EMPTY : new StringValue(name));
			this.environment = environment;
			this.code = code;
			this.prototypeProperty = this.get(interpreter, Names.prototype).toObjectValue(interpreter);
		}

		@Override
		public void displayRecursive(StringRepresentation representation, HashSet<ObjectValue> parents, boolean singleLine) {
			this.display(representation);
		}

		@Override
		public StringValue toStringMethod() {
			return new StringValue(code.toRepresentationString());
		}

		@Override
		public Value<?> call(Interpreter interpreter, Value<?>... arguments) throws AbruptCompletion {
			final String message = this.name.value.isEmpty() ?
				"Class constructors cannot be invoked without 'new'" :
				"Class constructor %s cannot be invoked without 'new'".formatted(this.name.value);
			throw AbruptCompletion.error(new TypeError(interpreter, message));
		}

		@Override
		public ObjectValue construct(Interpreter interpreter, Value<?>[] args) throws AbruptCompletion {
			final ObjectValue newInstance = new ObjectValue(this.prototypeProperty);
			final var returnValue = new Function(interpreter, environment, code).call(interpreter, newInstance, args);
			if (returnValue instanceof final ObjectValue asObject) return asObject;
			return newInstance;
		}
	}

	// A copy of core.value.function.Function, without the construct method - class methods cannot be constructed
	private static final class ClassMethod extends Executable {
		private final Environment environment;
		private final FunctionNode code;

		public ClassMethod(Interpreter interpreter, Environment environment, ClassExpression.ClassMethodNode code) {
			super(interpreter.intrinsics.functionPrototype, new StringValue(code.name));
			this.environment = environment;
			this.code = code;
		}

		@Override
		public void displayRecursive(StringRepresentation representation, HashSet<ObjectValue> parents, boolean singleLine) {
			this.display(representation);
		}

		@Override
		public StringValue toStringMethod() {
			return new StringValue(code.toRepresentationString());
		}

		@Override
		public Value<?> call(Interpreter interpreter, Value<?>... arguments) throws AbruptCompletion {
			return new Function(interpreter, environment, code).call(interpreter, arguments);
		}

		@Override
		public Value<?> call(Interpreter interpreter, Value<?> newThisValue, Value<?>... arguments) throws AbruptCompletion {
			return new Function(interpreter, environment, code).call(interpreter, newThisValue, arguments);
		}
	}
}

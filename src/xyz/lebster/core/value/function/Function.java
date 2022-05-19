package xyz.lebster.core.value.function;

import xyz.lebster.core.NonCompliant;
import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.interpreter.*;
import xyz.lebster.core.node.FunctionNode;
import xyz.lebster.core.value.Names;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.object.ObjectValue;
import xyz.lebster.core.value.string.StringValue;

import java.util.HashSet;

@SpecificationURL("https://tc39.es/ecma262/multipage#sec-ecmascript-function-objects")
public final class Function extends Constructor {
	private final Environment environment;
	private final FunctionNode code;

	public Function(Interpreter interpreter, Environment environment, FunctionNode code) {
		super(interpreter.intrinsics.objectPrototype, interpreter.intrinsics.functionPrototype, code.name() == null ? Names.EMPTY : new StringValue(code.name()));
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
		// Closures: The LexicalEnvironment of this.code; The surrounding `this` value
		final ExecutionContext context = interpreter.pushLexicalEnvironment(environment);
		return code.execute(interpreter, context, arguments);
	}

	@Override
	public Value<?> call(Interpreter interpreter, Value<?> newThisValue, Value<?>... arguments) throws AbruptCompletion {
		// Calling when `this` is bound: The LexicalEnvironment of this.code; The bound `this` value
		final ExecutionContext context = interpreter.pushEnvironmentAndThisValue(environment, newThisValue);
		return code.execute(interpreter, context, arguments);
	}

	@Override
	@NonCompliant
	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-ecmascript-function-objects-construct-argumentslist-newtarget")
	public ObjectValue construct(Interpreter interpreter, Value<?>[] args) throws AbruptCompletion {
		final Value<?> prop = this.get(interpreter, Names.prototype);
		final ObjectValue prototype = prop instanceof ObjectValue proto ? proto : interpreter.intrinsics.objectPrototype;
		final ObjectValue newInstance = new ObjectValue(prototype);

		final Value<?> returnValue = this.call(interpreter, newInstance, args);
		if (returnValue instanceof final ObjectValue asObject) return asObject;
		return newInstance;
	}
}
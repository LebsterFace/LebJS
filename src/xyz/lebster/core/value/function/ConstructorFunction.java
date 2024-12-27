package xyz.lebster.core.value.function;

import xyz.lebster.core.NonCompliant;
import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.Intrinsics;
import xyz.lebster.core.interpreter.environment.Environment;
import xyz.lebster.core.node.FunctionNode;
import xyz.lebster.core.value.Names;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.object.ObjectValue;
import xyz.lebster.core.value.primitive.string.StringValue;

/**
 * Variant of {@link OrdinaryFunction} with {@link #construct(Interpreter, Value[], ObjectValue) construct} method of {@link Constructor}
 */
@SpecificationURL("https://tc39.es/ecma262/multipage#constructor")
public class ConstructorFunction extends Constructor {
	private final Environment environment;
	private final FunctionNode code;

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-ConstructorFunctioncreate")
	public ConstructorFunction(Intrinsics intrinsics, StringValue name, Environment environment, FunctionNode code) {
		super(intrinsics, name, code.parameters().expectedArgumentCount(), true);
		this.environment = environment;
		this.code = code;
	}

	@Override
	public StringValue toStringMethod() {
		return new StringValue(code.range().getText());
	}

	@Override
	public Environment savedEnvironment(Interpreter interpreter) {
		return environment;
	}

	@Override
	public Value<?> internalCall(Interpreter interpreter, Value<?>... arguments) throws AbruptCompletion {
		return code.executeBody(interpreter, arguments);
	}

	@Override
	@NonCompliant
	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-ecmascript-function-objects-construct-argumentslist-newtarget")
	public ObjectValue construct(Interpreter interpreter, Value<?>[] args, ObjectValue newTarget) throws AbruptCompletion {
		final Value<?> prop = this.get(interpreter, Names.prototype);
		final ObjectValue prototype = prop instanceof ObjectValue proto ? proto : interpreter.intrinsics.objectPrototype;
		final ObjectValue newInstance = new ObjectValue(prototype);

		final Value<?> returnValue = this.call(interpreter, newInstance, args);
		if (returnValue instanceof final ObjectValue asObject) return asObject;
		return newInstance;
	}
}
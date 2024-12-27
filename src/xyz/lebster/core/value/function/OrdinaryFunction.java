package xyz.lebster.core.value.function;

import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.Intrinsics;
import xyz.lebster.core.interpreter.environment.Environment;
import xyz.lebster.core.node.FunctionNode;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.primitive.string.StringValue;

@SpecificationURL("https://tc39.es/ecma262/multipage#sec-ecmascript-function-objects")
public final class OrdinaryFunction extends Executable {
	private final Environment environment;
	private final FunctionNode code;

	/** Note that the {@link #name} of this function must be initialised separately with {@link #setName(StringValue)} */
	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-ordinaryfunctioncreate")
	public OrdinaryFunction(Intrinsics intrinsics, Environment environment, FunctionNode code) {
		super(intrinsics.functionPrototype, null, code.parameters().expectedArgumentCount());
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
}
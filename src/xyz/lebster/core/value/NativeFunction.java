package xyz.lebster.core.value;

import xyz.lebster.core.runtime.AbruptCompletion;
import xyz.lebster.core.runtime.Interpreter;
import xyz.lebster.exception.NotImplemented;

public class NativeFunction extends Executable<NativeCode> {
	public NativeFunction(NativeCode value) {
		super(Type.NativeFunction, value);
	}

	@Override
	public StringLiteral toStringLiteral() {
		return new StringLiteral("function() { [native code] }");
	}

	@Override
	public BooleanLiteral toBooleanLiteral() {
		return new BooleanLiteral(true);
	}

	@Override
	public NumericLiteral toNumericLiteral() {
		return new NumericLiteral(Double.NaN);
	}

	@Override
	public Function toFunction() throws NotImplemented {
		throw new NotImplemented("NativeFunction -> Function");
	}

	@Override
	public Dictionary toDictionary() throws NotImplemented {
		throw new NotImplemented("NativeFunction -> Dictionary");
	}

	@Override
	public void dump(int indent) {
		Interpreter.dumpValue(indent, "NativeFunction");
	}

	@Override
	public Value<?> executeChildren(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		return value.execute(interpreter, arguments);
	}
}

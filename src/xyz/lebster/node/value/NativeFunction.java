package xyz.lebster.node.value;


import xyz.lebster.exception.NotImplemented;
import xyz.lebster.interpreter.AbruptCompletion;
import xyz.lebster.interpreter.Interpreter;

public final class NativeFunction extends Executable<NativeCode> {
	public NativeFunction(NativeCode code) {
		super(code);
	}

	@Override
	public Value<?> call(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		try {
			return code.execute(interpreter, arguments);
		} catch (AbruptCompletion e) {
			if (e.type != AbruptCompletion.Type.Return) throw e;
			return e.value;
		}
	}

	@Override
	public String toString() {
		return "function() { [native code] }";
	}

	@Override
	public NumericLiteral toNumericLiteral() {
		throw new NotImplemented("NativeFunction -> NumericLiteral");
	}

	@Override
	public BooleanLiteral toBooleanLiteral() {
		throw new NotImplemented("NativeFunction -> BooleanLiteral");
	}

	@Override
	public Dictionary toDictionary() {
		throw new NotImplemented("NativeFunction -> Dictionary");
	}
}
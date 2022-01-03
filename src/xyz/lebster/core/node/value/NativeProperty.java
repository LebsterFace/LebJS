package xyz.lebster.core.node.value;

import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;

public class NativeProperty extends Value<NativeGetterSetter> {
	public NativeProperty(NativeGetterSetter value) {
		super(value, Type.Dictionary);
	}

	@Override
	public Value<?> execute(Interpreter interpreter) throws AbruptCompletion {
		return value.get(interpreter).execute(interpreter);
	}

	@Override
	public StringLiteral toStringLiteral(Interpreter interpreter) throws AbruptCompletion {
		return value.get(interpreter).toStringLiteral(interpreter);
	}

	@Override
	public NumericLiteral toNumericLiteral(Interpreter interpreter) throws AbruptCompletion {
		return value.get(interpreter).toNumericLiteral(interpreter);
	}

	@Override
	public BooleanLiteral toBooleanLiteral(Interpreter interpreter) throws AbruptCompletion {
		return value.get(interpreter).toBooleanLiteral(interpreter);
	}

	@Override
	public Dictionary toDictionary(Interpreter interpreter) throws AbruptCompletion {
		return value.get(interpreter).toDictionary(interpreter);
	}

	@Override
	public String typeOf(Interpreter interpreter) throws AbruptCompletion {
		return value.get(interpreter).typeOf(interpreter);
	}
}

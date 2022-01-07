package xyz.lebster.core.node.value;

import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.node.value.object.NativeGetterSetter;
import xyz.lebster.core.node.value.object.ObjectLiteral;

public class NativeProperty extends Value<NativeGetterSetter> {
	public NativeProperty(NativeGetterSetter value) {
		super(value, Type.Object);
	}

	public NativeProperty(Value<?> value) {
		this(new NativeGetterSetter() {
			@Override
			public Value<?> get(Interpreter interpreter) {
				return value;
			}

			@Override
			public void set(Interpreter interpreter, Value<?> value) {}
		});
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
	public ObjectLiteral toObjectLiteral(Interpreter interpreter) throws AbruptCompletion {
		return value.get(interpreter).toObjectLiteral(interpreter);
	}

	@Override
	public String typeOf(Interpreter interpreter) throws AbruptCompletion {
		return value.get(interpreter).typeOf(interpreter);
	}
}

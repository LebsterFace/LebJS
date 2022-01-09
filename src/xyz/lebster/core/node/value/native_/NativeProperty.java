package xyz.lebster.core.node.value.native_;

import xyz.lebster.core.ANSI;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.node.value.*;
import xyz.lebster.core.node.value.object.ObjectValue;

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
	public Value<?> getValue(Interpreter interpreter) throws AbruptCompletion {
		return value.get(interpreter).getValue(interpreter);
	}

	@Override
	public StringValue toStringValue(Interpreter interpreter) throws AbruptCompletion {
		return value.get(interpreter).toStringValue(interpreter);
	}

	@Override
	public NumberValue toNumberValue(Interpreter interpreter) throws AbruptCompletion {
		return value.get(interpreter).toNumberValue(interpreter);
	}

	@Override
	public BooleanValue toBooleanValue(Interpreter interpreter) throws AbruptCompletion {
		return value.get(interpreter).toBooleanValue(interpreter);
	}

	@Override
	public ObjectValue toObjectValue(Interpreter interpreter) throws AbruptCompletion {
		return value.get(interpreter).toObjectValue(interpreter);
	}

	@Override
	public String typeOf(Interpreter interpreter) throws AbruptCompletion {
		return value.get(interpreter).typeOf(interpreter);
	}

	@Override
	public void display(StringBuilder builder) {
		builder.append(ANSI.CYAN);
		builder.append("[Native Property]");
		builder.append(ANSI.RESET);
	}
}
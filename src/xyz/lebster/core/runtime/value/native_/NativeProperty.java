package xyz.lebster.core.runtime.value.native_;

import xyz.lebster.core.ANSI;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.runtime.value.Value;
import xyz.lebster.core.runtime.value.object.ObjectValue;
import xyz.lebster.core.runtime.value.primitive.BooleanValue;
import xyz.lebster.core.runtime.value.primitive.NumberValue;
import xyz.lebster.core.runtime.value.primitive.StringValue;

public final class NativeProperty extends Value<NativeGetterSetter> {
	public NativeProperty(NativeGetterSetter value) {
		super(value);
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
	public void display(StringRepresentation representation) {
		representation.append(ANSI.CYAN);
		representation.append("[Native Property]");
		representation.append(ANSI.RESET);
	}
}

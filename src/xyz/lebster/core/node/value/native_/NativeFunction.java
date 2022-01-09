package xyz.lebster.core.node.value.native_;


import xyz.lebster.core.ANSI;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.node.value.StringValue;
import xyz.lebster.core.node.value.Value;
import xyz.lebster.core.node.value.object.Executable;
import xyz.lebster.core.node.value.object.ObjectValue;
import xyz.lebster.core.runtime.prototype.ObjectPrototype;

import java.util.HashSet;

public final class NativeFunction extends Executable<NativeCode> {
	public NativeFunction(NativeCode code) {
		super(code);
		put(ObjectPrototype.toString, new NativeFunction(new StringValue("function () { [native code] }")));
	}

	public NativeFunction(Value<?> value) {
		super((interpreter, arguments) -> value);
	}

	@Override
	protected Value<?> internalCall(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		try {
			return code.execute(interpreter, arguments);
		} catch (AbruptCompletion e) {
			if (e.type != AbruptCompletion.Type.Return) throw e;
			return e.value;
		}
	}

	@Override
	public void display(StringBuilder builder) {
		builder.append(ANSI.BRIGHT_MAGENTA);
		builder.append("[Native Function]");
		builder.append(ANSI.RESET);
	}

	@Override
	public void displayRecursive(StringBuilder representation, HashSet<ObjectValue> parents) {
		this.display(representation);
	}
}
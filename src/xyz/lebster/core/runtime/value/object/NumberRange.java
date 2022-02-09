package xyz.lebster.core.runtime.value.object;

import xyz.lebster.core.runtime.Names;
import xyz.lebster.core.runtime.value.native_.NativeFunction;
import xyz.lebster.core.runtime.value.primitive.*;

public final class NumberRange extends ObjectValue {
	private final double end;
	private final double step;
	private double current;

	public NumberRange(double end) {
		this.initialise();
		this.current = 0;
		this.end = end;
		this.step = 1;
	}

	private void initialise() {
		this.put(SymbolValue.iterator, new NativeFunction(new StringValue("[Symbol.iterator]"), ($, $$) -> this));
		this.putMethod(Names.next, (interpreter, arguments) -> {
			final ObjectValue result = new ObjectValue();
			if (current >= end) {
				result.put(Names.done, BooleanValue.TRUE);
				result.put(Names.value, Undefined.instance);
			} else {
				result.put(Names.done, BooleanValue.FALSE);
				result.put(Names.value, new NumberValue(current));
				current += step;
			}

			return result;
		});
	}

	public NumberRange(double start, double end) {
		this.initialise();
		this.current = start;
		this.end = end;
		this.step = 1;
	}

	public NumberRange(double start, double end, double step) {
		this.initialise();
		this.current = start;
		this.end = end;
		this.step = step;
	}
}

package xyz.lebster.core.value.number;

import xyz.lebster.core.value.Names;
import xyz.lebster.core.value.boolean_.BooleanValue;
import xyz.lebster.core.value.function.FunctionPrototype;
import xyz.lebster.core.value.globals.Undefined;
import xyz.lebster.core.value.object.ObjectValue;
import xyz.lebster.core.value.symbol.SymbolValue;

public final class NumberRange extends ObjectValue {
	private final boolean isValid;
	private final boolean isDecreasing;

	private final double end;
	private final double step;
	private double current;

	public NumberRange(FunctionPrototype functionPrototype, double end) {
		this(functionPrototype, 0, end, 1);
	}

	public NumberRange(FunctionPrototype functionPrototype, double start, double end) {
		this(functionPrototype, start, end, 1);
	}

	public NumberRange(FunctionPrototype functionPrototype, double start, double end, double step) {
		super(null);
		this.initialise(functionPrototype);

		if (start < end) {
			this.isValid = step > 0;
			this.isDecreasing = false;
		} else if (start > end) {
			this.isValid = step < 0;
			this.isDecreasing = true;
		} else {
			this.isValid = false;
			this.isDecreasing = false;
		}

		this.current = start;
		this.end = end;
		this.step = step;
	}

	private boolean done() {
		if (!this.isValid) return true;
		return this.isDecreasing ? current <= end : current >= end;
	}

	private void initialise(FunctionPrototype functionPrototype) {
		this.putMethod(functionPrototype, SymbolValue.iterator, ($, $$) -> this);
		this.putMethod(functionPrototype, Names.next, (interpreter, arguments) -> {
			final ObjectValue result = new ObjectValue(interpreter.intrinsics.objectPrototype);
			if (done()) {
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
}

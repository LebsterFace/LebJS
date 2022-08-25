package xyz.lebster.core.value.number;

import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.value.Generator;
import xyz.lebster.core.value.IteratorResult;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.globals.Undefined;

public final class NumberRange extends Generator {
	private final boolean isValid;
	private final boolean isDecreasing;

	private final double end;
	private final double step;
	private double current;

	public NumberRange(Interpreter interpreter, double end) {
		this(interpreter, 0, end, 1);
	}

	public NumberRange(Interpreter interpreter, double start, double end) {
		this(interpreter, start, end, 1);
	}

	public NumberRange(Interpreter interpreter, double start, double end, double step) {
		super(interpreter.intrinsics.objectPrototype, interpreter.intrinsics.functionPrototype);

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

	@Override
	public IteratorResult nextMethod(Interpreter interpreter, Value<?>[] arguments) {
		if (done()) return new IteratorResult(Undefined.instance, true);

		final var result = new IteratorResult(new NumberValue(current), false);
		current += step;
		return result;
	}

	private boolean done() {
		if (!this.isValid) return true;
		return this.isDecreasing ? current <= end : current >= end;
	}
}

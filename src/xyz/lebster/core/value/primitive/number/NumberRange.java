package xyz.lebster.core.value.primitive.number;

import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.Intrinsics;
import xyz.lebster.core.value.Generator;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.globals.Undefined;

public final class NumberRange extends Generator {
	private final boolean isValid;
	private final boolean isDecreasing;

	private final double end;
	private final double step;
	private double current;

	public NumberRange(Intrinsics intrinsics, double end) {
		this(intrinsics, 0, end, 1);
	}

	public NumberRange(Intrinsics intrinsics, double start, double end) {
		this(intrinsics, start, end, 1);
	}

	public NumberRange(Intrinsics intrinsics, double start, double end, double step) {
		super(intrinsics);

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
	public Value<?> next(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		if (done()) {
			setCompleted();
			return Undefined.instance;
		}

		final NumberValue result = new NumberValue(current);
		current += step;
		return result;
	}

	private boolean done() {
		if (!this.isValid) return true;
		return this.isDecreasing ? current <= end : current >= end;
	}
}

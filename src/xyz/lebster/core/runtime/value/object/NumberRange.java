package xyz.lebster.core.runtime.value.object;

import xyz.lebster.core.runtime.Names;
import xyz.lebster.core.runtime.value.primitive.BooleanValue;
import xyz.lebster.core.runtime.value.primitive.NumberValue;
import xyz.lebster.core.runtime.value.primitive.SymbolValue;
import xyz.lebster.core.runtime.value.primitive.Undefined;
import xyz.lebster.core.runtime.value.prototype.FunctionPrototype;

public final class NumberRange extends ObjectValue {
	private final double end;
	private final double step;
	private double current;

	public NumberRange(FunctionPrototype functionPrototype, double end) {
		super(null);
		this.initialise(functionPrototype);
		this.current = 0;
		this.end = end;
		this.step = 1;
	}

	public NumberRange(FunctionPrototype functionPrototype, double start, double end) {
		super(null);
		this.initialise(functionPrototype);
		this.current = start;
		this.end = end;
		this.step = 1;
	}

	public NumberRange(FunctionPrototype functionPrototype, double start, double end, double step) {
		super(null);
		this.initialise(functionPrototype);
		this.current = start;
		this.end = end;
		this.step = step;
	}

	private void initialise(FunctionPrototype functionPrototype) {
		this.putMethod(functionPrototype, SymbolValue.iterator, ($, $$) -> this);
		this.putMethod(functionPrototype, Names.next, (interpreter, arguments) -> {
			final ObjectValue result = new ObjectValue(interpreter.intrinsics.objectPrototype);
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
}

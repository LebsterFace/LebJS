package xyz.lebster.core.value.number;

import xyz.lebster.core.value.Names;
import xyz.lebster.core.value.boolean_.BooleanValue;
import xyz.lebster.core.value.function.FunctionPrototype;
import xyz.lebster.core.value.globals.Undefined;
import xyz.lebster.core.value.object.ObjectValue;
import xyz.lebster.core.value.symbol.SymbolValue;

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

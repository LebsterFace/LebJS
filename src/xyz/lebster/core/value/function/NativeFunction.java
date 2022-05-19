package xyz.lebster.core.value.function;


import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.globals.Undefined;
import xyz.lebster.core.value.object.ObjectValue;
import xyz.lebster.core.value.string.StringValue;

import java.util.HashSet;

public final class NativeFunction extends Executable {
	private final NativeCode code;

	public NativeFunction(FunctionPrototype functionPrototype, StringValue name, NativeCode code) {
		super(functionPrototype, name);
		this.code = code;
	}

	public static StringValue toStringForName(String name) {
		return new StringValue("function " + name + "() { [native code] }");
	}

	public static Value<?> argument(int index, Value<?>[] arguments) {
		if (arguments.length <= index) return Undefined.instance;
		return arguments[index];
	}

	public static String argumentString(int index, String defaultValue, Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		if (arguments.length <= index || arguments[index] == Undefined.instance) return defaultValue;
		return arguments[index].toStringValue(interpreter).value;
	}

	public static double argumentDouble(int index, Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		if (arguments.length <= index || arguments[index] == Undefined.instance) return Double.NaN;
		return arguments[index].toNumberValue(interpreter).value;
	}

	public static int argumentInt(int index, int defaultValue, Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		if (arguments.length <= index || arguments[index] == Undefined.instance) return defaultValue;
		return arguments[index].toNumberValue(interpreter).value.intValue();
	}

	@Override
	public StringValue toStringMethod() {
		return NativeFunction.toStringForName(this.name.value);
	}

	@Override
	public Value<?> call(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		try {
			return code.execute(interpreter, arguments);
		} catch (AbruptCompletion e) {
			if (e.type != AbruptCompletion.Type.Return) throw e;
			return e.value;
		}
	}

	@Override
	public void displayRecursive(StringRepresentation representation, HashSet<ObjectValue> parents, boolean singleLine) {
		this.display(representation);
	}
}
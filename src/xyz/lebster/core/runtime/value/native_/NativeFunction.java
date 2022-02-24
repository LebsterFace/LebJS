package xyz.lebster.core.runtime.value.native_;


import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.runtime.value.Value;
import xyz.lebster.core.runtime.value.executable.Executable;
import xyz.lebster.core.runtime.value.object.ObjectValue;
import xyz.lebster.core.runtime.value.primitive.StringValue;
import xyz.lebster.core.runtime.value.primitive.Undefined;

import java.util.HashSet;

public final class NativeFunction extends Executable<NativeCode> {
	private final String name;

	public NativeFunction(ObjectValue.Key<?> name, NativeCode code) {
		super(name.toFunctionName(), code);
		this.name = name.toFunctionName().value;
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
	public Value<?> call(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		try {
			return code.execute(interpreter, arguments);
		} catch (AbruptCompletion e) {
			if (e.type != AbruptCompletion.Type.Return) throw e;
			return e.value;
		}
	}

	@Override
	protected String getName() {
		return this.name;
	}

	@Override
	public void displayRecursive(StringRepresentation representation, HashSet<ObjectValue> parents, boolean singleLine) {
		this.display(representation);
	}
}
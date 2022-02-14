package xyz.lebster.core.runtime.value.native_;


import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.runtime.value.Value;
import xyz.lebster.core.runtime.value.executable.Executable;
import xyz.lebster.core.runtime.value.object.ObjectValue;
import xyz.lebster.core.runtime.value.primitive.StringValue;
import xyz.lebster.core.runtime.value.primitive.SymbolValue;

import java.util.HashSet;

public final class NativeFunction extends Executable<NativeCode> {
	private final String name;

	public NativeFunction(StringValue name, NativeCode code) {
		super(name, code);
		this.name = name.value;
	}

	public NativeFunction(SymbolValue nameSymbol, NativeCode code) {
		this(nameSymbol.toFunctionName(), code);
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

	public static StringValue toStringForName(String name) {
		return new StringValue("function " + name + "() { [native code] }");
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
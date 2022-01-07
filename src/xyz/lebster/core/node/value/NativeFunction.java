package xyz.lebster.core.node.value;


import xyz.lebster.core.ANSI;
import xyz.lebster.core.exception.NotImplemented;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.runtime.prototype.ObjectPrototype;

import java.util.HashSet;

public final class NativeFunction extends Executable<NativeCode> {
	public NativeFunction(NativeCode code) {
		super(code);
		put(ObjectPrototype.toString, new NativeFunction(new StringLiteral("function () { [native code] }")));
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
	public void represent(StringRepresentation representation) {
		representation.append(ANSI.BRIGHT_MAGENTA);
		representation.append("[Native Function]");
		representation.append(ANSI.RESET);
	}

	@Override
	public void representRecursive(StringRepresentation representation, HashSet<Dictionary> parents) {
		this.represent(representation);
	}

	@Override
	public NumericLiteral toNumericLiteral(Interpreter interpreter) {
		throw new NotImplemented("NativeFunction -> NumericLiteral");
	}

	@Override
	public BooleanLiteral toBooleanLiteral(Interpreter interpreter) {
		throw new NotImplemented("NativeFunction -> BooleanLiteral");
	}

	@Override
	public Dictionary toDictionary(Interpreter interpreter) {
		throw new NotImplemented("NativeFunction -> Dictionary");
	}
}
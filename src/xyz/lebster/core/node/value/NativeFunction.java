package xyz.lebster.core.node.value;


import xyz.lebster.core.ANSI;
import xyz.lebster.core.exception.NotImplemented;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.StringRepresentation;

import java.util.Set;

public final class NativeFunction extends Executable<NativeCode> {
	public NativeFunction(NativeCode code) {
		super(code);
		put("toString", new NativeFunction(new StringLiteral("function () { [native code] }")));
	}

	public NativeFunction(Value<?> value) {
		super((interpreter, arguments) -> value);
	}

	@Override
	protected Value<?> call(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		return code.execute(interpreter, arguments);
	}

	@Override
	public void represent(StringRepresentation representation) {
		representation.append(ANSI.BRIGHT_MAGENTA);
		representation.append("[Native Function]");
		representation.append(ANSI.RESET);
	}

	@Override
	public void representRecursive(StringRepresentation representation, Set<Dictionary> parents) {
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
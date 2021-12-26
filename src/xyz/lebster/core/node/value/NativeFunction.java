package xyz.lebster.core.node.value;


import xyz.lebster.core.ANSI;
import xyz.lebster.core.exception.NotImplemented;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.StringRepresentation;

import java.util.List;

public final class NativeFunction extends Executable<NativeCode> {
	public NativeFunction(NativeCode code) {
		super(code);
	}

	@Override
	protected Value<?> call(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		try {
			return code.execute(interpreter, arguments);
		} catch (AbruptCompletion e) {
			if (e.type != AbruptCompletion.Type.Return) throw e;
			return e.value;
		}
	}

	@Override
	public String toStringWithoutSideEffects() {
//		TODO: Function name
		return "function() { [native code] }";
	}

	@Override
	public String toStringForLogging() {
//		TODO: Function name
		return "ƒ () { [native code] }";
	}

	@Override
	public void represent(StringRepresentation representation) {
		representation.append(ANSI.MAGENTA);
		representation.append("function");
		representation.append(ANSI.RESET);
		representation.append("() { ");
		representation.append(ANSI.YELLOW);
		representation.append("[native code]");
		representation.append(ANSI.RESET);
		representation.append(" }");
	}

	@Override
	protected void representRecursive(StringRepresentation representation, List<Dictionary> parents) {
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
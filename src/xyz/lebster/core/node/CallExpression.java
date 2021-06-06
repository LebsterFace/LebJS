package xyz.lebster.core.node;

import xyz.lebster.core.exception.LTypeError;
import xyz.lebster.core.runtime.Interpreter;
import xyz.lebster.core.exception.LanguageException;
import xyz.lebster.core.runtime.ScopeFrame;
import xyz.lebster.core.value.*;

public class CallExpression extends Expression {
	public final Identifier callee;

	public CallExpression(Identifier callee) {
		this.callee = callee;
	}

	@Override
	public void dump(int indent) {
		Interpreter.dumpIndent(indent);
		System.out.print("CallExpression ");
		System.out.print(callee);
		System.out.println("");
	}

	@Override
	public Value<?> execute(Interpreter interpreter) throws LanguageException {
		final Value<?> value = interpreter.getVariable(callee);

		if (value.type == Type.Function) {
			final ScopeNode func = ((Function) value).value;
			final Value<?> result = func.executeChildren(interpreter);
			interpreter.exitScope(func);
			return result;
		} else if (value.type == Type.NativeFunction) {
			final NativeCode code = ((NativeFunction) value).value;
			final Value<?> result = code.execute(interpreter, null);
			return result;
		} else {
			throw new LTypeError("Can only call a function!");
		}
	}
}

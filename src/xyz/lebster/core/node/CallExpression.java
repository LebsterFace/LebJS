package xyz.lebster.core.node;

import xyz.lebster.core.exception.LTypeError;
import xyz.lebster.core.runtime.Interpreter;
import xyz.lebster.core.exception.LanguageException;
import xyz.lebster.core.runtime.ScopeFrame;
import xyz.lebster.core.value.Function;
import xyz.lebster.core.value.Type;
import xyz.lebster.core.value.Undefined;
import xyz.lebster.core.value.Value;

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

		if (value.type != Type.Function) {
			throw new LTypeError("Can only call a function!");
		}

		final ScopeNode func = ((Function) value).value;
		Value<?> returnValue = new Undefined();

		// TODO: Probably can move this to ScopeNode
		final ScopeFrame frame = interpreter.enterScope(func);
		for (ASTNode child : func.children) {
			child.execute(interpreter);

			if (frame.didExit) {
				returnValue = frame.getExitValue();
				break;
			}
		}

		interpreter.exitScope(func);
		return returnValue;
	}
}

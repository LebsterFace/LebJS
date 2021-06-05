package xyz.lebster.node;

import xyz.lebster.Interpreter;
import xyz.lebster.exception.LanguageException;
import xyz.lebster.ScopeFrame;
import xyz.lebster.value.Function;
import xyz.lebster.value.Type;
import xyz.lebster.value.Undefined;
import xyz.lebster.value.Value;

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
			throw new LanguageException("Can only call a function!");
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

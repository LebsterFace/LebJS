package xyz.lebster.core.value;

import xyz.lebster.core.exception.LanguageException;
import xyz.lebster.core.exception.NotImplementedException;
import xyz.lebster.core.node.ASTNode;
import xyz.lebster.core.node.FunctionDeclaration;
import xyz.lebster.core.runtime.Interpreter;
import xyz.lebster.core.runtime.ScopeFrame;

public class Function extends Value<FunctionDeclaration> {
	public Function(FunctionDeclaration value) {
		super(Type.Function, value);
	}

	@Override
	public StringLiteral toStringLiteral() throws NotImplementedException {
		throw new NotImplementedException("Function -> StringLiteral");
	}

	@Override
	public BooleanLiteral toBooleanLiteral() {
		return new BooleanLiteral(true);
	}

	@Override
	public NumericLiteral toNumericLiteral() {
		return new NumericLiteral(Double.NaN);
	}

	@Override
	public Function toFunction() {
		return this;
	}

	@Override
	public Dictionary toDictionary() throws NotImplementedException {
		throw new NotImplementedException("Function -> Dictionary");
	}

	@Override
	public void dump(int indent) {
		Interpreter.dumpIndent(indent);
		System.out.println("Function");
	}

	public Value<?> executeChildren(Interpreter interpreter, Value<?>[] arguments) throws LanguageException {
		Value<?> result = new Undefined();
		final ScopeFrame frame = interpreter.enterScope(value);

		for (int i = 0; i < arguments.length; i++) {
			interpreter.declareVariable(value.arguments[i], arguments[i]);
		}

		for (ASTNode child : value.children) {
			child.execute(interpreter);

			if (frame.didExit) {
				result = frame.getExitValue();
				break;
			}
		}

		return result;
	}
}

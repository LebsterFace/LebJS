package xyz.lebster.core.value;

import xyz.lebster.core.node.ASTNode;
import xyz.lebster.core.node.FunctionDeclaration;
import xyz.lebster.core.runtime.Interpreter;
import xyz.lebster.core.runtime.ScopeFrame;
import xyz.lebster.exception.LanguageException;
import xyz.lebster.exception.NotImplemented;

public class Function extends Executable<FunctionDeclaration> {
	public Function(FunctionDeclaration value) {
		super(Type.Function, value);
	}

	@Override
	public StringLiteral toStringLiteral() throws NotImplemented {
		throw new NotImplemented("Function -> StringLiteral");
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
	public Dictionary toDictionary() throws NotImplemented {
		throw new NotImplemented("Function -> Dictionary");
	}

	@Override
	public void dump(int indent) {
		Interpreter.dumpIndent(indent);
		System.out.println("Function");
	}

	public Value<?> executeChildren(Interpreter interpreter, Value<?>[] arguments) throws LanguageException {
		Value<?> result = new Undefined();
		final ScopeFrame scope = interpreter.enterScope(value);

		for (int i = 0; i < arguments.length; i++) {
			interpreter.declareVariable(value.arguments[i], arguments[i]);
		}

		for (ASTNode child : value.children) {
			child.execute(interpreter);
			if (scope.didExit) {
				result = scope.getExitValue();
				break;
			}
		}

		interpreter.exitScope(scope);
		return result;
	}
}

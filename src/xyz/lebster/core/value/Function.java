package xyz.lebster.core.value;

import xyz.lebster.core.node.ASTNode;
import xyz.lebster.core.node.FunctionNode;
import xyz.lebster.core.runtime.AbruptCompletion;
import xyz.lebster.core.runtime.Interpreter;
import xyz.lebster.core.runtime.ScopeFrame;
import xyz.lebster.exception.NotImplemented;

public class Function extends Executable<FunctionNode> {
	public Function(FunctionNode value) {
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
		Interpreter.dumpValue(indent, "Function");
	}

	public Value<?> executeChildren(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		Value<?> result = new Undefined();
		final ScopeFrame scope = interpreter.enterScope(value.body);

		for (int i = 0; i < arguments.length && i < value.arguments.length; i++) {
			interpreter.declareVariable(value.arguments[i], arguments[i]);
		}

		for (ASTNode child : value.body.children) {
			try {
				child.execute(interpreter);
			} catch (AbruptCompletion e) {
				if (e.type != AbruptCompletion.Type.Return) throw e;
				result = e.value;
				break;
			}
		}

		interpreter.exitScope(scope);
		return result;
	}
}

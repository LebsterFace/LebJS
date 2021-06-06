package xyz.lebster.core.value;

import xyz.lebster.core.exception.LanguageException;
import xyz.lebster.core.exception.NotImplementedException;
import xyz.lebster.core.node.ScopeNode;
import xyz.lebster.core.runtime.Interpreter;

public class Function extends Value<ScopeNode> {
	public Function(ScopeNode value) {
		super(Type.Function, value);
	}

	@Override
	public StringLiteral toStringLiteral() throws NotImplementedException {
		throw new NotImplementedException("Function -> StringLiteral");
	}

	@Override
	public BooleanLiteral toBooleanLiteral() throws NotImplementedException {
		throw new NotImplementedException("Function -> BooleanLiteral");
	}

	@Override
	public NumericLiteral toNumericLiteral() throws NotImplementedException {
		throw new NotImplementedException("Function -> NumericLiteral");
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
		System.out.println("FunctionObject");
	}
}

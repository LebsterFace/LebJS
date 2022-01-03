package xyz.lebster.core.node.value;

import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.exception.NotImplemented;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.runtime.TypeError;

@SpecificationURL("https://tc39.es/ecma262/multipage#sec-ecmascript-language-types-symbol-type")
public class Symbol extends Dictionary.Key<Void> {
	public final String description;

	public Symbol() {
		super(null, Type.Symbol);
		this.description = null;
	}

	public Symbol(String description) {
		super(null, Type.Symbol);
		this.description = description;
	}

	@Override
	public StringLiteral toStringLiteral(Interpreter interpreter) throws AbruptCompletion {
		throw AbruptCompletion.error(new TypeError("Cannot convert a Symbol value to a string"));
	}

	@Override
	public NumericLiteral toNumericLiteral(Interpreter interpreter) throws AbruptCompletion {
		throw AbruptCompletion.error(new TypeError("Cannot convert a Symbol value to a number"));
	}

	@Override
	public BooleanLiteral toBooleanLiteral(Interpreter interpreter) throws AbruptCompletion {
		return new BooleanLiteral(true);
	}

	@Override
	public Dictionary toDictionary(Interpreter interpreter) throws AbruptCompletion {
		throw new NotImplemented("SymbolWrapper");
	}

	@Override
	public String typeOf(Interpreter interpreter) throws AbruptCompletion {
		return "symbol";
	}
}

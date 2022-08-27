package xyz.lebster.core.value.primitive.string;

import xyz.lebster.core.NonStandard;
import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.value.Names;
import xyz.lebster.core.value.primitive.PrimitiveConstructor;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.function.FunctionPrototype;
import xyz.lebster.core.value.primitive.symbol.SymbolValue;

@SpecificationURL("https://tc39.es/ecma262/multipage#sec-string-constructor")
@NonStandard
public class StringConstructor extends PrimitiveConstructor {
	public StringConstructor(FunctionPrototype functionPrototype) {
		super(functionPrototype, Names.String);
	}

	@Override
	public StringValue call(Interpreter interpreter, Value<?>... arguments) throws AbruptCompletion {
		if (arguments.length == 0) return StringValue.EMPTY;
		else if (arguments[0] instanceof SymbolValue symbolValue) return new StringValue(symbolValue.toDisplayString());
		else return arguments[0].toStringValue(interpreter);
	}
}

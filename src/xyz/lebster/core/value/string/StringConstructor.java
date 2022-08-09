package xyz.lebster.core.value.string;

import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.value.BuiltinConstructor;
import xyz.lebster.core.value.Names;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.function.FunctionPrototype;
import xyz.lebster.core.value.object.ObjectPrototype;
import xyz.lebster.core.value.object.ObjectValue;
import xyz.lebster.core.value.symbol.SymbolValue;

@SpecificationURL("https://tc39.es/ecma262/multipage#sec-string-constructor")
public class StringConstructor extends BuiltinConstructor<StringWrapper, StringPrototype> {
	public StringConstructor(ObjectPrototype objectPrototype, FunctionPrototype functionPrototype) {
		super(objectPrototype, functionPrototype, Names.String);
	}

	@Override
	public StringWrapper construct(Interpreter interpreter, Value<?>[] arguments, ObjectValue newTarget) throws AbruptCompletion {
		final StringValue data = arguments.length == 0 ? StringValue.EMPTY : arguments[0].toStringValue(interpreter);
		return new StringWrapper(interpreter.intrinsics.stringPrototype, data);
	}

	@Override
	public StringValue call(Interpreter interpreter, Value<?>... arguments) throws AbruptCompletion {
		if (arguments.length == 0) return StringValue.EMPTY;
		else if (arguments[0] instanceof SymbolValue symbolValue) return new StringValue(symbolValue.toDisplayString());
		else return arguments[0].toStringValue(interpreter);
	}
}

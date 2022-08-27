package xyz.lebster.core.value.primitive.symbol;

import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.value.Names;
import xyz.lebster.core.value.primitive.PrimitiveConstructor;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.function.FunctionPrototype;
import xyz.lebster.core.value.globals.Undefined;
import xyz.lebster.core.value.primitive.string.StringValue;

@SpecificationURL("https://tc39.es/ecma262/multipage#sec-symbol-constructor")
public final class SymbolConstructor extends PrimitiveConstructor {
	public SymbolConstructor(FunctionPrototype functionPrototype) {
		super(functionPrototype, Names.Symbol);
		put(Names.asyncIterator, SymbolValue.asyncIterator, false, false, false);
		put(Names.hasInstance, SymbolValue.hasInstance, false, false, false);
		put(Names.isConcatSpreadable, SymbolValue.isConcatSpreadable, false, false, false);
		put(Names.iterator, SymbolValue.iterator, false, false, false);
		put(Names.match, SymbolValue.match, false, false, false);
		put(Names.matchAll, SymbolValue.matchAll, false, false, false);
		put(Names.replace, SymbolValue.replace, false, false, false);
		put(Names.search, SymbolValue.search, false, false, false);
		put(Names.species, SymbolValue.species, false, false, false);
		put(Names.split, SymbolValue.split, false, false, false);
		put(Names.toPrimitive, SymbolValue.toPrimitive, false, false, false);
		put(Names.toStringTag, SymbolValue.toStringTag, false, false, false);
		put(Names.unscopables, SymbolValue.unscopables, false, false, false);
	}

	@Override
	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-symbol-description")
	public SymbolValue call(Interpreter interpreter, Value<?>... arguments) throws AbruptCompletion {
		// 20.4.1.1 Symbol ( [ description ] )
		final Value<?> description = arguments.length == 0 ? Undefined.instance : arguments[0];
		// 1. If NewTarget is not undefined, throw a TypeError exception.
		// 2. If description is undefined, let descString be undefined.
		// 3. Else, let descString be ? ToString(description).
		final StringValue descString = description == Undefined.instance ? null : description.toStringValue(interpreter);
		// 4. Return a new unique Symbol value whose [[Description]] value is descString.
		return new SymbolValue(descString);
	}
}

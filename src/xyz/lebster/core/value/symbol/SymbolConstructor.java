package xyz.lebster.core.value.symbol;

import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.value.Names;
import xyz.lebster.core.value.PrimitiveConstructor;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.function.Executable;
import xyz.lebster.core.value.function.FunctionPrototype;
import xyz.lebster.core.value.function.NativeFunction;
import xyz.lebster.core.value.globals.Undefined;
import xyz.lebster.core.value.string.StringValue;

@SpecificationURL("https://tc39.es/ecma262/multipage#sec-symbol-constructor")
public final class SymbolConstructor extends PrimitiveConstructor {
	public SymbolConstructor(FunctionPrototype functionPrototype) {
		super(functionPrototype, Names.Symbol);
		// FIXME: { [[Writable]]: false, [[Enumerable]]: false, [[Configurable]]: false }
		this.putNonWritable(Names.asyncIterator, SymbolValue.asyncIterator);
		this.putNonWritable(Names.hasInstance, SymbolValue.hasInstance);
		this.putNonWritable(Names.isConcatSpreadable, SymbolValue.isConcatSpreadable);
		this.putNonWritable(Names.iterator, SymbolValue.iterator);
		this.putNonWritable(Names.match, SymbolValue.match);
		this.putNonWritable(Names.matchAll, SymbolValue.matchAll);
		this.putNonWritable(Names.replace, SymbolValue.replace);
		this.putNonWritable(Names.search, SymbolValue.search);
		this.putNonWritable(Names.species, SymbolValue.species);
		this.putNonWritable(Names.split, SymbolValue.split);
		this.putNonWritable(Names.toPrimitive, SymbolValue.toPrimitive);
		this.putNonWritable(Names.toStringTag, SymbolValue.toStringTag);
		this.putNonWritable(Names.unscopables, SymbolValue.unscopables);
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

package xyz.lebster.core.runtime.value.constructor;

import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.exception.NotImplemented;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.runtime.Names;
import xyz.lebster.core.runtime.value.Value;
import xyz.lebster.core.runtime.value.object.SymbolWrapper;
import xyz.lebster.core.runtime.value.primitive.StringValue;
import xyz.lebster.core.runtime.value.primitive.SymbolValue;
import xyz.lebster.core.runtime.value.primitive.Undefined;
import xyz.lebster.core.runtime.value.prototype.FunctionPrototype;
import xyz.lebster.core.runtime.value.prototype.ObjectPrototype;
import xyz.lebster.core.runtime.value.prototype.SymbolPrototype;

@SpecificationURL("https://tc39.es/ecma262/multipage#sec-symbol-constructor")
public final class SymbolConstructor extends BuiltinConstructor<SymbolWrapper, SymbolPrototype> {
	public SymbolConstructor(ObjectPrototype objectPrototype, FunctionPrototype functionPrototype) {
		super(objectPrototype, functionPrototype, Names.Symbol);

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
	// @Override
	// public StringValue toStringMethod() {
	// 	return NativeFunction.toStringForName(Names.Symbol.value);
	// }

	@Override
	public SymbolWrapper construct(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		throw new NotImplemented("!!!!!");
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

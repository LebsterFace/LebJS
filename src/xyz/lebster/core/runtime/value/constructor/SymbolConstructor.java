package xyz.lebster.core.runtime.value.constructor;

import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.runtime.Names;
import xyz.lebster.core.runtime.value.Value;
import xyz.lebster.core.runtime.value.error.TypeError;
import xyz.lebster.core.runtime.value.executable.Constructor;
import xyz.lebster.core.runtime.value.object.ObjectValue;
import xyz.lebster.core.runtime.value.primitive.SymbolValue;
import xyz.lebster.core.runtime.value.primitive.UndefinedValue;
import xyz.lebster.core.runtime.value.prototype.SymbolPrototype;

@SpecificationURL("https://tc39.es/ecma262/multipage#sec-symbol-constructor")
public final class SymbolConstructor extends Constructor<Void> {
	public static final SymbolConstructor instance = new SymbolConstructor();

	static {
		instance.putNonWritable(Names.prototype, SymbolPrototype.instance);
		instance.putNonWritable(Names.asyncIterator, SymbolValue.asyncIterator);
		instance.putNonWritable(Names.hasInstance, SymbolValue.hasInstance);
		instance.putNonWritable(Names.isConcatSpreadable, SymbolValue.isConcatSpreadable);
		instance.putNonWritable(Names.iterator, SymbolValue.iterator);
		instance.putNonWritable(Names.match, SymbolValue.match);
		instance.putNonWritable(Names.matchAll, SymbolValue.matchAll);
		instance.putNonWritable(Names.replace, SymbolValue.replace);
		instance.putNonWritable(Names.search, SymbolValue.search);
		instance.putNonWritable(Names.species, SymbolValue.species);
		instance.putNonWritable(Names.split, SymbolValue.split);
		instance.putNonWritable(Names.toPrimitive, SymbolValue.toPrimitive);
		instance.putNonWritable(Names.toStringTag, SymbolValue.toStringTag);
		instance.putNonWritable(Names.unscopables, SymbolValue.unscopables);
	}

	private SymbolConstructor() {
		super(null);
	}

	@Override
	public ObjectValue construct(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		throw AbruptCompletion.error(new TypeError("Symbol is not a constructor"));
	}

	@Override
	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-symbol-description")
	public SymbolValue call(Interpreter interpreter, Value<?>... arguments) throws AbruptCompletion {
		// 20.4.1.1 Symbol ( [ description ] )
		final Value<?> description = arguments.length == 0 ? UndefinedValue.instance : arguments[0];
		// 1. If NewTarget is not undefined, throw a TypeError exception.
		// 2. If description is undefined, let descString be undefined.
		// 3. Else, let descString be ? ToString(description).
		final String descString = description == UndefinedValue.instance ? null : description.toStringValue(interpreter).value;
		// 4. Return a new unique Symbol value whose [[Description]] value is descString.
		return new SymbolValue(descString);
	}

	@Override
	protected String getName() {
		return "Symbol";
	}
}
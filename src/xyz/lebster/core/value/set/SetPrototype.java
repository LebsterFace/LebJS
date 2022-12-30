package xyz.lebster.core.value.set;

import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.exception.NotImplemented;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.Intrinsics;
import xyz.lebster.core.value.Names;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.error.type.TypeError;
import xyz.lebster.core.value.object.NativeAccessorDescriptor;
import xyz.lebster.core.value.object.ObjectValue;
import xyz.lebster.core.value.primitive.number.NumberValue;
import xyz.lebster.core.value.primitive.symbol.SymbolValue;

import static xyz.lebster.core.interpreter.AbruptCompletion.error;
import static xyz.lebster.core.value.function.NativeFunction.argument;

@SpecificationURL("https://tc39.es/ecma262/multipage#sec-properties-of-the-set-prototype-object")
public class SetPrototype extends ObjectValue {
	public SetPrototype(Intrinsics intrinsics) {
		super(intrinsics);

		putMethod(intrinsics, Names.add, 1, SetPrototype::add);
		putMethod(intrinsics, Names.clear, 0, SetPrototype::clear);
		putMethod(intrinsics, Names.delete, 1, SetPrototype::delete);
		putMethod(intrinsics, Names.entries, 0, SetPrototype::entries);
		putMethod(intrinsics, Names.forEach, 1, SetPrototype::forEach);
		putMethod(intrinsics, Names.has, 1, SetPrototype::has);
		putMethod(intrinsics, Names.keys, 0, SetPrototype::keys);


		final var values = putMethod(intrinsics, Names.values, 0, SetPrototype::values);
		put(SymbolValue.iterator, values, false, false, true);
		put(SymbolValue.toStringTag, Names.Set, false, false, true);
		// 1. Let S be the `this` value.
		// 2. Perform ? RequireInternalSlot(S, [[SetData]]).
		this.value.put(Names.size, new NativeAccessorDescriptor(false, true, true, false) {
			@Override
			public NumberValue get(Interpreter interpreter, ObjectValue thisValue) throws AbruptCompletion {
				// 1. Let S be the `this` value.
				final var S = thisValue;
				// 2. Perform ? RequireInternalSlot(S, [[SetData]]).
				if (S instanceof final SetObject setObject) {
					return setObject.getSize();
				} else {
					throw error(new TypeError(interpreter, "Set.prototype.size requires that 'this' be a Set"));
				}
			}
		});
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-set.prototype.add")
	private static Value<?> add(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 24.2.3.1 Set.prototype.add ( value )
		Value<?> value = argument(0, arguments);

		// 1. Let S be the `this` value.
		// 2. Perform ? RequireInternalSlot(S, [[SetData]]).
		if (!(interpreter.thisValue() instanceof final SetObject S)) {
			throw error(new TypeError(interpreter, "Set.prototype.add requires that 'this' be a Set."));
		}

		// 3. Let entries be the List that is S.[[SetData]].
		final var entries = S.setData;
		// 4. For each element e of entries, do
		for (final Value<?> e : entries) {
			// a. If e is not empty and SameValueZero(e, value) is true, then
			if (e != null && e.sameValueZero(value))
				// i. Return S.
				return S;
		}

		// 5. If value is -0𝔽, set value to +0𝔽.
		if (NumberValue.isNegativeZero(value)) value = NumberValue.ZERO;
		// 6. Append value to entries.
		entries.add(value);
		// 7. Return S.
		return S;
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-set.prototype.clear")
	private static Value<?> clear(Interpreter interpreter, Value<?>[] arguments) {
		// 24.2.3.2 Set.prototype.clear ( )

		throw new NotImplemented("Set.prototype.clear");
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-set.prototype.delete")
	private static Value<?> delete(Interpreter interpreter, Value<?>[] arguments) {
		// 24.2.3.4 Set.prototype.delete ( value )
		final Value<?> value = argument(0, arguments);

		throw new NotImplemented("Set.prototype.delete");
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-set.prototype.entries")
	private static Value<?> entries(Interpreter interpreter, Value<?>[] arguments) {
		// 24.2.3.5 Set.prototype.entries ( )

		throw new NotImplemented("Set.prototype.entries");
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-set.prototype.foreach")
	private static Value<?> forEach(Interpreter interpreter, Value<?>[] arguments) {
		// 24.2.3.6 Set.prototype.forEach ( callbackfn [ , thisArg ] )
		final Value<?> callbackfn = argument(0, arguments);
		final Value<?> thisArg = argument(1, arguments);

		throw new NotImplemented("Set.prototype.forEach");
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-set.prototype.has")
	private static Value<?> has(Interpreter interpreter, Value<?>[] arguments) {
		// 24.2.3.7 Set.prototype.has ( value )
		final Value<?> value = argument(0, arguments);

		throw new NotImplemented("Set.prototype.has");
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-set.prototype.keys")
	private static Value<?> keys(Interpreter interpreter, Value<?>[] arguments) {
		// 24.2.3.8 Set.prototype.keys ( )

		throw new NotImplemented("Set.prototype.keys");
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-set.prototype.values")
	private static Value<?> values(Interpreter interpreter, Value<?>[] arguments) {
		// 24.2.3.10 Set.prototype.values ( )

		throw new NotImplemented("Set.prototype.values");
	}
}
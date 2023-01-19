package xyz.lebster.core.value.shadowrealm;

import xyz.lebster.core.NonStandard;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.Intrinsics;
import xyz.lebster.core.value.Names;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.array.ArrayObject;
import xyz.lebster.core.value.error.type.TypeError;
import xyz.lebster.core.value.globals.Undefined;
import xyz.lebster.core.value.object.ObjectValue;
import xyz.lebster.core.value.primitive.string.StringValue;

import static xyz.lebster.core.interpreter.AbruptCompletion.error;
import static xyz.lebster.core.value.function.NativeFunction.argument;

@NonStandard
public final class ShadowRealmPrototype extends ObjectValue {
	public ShadowRealmPrototype(Intrinsics intrinsics) {
		super(intrinsics);

		putMethod(intrinsics, Names.evaluate, 1, ShadowRealmPrototype::evaluate);
		putMethod(intrinsics, Names.declare, 2, ShadowRealmPrototype::declare);
	}

	@NonStandard
	private static Value<?> evaluate(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// ShadowRealm.prototype.evaluate(sourceText: string): unknown

		if (!(interpreter.thisValue() instanceof final ShadowRealm shadowRealm)) {
			throw error(new TypeError(interpreter, "ShadowRealm.prototype.evaluate requires that 'this' be a ShadowRealm"));
		}

		if (arguments.length == 0) return Undefined.instance;

		final Value<?>[] results = new Value[arguments.length];
		for (int i = 0; i < arguments.length; i++) {
			results[i] = shadowRealm.evaluate(arguments[i].toStringValue(interpreter).value);
		}

		return results.length == 1 ? results[0] : new ArrayObject(interpreter, results);
	}

	@NonStandard
	private static Value<?> declare(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// ShadowRealm.prototype.declare<T>(name: string, value: T): T

		if (!(interpreter.thisValue() instanceof final ShadowRealm shadowRealm))
			throw error(new TypeError(interpreter, "ShadowRealm.prototype.declare requires that `this` be a ShadowRealm"));
		if (arguments.length < 1)
			throw error(new TypeError(interpreter, "Missing variable name"));

		final StringValue name = arguments[0].toStringValue(interpreter);
		final Value<?> value = argument(1, arguments);

		shadowRealm.declare(name, value);
		return value;
	}
}
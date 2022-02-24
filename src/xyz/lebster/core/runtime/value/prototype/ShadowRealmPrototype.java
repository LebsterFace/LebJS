package xyz.lebster.core.runtime.value.prototype;

import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.runtime.Names;
import xyz.lebster.core.runtime.value.Value;
import xyz.lebster.core.runtime.value.constructor.ShadowRealmConstructor;
import xyz.lebster.core.runtime.value.error.TypeError;
import xyz.lebster.core.runtime.value.object.ArrayObject;
import xyz.lebster.core.runtime.value.object.ObjectValue;
import xyz.lebster.core.runtime.value.object.ShadowRealm;
import xyz.lebster.core.runtime.value.primitive.Undefined;

import static xyz.lebster.core.runtime.value.native_.NativeFunction.argument;

public final class ShadowRealmPrototype extends ObjectValue {
	public static final ShadowRealmPrototype instance = new ShadowRealmPrototype();

	static {
		instance.put(Names.constructor, ShadowRealmConstructor.instance);
		instance.putMethod(Names.evaluate, ShadowRealmPrototype::evaluate);
		instance.putMethod(Names.declare, ShadowRealmPrototype::declare);
	}

	private ShadowRealmPrototype() {
	}

	private static Value<?> evaluate(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		if (!(interpreter.thisValue() instanceof final ShadowRealm shadowRealm)) {
			throw AbruptCompletion.error(new TypeError("ShadowRealm.prototype.evaluate requires that 'this' be a ShadowRealm"));
		}

		if (arguments.length == 0) return Undefined.instance;

		final Value<?>[] results = new Value[arguments.length];
		for (int i = 0; i < arguments.length; i++) {
			results[i] = shadowRealm.evaluate(arguments[i].toStringValue(interpreter).value);
		}

		return results.length == 1 ? results[0] : new ArrayObject(results);
	}

	private static Value<?> declare(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		if (!(interpreter.thisValue() instanceof final ShadowRealm shadowRealm))
			throw AbruptCompletion.error(new TypeError("ShadowRealm.prototype.declare requires that `this` be a ShadowRealm"));
		if (arguments.length < 1)
			throw AbruptCompletion.error(new TypeError("Missing variable name"));

		final String name = arguments[0].toStringValue(interpreter).value;
		final Value<?> value = argument(1, arguments);

		shadowRealm.declare(name, value);
		return value;
	}
}
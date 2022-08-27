package xyz.lebster.core.value.shadowrealm;

import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.value.Names;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.array.ArrayObject;
import xyz.lebster.core.value.error.TypeError;
import xyz.lebster.core.value.function.FunctionPrototype;
import xyz.lebster.core.value.globals.Undefined;
import xyz.lebster.core.value.object.ObjectPrototype;
import xyz.lebster.core.value.object.ObjectValue;

import static xyz.lebster.core.value.function.NativeFunction.argument;

public final class ShadowRealmPrototype extends ObjectValue {
	public ShadowRealmPrototype(ObjectPrototype objectPrototype, FunctionPrototype functionPrototype) {
		super(objectPrototype);

		this.putMethod(functionPrototype, Names.evaluate, ShadowRealmPrototype::evaluate);
		this.putMethod(functionPrototype, Names.declare, ShadowRealmPrototype::declare);
	}

	private static Value<?> evaluate(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		if (!(interpreter.thisValue() instanceof final ShadowRealm shadowRealm)) {
			throw AbruptCompletion.error(new TypeError(interpreter, "ShadowRealm.prototype.evaluate requires that 'this' be a ShadowRealm"));
		}

		if (arguments.length == 0) return Undefined.instance;

		final Value<?>[] results = new Value[arguments.length];
		for (int i = 0; i < arguments.length; i++) {
			results[i] = shadowRealm.evaluate(arguments[i].toStringValue(interpreter).value);
		}

		return results.length == 1 ? results[0] : new ArrayObject(interpreter, results);
	}

	private static Value<?> declare(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		if (!(interpreter.thisValue() instanceof final ShadowRealm shadowRealm))
			throw AbruptCompletion.error(new TypeError(interpreter, "ShadowRealm.prototype.declare requires that `this` be a ShadowRealm"));
		if (arguments.length < 1)
			throw AbruptCompletion.error(new TypeError(interpreter, "Missing variable name"));

		final String name = arguments[0].toStringValue(interpreter).value;
		final Value<?> value = argument(1, arguments);

		shadowRealm.declare(name, value);
		return value;
	}
}
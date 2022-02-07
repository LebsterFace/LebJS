package xyz.lebster.core.runtime.value.object;

import xyz.lebster.core.exception.CannotParse;
import xyz.lebster.core.exception.SyntaxError;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.Realm;
import xyz.lebster.core.runtime.value.Value;
import xyz.lebster.core.runtime.value.error.EvalError;
import xyz.lebster.core.runtime.value.error.TypeError;
import xyz.lebster.core.runtime.value.primitive.Undefined;
import xyz.lebster.core.runtime.value.prototype.ShadowRealmPrototype;

public final class ShadowRealm extends ObjectValue {
	private final Realm realm = new Realm(new Interpreter());

	public ShadowRealm() {
		this.putMethod("evaluate", (Interpreter interpreter, Value<?>[] arguments) -> {
			if (arguments.length == 0)
				return Undefined.instance;

			final Value<?>[] results = new Value[arguments.length];
			for (int i = 0; i < arguments.length; i++) {
				results[i] = evaluate(arguments[i].toStringValue(interpreter).value);
			}

			return results.length == 1 ? results[0] : new ArrayObject(results);
		});

		this.putMethod("declare", (interpreter, arguments) -> {
			if (arguments.length < 1)
				throw AbruptCompletion.error(new TypeError("Missing variable name"));

			final String name = arguments[0].toStringValue(interpreter).value;
			final Value<?> value = arguments.length > 1 ? arguments[1] : Undefined.instance;

			this.realm.interpreter().declareVariable(name, value);
			return Undefined.instance;
		});
	}

	@Override
	public ShadowRealmPrototype getDefaultPrototype() {
		return ShadowRealmPrototype.instance;
	}

	private Value<?> evaluate(String sourceText) throws AbruptCompletion {
		try {
			return this.realm.execute(sourceText, false);
		} catch (AbruptCompletion | SyntaxError | CannotParse e) {
			throw AbruptCompletion.error(new EvalError(e));
		}
	}
}

package xyz.lebster.core.runtime.value.object;

import xyz.lebster.core.exception.CannotParse;
import xyz.lebster.core.exception.SyntaxError;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.Realm;
import xyz.lebster.core.runtime.value.Value;
import xyz.lebster.core.runtime.value.error.EvalError;
import xyz.lebster.core.runtime.value.prototype.ShadowRealmPrototype;

public final class ShadowRealm extends ObjectValue {
	private final Realm realm = new Realm(new Interpreter());

	public ShadowRealm(ShadowRealmPrototype prototype) {
		super(prototype);
	}

	public Value<?> evaluate(String sourceText) throws AbruptCompletion {
		try {
			return this.realm.execute(sourceText, false);
		} catch (AbruptCompletion | SyntaxError | CannotParse e) {
			throw AbruptCompletion.error(new EvalError(realm.interpreter(), e));
		}
	}

	public void declare(String name, Value<?> value) throws AbruptCompletion {
		this.realm.interpreter().declareVariable(name, value);
	}
}

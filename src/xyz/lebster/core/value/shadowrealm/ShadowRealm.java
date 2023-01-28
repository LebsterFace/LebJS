package xyz.lebster.core.value.shadowrealm;

import xyz.lebster.core.exception.CannotParse;
import xyz.lebster.core.exception.SyntaxError;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.Intrinsics;
import xyz.lebster.core.interpreter.Realm;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.error.EvalError;
import xyz.lebster.core.value.object.ObjectValue;
import xyz.lebster.core.value.primitive.string.StringValue;

import static xyz.lebster.core.interpreter.AbruptCompletion.error;
import static xyz.lebster.core.node.declaration.VariableDeclaration.Kind.Let;

public final class ShadowRealm extends ObjectValue {
	private final Realm realm = new Realm(new Interpreter());

	public ShadowRealm(Intrinsics intrinsics) {
		super(intrinsics.shadowRealmPrototype);
	}

	public Value<?> evaluate(String sourceText) throws AbruptCompletion {
		try {
			return this.realm.execute(sourceText, false);
		} catch (AbruptCompletion | SyntaxError | CannotParse e) {
			throw error(new EvalError(realm.interpreter(), e));
		}
	}

	public void declare(StringValue name, Value<?> value) throws AbruptCompletion {
		realm.interpreter().declareVariable(Let, name, value);
	}
}

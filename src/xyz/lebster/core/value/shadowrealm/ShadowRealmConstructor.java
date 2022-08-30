package xyz.lebster.core.value.shadowrealm;

import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.Intrinsics;
import xyz.lebster.core.value.BuiltinConstructor;
import xyz.lebster.core.value.Names;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.error.type.TypeError;
import xyz.lebster.core.value.object.ObjectValue;

import static xyz.lebster.core.interpreter.AbruptCompletion.error;

public final class ShadowRealmConstructor extends BuiltinConstructor<ShadowRealm, ShadowRealmPrototype> {
	public ShadowRealmConstructor(Intrinsics intrinsics) {
		super(intrinsics, Names.ShadowRealm);
	}

	@Override
	public ShadowRealm construct(Interpreter interpreter, Value<?>[] arguments, ObjectValue newTarget) {
		return new ShadowRealm(interpreter.intrinsics);
	}

	@Override
	public Value<?> call(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		throw error(new TypeError(interpreter, "ShadowRealm constructor must be called with `new`"));
	}
}

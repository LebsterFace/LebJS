package xyz.lebster.core.value.shadowrealm;

import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.value.BuiltinConstructor;
import xyz.lebster.core.value.Names;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.error.TypeError;
import xyz.lebster.core.value.function.FunctionPrototype;
import xyz.lebster.core.value.object.ObjectPrototype;

public final class ShadowRealmConstructor extends BuiltinConstructor<ShadowRealm, ShadowRealmPrototype> {
	public ShadowRealmConstructor(ObjectPrototype objectPrototype, FunctionPrototype functionPrototype) {
		super(objectPrototype, functionPrototype, Names.ShadowRealm);
	}

	@Override
	public ShadowRealm construct(Interpreter interpreter, Value<?>[] arguments) {
		return new ShadowRealm(interpreter.intrinsics.shadowRealmPrototype);
	}

	@Override
	public Value<?> call(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		throw AbruptCompletion.error(new TypeError(interpreter, "ShadowRealm constructor must be called with `new`"));
	}
}

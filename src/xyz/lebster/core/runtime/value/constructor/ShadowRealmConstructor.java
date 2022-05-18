package xyz.lebster.core.runtime.value.constructor;

import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.runtime.Names;
import xyz.lebster.core.runtime.value.Value;
import xyz.lebster.core.runtime.value.error.TypeError;
import xyz.lebster.core.runtime.value.object.ShadowRealm;
import xyz.lebster.core.runtime.value.prototype.FunctionPrototype;
import xyz.lebster.core.runtime.value.prototype.ObjectPrototype;
import xyz.lebster.core.runtime.value.prototype.ShadowRealmPrototype;

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

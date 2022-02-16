package xyz.lebster.core.runtime.value.constructor;

import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.runtime.Names;
import xyz.lebster.core.runtime.value.Value;
import xyz.lebster.core.runtime.value.error.TypeError;
import xyz.lebster.core.runtime.value.object.ShadowRealm;
import xyz.lebster.core.runtime.value.prototype.ShadowRealmPrototype;

public final class ShadowRealmConstructor extends BuiltinConstructor<ShadowRealm> {
	public static final ShadowRealmConstructor instance = new ShadowRealmConstructor();

	static {
		instance.putNonWritable(Names.prototype, ShadowRealmPrototype.instance);
	}

	private ShadowRealmConstructor() {
		super();
	}

	@Override
	public ShadowRealm construct(Interpreter interpreter, Value<?>[] arguments) {
		return new ShadowRealm();
	}

	@Override
	public Value<?> call(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		throw AbruptCompletion.error(new TypeError("ShadowRealm constructor must be called with `new`"));
	}

	@Override
	protected String getName() {
		return "ShadowRealm";
	}
}

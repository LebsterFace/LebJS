package xyz.lebster.core.runtime.value.prototype;

import xyz.lebster.core.runtime.value.constructor.ShadowRealmConstructor;
import xyz.lebster.core.runtime.value.object.ObjectValue;

public final class ShadowRealmPrototype extends ObjectValue {
	public static final ShadowRealmPrototype instance = new ShadowRealmPrototype();

	static {
		instance.put("constructor", ShadowRealmConstructor.instance);
	}

	private ShadowRealmPrototype() {
	}
}
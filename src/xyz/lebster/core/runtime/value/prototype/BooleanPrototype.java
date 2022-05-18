package xyz.lebster.core.runtime.value.prototype;

import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.runtime.value.constructor.BooleanConstructor;
import xyz.lebster.core.runtime.value.object.BooleanWrapper;

@SpecificationURL("https://tc39.es/ecma262/multipage#sec-properties-of-the-boolean-prototype-object")
public final class BooleanPrototype extends BuiltinPrototype<BooleanWrapper, BooleanConstructor> {
	public BooleanPrototype(ObjectPrototype objectPrototype) {
		super(objectPrototype);
	}
}
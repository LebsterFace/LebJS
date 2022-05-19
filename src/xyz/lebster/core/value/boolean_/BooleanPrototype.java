package xyz.lebster.core.value.boolean_;

import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.value.BuiltinPrototype;
import xyz.lebster.core.value.object.ObjectPrototype;

@SpecificationURL("https://tc39.es/ecma262/multipage#sec-properties-of-the-boolean-prototype-object")
public final class BooleanPrototype extends BuiltinPrototype<BooleanWrapper, BooleanConstructor> {
	public BooleanPrototype(ObjectPrototype objectPrototype) {
		super(objectPrototype);
	}
}
package xyz.lebster.core.value.regexp;

import xyz.lebster.core.value.BuiltinPrototype;
import xyz.lebster.core.value.boolean_.BooleanConstructor;
import xyz.lebster.core.value.boolean_.BooleanWrapper;
import xyz.lebster.core.value.object.ObjectPrototype;

public class RegExpPrototype extends BuiltinPrototype<RegExpObject, RegExpConstructor> {
	public RegExpPrototype(ObjectPrototype objectPrototype) {
		super(objectPrototype);
	}
}
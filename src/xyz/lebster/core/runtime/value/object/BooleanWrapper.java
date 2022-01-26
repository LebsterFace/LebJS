package xyz.lebster.core.runtime.value.object;

import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.runtime.Names;
import xyz.lebster.core.runtime.value.HasBuiltinTag;
import xyz.lebster.core.runtime.value.primitive.NumberValue;
import xyz.lebster.core.runtime.value.primitive.StringValue;
import xyz.lebster.core.runtime.value.prototype.BooleanPrototype;

@SpecificationURL("https://tc39.es/ecma262/multipage#sec-string-objects")
public final class BooleanWrapper extends PrimitiveWrapper<StringValue> implements HasBuiltinTag {
	public BooleanWrapper(StringValue s) {
		super(s);
		this.put(Names.length, new NumberValue(s.value.length()));
	}

	@Override
	public BooleanPrototype getDefaultPrototype() {
		return BooleanPrototype.instance;
	}

	@Override
	public String getBuiltinTag() {
		return "Boolean";
	}
}
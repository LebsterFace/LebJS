package xyz.lebster.core.value.function;

import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.Intrinsics;
import xyz.lebster.core.value.Names;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.object.ObjectValue;
import xyz.lebster.core.value.primitive.string.StringValue;

public abstract class Constructor extends Executable {
	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-makeconstructor")
	public Constructor(Intrinsics intrinsics, StringValue name) {
		super(intrinsics, name);
		ObjectValue prototypeProperty = new ObjectValue(intrinsics);
		prototypeProperty.put(Names.constructor, this);
		this.put(Names.prototype, prototypeProperty);
	}

	public abstract ObjectValue construct(Interpreter interpreter, Value<?>[] argumentsList, ObjectValue newTarget) throws AbruptCompletion;
}
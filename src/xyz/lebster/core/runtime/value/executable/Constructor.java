package xyz.lebster.core.runtime.value.executable;

import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.runtime.Names;
import xyz.lebster.core.runtime.value.Value;
import xyz.lebster.core.runtime.value.object.ObjectValue;
import xyz.lebster.core.runtime.value.primitive.StringValue;

public abstract class Constructor extends Executable {
	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-makeconstructor")
	public Constructor(StringValue name) {
		super(name);
		final ObjectValue prototype = new ObjectValue();
		prototype.put(Names.constructor, this);
		this.put(Names.prototype, prototype);
	}

	// FIXME: newTarget
	public abstract ObjectValue construct(Interpreter interpreter, Value<?>[] argumentsList) throws AbruptCompletion;
}
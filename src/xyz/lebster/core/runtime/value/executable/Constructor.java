package xyz.lebster.core.runtime.value.executable;

import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.runtime.Names;
import xyz.lebster.core.runtime.value.Value;
import xyz.lebster.core.runtime.value.object.ObjectValue;
import xyz.lebster.core.runtime.value.primitive.StringValue;
import xyz.lebster.core.runtime.value.prototype.FunctionPrototype;
import xyz.lebster.core.runtime.value.prototype.ObjectPrototype;

public abstract class Constructor extends Executable {
	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-makeconstructor")
	public Constructor(ObjectPrototype objectPrototype, FunctionPrototype functionPrototype, StringValue name) {
		super(functionPrototype, name);
		final ObjectValue prototypeProperty = new ObjectValue(objectPrototype);
		prototypeProperty.put(Names.constructor, this);
		this.put(Names.prototype, prototypeProperty);
	}

	// FIXME: newTarget
	public abstract ObjectValue construct(Interpreter interpreter, Value<?>[] argumentsList) throws AbruptCompletion;
}
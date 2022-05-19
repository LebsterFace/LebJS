package xyz.lebster.core.value.function;

import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.value.Names;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.object.ObjectPrototype;
import xyz.lebster.core.value.object.ObjectValue;
import xyz.lebster.core.value.string.StringValue;

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
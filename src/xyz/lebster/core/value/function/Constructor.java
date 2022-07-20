package xyz.lebster.core.value.function;

import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.value.Names;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.object.ObjectValue;
import xyz.lebster.core.value.string.StringValue;

public abstract class Constructor extends Executable {
	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-makeconstructor")
	public Constructor(ObjectValue prototype, FunctionPrototype functionPrototype, StringValue name) {
		super(functionPrototype, name);
		ObjectValue prototypeProperty = new ObjectValue(prototype);
		prototypeProperty.put(Names.constructor, this);
		this.put(Names.prototype, prototypeProperty);
	}

	public abstract ObjectValue construct(Interpreter interpreter, Value<?>[] argumentsList, ObjectValue newTarget) throws AbruptCompletion;
}
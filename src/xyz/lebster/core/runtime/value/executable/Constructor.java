package xyz.lebster.core.runtime.value.executable;

import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.runtime.Names;
import xyz.lebster.core.runtime.value.Value;
import xyz.lebster.core.runtime.value.object.ObjectValue;

public abstract class Constructor<V> extends Executable<V> {
	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-makeconstructor")
	public Constructor(V code) {
		super(code);
		final ObjectValue prototype = new ObjectValue();
		prototype.put(Names.constructor, this);
		this.put(Names.prototype, prototype);
	}

	public abstract ObjectValue construct(Interpreter interpreter, Value<?>[] executedArguments) throws AbruptCompletion;
}
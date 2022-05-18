package xyz.lebster.core.runtime.value.constructor;

import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.runtime.Names;
import xyz.lebster.core.runtime.value.Value;
import xyz.lebster.core.runtime.value.executable.Constructor;
import xyz.lebster.core.runtime.value.native_.NativeFunction;
import xyz.lebster.core.runtime.value.object.ObjectValue;
import xyz.lebster.core.runtime.value.primitive.StringValue;
import xyz.lebster.core.runtime.value.prototype.BuiltinPrototype;
import xyz.lebster.core.runtime.value.prototype.FunctionPrototype;
import xyz.lebster.core.runtime.value.prototype.ObjectPrototype;

public abstract class BuiltinConstructor<T extends ObjectValue, P extends BuiltinPrototype<T, ?>> extends Constructor {
	public BuiltinConstructor(ObjectPrototype objectPrototype, FunctionPrototype functionPrototype, StringValue name) {
		super(objectPrototype, functionPrototype, name);
	}

	@Override
	public final StringValue toStringMethod() {
		return NativeFunction.toStringForName(this.name.value);
	}


	public final void linkToPrototype(P prototype) {
		this.putFrozen(Names.prototype, prototype);
		prototype.put(Names.constructor, this);
	}

	@Override
	public abstract T construct(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion;
}

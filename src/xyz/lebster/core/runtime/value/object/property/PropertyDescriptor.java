package xyz.lebster.core.runtime.value.object.property;

import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.runtime.value.Value;
import xyz.lebster.core.runtime.value.object.ObjectValue;

public interface PropertyDescriptor {
	boolean isWritable();

	void setWritable(boolean b);

	boolean isEnumerable();

	void setEnumerable(boolean b);

	boolean isConfigurable();

	void setConfigurable(boolean b);

	Value<?> get(Interpreter interpreter, ObjectValue thisValue) throws AbruptCompletion;

	void set(Interpreter interpreter, ObjectValue thisValue, Value<?> newValue) throws AbruptCompletion;
}

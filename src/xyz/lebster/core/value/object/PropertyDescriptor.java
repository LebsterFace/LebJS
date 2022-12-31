package xyz.lebster.core.value.object;

import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.value.Displayable;
import xyz.lebster.core.value.Value;

public interface PropertyDescriptor extends Displayable {
	boolean isWritable();

	void setWritable(boolean b);

	boolean isEnumerable();

	void setEnumerable(boolean b);

	boolean isConfigurable();

	void setConfigurable(boolean b);

	Value<?> get(Interpreter interpreter, ObjectValue thisValue) throws AbruptCompletion;

	void set(Interpreter interpreter, ObjectValue thisValue, Value<?> newValue) throws AbruptCompletion;

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-frompropertydescriptor")
	ObjectValue fromPropertyDescriptor(Interpreter interpreter) throws AbruptCompletion;
}

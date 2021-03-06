package xyz.lebster.core.value.object;

import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.value.Value;

import java.util.HashSet;

public interface PropertyDescriptor {
	boolean isWritable();

	void setWritable(boolean b);

	boolean isEnumerable();

	void setEnumerable(boolean b);

	boolean isConfigurable();

	void setConfigurable(boolean b);

	Value<?> get(Interpreter interpreter, ObjectValue thisValue) throws AbruptCompletion;

	void set(Interpreter interpreter, ObjectValue thisValue, Value<?> newValue) throws AbruptCompletion;

	void display(StringRepresentation representation, ObjectValue parent, HashSet<ObjectValue> parents, boolean singleLine);
}

package xyz.lebster.core.value.object;

import xyz.lebster.core.ANSI;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.value.Names;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.function.NativeFunction;
import xyz.lebster.core.value.globals.Undefined;
import xyz.lebster.core.value.primitive.boolean_.BooleanValue;
import xyz.lebster.core.value.primitive.string.StringValue;

import java.util.HashSet;

import static xyz.lebster.core.value.function.NativeFunction.argument;

public abstract class NativeAccessorDescriptor implements PropertyDescriptor {
	private boolean enumerable;
	private boolean configurable;
	private final boolean hasGetter;
	private final boolean hasSetter;

	public NativeAccessorDescriptor(boolean enumerable, boolean configurable, boolean hasGetter, boolean hasSetter) {
		this.enumerable = enumerable;
		this.configurable = configurable;
		this.hasGetter = hasGetter;
		this.hasSetter = hasSetter;
	}

	@Override
	public Value<?> get(Interpreter interpreter, ObjectValue thisValue) throws AbruptCompletion {
		return Undefined.instance;
	}

	@Override
	public void set(Interpreter interpreter, ObjectValue thisValue, Value<?> newValue) throws AbruptCompletion {
	}

	private Value<?> getter(Interpreter interpreter) {
		if (!this.hasGetter) return Undefined.instance;
		// TODO: Name 'get xyz'
		// TODO: 'this' value
		return new NativeFunction(interpreter.intrinsics, StringValue.EMPTY, (i, arguments) ->
			get(i, null), 0);
	}

	private Value<?> setter(Interpreter interpreter) {
		if (!this.hasSetter) return Undefined.instance;
		// TODO: Name 'set xyz'
		// TODO: 'this' value
		return new NativeFunction(interpreter.intrinsics, StringValue.EMPTY, (i, arguments) -> {
			set(i, null, argument(0, arguments));
			return Undefined.instance;
		}, 1);
	}

	@Override
	public boolean isWritable() {
		return true;
	}

	@Override
	public void setWritable(boolean writable) {
	}

	@Override
	public boolean isEnumerable() {
		return enumerable;
	}

	@Override
	public void setEnumerable(boolean enumerable) {
		this.enumerable = enumerable;
	}

	@Override
	public boolean isConfigurable() {
		return configurable;
	}

	@Override
	public void setConfigurable(boolean configurable) {
		this.configurable = configurable;
	}

	@Override
	public final void display(StringRepresentation representation, ObjectValue parent, HashSet<ObjectValue> parents, boolean singleLine) {
		representation.append(ANSI.MAGENTA);
		representation.append("[Getter/Setter]");
		representation.append(ANSI.RESET);
	}

	@Override
	public final ObjectValue fromPropertyDescriptor(Interpreter interpreter) throws AbruptCompletion {
		final var obj = new ObjectValue(interpreter.intrinsics);
		// TODO: Make CreateDataPropertyOrThrow
		obj.set(interpreter, Names.get, getter(interpreter));
		obj.set(interpreter, Names.set, setter(interpreter));
		obj.set(interpreter, Names.writable, BooleanValue.of(isWritable()));
		obj.set(interpreter, Names.enumerable, BooleanValue.of(isEnumerable()));
		obj.set(interpreter, Names.configurable, BooleanValue.of(isConfigurable()));
		return obj;
	}
}
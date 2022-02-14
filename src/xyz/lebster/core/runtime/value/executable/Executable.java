package xyz.lebster.core.runtime.value.executable;

import xyz.lebster.core.ANSI;
import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.ExecutionContext;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.runtime.Names;
import xyz.lebster.core.runtime.value.HasBuiltinTag;
import xyz.lebster.core.runtime.value.Value;
import xyz.lebster.core.runtime.value.error.TypeError;
import xyz.lebster.core.runtime.value.native_.NativeFunction;
import xyz.lebster.core.runtime.value.object.ObjectValue;
import xyz.lebster.core.runtime.value.primitive.BooleanValue;
import xyz.lebster.core.runtime.value.primitive.StringValue;
import xyz.lebster.core.runtime.value.prototype.FunctionPrototype;

import java.util.HashSet;

public abstract class Executable<JType> extends ObjectValue implements HasBuiltinTag {
	public final JType code;

	public Executable(StringValue name, JType code) {
		super();
		this.code = code;
		this.put(Names.name, name);
	}

	public Executable(JType code) {
		super();
		this.code = code;
		this.put(Names.name, new StringValue(this.getName()));
	}

	public static Executable<?> getExecutable(Value<?> value) throws AbruptCompletion {
		if (value instanceof final Executable<?> executable) return executable;

		final var representation = new StringRepresentation();
		value.display(representation);
		representation.append(" is not a function");
		throw AbruptCompletion.error(new TypeError(representation.toString()));
	}

	@Override
	public final String getBuiltinTag() {
		return "Function";
	}

	@Override
	public void display(StringRepresentation representation) {
		representation.append(ANSI.MAGENTA);
		representation.append("ƒ ");
		representation.append(ANSI.BRIGHT_MAGENTA);
		representation.append(this.getName());
		representation.append("()");
		representation.append(ANSI.RESET);
	}

	protected abstract String getName();

	public StringValue toStringMethod() {
		return NativeFunction.toStringForName(this.getName());
	}

	@Override
	public void displayRecursive(StringRepresentation representation, HashSet<ObjectValue> parents, boolean singleLine) {
		this.display(representation);
	}

	public Value<?> call(Interpreter interpreter, Value<?> thisValue, Value<?>... args) throws AbruptCompletion {
		final ExecutionContext context = interpreter.pushThisValue(thisValue);
		try {
			return this.call(interpreter, args);
		} finally {
			interpreter.exitExecutionContext(context);
		}
	}

	public abstract Value<?> call(final Interpreter interpreter, final Value<?>[] arguments) throws AbruptCompletion;

	@Override
	public String typeOf(Interpreter interpreter) {
		return "function";
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-ordinaryhasinstance")
	public BooleanValue ordinaryHasInstance(Interpreter interpreter, Value<?> O) throws AbruptCompletion {
		// 1. If IsCallable(C) is false, return false.

		// FIXME: BoundTargetFunction
		// 2. If C has a [[BoundTargetFunction]] internal slot, then
		// a. Let BC be C.[[BoundTargetFunction]].
		// b. Return ? InstanceofOperator(O, BC).

		// 3. If Type(O) is not Object, return false.
		if (!(O instanceof ObjectValue object)) return BooleanValue.FALSE;

		// 4. Let P be ? Get(C, "prototype").
		final Value<?> P = this.get(interpreter, Names.prototype);
		// 5. If Type(P) is not Object, throw a TypeError exception.
		if (P.type != Type.Object) throw AbruptCompletion.error(new TypeError("Not an object!"));

		// 6. Repeat,
		while (true) {
			// a. Set O to ? O.[[GetPrototypeOf]]().
			object = object.getPrototype();
			// b. If O is null, return false.
			if (object == null) return BooleanValue.FALSE;
			// c. If SameValue(P, O) is true, return true.
			if (P.sameValue(object)) return BooleanValue.TRUE;
		}
	}

	@Override
	public ObjectValue getDefaultPrototype() {
		return FunctionPrototype.instance;
	}
}
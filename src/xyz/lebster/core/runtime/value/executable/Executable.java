package xyz.lebster.core.runtime.value.executable;

import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.ExecutionContext;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.runtime.Names;
import xyz.lebster.core.runtime.value.Value;
import xyz.lebster.core.runtime.value.error.TypeError;
import xyz.lebster.core.runtime.value.object.ObjectValue;
import xyz.lebster.core.runtime.value.primitive.BooleanValue;
import xyz.lebster.core.runtime.value.primitive.NumberValue;
import xyz.lebster.core.runtime.value.primitive.StringValue;
import xyz.lebster.core.runtime.value.prototype.FunctionPrototype;

public abstract class Executable<JType> extends ObjectValue {
	public final JType code;

	public Executable(JType code) {
		super();
		this.code = code;
		this.put(Names.length, new NumberValue(0));
		this.put("name", new StringValue(""));
	}

	public Value<?> callWithContext(Interpreter interpreter, ExecutionContext frame, Value<?>... args) throws AbruptCompletion {
		interpreter.enterExecutionContext(frame);
		try {
			return this.internalCall(interpreter, args);
		} finally {
			interpreter.exitExecutionContext(frame);
		}
	}

	public Value<?> call(Interpreter interpreter, Value<?> thisValue, Value<?>... args) throws AbruptCompletion {
		final ExecutionContext context = new ExecutionContext(interpreter.lexicalEnvironment(), this, thisValue);
		interpreter.enterExecutionContext(context);
		try {
			return this.internalCall(interpreter, args);
		} finally {
			interpreter.exitExecutionContext(context);
		}
	}

	protected abstract Value<?> internalCall(final Interpreter interpreter, final Value<?>... arguments) throws AbruptCompletion;

	@Override
	public String typeOf(Interpreter interpreter) {
		return "function";
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-ordinaryhasinstance")
	public BooleanValue ordinaryHasInstance(Value<?> O) throws AbruptCompletion {
		// 1. If IsCallable(C) is false, return false.

		// FIXME: BoundTargetFunction
		// 2. If C has a [[BoundTargetFunction]] internal slot, then
		// a. Let BC be C.[[BoundTargetFunction]].
		// b. Return ? InstanceofOperator(O, BC).

		// 3. If Type(O) is not Object, return false.
		if (!(O instanceof ObjectValue object))
			return BooleanValue.FALSE;

		// 4. Let P be ? Get(C, "prototype").
		final Value<?> P = this.get(new StringValue("prototype"));
		// 5. If Type(P) is not Object, throw a TypeError exception.
		if (P.type != Type.Object)
			throw AbruptCompletion.error(new TypeError("Not an object!"));

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
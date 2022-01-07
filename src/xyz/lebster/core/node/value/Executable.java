package xyz.lebster.core.node.value;

import xyz.lebster.core.Dumper;
import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.ExecutionContext;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.runtime.TypeError;
import xyz.lebster.core.runtime.prototype.FunctionPrototype;

import java.util.HashSet;

public abstract class Executable<JType> extends Dictionary {
	// public static Identifier name = new Identifier("name");
	public final JType code;

	public Executable(JType code) {
		super();
		this.code = code;
		this.put("length", new NumericLiteral(0));
		this.put("name", new StringLiteral(""));
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
	public void dump(int indent) {
		Dumper.dumpValue(indent, getClass().getSimpleName(), toString());
	}

	@Override
	protected void dumpRecursive(int indent, HashSet<Dictionary> parents) {
		dump(indent);
	}

	@Override
	public String typeOf(Interpreter interpreter) {
		return "function";
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-ordinaryhasinstance")
	public BooleanLiteral ordinaryHasInstance(Value<?> O) throws AbruptCompletion {
		final var C = this;
		// 1. If IsCallable(C) is false, return false.

		// FIXME: BoundTargetFunction
		// 2. If C has a [[BoundTargetFunction]] internal slot, then
			// a. Let BC be C.[[BoundTargetFunction]].
			// b. Return ? InstanceofOperator(O, BC).

		// 3. If Type(O) is not Object, return false.
		if (!(O instanceof Dictionary dictionary))
			return BooleanLiteral.FALSE;

		// 4. Let P be ? Get(C, "prototype").
		final Value<?> P = C.get(new StringLiteral("prototype"));
		// 5. If Type(P) is not Object, throw a TypeError exception.
		if (P.type != Type.Dictionary)
			throw AbruptCompletion.error(new TypeError("Not an object!"));

		// 6. Repeat,
		while (true) {
			// a. Set O to ? O.[[GetPrototypeOf]]().
			dictionary = dictionary.getPrototype();
			// b. If O is null, return false.
			if (dictionary == null) return BooleanLiteral.FALSE;
			// c. If SameValue(P, O) is true, return true.
			// FIXME: SameValue
			if (P.equals(O)) return BooleanLiteral.TRUE;
		}
	}

	@Override
	public Dictionary getPrototype() {
		return FunctionPrototype.instance;
	}
}
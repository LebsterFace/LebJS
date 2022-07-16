package xyz.lebster.core.value.function;

import xyz.lebster.core.ANSI;
import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.environment.ExecutionContext;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.node.FunctionNode;
import xyz.lebster.core.node.expression.ArrowFunctionExpression;
import xyz.lebster.core.node.expression.ClassExpression;
import xyz.lebster.core.node.expression.Expression;
import xyz.lebster.core.value.HasBuiltinTag;
import xyz.lebster.core.value.Names;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.boolean_.BooleanValue;
import xyz.lebster.core.value.error.TypeError;
import xyz.lebster.core.value.object.ObjectValue;
import xyz.lebster.core.value.string.StringValue;

import java.util.HashSet;

public abstract class Executable extends ObjectValue implements HasBuiltinTag {
	public StringValue name;

	public Executable(FunctionPrototype functionPrototype, StringValue name) {
		super(functionPrototype);
		this.name = name;
		this.put(Names.name, name);
	}

	public static Executable getExecutable(Interpreter interpreter, Value<?> value) throws AbruptCompletion {
		if (value instanceof final Executable executable) return executable;

		final String message = ANSI.stripFormatting(value.toDisplayString()) + " is not a function";
		throw AbruptCompletion.error(new TypeError(interpreter, message));
	}

	public static boolean isAnonymousFunctionExpression(Expression expression) {
		if (expression instanceof ArrowFunctionExpression) return true;
		if (expression instanceof final ClassExpression classExpression) return classExpression.className() == null;
		if (expression instanceof final FunctionNode functionNode) return functionNode.name() == null || functionNode.name().isEmpty();
		return false;
	}

	public final void updateName(StringValue newName) {
		this.name = newName;
	}

	@Override
	public final String getBuiltinTag() {
		return "Function";
	}

	@Override
	public final void display(StringRepresentation representation) {
		representation.append(ANSI.MAGENTA);
		representation.append("[Function: ");
		representation.append(ANSI.BRIGHT_MAGENTA);
		representation.append(this.name.value.isEmpty() ? "(anonymous)" : this.name.value);
		representation.append(ANSI.MAGENTA);
		representation.append(']');
		representation.append(ANSI.RESET);
	}

	@Override
	public final void displayRecursive(StringRepresentation representation, HashSet<ObjectValue> parents, boolean singleLine) {
		this.display(representation);
	}

	public abstract StringValue toStringMethod();

	public Value<?> call(Interpreter interpreter, Value<?> thisValue, Value<?>... args) throws AbruptCompletion {
		final ExecutionContext context = interpreter.pushFunctionEnvironment(thisValue, this, this);
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
		if (!(P instanceof ObjectValue)) throw AbruptCompletion.error(new TypeError(interpreter, "Not an object!"));

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
}
package xyz.lebster.core.value.function;

import xyz.lebster.core.ANSI;
import xyz.lebster.core.NonCompliant;
import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.Intrinsics;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.interpreter.environment.Environment;
import xyz.lebster.core.interpreter.environment.ExecutionContext;
import xyz.lebster.core.interpreter.environment.FunctionEnvironment;
import xyz.lebster.core.node.FunctionNode;
import xyz.lebster.core.node.expression.ArrowFunctionExpression;
import xyz.lebster.core.node.expression.ClassExpression;
import xyz.lebster.core.node.expression.Expression;
import xyz.lebster.core.node.expression.ParenthesizedExpression;
import xyz.lebster.core.value.HasBuiltinTag;
import xyz.lebster.core.value.Names;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.error.type.TypeError;
import xyz.lebster.core.value.object.Key;
import xyz.lebster.core.value.object.ObjectValue;
import xyz.lebster.core.value.primitive.boolean_.BooleanValue;
import xyz.lebster.core.value.primitive.number.NumberValue;
import xyz.lebster.core.value.primitive.string.StringValue;

import static xyz.lebster.core.interpreter.AbruptCompletion.error;

@SpecificationURL("https://tc39.es/ecma262/multipage#sec-function-instances")
public abstract class Executable extends ObjectValue implements HasBuiltinTag {
	public StringValue name;

	Executable(FunctionPrototype functionPrototype, StringValue name, int expectedArgumentCount) {
		super(functionPrototype);
		this.name = name;
		this.put(Names.name, name, false, false, true);
		this.put(Names.length, new NumberValue(expectedArgumentCount), false, false, true);
	}

	public Executable(Intrinsics intrinsics, StringValue name, int expectedArgumentCount) {
		this(intrinsics.functionPrototype, name, expectedArgumentCount);
	}

	public static Executable getExecutable(Interpreter interpreter, Value<?> value) throws AbruptCompletion {
		if (value instanceof final Executable executable) return executable;
		throw notCallable(interpreter, value);
	}

	public static AbruptCompletion notCallable(Interpreter interpreter, Value<?> value) {
		final String message = ANSI.stripFormatting(value.toDisplayString()) + " is not a function";
		return error(new TypeError(interpreter, message));
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-isanonymousfunctiondefinition")
	public static boolean isAnonymousFunctionDefinition(Expression expression) {
		if (expression instanceof final ParenthesizedExpression parenthesizedExpression)
			return isAnonymousFunctionDefinition(parenthesizedExpression.expression());
		if (expression instanceof ArrowFunctionExpression) return true;
		if (expression instanceof final ClassExpression classExpression) return classExpression.className() == null;
		if (expression instanceof final FunctionNode functionNode) return functionNode.name() == null;
		return false;
	}

	@NonCompliant
	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-runtime-semantics-namedevaluation")
	public static Value<?> namedEvaluation(Interpreter interpreter, Expression expression, Key<?> name) throws AbruptCompletion {
		final Value<?> executedValue = expression.execute(interpreter);

		if (Executable.isAnonymousFunctionDefinition(expression) && executedValue instanceof final Executable function) {
			// TODO: Do this when creating the function, not after.
			function.getOwnProperty(Names.name).set(interpreter, function, name);
			function.updateName(name.toFunctionName());
		}

		return executedValue;
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-ordinaryhasinstance")
	public static BooleanValue ordinaryHasInstance(Interpreter interpreter, Value<?> C_, Value<?> O) throws AbruptCompletion {
		// 1. If IsCallable(C) is false, return false.
		if (!(C_ instanceof final Executable C)) return BooleanValue.FALSE;

		// FIXME: BoundTargetFunction
		// 2. If C has a [[BoundTargetFunction]] internal slot, then
		// a. Let BC be C.[[BoundTargetFunction]].
		// b. Return ? InstanceofOperator(O, BC).

		// 3. If Type(O) is not Object, return false.
		if (!(O instanceof ObjectValue object)) return BooleanValue.FALSE;

		// 4. Let P be ? Get(C, "prototype").
		final Value<?> P = C.get(interpreter, Names.prototype);
		// 5. If Type(P) is not Object, throw a TypeError exception.
		if (!(P instanceof ObjectValue)) throw error(new TypeError(interpreter, "Not an object!"));

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
	public boolean displayAsJSON() {
		return false;
	}

	public abstract StringValue toStringMethod();

	// Closures: Functions have access to a saved Environment
	public Environment savedEnvironment(Interpreter interpreter) {
		// By default, that environment is just the environment the function is called in
		// (this is for eval() - so that it can access variables outside its sourceText)
		return interpreter.environment();
	}

	public final Value<?> call(Interpreter interpreter, Value<?> thisValue, Value<?>... arguments) throws AbruptCompletion {
		final var env = new FunctionEnvironment(savedEnvironment(interpreter), thisValue, this, this);
		final ExecutionContext context = interpreter.pushContextWithEnvironment(env);
		try {
			return internalCall(interpreter, arguments);
		} finally {
			interpreter.exitExecutionContext(context);
		}
	}

	public abstract Value<?> internalCall(Interpreter interpreter, Value<?>... arguments) throws AbruptCompletion;

	@Override
	public String typeOf(Interpreter interpreter) {
		return "function";
	}
}
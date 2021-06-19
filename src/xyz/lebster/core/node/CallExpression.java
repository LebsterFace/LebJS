package xyz.lebster.core.node;

import xyz.lebster.exception.TypeError;
import xyz.lebster.exception.LanguageError;
import xyz.lebster.core.runtime.CallFrame;
import xyz.lebster.core.runtime.Interpreter;
import xyz.lebster.core.value.*;

public class CallExpression implements Expression {
	public final Expression callee;
	public final Expression[] arguments;

	public CallExpression(Expression callee, Expression... args) {
		this.callee = callee;
		this.arguments = args;
	}

	public CallExpression(String callee, Expression... args) {
		this(new Identifier(callee), args);
	}

	@Override
	public void dump(int indent) {
		Interpreter.dumpIndent(indent);
		System.out.println("CallExpression:");
		Interpreter.dumpIndent(indent + 1);
		System.out.println("Callee:");
		callee.dump(indent + 2);
		Interpreter.dumpIndent(indent + 1);
		System.out.println(arguments.length > 0 ? "Arguments:" : "[[NO ARGS]]");
		for (Expression argument : arguments) {
			argument.dump(indent + 2);
		}
	}

	private Value<?>[] executeArguments(Interpreter interpreter) throws LanguageError {
		final Value<?>[] result = new Value<?>[arguments.length];
		for (int i = 0; i < arguments.length; i++) result[i] = arguments[i].execute(interpreter);
		return result;
	}

	@Override
	public Value<?> execute(Interpreter interpreter) throws LanguageError {
		final Value<?> value = callee.execute(interpreter);

		if (value.type == Type.Function) {
			final Function func = (Function) value;
			return func.executeChildren(interpreter, this.executeArguments(interpreter));
		} else if (value.type == Type.NativeFunction) {
			final CallFrame frame = interpreter.enterCallFrame(getCorrectThis(interpreter));

			final NativeCode func = (NativeCode) value.value;
			final Value<?> result = func.execute(interpreter, this.executeArguments(interpreter));

			interpreter.exitCallFrame(frame);
			return result;
		} else {
			throw new TypeError("'LittleLang::" + value.getClass().getSimpleName() + "' is not a function");
		}
	}

	private Value<?> getCorrectThis(Interpreter interpreter) throws LanguageError {
		if (callee instanceof MemberExpression) {
			return ((MemberExpression) callee).object().execute(interpreter);
		} else {
			return new Undefined();
		}
	}
}

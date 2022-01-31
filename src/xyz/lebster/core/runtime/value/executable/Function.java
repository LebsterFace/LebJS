package xyz.lebster.core.runtime.value.executable;

import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.ExecutionContext;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.LexicalEnvironment;
import xyz.lebster.core.node.declaration.FunctionNode;
import xyz.lebster.core.runtime.Names;
import xyz.lebster.core.runtime.value.Value;
import xyz.lebster.core.runtime.value.object.ObjectValue;
import xyz.lebster.core.runtime.value.primitive.StringValue;
import xyz.lebster.core.runtime.value.primitive.UndefinedValue;
import xyz.lebster.core.runtime.value.prototype.ObjectPrototype;

public final class Function extends Constructor<FunctionNode> {
	public final LexicalEnvironment environment;

	public Function(FunctionNode code, LexicalEnvironment environment) {
		super(code);
		this.environment = environment;
		this.putMethod(Names.toString, ($, $$) -> new StringValue(code.toRepresentationString()));
	}

	@Override
	protected String getName() {
		return code.name;
	}

	private Value<?> executeCode(ExecutionContext context, Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// Declare passed arguments as variables
		for (int i = 0; i < arguments.length && i < code.arguments.length; i++) {
			interpreter.declareVariable(code.arguments[i], arguments[i]);
		}

		try {
			code.body.execute(interpreter);
			return UndefinedValue.instance;
		} catch (AbruptCompletion e) {
			if (e.type != AbruptCompletion.Type.Return) throw e;
			return e.value;
		} finally {
			interpreter.exitExecutionContext(context);
		}
	}

	@Override
	public Value<?> call(Interpreter interpreter, Value<?>... arguments) throws AbruptCompletion {
		// Closures: The LexicalEnvironment of this.code; The surrounding `this` value
		final ExecutionContext context = interpreter.pushLexicalEnvironment(environment);
		return this.executeCode(context, interpreter, arguments);
	}

	@Override
	public Value<?> call(Interpreter interpreter, Value<?> newThisValue, Value<?>... arguments) throws AbruptCompletion {
		// Calling when `this` is bound: The LexicalEnvironment of this.code; The bound `this` value
		final ExecutionContext context = interpreter.pushEnvironmentAndThisValue(environment, newThisValue);
		return this.executeCode(context, interpreter, arguments);
	}

	@Override
	public ObjectValue construct(Interpreter interpreter, Value<?>[] args) throws AbruptCompletion {
		final Value<?> prototypeProperty = this.get(interpreter, Names.prototype);

		final ObjectValue prototype = prototypeProperty instanceof ObjectValue object ? object : ObjectPrototype.instance;
		final ObjectValue newInstance = new ObjectValue();
		newInstance.setPrototype(prototype);

		final Value<?> returnValue = this.call(interpreter, newInstance, args);

		if (returnValue instanceof final ObjectValue asObject) {
			// TODO: Improve this as it is a little hackish
			newInstance.value.putAll(asObject.value);
		}

		return newInstance;
	}
}
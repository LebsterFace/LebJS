package xyz.lebster.core.runtime.value.executable;

import xyz.lebster.core.interpreter.*;
import xyz.lebster.core.node.declaration.FunctionNode;
import xyz.lebster.core.runtime.Names;
import xyz.lebster.core.runtime.value.Value;
import xyz.lebster.core.runtime.value.object.ObjectValue;
import xyz.lebster.core.runtime.value.primitive.StringValue;
import xyz.lebster.core.runtime.value.primitive.Undefined;
import xyz.lebster.core.runtime.value.prototype.ObjectPrototype;

import java.util.HashSet;

public final class Function extends Constructor<FunctionNode> {
	public final LexicalEnvironment environment;

	public Function(FunctionNode code, LexicalEnvironment environment) {
		super(code);
		this.environment = environment;
	}

	@Override
	protected String getName() {
		return code.name;
	}

	@Override
	public void displayRecursive(StringRepresentation representation, HashSet<ObjectValue> parents, boolean singleLine) {
		this.display(representation);
	}

	@Override
	public StringValue toStringMethod() {
		return new StringValue(code.toRepresentationString());
	}

	private Value<?> executeCode(ExecutionContext context, Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// Declare passed arguments as variables
		for (int i = 0; i < arguments.length && i < code.arguments.length; i++) {
			interpreter.declareVariable(code.arguments[i], arguments[i]);
		}

		try {
			code.body.execute(interpreter);
			return Undefined.instance;
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
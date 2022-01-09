package xyz.lebster.core.node.value.object;

import xyz.lebster.core.ANSI;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.ExecutionContext;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.node.declaration.FunctionNode;
import xyz.lebster.core.node.value.native_.NativeFunction;
import xyz.lebster.core.node.value.StringValue;
import xyz.lebster.core.node.value.UndefinedValue;
import xyz.lebster.core.node.value.Value;
import xyz.lebster.core.runtime.prototype.ObjectPrototype;

import java.util.HashSet;

public final class Function extends Constructor<FunctionNode> {
	public final ExecutionContext context;

	public Function(FunctionNode code, ExecutionContext context) {
		super(code);
		this.context = context;
		final ObjectValue prototype = new ObjectValue();
		prototype.put("constructor", this);
		this.put("prototype", prototype);
		this.put(ObjectPrototype.toString, new NativeFunction(new StringValue(code.toRepresentationString())));
	}

	@Override
	public void display(StringBuilder builder) {
		builder.append(ANSI.BRIGHT_MAGENTA);
		builder.append("[Function: ");
		builder.append(code.name);
		builder.append(']');
		builder.append(ANSI.RESET);
	}

	@Override
	public void displayRecursive(StringBuilder representation, HashSet<ObjectValue> parents) {
		this.display(representation);
	}

	@Override
	protected Value<?> internalCall(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		interpreter.enterExecutionContext(context);

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

	public Function boundTo(Value<?> value) {
		return new Function(code, this.context.boundTo(value));
	}

	@Override
	public ObjectValue construct(Interpreter interpreter, Value<?>[] args) throws AbruptCompletion {
		final Value<?> prototypeProperty = this.get(new StringValue("prototype"));

		final ObjectValue prototype = prototypeProperty instanceof ObjectValue object ? object : ObjectPrototype.instance;
		final ObjectValue newInstance = new ObjectValue();
		newInstance.setPrototype(prototype);

		final Function boundSelf = this.boundTo(newInstance);
		final Value<?> returnValue = boundSelf.internalCall(interpreter, args);

		if (returnValue instanceof final ObjectValue object) {
			// TODO: Improve this as it is a little hackish
			for (var entry : object.value.entrySet()) {
				newInstance.put(entry.getKey(), entry.getValue());
			}
		}

		return newInstance;
	}
}
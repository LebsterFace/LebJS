package xyz.lebster.core.runtime.value.executable;

import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.ExecutionContext;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.node.declaration.FunctionNode;
import xyz.lebster.core.runtime.Names;
import xyz.lebster.core.runtime.value.Value;
import xyz.lebster.core.runtime.value.object.ObjectValue;
import xyz.lebster.core.runtime.value.primitive.StringValue;
import xyz.lebster.core.runtime.value.primitive.UndefinedValue;
import xyz.lebster.core.runtime.value.prototype.ObjectPrototype;

public final class Function extends Constructor<FunctionNode> {
	public final ExecutionContext context;

	public Function(FunctionNode code, ExecutionContext context) {
		super(code);
		this.context = context;
		final ObjectValue prototype = new ObjectValue();
		prototype.put(Names.constructor, this);
		this.put(Names.prototype, prototype);
		this.putMethod(Names.toString, ($, $$) -> new StringValue(code.toRepresentationString()));
	}

	@Override
	protected String getName() {
		return code.name;
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
		final Value<?> prototypeProperty = this.get(interpreter, Names.prototype);

		final ObjectValue prototype = prototypeProperty instanceof ObjectValue object ? object : ObjectPrototype.instance;
		final ObjectValue newInstance = new ObjectValue();
		newInstance.setPrototype(prototype);

		final Function boundSelf = this.boundTo(newInstance);
		final Value<?> returnValue = boundSelf.internalCall(interpreter, args);

		if (returnValue instanceof final ObjectValue asObject) {
			// TODO: Improve this as it is a little hackish
			newInstance.value.putAll(asObject.value);
		}

		return newInstance;
	}
}
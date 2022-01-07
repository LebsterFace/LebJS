package xyz.lebster.core.node.value;

import xyz.lebster.core.ANSI;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.ExecutionContext;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.node.declaration.FunctionNode;
import xyz.lebster.core.runtime.prototype.ObjectPrototype;

import java.util.HashSet;

public final class Function extends Constructor<FunctionNode> {
	public final ExecutionContext context;

	public Function(FunctionNode code, ExecutionContext context) {
		super(code);
		this.context = context;
		final ObjectLiteral prototype = new ObjectLiteral();
		prototype.put("constructor", this);
		this.put("prototype", prototype);
		this.put(ObjectPrototype.toString, new NativeFunction(new StringLiteral(code.toRepresentationString())));
	}

	@Override
	public void represent(StringRepresentation representation) {
		representation.append(ANSI.BRIGHT_MAGENTA);
		representation.append("[Function: ");
		representation.append(code.name);
		representation.append(']');
		representation.append(ANSI.RESET);
	}

	@Override
	public void representRecursive(StringRepresentation representation, HashSet<ObjectLiteral> parents) {
		this.represent(representation);
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
			return Undefined.instance;
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
	public Instance construct(Interpreter interpreter, Value<?>[] args) throws AbruptCompletion {
		final Value<?> prototypeProperty = this.get(new StringLiteral("prototype"));

		final Instance newInstance = new Instance(prototypeProperty instanceof ObjectLiteral object ? object : ObjectPrototype.instance);
		final Function boundSelf = this.boundTo(newInstance);
		final Value<?> returnValue = boundSelf.internalCall(interpreter, args);

		if (returnValue instanceof final ObjectLiteral object) {
			// TODO: Improve this as it is a little hackish
			for (var entry : object.value.entrySet()) {
				newInstance.put(entry.getKey(), entry.getValue());
			}
		}

		return newInstance;
	}
}
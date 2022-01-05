package xyz.lebster.core.node.value;

import xyz.lebster.core.ANSI;
import xyz.lebster.core.exception.NotImplemented;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.ExecutionContext;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.node.declaration.FunctionNode;
import xyz.lebster.core.runtime.prototype.FunctionPrototype;
import xyz.lebster.core.runtime.prototype.ObjectPrototype;

import java.util.HashSet;

public final class Function extends Constructor<FunctionNode> {
	public final ExecutionContext context;

	public Function(FunctionNode code, ExecutionContext context) {
		super(code);
		this.context = context;
		this.put("prototype", FunctionPrototype.instance);
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
	public void representRecursive(StringRepresentation representation, HashSet<Dictionary> parents) {
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
			return code.body.execute(interpreter);
		} finally {
			interpreter.exitExecutionContext(context);
		}
	}

	public StringLiteral toStringLiteral() {
		throw new NotImplemented("Function -> StringLiteral");
	}

	public BooleanLiteral toBooleanLiteral() {
		return BooleanLiteral.TRUE;
	}

	public NumericLiteral toNumericLiteral() {
		return new NumericLiteral(Double.NaN);
	}

	public Dictionary toDictionary() {
		return this;
	}

	public Function boundTo(Value<?> value) {
		return new Function(code, this.context.boundTo(value));
	}

	@Override
	public Dictionary construct(Interpreter i, Value<?>[] args) throws AbruptCompletion {
		final Dictionary newInstance = new Dictionary();
		newInstance.put("constructor", this);

		final Function boundSelf = this.boundTo(newInstance);
		final Value<?> returnValue = boundSelf.internalCall(i, args);

		if (returnValue instanceof final Dictionary dictionary) {
			return dictionary;
		} else {
			return newInstance;
		}
	}

	@Override
	public FunctionPrototype getPrototype() {
		return FunctionPrototype.instance;
	}
}
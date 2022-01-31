package xyz.lebster.core.node.expression;

import xyz.lebster.core.Dumper;
import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.interpreter.*;
import xyz.lebster.core.runtime.value.Value;
import xyz.lebster.core.runtime.value.primitive.StringValue;

public record IdentifierExpression(String value) implements LeftHandSideExpression {
	@Override
	public Value<?> execute(Interpreter interpreter) throws AbruptCompletion {
		return this.toReference(interpreter).getValue(interpreter);
	}

	@Override
	public void dump(int indent) {
		Dumper.dumpValue(indent, "VariableLookup", value);
	}

	@Override
	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-getidentifierreference")
	public Reference toReference(Interpreter interpreter) {
		final StringValue name = new StringValue(this.value);

		LexicalEnvironment env = interpreter.lexicalEnvironment();
		while (env != null) {
			if (env.hasBinding(name)) {
				return new Reference(env.variables(), name);
			}

			env = env.parent();
		}

		// Unresolvable reference
		return new Reference(null, name);
	}

	@Override
	public String toString() {
		return value;
	}

	@Override
	public void represent(StringRepresentation representation) {
		representation.append(value);
	}
}
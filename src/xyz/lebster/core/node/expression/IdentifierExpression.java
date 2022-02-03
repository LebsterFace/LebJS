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
		Dumper.dumpValue(indent, "IdentifierExpression", value);
	}

	@Override
	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-getidentifierreference")
	public Reference toReference(Interpreter interpreter) {
		final StringValue name = new StringValue(this.value);

		LexicalEnvironment env = interpreter.lexicalEnvironment();
		while (env != null) {
			// 2. Let exists be ? env.HasBinding(name).
			if (env.hasBinding(name)) {
				// 3. If exists is true, then
				// a. Return the Reference Record { base: env, referencedName: name }.
				return new Reference(env.variables(), name);
			}

			// 4. Else,
			// a. Let outer be env.[[OuterEnv]].
			env = env.parent();
			// (Recursive call)
		}

		// 1. If env is the value null, then
		// a. Return the Reference Record { base: unresolvable, referencedName: name }.
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
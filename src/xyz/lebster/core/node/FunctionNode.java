package xyz.lebster.core.node;

import xyz.lebster.core.DumpBuilder;
import xyz.lebster.core.exception.ShouldNotHappen;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.interpreter.environment.ExecutionContext;
import xyz.lebster.core.node.expression.Expression;
import xyz.lebster.core.node.expression.literal.StringLiteral;
import xyz.lebster.core.node.statement.BlockStatement;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.globals.Undefined;

public interface FunctionNode extends ASTNode {
	Expression name();

	default boolean computedName() {
		return false;
	}

	FunctionParameters parameters();

	BlockStatement body();

	default void representCall(StringRepresentation representation) {
		final Expression name = name();
		if (computedName()) {
			representation.append('[');
			name.represent(representation);
			representation.append("]");
		} else if (name instanceof final StringLiteral stringLiteral) {
			stringLiteral.value().displayForObjectKey(representation);
		} else if (name != null) {
			throw new ShouldNotHappen("FunctionNode's name is not computed, present, but was not a string literal");
		}

		representation.append('(');
		parameters().represent(representation);
		representation.append(')');
	}

	default Value<?> executeBody(Interpreter interpreter, Value<?>[] passedArguments) throws AbruptCompletion {
		final ExecutionContext context = interpreter.pushContextWithNewEnvironment();
		try {
			parameters().declareArguments(interpreter, passedArguments);
			body().executeWithoutNewContext(interpreter);
			return Undefined.instance;
		} catch (AbruptCompletion e) {
			if (e.type != AbruptCompletion.Type.Return) throw e;
			return e.value;
		} finally {
			interpreter.exitExecutionContext(context);
		}
	}

	@Override
	default void dump(int indent) {
		DumpBuilder.begin(indent)
			.self(this)
			.optionalChild("Name", name())
			.children("Parameters", parameters())
			.child("Body", body());
	}
}
package xyz.lebster.core.node.expression;

import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.interpreter.environment.DeclarativeEnvironment;
import xyz.lebster.core.interpreter.environment.Environment;
import xyz.lebster.core.node.FunctionNode;
import xyz.lebster.core.node.FunctionParameters;
import xyz.lebster.core.node.expression.literal.StringLiteral;
import xyz.lebster.core.node.statement.BlockStatement;
import xyz.lebster.core.value.Names;
import xyz.lebster.core.value.function.Function;
import xyz.lebster.core.value.primitive.string.StringValue;

public record FunctionExpression(BlockStatement body, StringLiteral name, FunctionParameters parameters) implements FunctionNode, Expression {
	@Override
	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-runtime-semantics-instantiateordinaryfunctionexpression")
	public Function execute(Interpreter interpreter) {
		if (name == null) {
			return new Function(interpreter.intrinsics, Names.EMPTY, interpreter.environment(), this);
		} else {
			// 1. Assert: name is not present.
			// 2. Set name to StringValue of BindingIdentifier.
			final StringValue executedName = name.value();
			// 3. Let outerEnv be the running execution context's LexicalEnvironment.
			final Environment outerEnv = interpreter.environment();
			// 4. Let funcEnv be NewDeclarativeEnvironment(outerEnv).
			final Environment funcEnv = new DeclarativeEnvironment(outerEnv);
			// TODO: 5. Perform ! funcEnv.CreateImmutableBinding(name, false).
			// TODO: 6. Let privateEnv be the running execution context's PrivateEnvironment.
			// TODO: 7. Let sourceText be the source text matched by FunctionExpression.
			// 8. Let closure be OrdinaryFunctionCreate(%Function.prototype%, sourceText, FormalParameters, FunctionBody, non-lexical-this, funcEnv, privateEnv).
			final Function closure = new Function(interpreter.intrinsics, executedName, funcEnv, this);
			// TODO: 9. Perform SetFunctionName(closure, name).
			// TODO: 10. Perform MakeConstructor(closure).
			// 11. Perform ! funcEnv.InitializeBinding(name, closure).
			funcEnv.createBinding(interpreter, executedName, closure);
			// 12. Return closure.
			return closure;
		}
	}

	@Override
	public void represent(StringRepresentation representation) {
		representation.append("function ");
		representCall(representation);
		representation.append(' ');
		body.represent(representation);
	}
}
package xyz.lebster.core.node.expression;

import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.environment.DeclarativeEnvironment;
import xyz.lebster.core.interpreter.environment.Environment;
import xyz.lebster.core.node.FunctionNode;
import xyz.lebster.core.node.FunctionParameters;
import xyz.lebster.core.node.SourceRange;
import xyz.lebster.core.node.expression.literal.PrimitiveLiteral;
import xyz.lebster.core.node.statement.BlockStatement;
import xyz.lebster.core.value.function.ConstructorFunction;
import xyz.lebster.core.value.primitive.string.StringValue;

public record FunctionExpression(SourceRange range, BlockStatement body, PrimitiveLiteral<StringValue> name, FunctionParameters parameters) implements FunctionNode, Expression {
	@Override
	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-runtime-semantics-instantiateordinaryfunctionexpression")
	public ConstructorFunction execute(Interpreter interpreter) {
		if (name == null) {
			// 1. If name is not present, set name to "".
			final StringValue name = StringValue.EMPTY;
			// 2. Let env be the LexicalEnvironment of the running execution context.
			final Environment env = interpreter.environment();
			// TODO: 3. Let privateEnv be the running execution context's PrivateEnvironment.
			// TODO: 4. Let sourceText be the source text matched by FunctionExpression.
			// 5. Let closure be OrdinaryFunctionCreate(%Function.prototype%, sourceText, FormalParameters, FunctionBody, non-lexical-this, env, privateEnv).
			// 6. Perform SetFunctionName(closure, name).
			// 7. Perform MakeConstructor(closure).
			// 8. Return closure.
			return new ConstructorFunction(interpreter.intrinsics, name, env, this);
		} else {
			// 1. Assert: name is not present.
			// 2. Set name to the StringValue of BindingIdentifier.
			final StringValue name = this.name.value();
			// 3. Let outerEnv be the running execution context's LexicalEnvironment.
			final Environment outerEnv = interpreter.environment();
			// 4. Let funcEnv be NewDeclarativeEnvironment(outerEnv).
			final DeclarativeEnvironment funcEnv = new DeclarativeEnvironment(outerEnv);
			// 5. Perform ! funcEnv.CreateImmutableBinding(name, false).
			// TODO: 6. Let privateEnv be the running execution context's PrivateEnvironment.
			// TODO: 7. Let sourceText be the source text matched by FunctionExpression.
			// 8. Let closure be OrdinaryFunctionCreate(%Function.prototype%, sourceText, FormalParameters, FunctionBody, non-lexical-this, funcEnv, privateEnv).
			// 9. Perform SetFunctionName(closure, name).
			// 10. Perform MakeConstructor(closure).
			final ConstructorFunction closure = new ConstructorFunction(interpreter.intrinsics, name, funcEnv, this);
			// 11. Perform ! funcEnv.InitializeBinding(name, closure).
			funcEnv.createBinding(interpreter, name, closure);
			// 12. Return closure.
			return closure;
		}
	}
}
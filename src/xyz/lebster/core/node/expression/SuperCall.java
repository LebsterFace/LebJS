package xyz.lebster.core.node.expression;

import xyz.lebster.core.ANSI;
import xyz.lebster.core.DumpBuilder;
import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.exception.NotImplemented;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.Reference;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.node.SourcePosition;
import xyz.lebster.core.node.SourceRange;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.error.TypeError;
import xyz.lebster.core.value.function.Executable;

import java.util.SortedMap;

public record SuperCall(ExpressionList arguments, SourceRange range) implements Expression {
	@Override
	@SpecificationURL("https://tc39.es/ecma262/multipage#prod--j5ruhLQ")
	public Value<?> execute(Interpreter interpreter) throws AbruptCompletion {
		// 1. Let newTarget be GetNewTarget().
		// 2. Assert: Type(newTarget) is Object.
		// 3. Let func be GetSuperConstructor().
		// 4. Let argList be ? ArgumentListEvaluation of Arguments.
		// 5. If IsConstructor(func) is false, throw a TypeError exception.
		// 6. Let result be ? Construct(func, argList, newTarget).
		// 7. Let thisER be GetThisEnvironment().
		// 8. Perform ? thisER.BindThisValue(result).
		// 9. Let F be thisER.[[FunctionObject]].
		// 10. Assert: F is an ECMAScript function object.
		// 11. Perform ? InitializeInstanceElements(result, F).
		// 12. Return result.
		throw new NotImplemented("Executing SuperCall expressions");
	}

	@Override
	public void dump(int indent) {
		DumpBuilder.begin(indent)
			.self(this)
			.expressionList("Arguments", arguments);
	}

	@Override
	public void represent(StringRepresentation representation) {
		representation.append("super");
		representation.append('(');
		arguments.represent(representation);
		representation.append(')');
	}
}
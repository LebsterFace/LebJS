package xyz.lebster.core.node.expression;

import xyz.lebster.core.DumpBuilder;
import xyz.lebster.core.NonCompliant;
import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.exception.ShouldNotHappen;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.interpreter.environment.FunctionEnvironment;
import xyz.lebster.core.node.SourceRange;
import xyz.lebster.core.node.statement.Statement;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.error.TypeError;
import xyz.lebster.core.value.function.Constructor;
import xyz.lebster.core.value.object.ObjectValue;

public record SuperCallStatement(ExpressionList arguments, SourceRange range) implements Statement {
	@Override
	@NonCompliant
	@SpecificationURL("https://tc39.es/ecma262/multipage#prod--j5ruhLQ")
	public Value<?> execute(Interpreter interpreter) throws AbruptCompletion {
		// 1. Let newTarget be GetNewTarget().
		final ObjectValue newTarget = interpreter.getNewTarget();
		// 2. Assert: Type(newTarget) is Object.
		if (newTarget == null) {
			throw new ShouldNotHappen("new.target was null when super(...) executed");
		}

		// 3. Let func be GetSuperConstructor().
		final Value<?> func = interpreter.getSuperConstructor();
		// 4. Let argList be ? ArgumentListEvaluation of Arguments.
		final Value<?>[] argList = arguments.executeAll(interpreter).toArray(new Value[0]);
		// 5. If IsConstructor(func) is false, throw a TypeError exception.
		if (!(func instanceof final Constructor parentConstructor)) {
			throw AbruptCompletion.error(new TypeError(interpreter, "Not a constructor!"));
		}
		// 6. Let result be ? Construct(func, argList, newTarget).
		final ObjectValue result = parentConstructor.construct(interpreter, argList, newTarget);
		// 7. Let thisER be GetThisEnvironment().
		final FunctionEnvironment thisER = interpreter.getThisEnvironment();
		// 8. Perform ? thisER.BindThisValue(result).
		thisER.thisValue = result;
		// FIXME: 9. Let F be thisER.[[FunctionObject]].
		//        10. Assert: F is an ECMAScript function object.
		//        11. Perform ? InitializeInstanceElements(result, F).
		// 12. Return result.
		return result;
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
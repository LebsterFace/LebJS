package xyz.lebster.core.node;

import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.node.declaration.AssignmentPattern;
import xyz.lebster.core.node.declaration.AssignmentTarget;
import xyz.lebster.core.node.declaration.IdentifierExpression;
import xyz.lebster.core.node.declaration.Kind;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.array.ArrayObject;
import xyz.lebster.core.value.globals.Undefined;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public record FunctionParameters(List<AssignmentPattern> formalParameters, AssignmentTarget rest) implements Iterable<AssignmentPattern> {
	public FunctionParameters(IdentifierExpression identifier) {
		this(Collections.singletonList(new AssignmentPattern(identifier, null)), null);
	}

	public void declareArguments(Interpreter interpreter, Value<?>[] passedArguments) throws AbruptCompletion {
		int i = 0;
		while (i < formalParameters.size() && i < passedArguments.length) {
			formalParameters.get(i).declare(interpreter, Kind.Let, passedArguments[i]);
			i++;
		}

		if (rest == null) {
			// For any remaining formal parameters
			while (i < formalParameters.size()) {
				formalParameters.get(i).declare(interpreter, Kind.Let, Undefined.instance);
				i++;
			}
		} else {
			// let rest = passedArguments.slice(i)
			rest.declare(interpreter, Kind.Let, new ArrayObject(interpreter,
				Arrays.copyOfRange(passedArguments, i, passedArguments.length)
			));
		}
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-static-semantics-expectedargumentcount")
	public int expectedArgumentCount() {
		int count = 0;
		for (final var parameter : formalParameters) {
			if (parameter.defaultExpression() != null) break;
			count++;
		}

		return count;
	}

	@Override
	public Iterator<AssignmentPattern> iterator() {
		return formalParameters.iterator();
	}
}

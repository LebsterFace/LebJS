package xyz.lebster.core.node;

import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.node.declaration.AssignmentPattern;
import xyz.lebster.core.node.declaration.AssignmentTarget;
import xyz.lebster.core.node.declaration.Kind;
import xyz.lebster.core.node.expression.Expression;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.array.ArrayObject;
import xyz.lebster.core.value.globals.Undefined;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public final class FunctionParameters implements Iterable<AssignmentPattern>, Representable {
	private final List<AssignmentPattern> formalParameters = new ArrayList<>();
	public AssignmentTarget rest;

	public FunctionParameters() {
	}

	public FunctionParameters(AssignmentTarget... assignmentTargets) {
		for (final AssignmentTarget p : assignmentTargets) {
			this.add(p);
		}
	}

	public void addWithDefault(AssignmentTarget target, Expression defaultExpression) {
		this.formalParameters.add(new AssignmentPattern(target, defaultExpression));
	}

	public void add(AssignmentTarget target) {
		this.formalParameters.add(new AssignmentPattern(target, null));
	}

	@Override
	public void represent(StringRepresentation representation) {
		if (formalParameters.size() > 0) {
			formalParameters.get(0).represent(representation);
			for (int i = 1; i < formalParameters.size(); i++) {
				representation.append(", ");
				formalParameters.get(i).represent(representation);
			}
		}
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

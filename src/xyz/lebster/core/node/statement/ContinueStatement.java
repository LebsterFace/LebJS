package xyz.lebster.core.node.statement;

import xyz.lebster.core.Dumper;
import xyz.lebster.core.NonCompliant;
import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.value.Value;

public final class ContinueStatement implements Statement {
	@Override
	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-continue-statement-runtime-semantics-evaluation")
	@NonCompliant
	// FIXME: Follow spec (labels)
	public Value<?> execute(Interpreter interpreter) throws AbruptCompletion {
		throw new AbruptCompletion(null, AbruptCompletion.Type.Continue);
	}

	@Override
	public void dump(int indent) {
		Dumper.dumpString(indent, "ContinueStatement");
	}

	@Override
	public void represent(StringRepresentation representation) {
		representation.append("continue;");
		representation.append('\n');
	}
}
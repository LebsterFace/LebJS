package xyz.lebster.node;

import xyz.lebster.Dumper;
import xyz.lebster.interpreter.AbruptCompletion;
import xyz.lebster.interpreter.Interpreter;
import xyz.lebster.interpreter.StringRepresentation;
import xyz.lebster.node.value.Value;

public class BreakStatement implements Statement {
	@Override
	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-break-statement-runtime-semantics-evaluation")
//	FIXME: Follow spec (labels)
	public Value<?> execute(Interpreter interpreter) throws AbruptCompletion {
		throw new AbruptCompletion(null, AbruptCompletion.Type.Break);
	}

	@Override
	public void dump(int indent) {
		Dumper.dumpSingle(indent, "BreakStatement");
	}

	@Override
	public void represent(StringRepresentation representation) {
		representation.appendLine("break;");
	}
}
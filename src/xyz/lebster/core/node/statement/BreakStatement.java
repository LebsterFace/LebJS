package xyz.lebster.core.node.statement;

import xyz.lebster.core.NonCompliant;
import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.node.SourceRange;
import xyz.lebster.core.value.Value;

public record BreakStatement(SourceRange range) implements Statement {
	@Override
	@NonCompliant
	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-break-statement-runtime-semantics-evaluation")
	public Value<?> execute(Interpreter interpreter) throws AbruptCompletion {
		// FIXME: Follow spec (labels)
		throw new AbruptCompletion(null, AbruptCompletion.Type.Break);
	}
}
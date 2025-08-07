package xyz.lebster.core.node.declaration;

import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.node.SourceRange;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.globals.Undefined;

public record VariableDeclaration(SourceRange range, Kind kind, VariableDeclarator... declarations) implements Declaration {
	@Override
	public Value<?> execute(Interpreter interpreter) throws AbruptCompletion {
		for (VariableDeclarator declarator : declarations)
			declarator.execute(interpreter);
		return Undefined.instance;
	}
}
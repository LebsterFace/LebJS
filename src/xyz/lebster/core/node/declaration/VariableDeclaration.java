package xyz.lebster.core.node.declaration;

import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.node.SourceRange;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.error.CheckedError;
import xyz.lebster.core.value.globals.Undefined;

import static xyz.lebster.core.interpreter.AbruptCompletion.error;

public record VariableDeclaration(SourceRange range, Kind kind, VariableDeclarator... declarations) implements Declaration {
	@Override
	public Value<?> execute(Interpreter interpreter) throws AbruptCompletion {
		if (this.kind == Kind.Var && interpreter.isCheckedMode()) {
			throw error(new CheckedError(interpreter, "Usage of `var` in checked mode. Use `let` or `const` instead."));
		}

		for (VariableDeclarator declarator : declarations)
			declarator.execute(interpreter);
		return Undefined.instance;
	}
}
package xyz.lebster.core.node.declaration;

import xyz.lebster.core.Dumper;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.runtime.value.Value;
import xyz.lebster.core.runtime.value.error.CheckedError;
import xyz.lebster.core.runtime.value.primitive.Undefined;

public record VariableDeclaration(Kind kind, VariableDeclarator... declarations) implements Declaration {
	@Override
	public void dump(int indent) {
		Dumper.dumpName(indent, "VariableDeclaration");
		for (VariableDeclarator declarator : declarations) {
			declarator.dump(indent + 1);
		}
	}

	@Override
	public Value<?> execute(Interpreter interpreter) throws AbruptCompletion {
		if (this.kind == Kind.Var && interpreter.isCheckedMode()) {
			throw AbruptCompletion.error(new CheckedError("Usage of `var` in checked mode. Use `let` or `const` instead."));
		}

		for (VariableDeclarator declarator : declarations)
			declarator.execute(interpreter);
		return Undefined.instance;
	}

	@Override
	public void represent(StringRepresentation representation) {
		for (VariableDeclarator declaration : declarations) {
			declaration.represent(representation);
		}
	}

	public enum Kind {
		Const, Let, Var
	}
}
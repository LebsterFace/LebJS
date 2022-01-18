package xyz.lebster.core.node.declaration;

import xyz.lebster.core.ANSI;
import xyz.lebster.core.Dumper;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.runtime.value.Value;
import xyz.lebster.core.runtime.value.primitive.UndefinedValue;

public record VariableDeclaration(Kind kind, VariableDeclarator... declarations) implements Declaration {
	public VariableDeclaration(Kind kind, VariableDeclarator... declarations) {
		this.kind = kind;
		this.declarations = declarations;
		if (this.kind == Kind.Var) {
			System.err.print(ANSI.BACKGROUND_BRIGHT_YELLOW);
			System.err.print(ANSI.BLACK);
			System.err.print("WARNING:");
			System.err.print(ANSI.RESET);
			System.err.print(ANSI.BRIGHT_YELLOW);
			System.err.print(" `var` should not be used. Please use `const` or `let` instead.");
			System.err.println(ANSI.RESET);
		}
	}

	@Override
	public void dump(int indent) {
		Dumper.dumpName(indent, "VariableDeclaration");
		for (VariableDeclarator declarator : declarations) {
			declarator.dump(indent + 1);
		}
	}

	@Override
	public Value<?> execute(Interpreter interpreter) throws AbruptCompletion {
		for (VariableDeclarator declarator : declarations) declarator.execute(interpreter);
		return UndefinedValue.instance;
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
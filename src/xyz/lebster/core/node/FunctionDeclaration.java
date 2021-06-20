package xyz.lebster.core.node;

import xyz.lebster.core.runtime.Interpreter;
import xyz.lebster.core.value.Function;

import java.util.stream.Stream;

public class FunctionDeclaration extends ScopeNode {
	public final Identifier name;
	public final Identifier[] arguments;

	public FunctionDeclaration(Identifier name, Identifier... arguments) {
		this.name = name;
		this.arguments = arguments;
	}
	
	public FunctionDeclaration(String name, String... arguments) {
		this(new Identifier(name), Stream.of(arguments).map(Identifier::new).toArray(Identifier[]::new));
	}

	@Override
	public void dump(int indent) {
		Interpreter.dumpIndent(indent);
		System.out.print("FunctionDeclaration '");
		System.out.print(name.value);

		System.out.print("(");
		System.out.print(arguments[0].value);
		for (int i = 1; i < arguments.length; i++) {
			System.out.print(", ");
			System.out.print(arguments[i].value);
		}
		System.out.println(")':");

		for (ASTNode child : children) child.dump(indent + 1);
	}

	@Override
	public Function execute(Interpreter interpreter) {
		final Function value = new Function(this);
		interpreter.declareVariable(name, value);
		return value;
	}

	public CallExpression getCall() {
		return new CallExpression(name);
	}
}

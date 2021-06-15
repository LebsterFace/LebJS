package xyz.lebster.parser;

import xyz.lebster.core.node.*;
import xyz.lebster.core.value.StringLiteral;

public class Parser {
	public final Lexer lexer;
	private Token currentToken;

	public Parser(Lexer lexer) {
		this.lexer = lexer;
	}

	private void expected(TokenType t) {
		throw new Error("Unexpected token " + currentToken.type() + ". Expected " + t);
	}

	private Token consume() {
		final Token oldToken = currentToken;
		currentToken = lexer.next();
		return oldToken;
	}

	private boolean match(TokenType t) {
		return currentToken.type() == t;
	}

	private Token consume(TokenType t) {
		if (!match(t)) expected(t);
		return consume();
	}

	public Program parse() {
		final Program program = new Program();
		consume();

		while (!lexer.isFinished()) {
			if (matchDeclaration()) {
				program.append(parseDeclaration());
			} else {
				throw new Error("I can't handle this!!!"); // YOU CAN'T HANDLE THE TRUTH!
			}
		}

		return program;
	}

	private VariableDeclaration parseDeclaration() {
		consume(TokenType.Let);
		final Token identifier = consume(TokenType.Identifier);
		consume(TokenType.Assign);
		final Expression value = parseExpression();
		return new VariableDeclaration(new VariableDeclarator[] {
			new VariableDeclarator(new Identifier(identifier.value()), value)
		});
	}

	private Expression parseExpression() {
		return switch (currentToken.type()) {
			case StringLiteral -> new StringLiteral(currentToken.value());
			default -> {
				throw new Error("Unsupported expression type");
			}
		};
	}

	private boolean matchDeclaration() {
		final TokenType t = currentToken.type();
		return t == TokenType.Let;
	}
}

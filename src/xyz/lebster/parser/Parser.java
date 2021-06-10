package xyz.lebster.parser;

import xyz.lebster.core.exception.LanguageException;
import xyz.lebster.core.node.CallExpression;
import xyz.lebster.core.node.Expression;
import xyz.lebster.core.node.Identifier;
import xyz.lebster.core.node.Program;
import xyz.lebster.core.value.StringLiteral;

public class Parser {
	public final Token[] tokens;
	private int index = 0;
	private Token token;

	public Parser(Token[] tokens) {
		this.tokens = tokens;
		this.token = tokens[index];
	}

	public Parser(String source) {
		this(new Lexer(source).tokenize());
	}

	private Token consume() {
		if (index + 1 == tokens.length) return null;
		final Token old = token;
		index++;
		token = tokens[index];
		return old;
	}

	private Token accept(TokenType t) {
		return token.type == t ? consume() : null;
	}

	private boolean didConsume(TokenType t) {
		if (token.type == t) {
			consume();
			return true;
		} else {
			return false;
		}
	}

	private StringLiteral parseStringLiteral() {
		final Token string = accept(TokenType.StringLiteral);
		return string == null ? null : new StringLiteral(string.value);
	}

	private Expression parseExpression() {
		// FIXME: Support everything
		return parseStringLiteral();
	}

	private Identifier parseIdentifier() {
		final Token id = accept(TokenType.Identifier);
		return id == null ? null : new Identifier(id.value);
	}

	private CallExpression parseCallExpression() {
		final Identifier id = parseIdentifier();
		if (id == null) return null;
		if (!didConsume(TokenType.LParen)) return null;
		final Expression argument = parseExpression();
		if (argument == null) return null;
		if (!didConsume(TokenType.RParen)) return null;
		return new CallExpression(id, argument);
	}

	public Program parse() {
		final Program result = new Program();
		final CallExpression call = parseCallExpression();
		if (call == null) throw new Error("Expecting CallExpression");
		result.append(call);
		return result;
	}
}

package xyz.lebster.core.parser;

import xyz.lebster.core.StringEscapeUtils;
import xyz.lebster.core.exception.SyntaxError;
import xyz.lebster.core.node.SourcePosition;
import xyz.lebster.core.node.expression.ObjectExpression;
import xyz.lebster.core.node.expression.literal.PrimitiveLiteral;
import xyz.lebster.core.value.primitive.string.StringValue;

import java.util.HashMap;

import static xyz.lebster.core.parser.TokenType.EOF;

public final class ParserState {
	private final Lexer lexer;
	private Token previousToken;

	public final HashMap<ObjectExpression, SourcePosition> invalidProperties = new HashMap<>();
	public Token token;
	public boolean inBreakContext = false;
	public boolean inContinueContext = false;

	public ParserState(Lexer lexer) throws SyntaxError {
		this.lexer = lexer;
		this.token = lexer.next();
	}

	private ParserState(Lexer lexer, Token token, Token previousToken, boolean inBreakContext, boolean inContinueContext) {
		this.lexer = lexer;
		this.token = token;
		this.previousToken = previousToken;
		this.inBreakContext = inBreakContext;
		this.inContinueContext = inContinueContext;
	}

	int startIndex() {
		return token.range().startIndex;
	}

	int lastEndIndex() {
		if (previousToken == null) {
			return token.range().startIndex;
		} else {
			return previousToken.range().endIndex;
		}
	}

	public SyntaxError expected(TokenType type) {
		return expected(StringEscapeUtils.quote(Lexer.valueForSymbol(type), false));
	}

	public SyntaxError expected(String value) {
		if (token.type() == EOF) return new SyntaxError("Unexpected end of input, expected %s".formatted(value), token.range().start());
		return new SyntaxError("Unexpected token %s, expected %s".formatted(token, value), token.range().start());
	}

	public SyntaxError unexpected() {
		return unexpected(token);
	}

	public SyntaxError unexpected(Token unexpectedToken) {
		if (unexpectedToken.type() == EOF) return new SyntaxError("Unexpected end of input", unexpectedToken.range().start());
		return new SyntaxError("Unexpected token " + unexpectedToken, unexpectedToken.range().start());
	}

	Token consume() throws SyntaxError {
		previousToken = token;
		token = lexer.next();
		return previousToken;
	}

	String require(TokenType type) throws SyntaxError {
		if (token.type() != type) throw expected(type);
		return consume().value();
	}

	Token accept(TokenType type) throws SyntaxError {
		return token.type() == type ? consume() : null;
	}

	boolean optional(TokenType type) throws SyntaxError {
		if (token.type() == type) {
			consume();
			return true;
		}

		return false;
	}

	boolean optional(TokenType type, String value) throws SyntaxError {
		if (token.type() == type && token.value().equals(value)) {
			consume();
			return true;
		}

		return false;
	}

	PrimitiveLiteral<StringValue> optionalStringLiteral(TokenType type) throws SyntaxError {
		final Token token = accept(type);
		if (token == null) return null;
		return token.asStringLiteral();
	}

	boolean optional(TokenType... types) throws SyntaxError {
		if (is(types)) {
			consume();
			return true;
		}

		return false;
	}

	boolean is(TokenType... types) {
		for (final TokenType type : types) {
			if (token.type() == type)
				return true;
		}

		return false;
	}

	boolean is(TokenType type) {
		return token.type() == type;
	}

	boolean is(TokenType type, String value) {
		return token.type() == type && token.value().equals(value);
	}

	public ParserState copy() {
		return new ParserState(
			lexer.copy(),
			token,
			previousToken,
			inBreakContext,
			inContinueContext
		);
	}
}
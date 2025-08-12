package xyz.lebster.core.parser;

import xyz.lebster.core.StringEscapeUtils;
import xyz.lebster.core.exception.SyntaxError;
import xyz.lebster.core.node.SourcePosition;
import xyz.lebster.core.node.expression.ObjectExpression;

import java.util.HashMap;

import static xyz.lebster.core.parser.TokenType.EOF;
import static xyz.lebster.core.parser.TokenType.Identifier;

public final class ParserState {
	final Lexer lexer;
	private Token previousToken;

	final HashMap<ObjectExpression, SourcePosition> invalidProperties = new HashMap<>();
	boolean inBreakContext = false;
	boolean inContinueContext = false;

	ParserState(Lexer lexer) throws SyntaxError {
		this.lexer = lexer;
		lexer.next();
	}

	private ParserState(Lexer lexer, Token previousToken, boolean inBreakContext, boolean inContinueContext) {
		this.lexer = lexer;
		this.previousToken = previousToken;
		this.inBreakContext = inBreakContext;
		this.inContinueContext = inContinueContext;
	}

	Token token() {
		return lexer.latestToken;
	}

	int startIndex() {
		return token().range().startIndex;
	}

	int lastEndIndex() {
		if (previousToken == null) {
			return token().range().startIndex;
		} else {
			return previousToken.range().endIndex;
		}
	}

	SyntaxError expected(TokenType type) {
		final String symbol = Lexer.valueForSymbol(type);
		if (symbol == null) return expected(type.name());
		return expected(StringEscapeUtils.quote(symbol, false));
	}

	SyntaxError expected(String value) {
		if (token().type() == EOF) return new SyntaxError("Unexpected end of input, expected %s".formatted(value), token().range().start());
		return new SyntaxError("Unexpected token %s, expected %s".formatted(token(), value), token().range().start());
	}

	SyntaxError unexpected() {
		return unexpected(token());
	}

	SyntaxError unexpected(Token unexpectedToken) {
		if (unexpectedToken.type() == EOF) return new SyntaxError("Unexpected end of input", unexpectedToken.range().start());
		return new SyntaxError("Unexpected token " + unexpectedToken, unexpectedToken.range().start());
	}

	Token consume() throws SyntaxError {
		previousToken = token();
		lexer.next();
		return previousToken;
	}

	String require(TokenType type) throws SyntaxError {
		if (token().type() != type) throw expected(type);
		return consume().value();
	}

	Token accept(TokenType type) throws SyntaxError {
		return token().type() == type ? consume() : null;
	}

	boolean optional(TokenType type) throws SyntaxError {
		if (token().type() == type) {
			consume();
			return true;
		}

		return false;
	}

	boolean optional(TokenType type, String value) throws SyntaxError {
		if (token().type() == type && token().value().equals(value)) {
			consume();
			return true;
		}

		return false;
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
			if (token().type() == type)
				return true;
		}

		return false;
	}

	boolean is(TokenType type) {
		return token().type() == type;
	}

	boolean is(String value) {
		return token().type() == Identifier && token().value().equals(value);
	}

	ParserState copy() {
		return new ParserState(
			lexer.copy(),
			previousToken,
			inBreakContext,
			inContinueContext
		);
	}
}
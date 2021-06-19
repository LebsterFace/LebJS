package xyz.lebster.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Lexer {
	private final String source;
	private final StringBuilder builder = new StringBuilder();
	private final int length;

	private int index = -1;
	private char currentChar = '\0';

	private static final HashMap<String, TokenType> keywords = new HashMap<>();
	private static final HashMap<Character, TokenType> symbols = new HashMap<>();

	static {
		keywords.put("let", TokenType.Let);

		symbols.put('=', TokenType.Equals);
		symbols.put('(', TokenType.LParen);
		symbols.put(')', TokenType.RParen);
		symbols.put(';', TokenType.Semicolon);
		symbols.put('+', TokenType.Plus);
		symbols.put('-', TokenType.Minus);
		symbols.put('*', TokenType.Multiply);
		symbols.put('/', TokenType.Divide);
		symbols.put('.', TokenType.Period);
	}

	public Lexer(String source) {
		this.source = source;
		this.length = source.length();
		consume();
	}

	public boolean isFinished() {
		return index == length;
	}

	private boolean isDigit(char c) {
		return c >= '0' && c <= '9';
	}

	private boolean isAlphabetical(char c) {
		return (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z');
	}

	private boolean isTerminator(char c) {
		return c == '\n';
	}

	private char consume() {
		final char old = currentChar;
		if (++index == length) {
			currentChar = '\0';
		} else {
			currentChar = source.charAt(index);
		}
		return old;
	}

	private void collect() {
		builder.append(currentChar);
		consume();
	}

	private void consumeWhitespace() {
		while (currentChar == '\t' || currentChar == 0xB || currentChar == 0xC || currentChar == ' ') {
			consume();
		}
	}

	private boolean peek(String compare) {
		return source.startsWith(compare, index);
	}

	private boolean isIdentifierStart() {
		return isAlphabetical(currentChar) || currentChar == '_' || currentChar == '$';
	}

	private boolean isIdentifierMiddle() {
		return isIdentifierStart() || isDigit(currentChar);
	}

	public Token next() {
		if (index == length) return null;
		consumeWhitespace();
		if (currentChar == '\r') consume();
		int start = index;
		builder.setLength(0);

		if (isTerminator(currentChar)) {
			consume();
			return new Token(TokenType.Terminator, start, index);
		} else if (isIdentifierStart()) {
			while (isIdentifierMiddle()) collect();

			final String value = builder.toString();
			if (value.equals("true") || value.equals("false")) {
				return new Token(TokenType.BooleanLiteral, value, start, index);
			} else if (keywords.containsKey(value)) {
				return new Token(keywords.get(value), start, index);
			} else {
				return new Token(TokenType.Identifier, value, start, index);
			}
		} else if (symbols.containsKey(currentChar)) {
			return new Token(symbols.get(consume()), start, index);
		} else if (currentChar == '"' || currentChar == '\'') {
			final char stringType = currentChar;
			consume();
			while (currentChar != stringType) collect();
			consume();
			return new Token(TokenType.StringLiteral, builder.toString(), start, index);
		}  else if (isDigit(currentChar)) {
			int decimalPos = -1;

			while (isDigit(currentChar) || (currentChar == '.' && decimalPos == -1)) {
				if (currentChar == '.') decimalPos = index;
				collect();
			}

			return new Token(TokenType.NumericLiteral, builder.toString(), start, index);
		} else {
			throw new Error(StringEscapeUtils.escape("Invalid character '" + currentChar + "' at " + getRow() + ":" + getColumn()));
		}
	}

	private int getColumn() {
		int i = index - 1;
		while (i >= 0 && !isTerminator(source.charAt(i))) i--;
		return index - i;
	}

	private int getRow() {
		int result = 1;
		for (int i = 0; i < index; i++) {
			if (isTerminator(source.charAt(i))) {
				result++;
			}
		}

		return result;
	}

	public Token[] tokenize() {
		final List<Token> result = new ArrayList<>();
		while (!isFinished()) result.add(next());
		result.add(new Token(TokenType.EOF, "", length, length));
		return result.toArray(new Token[0]);
	}
}

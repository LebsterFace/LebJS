package xyz.lebster.parser;

import java.util.HashMap;

public class Lexer {
	private final String source;
	private final int length;

	private int index = -1;
	private char currentChar = '\0';

	private static final HashMap<String, TokenType> keywords = new HashMap<>();
	private static final HashMap<Character, TokenType> symbols = new HashMap<>();

	static {
		keywords.put("let", TokenType.Let);
		symbols.put('=', TokenType.Assign);
	}

	public Lexer(String source) {
		this.source = source;
		this.length = source.length();
		consume();
	}

	public boolean isFinished() {
		return index == length;
	}

	private boolean isDigit() {
		return currentChar >= '0' && currentChar <= '9';
	}

	private boolean isAlphabetical() {
		return (currentChar >= 'A' && currentChar <= 'Z') || (currentChar >= 'a' && currentChar <= 'z');
	}

	private void consume() {
		index++;
		if (index >= length - 1) {
			currentChar = '\0';
		} else {
			currentChar = source.charAt(index);
		}
	}

	private void consumeWhitespace() {
		while (Character.isWhitespace(currentChar)) {
			consume();
		}
	}

	private boolean isIdentifierStart() {
		return isAlphabetical() || currentChar == '_' || currentChar == '$';
	}

	private boolean isIdentifierMiddle() {
		return isIdentifierStart() || isDigit();
	}

	public Token next() {
		if (index == length) return null;
		consumeWhitespace();

		int start = index;
		int end = -1;
		TokenType tokenType = null;
		String value = null;

		if (isIdentifierStart()) {
			while (isIdentifierMiddle()) {
				consume();
			}

			tokenType = TokenType.Identifier;
		} else if (symbols.containsKey(currentChar)) {
			tokenType = symbols.get(currentChar);
			consume();
		} else {
			tokenType = TokenType.Invalid;
			consume();
		}

		if (value == null) value = source.substring(start, index);
		if (end == -1) end = index;

		return new Token(tokenType, value, start, end);
	}
}

package xyz.lebster.parser;

import java.util.ArrayList;
import java.util.HashMap;

public class Lexer {
	private final ArrayList<Token> result = new ArrayList<>();
	private final StringBuffer buffer = new StringBuffer();
	private TokenType last;
	private final char[] chars;
	private int index = 0;

	public Lexer(String source) {
		this.chars = source.toCharArray();
	}

	private void finish(Token t) {
		result.add(t);
		buffer.setLength(0);
	}

	private void finishLast() {
		if (last == null) {
			return;
		}

		finish(new Token(last, buffer));
		last = null;
	}

	private void next() {
		buffer.append(chars[index]);
		index++;
	}

	// TODO: Exception instead of error
	private void fail(String msg) {
		throw new Error(msg + " @ index " + index);
	}

	private void tokenizeString(char initial) {
		finishLast();
		index++;
		last = TokenType.StringLiteral;
		while (chars[index] != initial) next();
		finishLast();
		index++;
	}

	private static final HashMap<Character, TokenType> punctuation = new HashMap<>();
	static {
		punctuation.put('(', TokenType.LParen);
		punctuation.put(')', TokenType.RParen);
		punctuation.put(';', TokenType.Semicolon);
	}

	private void tokenizePunctuation(char initial) {
		finishLast();
		next();
		if (punctuation.containsKey(initial)) {
			last = punctuation.get(initial);
			finishLast();
		} else {
			fail("Unexpected symbol '" + initial + "'");
		}
	}

	private void tokenizeIdentifier() {
		finishLast();
		next();
		last = TokenType.Identifier;

		while (Character.isAlphabetic(chars[index]))
			next();

		finishLast();
	}

	public Token[] tokenize() {
		while (index < chars.length) {
			final char c = chars[index];
			switch (c) {
				case ' ': case '\t':
				case '\n': case '\r':
					break;

				case '"': case '\'':
					tokenizeString(c);
					break;

				default:
					if (Character.isAlphabetic(c)) {
						tokenizeIdentifier();
					} else {
						tokenizePunctuation(c);
					}

					break;
			}
		}

		final Token[] array = new Token[0];
		return result.toArray(array);
	}
}

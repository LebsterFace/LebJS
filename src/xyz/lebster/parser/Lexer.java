package xyz.lebster.parser;

import xyz.lebster.exception.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Lexer {
	private static final HashMap<String, TokenType> keywords = new HashMap<>();
	private static final HashMap<Character, TokenType> symbols = new HashMap<>();

	static {
		keywords.put("let", TokenType.Let);
		keywords.put("break", TokenType.Break);
		keywords.put("case", TokenType.Case);
		keywords.put("catch", TokenType.Catch);
		keywords.put("class", TokenType.Class);
		keywords.put("const", TokenType.Const);
		keywords.put("continue", TokenType.Continue);
		keywords.put("debugger", TokenType.Debugger);
		keywords.put("default", TokenType.Default);
		keywords.put("delete", TokenType.Delete);
		keywords.put("do", TokenType.Do);
		keywords.put("else", TokenType.Else);
		keywords.put("export", TokenType.Export);
		keywords.put("extends", TokenType.Extends);
		keywords.put("finally", TokenType.Finally);
		keywords.put("for", TokenType.For);
		keywords.put("function", TokenType.Function);
		keywords.put("if", TokenType.If);
		keywords.put("import", TokenType.Import);
		keywords.put("in", TokenType.In);
		keywords.put("Infinity", TokenType.Infinity);
		keywords.put("instanceof", TokenType.Instanceof);
		keywords.put("NaN", TokenType.NaN);
		keywords.put("new", TokenType.New);
		keywords.put("null", TokenType.Null);
		keywords.put("return", TokenType.Return);
		keywords.put("super", TokenType.Super);
		keywords.put("switch", TokenType.Switch);
		keywords.put("this", TokenType.This);
		keywords.put("throw", TokenType.Throw);
		keywords.put("try", TokenType.Try);
		keywords.put("typeof", TokenType.Typeof);
		keywords.put("undefined", TokenType.Undefined);
		keywords.put("var", TokenType.Var);
		keywords.put("void", TokenType.Void);
		keywords.put("while", TokenType.While);
		keywords.put("with", TokenType.With);
		keywords.put("yield", TokenType.Yield);

		symbols.put('=', TokenType.Equals);
		symbols.put('(', TokenType.LParen);
		symbols.put(')', TokenType.RParen);
		symbols.put('{', TokenType.LBrace);
		symbols.put('}', TokenType.RBrace);
		symbols.put('[', TokenType.LBracket);
		symbols.put(']', TokenType.RBracket);
		symbols.put(';', TokenType.Semicolon);
		symbols.put('+', TokenType.Plus);
		symbols.put('-', TokenType.Minus);
		symbols.put('*', TokenType.Multiply);
		symbols.put('/', TokenType.Divide);
		symbols.put('.', TokenType.Period);
		symbols.put(',', TokenType.Comma);
		symbols.put('!', TokenType.Bang);
	}

	private final String source;
	private final StringBuilder builder = new StringBuilder();
	private final int length;
	private int index = -1;
	private char currentChar = '\0';

	public Lexer(String source) {
		this.source = source;
		this.length = source.length();
		consume();
	}

	public boolean isFinished() {
		return index >= length;
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
		if (++index >= length) {
			currentChar = '\0';
		} else {
			currentChar = source.charAt(index);
		}

		return old;
	}

	private void collect() {
		builder.append(consume());
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

	public Token next() throws ParseException {
		if (index == length) return null;
		consumeWhitespace();
		if (currentChar == '\r') consume();
		int start = index;
		builder.setLength(0);

		if (isTerminator(currentChar)) {
			consume();
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
		} else if (isDigit(currentChar)) {
			int decimalPos = -1;

			while (isDigit(currentChar) || (currentChar == '.' && decimalPos == -1)) {
				if (currentChar == '.') decimalPos = index;
				collect();
			}

			return new Token(TokenType.NumericLiteral, builder.toString(), start, index);
		} else {
			throw new ParseException(StringEscapeUtils.escape("Invalid character '" + currentChar + "' at " + 0 + ":" + 0));
		}
	}

	public Token[] tokenize() throws ParseException {
		final List<Token> result = new ArrayList<>();
		while (!isFinished()) result.add(next());
		result.add(new Token(TokenType.EOF, "", length, length));
		return result.toArray(new Token[0]);
	}
}

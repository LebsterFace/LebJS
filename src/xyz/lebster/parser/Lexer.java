package xyz.lebster.parser;

import xyz.lebster.exception.ParseException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Lexer {
	private static final HashMap<String, TokenType> keywords = new HashMap<>();
	private static final List<HashMap<String, TokenType>> symbols = new ArrayList<>();

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

		final HashMap<String, TokenType> symbols_length_1 = new HashMap<>();
		final HashMap<String, TokenType> symbols_length_2 = new HashMap<>();
		final HashMap<String, TokenType> symbols_length_3 = new HashMap<>();
		final HashMap<String, TokenType> symbols_length_4 = new HashMap<>();

		symbols_length_4.put(">>>=", TokenType.UnsignedRightShiftEquals);

		symbols_length_3.put("||=", TokenType.LogicalOrEquals);
		symbols_length_3.put(">>>", TokenType.UnsignedRightShift);
		symbols_length_3.put(">>=", TokenType.RightShiftEquals);
		symbols_length_3.put("===", TokenType.StrictEqual);
		symbols_length_3.put("<<=", TokenType.LeftShiftEquals);
		symbols_length_3.put("&&=", TokenType.LogicalAndEquals);
		symbols_length_3.put("**=", TokenType.ExponentEquals);
		symbols_length_3.put("??=", TokenType.NullishCoalescingEquals);
		symbols_length_3.put("!==", TokenType.StrictNotEqual);

		symbols_length_2.put("||", TokenType.LogicalOr);
		symbols_length_2.put("|=", TokenType.PipeEquals);
		symbols_length_2.put(">>", TokenType.RightShift);
		symbols_length_2.put(">=", TokenType.GreaterThanEqual);
		symbols_length_2.put("=>", TokenType.Arrow);
		symbols_length_2.put("==", TokenType.LooseEqual);
		symbols_length_2.put("<=", TokenType.LessThanEqual);
		symbols_length_2.put("<<", TokenType.LeftShift);
		symbols_length_2.put("+=", TokenType.PlusEquals);
		symbols_length_2.put("++", TokenType.Increment);
		symbols_length_2.put("^=", TokenType.CaretEquals);
		symbols_length_2.put("%=", TokenType.PercentEquals);
		symbols_length_2.put("&=", TokenType.AmpersandEquals);
		symbols_length_2.put("&&", TokenType.LogicalAnd);
		symbols_length_2.put("/=", TokenType.DivideEquals);
		symbols_length_2.put("*=", TokenType.MultiplyEquals);
		symbols_length_2.put("**", TokenType.Exponent);
		symbols_length_2.put("?.", TokenType.OptionalChain);
		symbols_length_2.put("??", TokenType.NullishCoalescing);
		symbols_length_2.put("!=", TokenType.NotEqual);
		symbols_length_2.put("-=", TokenType.MinusEquals);
		symbols_length_2.put("--", TokenType.Decrement);

		symbols_length_1.put("~", TokenType.Tilde);
		symbols_length_1.put("|", TokenType.Pipe);
		symbols_length_1.put(">", TokenType.GreaterThan);
		symbols_length_1.put("=", TokenType.Equals);
		symbols_length_1.put("<", TokenType.LessThan);
		symbols_length_1.put("+", TokenType.Plus);
		symbols_length_1.put("^", TokenType.Caret);
		symbols_length_1.put("%", TokenType.Percent);
		symbols_length_1.put("&", TokenType.Ampersand);
		symbols_length_1.put("/", TokenType.Slash);
		symbols_length_1.put("*", TokenType.Star);
		symbols_length_1.put("}", TokenType.RBrace);
		symbols_length_1.put("{", TokenType.LBrace);
		symbols_length_1.put("]", TokenType.RBracket);
		symbols_length_1.put("[", TokenType.LBracket);
		symbols_length_1.put(")", TokenType.RParen);
		symbols_length_1.put("(", TokenType.LParen);
		symbols_length_1.put(".", TokenType.Period);
		symbols_length_1.put("!", TokenType.Bang);
		symbols_length_1.put(";", TokenType.Semicolon);
		symbols_length_1.put(",", TokenType.Comma);
		symbols_length_1.put("-", TokenType.Minus);

		symbols.add(symbols_length_4);
		symbols.add(symbols_length_3);
		symbols.add(symbols_length_2);
		symbols.add(symbols_length_1);
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

	private boolean isTerminator() {
		return peek("\r\n") || currentChar == '\n' || index == length;
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

	private char consume(int size) {
		final char old = currentChar;
		index += size;

		if (index >= length) {
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

	private boolean accept(String s) {
		final boolean result = peek(s);
		if (result) consume(s.length());
		return result;
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
		consumeComment();
		consumeWhitespace();
		int start = index;
		builder.setLength(0);

		if (isTerminator()) {
			while (isTerminator()) consume();
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
			for (Map<String, TokenType> symbolSize : symbols) {
				for (Map.Entry<String, TokenType> entry : symbolSize.entrySet()) {
					final String key = entry.getKey();
					if (accept(key)) {
						return new Token(entry.getValue(), key, start, index);
					}
				}
			}

			throw new ParseException(StringEscapeUtils.escape("Invalid character '" + currentChar + "' at " + 0 + ":" + 0));
		}
	}

	private void consumeComment() {
		if (accept("//")) {
			while (!isTerminator()) consume();
		} else if (accept("/*")) {
			while (!accept("*/")) consume();
		}
	}

	public Token[] tokenize() throws ParseException {
		final List<Token> result = new ArrayList<>();
		boolean lastWasTerminator = true;

		while (!isFinished()) {
			final Token token = next();
			if (token.type == TokenType.Terminator) {
				if (lastWasTerminator) {
					continue;
				} else {
					lastWasTerminator = true;
				}
			} else {
				lastWasTerminator = false;
			}

			result.add(token);
		}

		result.add(new Token(TokenType.EOF, "", length, length));
		return result.toArray(new Token[0]);
	}
}

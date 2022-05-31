package xyz.lebster.core.parser;

import xyz.lebster.core.exception.SyntaxError;
import xyz.lebster.core.node.SourcePosition;

import java.util.*;

public final class Lexer {
	private static final HashMap<String, TokenType> keywords = new HashMap<>();
	private static final List<HashMap<String, TokenType>> symbols = new ArrayList<>();

	static {
		keywords.put("async", TokenType.Async);
		keywords.put("await", TokenType.Await);
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
		keywords.put("enum", TokenType.Enum);
		keywords.put("export", TokenType.Export);
		keywords.put("extends", TokenType.Extends);
		keywords.put("false", TokenType.False);
		keywords.put("finally", TokenType.Finally);
		keywords.put("for", TokenType.For);
		keywords.put("function", TokenType.Function);
		keywords.put("if", TokenType.If);
		keywords.put("import", TokenType.Import);
		keywords.put("in", TokenType.In);
		keywords.put("instanceof", TokenType.Instanceof);
		keywords.put("let", TokenType.Let);
		keywords.put("new", TokenType.New);
		keywords.put("null", TokenType.Null);
		keywords.put("return", TokenType.Return);
		keywords.put("super", TokenType.Super);
		keywords.put("switch", TokenType.Switch);
		keywords.put("this", TokenType.This);
		keywords.put("throw", TokenType.Throw);
		keywords.put("true", TokenType.True);
		keywords.put("try", TokenType.Try);
		keywords.put("typeof", TokenType.Typeof);
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
		symbols_length_3.put("...", TokenType.DotDotDot);

		symbols_length_2.put("||", TokenType.LogicalOr);
		symbols_length_2.put("|=", TokenType.PipeEquals);
		symbols_length_2.put(">>", TokenType.RightShift);
		symbols_length_2.put(">=", TokenType.GreaterThanEqual);
		symbols_length_2.put("=>", TokenType.Arrow);
		symbols_length_2.put("==", TokenType.LooseEqual);
		symbols_length_2.put("<=", TokenType.LessThanEqual);
		symbols_length_2.put("<<", TokenType.LeftShift);
		symbols_length_2.put("+=", TokenType.PlusEquals);
		symbols_length_2.put("++", TokenType.PlusPlus);
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
		symbols_length_2.put("--", TokenType.MinusMinus);

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
		symbols_length_1.put("?", TokenType.QuestionMark);
		symbols_length_1.put(";", TokenType.Semicolon);
		symbols_length_1.put(",", TokenType.Comma);
		symbols_length_1.put("-", TokenType.Minus);
		symbols_length_1.put(":", TokenType.Colon);
		symbols_length_1.put("\\", TokenType.Backslash);
		symbols_length_1.put("@", TokenType.At);
		symbols_length_1.put("#", TokenType.Hashtag);

		symbols.add(symbols_length_4);
		symbols.add(symbols_length_3);
		symbols.add(symbols_length_2);
		symbols.add(symbols_length_1);
	}

	private final String source;
	private final StringBuilder builder = new StringBuilder();
	private final int length;
	private final ArrayDeque<TemplateLiteralState> templateLiteralStates = new ArrayDeque<>();
	private int index = -1;
	private char currentChar = '\0';
	private Token currentToken;

	public Lexer(String source) {
		this.source = source;
		this.length = source.length();
		consume();
		if (accept("#!")) {
			while (!isTerminator()) {
				consume();
			}
		}
	}

	public boolean isFinished() {
		return index > length;
	}

	private boolean isDigit(char c) {
		return c >= '0' && c <= '9';
	}

	private boolean isDigit(char digit, int radix) {
		if (radix < Character.MIN_RADIX || radix > Character.MAX_RADIX) {
			return false;
		}

		if (digit >= '0' && digit <= '9') {
			return digit - '0' < radix;
		} else if (digit >= 'A' && digit <= 'Z') {
			return digit - 'A' + 10 < radix;
		} else if (digit >= 'a' && digit <= 'z') {
			return digit - 'a' + 10 < radix;
		} else {
			return false;
		}
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

	private void consume(int size) {
		index += size;

		if (index >= length) {
			currentChar = '\0';
		} else {
			currentChar = source.charAt(index);
		}

	}

	private void collect() {
		builder.append(consume());
	}

	private void consumeThenAppend(char special) {
		consume();
		builder.append(special);
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

	private boolean accept(char c) {
		final boolean result = currentChar == c;
		if (result) consume();
		return result;
	}

	private boolean isIdentifierStart() {
		if (currentChar == '\\') {
			return false;
		} else if (isAlphabetical(currentChar) || currentChar == '_' || currentChar == '$') {
			return true;
		} else if (currentChar < 0x80) {
			// Optimization: the first codepoint with the ID_Start property after A-Za-z is outside the
			// ASCII range (0x00AA), so we can skip isUnicodeIdentifierStart() for any ASCII characters.
			// (Thanks Serenity!)
			return false;
		} else {
			return Character.isUnicodeIdentifierStart(currentChar);
		}
	}

	private boolean isIdentifierMiddle() {
		if (currentChar == '\\') {
			return false;
		} else if (isAlphabetical(currentChar) || isDigit(currentChar) || currentChar == '_' || currentChar == '$') {
			return true;
		} else if (currentChar < 0x80) {
			// Optimization: the first codepoint with the ID_Continue property after A-Za-z0-9_ is outside the
			// ASCII range (0x00AA), so we can skip isUnicodeIdentifierPart() for any ASCII characters.
			// (Thanks Serenity!)
			return false;
		} else {
			return Character.isUnicodeIdentifierPart(currentChar);
		}
	}

	private boolean slashMeansDivision() {
		return currentToken != null && (
			currentToken.type == TokenType.BigIntLiteral
			|| currentToken.type == TokenType.True
			|| currentToken.type == TokenType.False
			|| currentToken.type == TokenType.RBrace
			|| currentToken.type == TokenType.RBracket
			|| currentToken.type == TokenType.Identifier
			|| currentToken.type == TokenType.In
			|| currentToken.type == TokenType.Instanceof
			|| currentToken.type == TokenType.MinusMinus
			|| currentToken.type == TokenType.Null
			|| currentToken.type == TokenType.NumericLiteral
			|| currentToken.type == TokenType.RParen
			|| currentToken.type == TokenType.PlusPlus
			|| currentToken.type == TokenType.PrivateIdentifier
			|| currentToken.type == TokenType.RegexpLiteral
			|| currentToken.type == TokenType.StringLiteral
			|| currentToken.type == TokenType.TemplateExpressionEnd
			|| currentToken.type == TokenType.This);
	}

	private Token next() throws SyntaxError {
		builder.setLength(0);
		final boolean inTemplateLiteral = !templateLiteralStates.isEmpty();

		if (!inTemplateLiteral || templateLiteralStates.getFirst().inExpression) {
			consumeWhitespace();
			consumeComment();
			consumeWhitespace();
		}

		if (accept('`')) {
			if (!inTemplateLiteral || templateLiteralStates.getFirst().inExpression) {
				templateLiteralStates.push(new TemplateLiteralState());
				return new Token(TokenType.TemplateStart, position());
			} else {
				templateLiteralStates.pop();
				return new Token(TokenType.TemplateEnd, position());
			}
		} else if (
			inTemplateLiteral &&
			templateLiteralStates.getFirst().inExpression &&
			templateLiteralStates.getFirst().bracketCount == 0 &&
			accept('}')
		) {
			templateLiteralStates.getFirst().inExpression = false;
			return new Token(TokenType.TemplateExpressionEnd, position());
		} else if (inTemplateLiteral && !templateLiteralStates.getFirst().inExpression) {
			if (isFinished()) {
				throw new SyntaxError("Unterminated template literal", position());
			} else if (accept("${")) {
				templateLiteralStates.getFirst().inExpression = true;
				return new Token(TokenType.TemplateExpressionStart, position());
			} else {
				boolean escaped = false;

				while (!(isFinished() || peek("${") && !escaped)) {
					if (escaped) {
						escaped = false;
						if (isTerminator()) {
							consumeTerminator();
						} else {
							collectEscapedCharacter();
						}
					} else if (currentChar == '\\') {
						escaped = true;
						consume();
					} else if (currentChar == '`') {
						break;
					} else {
						collect();
					}
				}
				if (isFinished() && !templateLiteralStates.isEmpty()) {
					throw new SyntaxError("Unterminated template literal", position());
				} else {
					return new Token(TokenType.TemplateSpan, builder.toString(), position());
				}
			}
		}

		if (isFinished()) return null;

		if (isTerminator()) {
			while (isTerminator()) consume();
			return new Token(TokenType.LineTerminator, position());
		} else if (isIdentifierStart()) {
			while (isIdentifierMiddle()) collect();
			final String value = builder.toString();
			final TokenType type = keywords.getOrDefault(value, TokenType.Identifier);
			return new Token(type, value, position());
		} else if (currentChar == '"' || currentChar == '\'') {
			final char stringType = currentChar;
			consume();

			boolean escaped = false;
			while (true) {
				if (isFinished())
					throw new SyntaxError("Unterminated string literal", position());

				if (escaped) {
					escaped = false;
					collectEscapedCharacter();
				} else if (currentChar == '\\') {
					escaped = true;
					consume();
				} else if (currentChar == stringType) {
					break;
				} else {
					collect();
				}
			}

			consume();
			return new Token(TokenType.StringLiteral, builder.toString(), position());
		} else if (isDigit(currentChar)) {
			if (accept("0x")) return numericLiteralRadix(16, "Hexadecimal");
			if (accept("0b")) return numericLiteralRadix(2, "Binary");
			if (accept("0o")) return numericLiteralRadix(8, "Octal");

			boolean isInteger = true;
			while (isDigit(currentChar) || (currentChar == '.' && isInteger)) {
				if (currentChar == '.') isInteger = false;
				collect();
			}

			if (acceptCollect('e', 'E')) {
				acceptCollect('-');
				while (isDigit(currentChar)) {
					collect();
				}
			}

			return new Token(TokenType.NumericLiteral, builder.toString(), position());
		} else if (currentChar == '/' && !slashMeansDivision()) {
			return consumeRegexpLiteral();
		} else {
			for (Map<String, TokenType> symbolSize : symbols) {
				for (Map.Entry<String, TokenType> entry : symbolSize.entrySet()) {
					final String key = entry.getKey();
					if (accept(key)) {
						return new Token(entry.getValue(), key, position());
					}
				}
			}

			throw new SyntaxError(StringEscapeUtils.escape("Cannot tokenize character '" + currentChar + "'"), position());
		}
	}

	private boolean acceptCollect(char... characters) {
		for (final char c : characters) {
			if (currentChar == c) {
				collect();
				return true;
			}
		}

		return false;
	}

	private Token consumeRegexpLiteral() {
		collect();

		boolean escaped = false;
		while (!isFinished()) {
			if (escaped) {
				escaped = false;
				collect();
			} else if (currentChar == '\\') {
				escaped = true;
				consume();
			} else if (currentChar == '/') {
				collect();
				break;
			} else {
				collect();
			}
		}

		while (isAlphabetical(currentChar)) {
			collect();
		}

		return new Token(TokenType.RegexpLiteral, builder.toString(), position());
	}

	private Token numericLiteralRadix(int radix, String name) throws SyntaxError {
		long result = 0;
		boolean isEmpty = true;
		while (isDigit(currentChar) || isAlphabetical(currentChar)) {
			if (!isDigit(currentChar, radix)) {
				throw new SyntaxError("Invalid digit '" + currentChar + "' in " + name.toLowerCase() + " numeric literal", position());
			}

			isEmpty = false;
			int digit = Character.digit(consume(), radix);
			result *= radix;
			result -= digit;

		}

		if (isEmpty) throw new SyntaxError(name + " numeric literal requires at least one digit", position());
		return new Token(TokenType.NumericLiteral, Long.toString(-result), position());
	}

	private void consumeTerminator() {
		if (peek("\r\n")) {
			consume(2);
		} else if (currentChar == '\n') {
			consume();
		}
	}

	private void collectEscapedCharacter() throws SyntaxError {
		switch (currentChar) {
			case '0' -> consumeThenAppend('\0');
			case '\'' -> consumeThenAppend('\'');
			case '\\' -> consumeThenAppend('\\');
			case 'b' -> consumeThenAppend('\b');
			case 'f' -> consumeThenAppend('\f');
			case 'n' -> consumeThenAppend('\n');
			case 'r' -> consumeThenAppend('\r');
			case 't' -> consumeThenAppend('\t');
			case 'v' -> consumeThenAppend('\13');
			case 'u' -> {
				consume();
				if (this.currentChar == '{') {
					consume();
					if (this.currentChar == '}')
						throw new SyntaxError("Invalid unicode escape sequence: No digits", position());
					int result = 0;
					final StringBuilder sequence = new StringBuilder();
					for (int i = 0; i < 6 && this.currentChar != '}'; i++) {
						result *= 16;
						final char digit = consumeHexDigit();
						sequence.append(digit);
						result += Character.digit(digit, 16);
					}

					char last = consume();
					sequence.append(last);
					if (last != '}')
						throw new SyntaxError("Invalid Unicode escape sequence '" + sequence + "'", position());
					this.builder.append((char) result);
				} else {
					int result = 0;
					for (int i = 0; i < 4; i++) {
						result *= 16;
						result += Character.digit(consumeHexDigit(), 16);
					}
					this.builder.append((char) result);
				}
			}

			case 'x' -> {
				consume();
				builder.append((char) (
					Character.digit(consumeHexDigit(), 16) * 16 +
					Character.digit(consumeHexDigit(), 16)
				));
			}

			default -> collect();
		}
	}

	private SourcePosition position() {
		return new SourcePosition(source, index - 1);
	}

	private char consumeHexDigit() throws SyntaxError {
		final char c = consume();
		if ((c < 'a' || c > 'f') && (c < 'A' || c > 'F') && (c < '0' || c > '9'))
			throw new SyntaxError("Invalid Unicode escape sequence (invalid character '" + c + "')", position());
		return c;
	}

	private void consumeComment() {
		if (accept("//")) {
			while (!isTerminator()) consume();
		} else if (accept("/*")) {
			while (!isFinished()) {
				if (accept("*/")) break;
				consume();
			}
		}
	}

	public Token[] tokenize() throws SyntaxError {
		final List<Token> result = new ArrayList<>();
		boolean lastWasTerminator = true;

		while (!isFinished()) {
			final Token token = next();
			currentToken = token;
			if (token == null) break;
			if (token.type == TokenType.LineTerminator) {
				if (lastWasTerminator) {
					continue;
				} else {
					lastWasTerminator = true;
				}
			} else {
				lastWasTerminator = false;
			}

			if (!templateLiteralStates.isEmpty() && templateLiteralStates.getFirst().inExpression) {
				if (token.type == TokenType.LBrace) {
					templateLiteralStates.getFirst().bracketCount++;
				} else if (token.type == TokenType.RBrace) {
					templateLiteralStates.getFirst().bracketCount--;
				}
			}

			result.add(token);
		}

		result.add(new Token(TokenType.EOF, position()));
		return result.toArray(new Token[0]);
	}

	private static final class TemplateLiteralState {
		public boolean inExpression = false;
		public int bracketCount = 0;
	}
}
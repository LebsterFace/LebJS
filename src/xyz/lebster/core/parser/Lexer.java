package xyz.lebster.core.parser;

import xyz.lebster.core.StringEscapeUtils;
import xyz.lebster.core.exception.SyntaxError;
import xyz.lebster.core.node.SourcePosition;

import java.math.BigInteger;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
		keywords.put("instanceof", TokenType.InstanceOf);
		keywords.put("let", TokenType.Let);
		keywords.put("new", TokenType.New);
		keywords.put("null", TokenType.Null);
		keywords.put("return", TokenType.Return);
		keywords.put("static", TokenType.Static);
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
		// TODO: get / set keywords

		final HashMap<String, TokenType> symbols_length_4 = new HashMap<>();
		symbols_length_4.put(">>>=", TokenType.UnsignedRightShiftEquals);

		final HashMap<String, TokenType> symbols_length_3 = new HashMap<>();
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

		final HashMap<String, TokenType> symbols_length_2 = new HashMap<>();
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

		final HashMap<String, TokenType> symbols_length_1 = new HashMap<>();
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

		symbols.add(symbols_length_1);
		symbols.add(symbols_length_2);
		symbols.add(symbols_length_3);
		symbols.add(symbols_length_4);
	}

	private final String sourceText;
	private final int[] codePoints;
	private final ArrayDeque<TemplateLiteralState> templateLiteralStates = new ArrayDeque<>();
	private int codePoint;
	private int index = -1;
	private TokenType lastTokenType;

	private Lexer(String sourceText) throws SyntaxError {
		this.codePoints = sourceText.codePoints().toArray();
		this.sourceText = sourceText;
		consume();
		if (accept("#!")) {
			consumeSingleLineComment();
		}
	}

	public static Token[] tokenize(String sourceText) throws SyntaxError {
		final var instance = new Lexer(sourceText);
		final ArrayList<Token> tokens = new ArrayList<>();
		while (instance.hasNext()) {
			final Token next = instance.next();
			if (next == null) {
				break;
			} else {
				instance.lastTokenType = next.type;
				if (!instance.templateLiteralStates.isEmpty() && instance.templateLiteralStates.getFirst().inExpression) {
					if (next.type == TokenType.LBrace) {
						instance.templateLiteralStates.getFirst().bracketCount++;
					} else if (next.type == TokenType.RBrace) {
						instance.templateLiteralStates.getFirst().bracketCount--;
					}
				}

				tokens.add(next);
			}
		}

		tokens.add(new Token(TokenType.EOF, null));
		return tokens.toArray(new Token[0]);
	}

	private static boolean isDigit(int c) {
		return c >= '0' && c <= '9';
	}

	private static boolean isDigit(int ch, int radix) {
		if (radix < Character.MIN_RADIX || radix > Character.MAX_RADIX) {
			return false;
		}

		if (ch >= '0' && ch <= '9') {
			return ch - '0' < radix;
		} else if (ch >= 'A' && ch <= 'Z') {
			return ch - 'A' + 10 < radix;
		} else if (ch >= 'a' && ch <= 'z') {
			return ch - 'a' + 10 < radix;
		} else {
			return false;
		}
	}

	private static boolean isAlphabetical(int c) {
		return (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z');
	}

	public static boolean isIdentifierStart(int codePoint) {
		if (codePoint == '\\') {
			return false;
		} else if (isAlphabetical(codePoint) || codePoint == '_' || codePoint == '$') {
			return true;
		} else if (codePoint < 0x80) {
			// Optimization: the first codepoint with the ID_Start property after A-Za-z is outside the
			// ASCII range (0x00AA), so we can skip isUnicodeIdentifierStart() for any ASCII characters.
			// (Thanks Serenity!)
			return false;
		} else {
			return Character.isUnicodeIdentifierStart(codePoint);
		}
	}

	public static boolean isIdentifierMiddle(int codePoint) {
		if (codePoint == '\\') {
			return false;
		} else if (isAlphabetical(codePoint) || isDigit(codePoint) || codePoint == '_' || codePoint == '$') {
			return true;
		} else if (codePoint < 0x80) {
			// Optimization: the first codepoint with the ID_Continue property after A-Za-z0-9_ is outside the
			// ASCII range (0x00AA), so we can skip isUnicodeIdentifierPart() for any ASCII characters.
			// (Thanks Serenity!)
			return false;
		} else {
			return Character.isUnicodeIdentifierPart(codePoint);
		}
	}

	private boolean isLineTerminator() {
		return codePoint == '\n' ||
			   codePoint == '\r' ||
			   codePoint == '\u2028' ||
			   codePoint == '\u2029';
	}

	private boolean isLineTerminatorSequence() {
		return codePoint == '\n' ||
			   codePoint == '\u2028' ||
			   codePoint == '\u2029' ||
			   peek("\r\n") ||
			   codePoint == '\r';
	}

	private int consume() throws SyntaxError {
		if (!hasNext()) throw new SyntaxError("Unexpected end of input", position());
		final int old = codePoint;
		consume(1);
		return old;
	}

	private void consume(int count) {
		index += count;
		codePoint = !hasNext() ? -1 : codePoints[index];
	}

	private void collect(StringBuilder builder) throws SyntaxError {
		builder.appendCodePoint(consume());
	}

	private void consumeWhitespace() throws SyntaxError {
		while (codePoint == '\t'      // Tab
			   || codePoint == '\013' // Vertical tab
			   || codePoint == '\014' // Form feed
			   || codePoint == ' '    // Space
		) {
			consume();
		}
	}

	private String next(int length) {
		return sourceText.substring(sourceText.offsetByCodePoints(0, index), sourceText.offsetByCodePoints(0, Integer.min(index + length, this.codePoints.length)));
	}

	private boolean peek(String compare) {
		return sourceText.startsWith(compare, sourceText.offsetByCodePoints(0, index));
	}

	private boolean accept(String s) {
		final boolean result = peek(s);
		if (result) consume(s.codePointCount(0, s.length()));
		return result;
	}

	private boolean anyOf(int[] codePoints) {
		for (final int i : codePoints)
			if (codePoint == i)
				return true;

		return false;
	}

	private boolean anyOf(String codePoints) {
		return anyOf(codePoints.codePoints().toArray());
	}

	private boolean consumeAnyOf(String codePoints) throws SyntaxError {
		final boolean result = anyOf(codePoints.codePoints().toArray());
		if (result) consume();
		return result;
	}

	private boolean slashMeansDivision() {
		return lastTokenType != null && (lastTokenType == TokenType.BigIntLiteral
										 || lastTokenType == TokenType.True
										 || lastTokenType == TokenType.False
										 || lastTokenType == TokenType.RBrace
										 || lastTokenType == TokenType.RBracket
										 || lastTokenType == TokenType.Identifier
										 || lastTokenType == TokenType.In
										 || lastTokenType == TokenType.InstanceOf
										 || lastTokenType == TokenType.MinusMinus
										 || lastTokenType == TokenType.Null
										 || lastTokenType == TokenType.NumericLiteral
										 || lastTokenType == TokenType.RParen
										 || lastTokenType == TokenType.PlusPlus
										 || lastTokenType == TokenType.PrivateIdentifier
										 || lastTokenType == TokenType.RegexpPattern
										 || lastTokenType == TokenType.StringLiteral
										 || lastTokenType == TokenType.TemplateExpressionEnd
										 || lastTokenType == TokenType.This);
	}

	public Token next() throws SyntaxError {
		if (lastTokenType == TokenType.RegexpPattern) {
			final StringBuilder regexpFlags = new StringBuilder();
			while (anyOf("dgimsuy")) {
				collect(regexpFlags);
			}

			return new Token(TokenType.RegexpFlags, regexpFlags.toString(), position());
		}

		final boolean inTemplateLiteral = !templateLiteralStates.isEmpty();

		if (!inTemplateLiteral || templateLiteralStates.getFirst().inExpression) {
			consumeWhitespace();
			consumeComment();
			consumeWhitespace();
		}

		if (accept("`")) {
			return tokenizeTemplateLiteralStart(inTemplateLiteral);
		}

		if (inTemplateLiteral && currentTemplateLiteralIsEnding()) {
			return tokenizeTemplateLiteralEnd();
		}

		if (inTemplateLiteral && !templateLiteralStates.getFirst().inExpression) {
			if (!hasNext()) {
				throw new SyntaxError("Unterminated template literal", position());
			}

			if (accept("${")) {
				templateLiteralStates.getFirst().inExpression = true;
				return new Token(TokenType.TemplateExpressionStart, position());
			}

			return tokenizeTemplateLiteralSpan();
		}

		if (!hasNext()) {
			return null;
		}

		if (isLineTerminator()) {
			consumeLineTerminators();
			return new Token(TokenType.LineTerminator, position());
		}

		if (isIdentifierStart(codePoint)) {
			return tokenizeKeywordOrIdentifier();
		}

		if (isDigit(codePoint)) {
			return tokenizeNumericLiteral();
		}

		if (codePoint == '"' || codePoint == '\'') {
			return tokenizeStringLiteral();
		}

		if (codePoint == '/' && !slashMeansDivision()) {
			return consumeRegexpLiteral();
		}

		return tokenizeSymbol();
	}

	private void consumeLineTerminators() throws SyntaxError {
		while (isLineTerminator()) {
			consume();
		}
	}

	private boolean handleNumericSeparator(boolean lastWasNumericSeparator) throws SyntaxError {
		if (codePoint == '_') {
			if (lastWasNumericSeparator) {
				throw new SyntaxError("Only one underscore is allowed as numeric separator", position());
			}

			consume();
			return true;
		} else {
			return false;
		}
	}

	private Token consumeRegexpLiteral() throws SyntaxError {
		final StringBuilder builder = new StringBuilder();
		consume();

		boolean inCharacterClass = false;
		boolean escaped = false;
		while (hasNext()) {
			if (escaped) {
				escaped = false;
				collect(builder);
			} else if (inCharacterClass) {
				if (codePoint == ']') {
					inCharacterClass = false;
				}

				collect(builder);
			} else if (codePoint == '[') {
				inCharacterClass = true;
				collect(builder);
			} else if (codePoint == '\\') {
				escaped = true;
				collect(builder);
			} else if (codePoint == '/') {
				consume();
				break;
			} else {
				collect(builder);
			}
		}

		return new Token(TokenType.RegexpPattern, builder.toString(), position());
	}

	private boolean hasNext() {
		return index < codePoints.length;
	}

	private void consumeComment() throws SyntaxError {
		if (accept("//")) {
			consumeSingleLineComment();
			return;
		}

		if (accept("/*")) {
			while (hasNext()) {
				if (accept("*/")) break;
				consume();
			}
		}
	}

	private void consumeSingleLineComment() throws SyntaxError {
		while (!isLineTerminator() && hasNext()) {
			consume();
		}
	}

	private Token tokenizeTemplateLiteralSpan() throws SyntaxError {
		final StringBuilder builder = new StringBuilder();
		boolean escaped = false;
		while (hasNext() && (escaped || !peek("${"))) {
			if (escaped) {
				escaped = false;
				if (isLineTerminator()) {
					consumeLineTerminators();
				} else {
					builder.appendCodePoint(readEscapedCharacter());
				}
			} else if (codePoint == '\\') {
				escaped = true;
				consume();
			} else if (codePoint == '`') {
				break;
			} else {
				collect(builder);
			}
		}

		if (!hasNext() && !templateLiteralStates.isEmpty()) {
			throw new SyntaxError("Unterminated template literal", position());
		}

		return new Token(TokenType.TemplateSpan, builder.toString(), position());
	}

	private boolean currentTemplateLiteralIsEnding() {
		return templateLiteralStates.getFirst().inExpression && templateLiteralStates.getFirst().bracketCount == 0 && accept("}");
	}

	private Token tokenizeTemplateLiteralEnd() {
		templateLiteralStates.getFirst().inExpression = false;
		return new Token(TokenType.TemplateExpressionEnd, position());
	}

	private Token tokenizeTemplateLiteralStart(boolean inTemplateLiteral) {
		if (!inTemplateLiteral || templateLiteralStates.getFirst().inExpression) {
			templateLiteralStates.push(new TemplateLiteralState());
			return new Token(TokenType.TemplateStart, position());
		} else {
			templateLiteralStates.pop();
			return new Token(TokenType.TemplateEnd, position());
		}
	}

	private Token tokenizeStringLiteral() throws SyntaxError {
		final StringBuilder builder = new StringBuilder();
		final int stringType = codePoint;
		consume();

		boolean escaped = false;
		while (true) {
			if (!hasNext()) throw new SyntaxError("Unterminated string literal", position());

			if (escaped) {
				escaped = false;
				if (isLineTerminatorSequence()) {
					// Line continuation
					consumeLineContinuation();
				} else {
					builder.appendCodePoint(readEscapedCharacter());
				}

				continue;
			}

			// All code points may appear literally in a string literal except for
			// the closing quote code points
			if (codePoint == stringType) break;

			// U+005C (REVERSE SOLIDUS)
			if (codePoint == '\\') {
				escaped = true;
				consume();
				continue;
			}

			// U+000D (CARRIAGE RETURN)
			// and U+000A (LINE FEED)
			if (codePoint == '\r' || codePoint == '\n') {
				throw new SyntaxError("Unterminated string literal", position());
			}

			collect(builder);
		}

		consume();
		return new Token(TokenType.StringLiteral, builder.toString(), position());
	}

	private void consumeLineContinuation() throws SyntaxError {
		if (!consumeAnyOf("\n\u2028\u2029") && !accept("\r\n")) {
			if (codePoint == '\r') {
				consume();
			} else {
				throw new SyntaxError("Expecting to see LineTerminatorSequence after escape", position());
			}
		}
	}

	private int readEscapedCharacter() throws SyntaxError {
		final int initialConsumed = consume();
		return switch (initialConsumed) {
			case '0' -> '\0';
			case '\'' -> '\'';
			case '\\' -> '\\';
			case 'b' -> '\b';
			case 'f' -> '\f';
			case 'n' -> '\n';
			case 'r' -> '\r';
			case 't' -> '\t';
			case 'v' -> '\13';
			case 'u' -> codePoint == '{' ? readUnicodeEscape() : readUnicodeBMPEscape();
			case 'x' -> readHexEscape();
			default -> initialConsumed;
		};
	}

	private int readHexEscape() throws SyntaxError {
		return Character.digit(consumeHexDigit(), 16) * 16 + Character.digit(consumeHexDigit(), 16);
	}

	private int readUnicodeEscape() throws SyntaxError {
		consume(); // Consume '{'
		if (codePoint == '}') throw new SyntaxError("Invalid unicode escape sequence: No digits", position());

		int result = 0;
		final StringBuilder sequence = new StringBuilder();
		for (int i = 0; i < 6 && codePoint != '}'; i++) {
			result *= 16;
			final char digit = consumeHexDigit();
			sequence.append(digit);
			result += Character.digit(digit, 16);
		}

		final int last = consume();
		sequence.appendCodePoint(last);

		if (last != '}') {
			final String quoted = StringEscapeUtils.quote(sequence.toString(), false);
			throw new SyntaxError("Invalid Unicode escape sequence %s (missing ending '}')".formatted(quoted), position());
		}

		return result;
	}

	private int readUnicodeBMPEscape() throws SyntaxError {
		int result = 0;
		for (int i = 0; i < 4; i++) {
			result *= 16;
			result += Character.digit(consumeHexDigit(), 16);
		}
		return result;
	}

	private char consumeHexDigit() throws SyntaxError {
		final int c = consume();
		if ((c < 'a' || c > 'f') && (c < 'A' || c > 'F') && (c < '0' || c > '9')) {
			throw new SyntaxError("Invalid Unicode escape sequence (invalid character %s)".formatted(quoteCodePoint(c)), position());
		}

		return (char) c;
	}

	private Token tokenizeNumericLiteral() throws SyntaxError {
		if (accept("0x")) return numericLiteralRadix(16, "Hexadecimal");
		if (accept("0b")) return numericLiteralRadix(2, "Binary");
		if (accept("0o")) return numericLiteralRadix(8, "Octal");

		final StringBuilder builder = new StringBuilder();
		boolean isInteger = true;
		boolean lastWasNumericSeparator = false;
		while (isDigit(codePoint) || (codePoint == '.' && isInteger) || codePoint == '_') {
			if (codePoint == '.') isInteger = false;
			lastWasNumericSeparator = handleNumericSeparator(lastWasNumericSeparator);
			if (!lastWasNumericSeparator) collect(builder);
		}

		final boolean hasExponent = codePoint == 'e' || codePoint == 'E';
		if (hasExponent) {
			collect(builder);
			if (codePoint == '-') collect(builder);

			lastWasNumericSeparator = false;
			while (codePoint == '_' || isDigit(codePoint)) {
				lastWasNumericSeparator = handleNumericSeparator(lastWasNumericSeparator);
				if (!lastWasNumericSeparator) collect(builder);
			}
		}

		if (isInteger && !hasExponent) return potentialBigInt(builder.toString(), 10);
		return new Token(TokenType.NumericLiteral, builder.toString(), position());
	}

	private Token numericLiteralRadix(int radix, String name) throws SyntaxError {
		final StringBuilder builder = new StringBuilder();
		boolean isEmpty = true;
		boolean lastWasNumericSeparator = false;
		while (codePoint == '_' || isDigit(codePoint) || isAlphabetical(codePoint)) {
			lastWasNumericSeparator = handleNumericSeparator(lastWasNumericSeparator);
			if (!lastWasNumericSeparator) {
				if (!isDigit(codePoint, radix)) {
					throw new SyntaxError("Invalid digit %s in %s numeric literal".formatted(quoteCodePoint(codePoint), name.toLowerCase()), position());
				}

				isEmpty = false;
				collect(builder);
			}
		}

		if (isEmpty) throw new SyntaxError(name + " numeric literal requires at least one digit", position());
		return potentialBigInt(builder.toString(), radix);
	}

	private String quoteCodePoint(int ch) {
		return StringEscapeUtils.quote(codePoint == -1 ? "[-1]" : Character.toString(ch), false);
	}

	private Token potentialBigInt(String integerString, int radix) {
		final TokenType type = accept("n") ? TokenType.BigIntLiteral : TokenType.NumericLiteral;
		return new Token(type, new BigInteger(integerString, radix).toString(), position());
	}

	private Token tokenizeKeywordOrIdentifier() throws SyntaxError {
		final var builder = new StringBuilder();
		while (isIdentifierMiddle(codePoint)) collect(builder);
		final String value = builder.toString();
		final TokenType type = keywords.getOrDefault(value, TokenType.Identifier);
		return new Token(type, value, position());
	}

	private Token tokenizeSymbol() throws SyntaxError {
		for (int i = 4; i >= 1; i--) {
			final HashMap<String, TokenType> symbolSize = symbols.get(i - 1);
			final String key = next(i);
			final TokenType value = symbolSize.get(key);
			if (value != null) {
				consume(i);
				return new Token(value, key, position());
			}
		}

		throw new SyntaxError("Cannot tokenize character %s".formatted(quoteCodePoint(codePoint)), position());
	}

	private SourcePosition position() {
		return new SourcePosition(sourceText, index - 1);
	}

	private static final class TemplateLiteralState {

		public boolean inExpression = false;
		public int bracketCount = 0;
	}
}
package xyz.lebster.core.parser;

import xyz.lebster.core.StringEscapeUtils;
import xyz.lebster.core.exception.ShouldNotHappen;
import xyz.lebster.core.exception.SyntaxError;
import xyz.lebster.core.node.SourcePosition;
import xyz.lebster.core.node.SourceRange;

import java.math.BigInteger;
import java.util.*;

import static xyz.lebster.core.parser.TokenType.*;

public final class Lexer {
	private static final HashMap<String, TokenType> keywords = new HashMap<>();
	private static final List<HashMap<String, TokenType>> symbols = new ArrayList<>();

	static {
		keywords.put("async", Async);
		keywords.put("await", Await);
		keywords.put("break", Break);
		keywords.put("case", Case);
		keywords.put("catch", Catch);
		keywords.put("class", Class);
		keywords.put("const", Const);
		keywords.put("continue", Continue);
		keywords.put("debugger", Debugger);
		keywords.put("default", Default);
		keywords.put("delete", Delete);
		keywords.put("do", Do);
		keywords.put("else", Else);
		keywords.put("enum", Enum);
		keywords.put("export", Export);
		keywords.put("extends", Extends);
		keywords.put("false", False);
		keywords.put("finally", Finally);
		keywords.put("for", For);
		keywords.put("function", Function);
		keywords.put("if", If);
		keywords.put("import", Import);
		keywords.put("in", In);
		keywords.put("instanceof", InstanceOf);
		keywords.put("let", Let);
		keywords.put("new", New);
		keywords.put("null", NullLiteral);
		keywords.put("return", Return);
		keywords.put("static", Static);
		keywords.put("super", Super);
		keywords.put("switch", Switch);
		keywords.put("this", This);
		keywords.put("throw", Throw);
		keywords.put("true", True);
		keywords.put("try", Try);
		keywords.put("typeof", Typeof);
		keywords.put("var", Var);
		keywords.put("void", Void);
		keywords.put("while", While);
		keywords.put("yield", Yield);
		// TODO: get / set keywords

		final HashMap<String, TokenType> symbols_length_4 = new HashMap<>();
		symbols_length_4.put(">>>=", UnsignedRightShiftEquals);

		final HashMap<String, TokenType> symbols_length_3 = new HashMap<>();
		symbols_length_3.put("||=", LogicalOrEquals);
		symbols_length_3.put(">>>", UnsignedRightShift);
		symbols_length_3.put(">>=", RightShiftEquals);
		symbols_length_3.put("===", StrictEqual);
		symbols_length_3.put("<<=", LeftShiftEquals);
		symbols_length_3.put("&&=", LogicalAndEquals);
		symbols_length_3.put("**=", ExponentEquals);
		symbols_length_3.put("??=", NullishCoalescingEquals);
		symbols_length_3.put("!==", StrictNotEqual);
		symbols_length_3.put("...", DotDotDot);

		final HashMap<String, TokenType> symbols_length_2 = new HashMap<>();
		symbols_length_2.put("||", LogicalOr);
		symbols_length_2.put("|=", PipeEquals);
		symbols_length_2.put(">>", RightShift);
		symbols_length_2.put(">=", GreaterThanEqual);
		symbols_length_2.put("=>", Arrow);
		symbols_length_2.put("==", LooseEqual);
		symbols_length_2.put("<=", LessThanEqual);
		symbols_length_2.put("<<", LeftShift);
		symbols_length_2.put("+=", PlusEquals);
		symbols_length_2.put("++", PlusPlus);
		symbols_length_2.put("^=", CaretEquals);
		symbols_length_2.put("%=", PercentEquals);
		symbols_length_2.put("&=", AmpersandEquals);
		symbols_length_2.put("&&", LogicalAnd);
		symbols_length_2.put("/=", DivideEquals);
		symbols_length_2.put("*=", MultiplyEquals);
		symbols_length_2.put("**", Exponent);
		symbols_length_2.put("?.", OptionalChain);
		symbols_length_2.put("??", NullishCoalescing);
		symbols_length_2.put("!=", NotEqual);
		symbols_length_2.put("-=", MinusEquals);
		symbols_length_2.put("--", MinusMinus);

		final HashMap<String, TokenType> symbols_length_1 = new HashMap<>();
		symbols_length_1.put("~", Tilde);
		symbols_length_1.put("|", Pipe);
		symbols_length_1.put(">", GreaterThan);
		symbols_length_1.put("=", Equals);
		symbols_length_1.put("<", LessThan);
		symbols_length_1.put("+", Plus);
		symbols_length_1.put("^", Caret);
		symbols_length_1.put("%", Percent);
		symbols_length_1.put("&", Ampersand);
		symbols_length_1.put("/", Slash);
		symbols_length_1.put("*", Star);
		symbols_length_1.put("}", RBrace);
		symbols_length_1.put("{", LBrace);
		symbols_length_1.put("]", RBracket);
		symbols_length_1.put("[", LBracket);
		symbols_length_1.put(")", RParen);
		symbols_length_1.put("(", LParen);
		symbols_length_1.put(".", Period);
		symbols_length_1.put("!", Bang);
		symbols_length_1.put("?", QuestionMark);
		symbols_length_1.put(";", Semicolon);
		symbols_length_1.put(",", Comma);
		symbols_length_1.put("-", Minus);
		symbols_length_1.put(":", Colon);
		symbols_length_1.put("\\", Backslash);
		symbols_length_1.put("@", At);
		symbols_length_1.put("#", Hashtag);

		symbols.add(symbols_length_1);
		symbols.add(symbols_length_2);
		symbols.add(symbols_length_3);
		symbols.add(symbols_length_4);
	}

	public static String valueForSymbol(TokenType type) {
		for (final var map : symbols) {
			for (final var entry : map.entrySet()) {
				if (Objects.equals(type, entry.getValue())) {
					return entry.getKey();
				}
			}
		}

		throw new ShouldNotHappen("TokenType %s has no corresponding value.".formatted(type));
	}

	private final String sourceText;
	private final int[] codePoints;
	private final ArrayDeque<TemplateLiteralState> templateLiteralStates = new ArrayDeque<>();
	private final int codePointCount;
	private int index = 0;
	private TokenType lastTokenType;

	private Lexer(String sourceText) throws SyntaxError {
		final int[] unpaddedCodePoints = sourceText.codePoints().toArray();
		this.codePointCount = unpaddedCodePoints.length;
		this.codePoints = Arrays.copyOf(unpaddedCodePoints, codePointCount + 3); // Padding for branchless lookahead
		this.sourceText = sourceText;
		if (accept("#!")) {
			consumeSingleLineComment();
		}
	}

	public static Token[] tokenize(String sourceText) throws SyntaxError {
		final var instance = new Lexer(sourceText);
		final ArrayList<Token> tokens = new ArrayList<>();
		while (instance.inBounds()) {
			final Token next = instance.next();
			if (next == null) break;
			instance.lastTokenType = next.type();
			if (!instance.templateLiteralStates.isEmpty() && instance.templateLiteralStates.getFirst().inExpression) {
				if (next.type() == LBrace) {
					instance.templateLiteralStates.getFirst().bracketCount++;
				} else if (next.type() == RBrace) {
					instance.templateLiteralStates.getFirst().bracketCount--;
				}
			}

			tokens.add(next);
		}

		tokens.add(instance.eof());
		return tokens.toArray(new Token[0]);
	}

	private Token eof() {
		if (inBounds()) throw new ShouldNotHappen("Creating EOF token before consuming all codepoints");
		return new Token(range(index), EOF);
	}

	private static boolean isDecimalDigit(int codepoint) {
		return codepoint >= '0' && codepoint <= '9';
	}

	public static boolean isDigit(int codepoint, int radix) {
		if (radix < Character.MIN_RADIX || radix > Character.MAX_RADIX) {
			return false;
		}

		if (codepoint >= '0' && codepoint <= '9') {
			return codepoint - '0' < radix;
		} else if (codepoint >= 'A' && codepoint <= 'Z') {
			return codepoint - 'A' + 10 < radix;
		} else if (codepoint >= 'a' && codepoint <= 'z') {
			return codepoint - 'a' + 10 < radix;
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
			return false;
		} else {
			return Character.isUnicodeIdentifierStart(codePoint);
		}
	}

	public static boolean isIdentifierPart(int codePoint) {
		if (codePoint == '\\') {
			return false;
		} else if (isAlphabetical(codePoint) || isDecimalDigit(codePoint) || codePoint == '_' || codePoint == '$') {
			return true;
		} else if (codePoint < 0x80) {
			// Optimization: the first codepoint with the ID_Continue property after A-Za-z0-9_ is outside the
			// ASCII range (0x00AA), so we can skip isUnicodeIdentifierPart() for any ASCII characters.
			return false;
		} else {
			return Character.isUnicodeIdentifierPart(codePoint);
		}
	}

	private boolean isIdentifierPart() {
		return inBounds() && isIdentifierPart(codePoints[index]);
	}

	private boolean is(int c) {
		return inBounds() && codePoints[index] == c;
	}

	private boolean is(String compare) {
		return sourceText.startsWith(compare, sourceText.offsetByCodePoints(0, index));
	}

	private boolean isLineTerminator() {
		if (!inBounds()) return false;
		final int c = codePoints[index];
		return c == '\n' || c == '\r' || c == '\u2028' || c == '\u2029';
	}

	private boolean isLineTerminatorSequence() {
		if (!inBounds()) return false;
		final int c = codePoints[index];
		return c == '\r' || c == '\n' || c == '\u2028' || c == '\u2029';
	}

	private int consume() throws SyntaxError {
		if (!inBounds()) throw new SyntaxError("Unexpected end of input", position());
		return codePoints[index++];
	}

	private void acceptWhitespace() {
		while (inBounds() && (
			codePoints[index] == '\t'      // Tab
			|| codePoints[index] == '\013' // Vertical tab
			|| codePoints[index] == '\014' // Form feed
			|| codePoints[index] == ' '    // Space
		)) {
			index++;
		}
	}

	private int peekNext() throws SyntaxError {
		if (index + 1 >= codePointCount) throw new SyntaxError("Unexpected end of input", position());
		return codePoints[index + 1];
	}

	private boolean accept(String s) {
		final boolean result = is(s);
		if (result) index += s.codePointCount(0, s.length());
		return result;
	}

	private boolean slashMeansDivision() {
		final boolean isAcceptedType =
			lastTokenType == BigIntLiteral
			|| lastTokenType == True
			|| lastTokenType == False
			|| lastTokenType == RBrace
			|| lastTokenType == RBracket
			|| lastTokenType == Identifier
			|| lastTokenType == In
			|| lastTokenType == InstanceOf
			|| lastTokenType == MinusMinus
			|| lastTokenType == NullLiteral
			|| lastTokenType == NumericLiteral
			|| lastTokenType == RParen
			|| lastTokenType == PlusPlus
			|| lastTokenType == PrivateIdentifier
			|| lastTokenType == RegexpPattern
			|| lastTokenType == StringLiteral
			|| lastTokenType == TemplateExpressionEnd
			|| lastTokenType == This;
		return lastTokenType != null && isAcceptedType;
	}

	private static final int[] REGEXP_FLAGS = new int[] { 'd', 'g', 'i', 'm', 's', 'u', 'v', 'y' };

	public Token next() throws SyntaxError {
		if (lastTokenType == RegexpPattern) {
			final Set<Integer> flags = new HashSet<>();
			final int startIndex = index;
			while (isIdentifierPart()) {
				if (flags.contains(codePoints[index]))
					throw new SyntaxError("Duplicate flag %s in regular expression literal".formatted(quoteCodePoint(codePoints[index])), position());
				if (Arrays.binarySearch(REGEXP_FLAGS, codePoints[index]) < 0)
					throw new SyntaxError("Invalid regular expression flag %s".formatted(quoteCodePoint(codePoints[index])), position());
				flags.add(consume());
			}

			return new Token(range(startIndex), RegexpFlags);
		} else if (lastTokenType == NumericLiteral && isIdentifierStart(codePoints[index])) {
			throw new SyntaxError("Identifier starts immediately after numeric literal", position());
		}

		if (notInTemplateSpan()) {
			acceptWhitespace();
			consumeComment();
			acceptWhitespace();
		}

		final int startIndex = index;
		if (accept("`")) {
			if (notInTemplateSpan()) {
				templateLiteralStates.push(new TemplateLiteralState());
				return new Token(range(startIndex), TemplateStart);
			} else {
				templateLiteralStates.pop();
				return new Token(range(startIndex), TemplateEnd);
			}
		}

		if (inTemplateLiteral() && currentTemplateLiteralIsEnding()) {
			return tokenizeTemplateLiteralEnd(startIndex);
		}

		if (inTemplateLiteral() && !templateLiteralStates.getFirst().inExpression) {
			if (!inBounds()) {
				throw new SyntaxError("Unterminated template literal", position());
			}

			if (accept("${")) {
				templateLiteralStates.getFirst().inExpression = true;
				return new Token(range(startIndex), TemplateExpressionStart);
			} else {
				return tokenizeTemplateLiteralSpan(startIndex);
			}
		}

		if (!inBounds()) {
			return null;
		} else if (isLineTerminator()) {
			consumeLineTerminators();
			return new Token(range(startIndex), LineTerminator);
		} else if (isIdentifierStart(codePoints[index])) {
			return tokenizeKeywordOrIdentifier(startIndex);
		} else if (isDecimalDigit(codePoints[index]) || (is('.') && isDecimalDigit(peekNext()))) {
			return tokenizeNumericLiteral(startIndex);
		} else if (is('"') || is('\'')) {
			return tokenizeStringLiteral(startIndex);
		} else if (is('/') && !slashMeansDivision()) {
			return tokenizeRegexpLiteral(startIndex);
		} else {
			return tokenizeSymbol(startIndex);
		}
	}

	private boolean notInTemplateSpan() {
		return !inTemplateLiteral() || templateLiteralStates.getFirst().inExpression;
	}

	private boolean inTemplateLiteral() {
		return !templateLiteralStates.isEmpty();
	}

	private void consumeLineTerminators() throws SyntaxError {
		while (isLineTerminator()) {
			consume();
		}
	}

	private Token tokenizeRegexpLiteral(int startIndex) throws SyntaxError {
		final StringBuilder builder = new StringBuilder();
		consume();

		boolean isClosed = false;
		boolean escaped = false;
		int characterClassStart = -1;
		while (inBounds()) {
			if (isLineTerminator()) {
				throw new SyntaxError("Regular expression literal may not contain line terminator", position());
			}

			if (escaped) {
				escaped = false;
				collect(builder);
				continue;
			}

			if (characterClassStart != -1) {
				if (is(']')) characterClassStart = -1;
				collect(builder);
				continue;
			}

			if (is('[')) {
				characterClassStart = index;
				collect(builder);
				continue;
			}

			if (codePoints[index] == '\\') {
				escaped = true;
				collect(builder);
				continue;
			}

			if (is('/')) {
				isClosed = true;
				consume();
				break;
			}

			collect(builder);
		}

		if (characterClassStart != -1)
			throw new SyntaxError("Unclosed character class", position(characterClassStart));

		if (!isClosed)
			throw new SyntaxError("Unterminated regular expression literal", position(startIndex));

		return new Token(range(startIndex), RegexpPattern, builder.toString());
	}

	private void collect(StringBuilder builder) throws SyntaxError {
		builder.appendCodePoint(consume());
	}

	private boolean inBounds() {
		return index < codePointCount;
	}

	private void consumeComment() throws SyntaxError {
		if (accept("//")) {
			consumeSingleLineComment();
			return;
		}

		if (accept("/*")) {
			while (inBounds()) {
				if (accept("*/")) break;
				consume();
			}
		}
	}

	private void consumeSingleLineComment() throws SyntaxError {
		while (inBounds() && !isLineTerminator()) {
			consume();
		}
	}

	private Token tokenizeTemplateLiteralSpan(int startIndex) throws SyntaxError {
		final StringBuilder builder = new StringBuilder();
		boolean escaped = false;
		while (inBounds() && (escaped || !is("${"))) {
			if (escaped) {
				escaped = false;
				if (isLineTerminator()) {
					consumeLineTerminators();
				} else {
					builder.appendCodePoint(readEscapedCharacter());
				}
			} else if (codePoints[index] == '\\') {
				escaped = true;
				consume();
			} else if (is('`')) {
				break;
			} else {
				collect(builder);
			}
		}

		if (!inBounds() && inTemplateLiteral()) {
			throw new SyntaxError("Unterminated template literal", position());
		}

		return new Token(range(startIndex), TemplateSpan, builder.toString());
	}

	private boolean currentTemplateLiteralIsEnding() {
		return templateLiteralStates.getFirst().inExpression && templateLiteralStates.getFirst().bracketCount == 0 && accept("}");
	}

	private Token tokenizeTemplateLiteralEnd(int startIndex) {
		templateLiteralStates.getFirst().inExpression = false;
		return new Token(range(startIndex), TemplateExpressionEnd);
	}

	private Token tokenizeStringLiteral(int startIndex) throws SyntaxError {
		final StringBuilder builder = new StringBuilder();
		final int stringType = codePoints[index];
		consume();

		boolean escaped = false;
		while (true) {
			if (!inBounds()) throw new SyntaxError("Unterminated string literal", position());

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
			if (codePoints[index] == stringType) break;

			// U+005C (REVERSE SOLIDUS)
			if (codePoints[index] == '\\') {
				escaped = true;
				consume();
				continue;
			}

			// U+000D (CARRIAGE RETURN)
			// and U+000A (LINE FEED)
			if (codePoints[index] == '\r' || codePoints[index] == '\n') {
				throw new SyntaxError("Unterminated string literal", position());
			}

			collect(builder);
		}

		consume();
		return new Token(range(startIndex), StringLiteral, builder.toString());
	}

	private void consumeLineContinuation() throws SyntaxError {
		if (accept("\n") || accept("\u2028") || accept("\u2029") || accept("\r\n")) return;

		if (codePoints[index] == '\r') {
			consume();
		} else {
			throw new SyntaxError("Expecting to see LineTerminatorSequence after escape", position());
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
			case 'u' -> is('{') ? readUnicodeEscape() : readUnicodeBMPEscape();
			case 'x' -> readHexEscape();
			default -> initialConsumed;
		};
	}

	private int readHexEscape() throws SyntaxError {
		return Character.digit(consumeHexDigit(), 16) * 16 + Character.digit(consumeHexDigit(), 16);
	}

	private int readUnicodeEscape() throws SyntaxError {
		consume(); // Consume '{'
		if (is('}')) throw new SyntaxError("Invalid unicode escape sequence: No digits", position());

		int result = 0;
		final StringBuilder sequence = new StringBuilder();
		for (int i = 0; i < 6 && codePoints[index] != '}'; i++) {
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

	private Token tokenizeNumericLiteral(int startIndex) throws SyntaxError {
		final int radix;
		if (accept("0x") || accept("0X")) radix = 16;
		else if (accept("0b") || accept("0B")) radix = 2;
		else if (accept("0o") || accept("0O")) radix = 8;
		else radix = 10;

		final StringBuilder builder = new StringBuilder();
		if (is('_')) throw new SyntaxError("Numeric separators are not allowed at the start of numeric literals", position());
		collectIntegerDigits(builder, radix);

		if (radix != 10) {
			if (builder.isEmpty()) {
				if (radix == 16) throw new SyntaxError("Missing hexadecimal digits after '0x'", position());
				else if (radix == 2) throw new SyntaxError("Missing binary digits after '0b'", position());
				else throw new SyntaxError("Missing octal digits after '0o'", position());
			}
		} else {
			if (builder.length() > 1 && builder.charAt(0) == '0') {
				throw new SyntaxError("Unexpected leading zero", position());
			}

			// Optional: decimal part
			final boolean isDecimal = is('.');
			if (isDecimal) {
				collect(builder);
				if (is('_')) throw new SyntaxError("Numeric separators are not allowed immediately after '.'", position());
				collectIntegerDigits(builder, 10);
			}

			// Optional: exponent
			final boolean hasExponent = inBounds() && (is('e') || is('E'));
			if (hasExponent) {
				collect(builder);
				if (is('-') || is('+')) collect(builder);
				if (is('_')) throw new SyntaxError("Numeric separators are not allowed immediately after 'e'", position());
				if (!collectIntegerDigits(builder, 10)) {
					throw new SyntaxError("Missing exponent", position());
				}
			}

			if (isDecimal || hasExponent) {
				return new Token(range(startIndex), NumericLiteral, builder.toString());
			}
		}

		final TokenType type = accept("n") ? BigIntLiteral : NumericLiteral;
		return new Token(range(startIndex), type, new BigInteger(builder.toString(), radix).toString());
	}

	private boolean collectIntegerDigits(StringBuilder builder, int radix) throws SyntaxError {
		boolean lastWasNumericSeparator = false;
		boolean consumedAnything = false;

		while (isDigit(radix) || is('_')) {
			if (is('_')) {
				if (lastWasNumericSeparator) {
					throw new SyntaxError("Only one underscore is allowed as numeric separator", position());
				}

				consume();
				lastWasNumericSeparator = true;
				continue;
			} else {
				lastWasNumericSeparator = false;
			}

			if (isDigit(radix)) {
				collect(builder);
				if (!consumedAnything) consumedAnything = true;
			}
		}

		if (lastWasNumericSeparator) {
			throw new SyntaxError("Numeric separators are not allowed at the end of numeric literals", position());
		}

		return consumedAnything;
	}

	private boolean isDigit(int radix) {
		return inBounds() && isDigit(codePoints[index], radix);
	}

	private String quoteCodePoint(int codePoint) {
		return StringEscapeUtils.quote(codePoint == -1 ? "[-1]" : Character.toString(codePoint), false);
	}

	private Token tokenizeKeywordOrIdentifier(int startIndex) throws SyntaxError {
		final StringBuilder builder = new StringBuilder();
		while (isIdentifierPart()) collect(builder);
		final TokenType type = keywords.getOrDefault(builder.toString(), Identifier);
		return new Token(range(startIndex), type);
	}

	private Token tokenizeSymbol(int startIndex) throws SyntaxError {
		for (int i = 4; i >= 1; i--) {
			final HashMap<String, TokenType> symbolSize = symbols.get(i - 1);
			final String key = new String(codePoints, index, i);
			final TokenType value = symbolSize.get(key);
			if (value != null) {
				index += i;
				return new Token(range(startIndex), value, key);
			}
		}

		throw new SyntaxError("Cannot tokenize character %s".formatted(quoteCodePoint(codePoints[index])), position());
	}

	private SourcePosition position() {
		return new SourcePosition(sourceText, index);
	}

	private SourcePosition position(int startIndex) {
		return new SourcePosition(sourceText, startIndex);
	}

	private SourceRange range(int startIndex) {
		return new SourceRange(sourceText, startIndex, index);
	}

	private static final class TemplateLiteralState {
		public boolean inExpression = false;
		public int bracketCount = 0;
	}
}
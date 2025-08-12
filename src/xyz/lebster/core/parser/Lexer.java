package xyz.lebster.core.parser;

import xyz.lebster.core.StringEscapeUtils;
import xyz.lebster.core.exception.ShouldNotHappen;
import xyz.lebster.core.exception.SyntaxError;
import xyz.lebster.core.node.SourcePosition;
import xyz.lebster.core.node.SourceRange;

import java.math.BigInteger;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

import static xyz.lebster.core.parser.TokenType.*;

public final class Lexer {
	private static final HashMap<String, TokenType> keywords = new HashMap<>();
	private static final HashMap<String, TokenType> symbols = new HashMap<>();

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
		keywords.put("get", Get);
		keywords.put("if", If);
		keywords.put("import", Import);
		keywords.put("in", In);
		keywords.put("Infinity", Infinity);
		keywords.put("instanceof", InstanceOf);
		keywords.put("let", Let);
		keywords.put("NaN", NaN);
		keywords.put("new", New);
		keywords.put("null", NullLiteral);
		keywords.put("of", Of);
		keywords.put("return", Return);
		keywords.put("set", Set);
		keywords.put("static", Static);
		keywords.put("super", Super);
		keywords.put("switch", Switch);
		keywords.put("this", This);
		keywords.put("throw", Throw);
		keywords.put("true", True);
		keywords.put("try", Try);
		keywords.put("typeof", Typeof);
		keywords.put("undefined", Undefined);
		keywords.put("var", Var);
		keywords.put("void", Void);
		keywords.put("while", While);
		keywords.put("yield", Yield);

		symbols.put(">>>=", UnsignedRightShiftEquals);
		symbols.put("||=", LogicalOrEquals);
		symbols.put(">>>", UnsignedRightShift);
		symbols.put(">>=", RightShiftEquals);
		symbols.put("===", StrictEqual);
		symbols.put("<<=", LeftShiftEquals);
		symbols.put("&&=", LogicalAndEquals);
		symbols.put("**=", ExponentEquals);
		symbols.put("??=", NullishCoalescingEquals);
		symbols.put("!==", StrictNotEqual);
		symbols.put("...", DotDotDot);
		symbols.put("||", LogicalOr);
		symbols.put("|=", PipeEquals);
		symbols.put(">>", RightShift);
		symbols.put(">=", GreaterThanEqual);
		symbols.put("=>", Arrow);
		symbols.put("==", LooseEqual);
		symbols.put("<=", LessThanEqual);
		symbols.put("<<", LeftShift);
		symbols.put("+=", PlusEquals);
		symbols.put("++", PlusPlus);
		symbols.put("^=", CaretEquals);
		symbols.put("%=", PercentEquals);
		symbols.put("&=", AmpersandEquals);
		symbols.put("&&", LogicalAnd);
		symbols.put("/=", DivideEquals);
		symbols.put("*=", MultiplyEquals);
		symbols.put("**", Exponent);
		symbols.put("?.", OptionalChain);
		symbols.put("??", NullishCoalescing);
		symbols.put("!=", NotEqual);
		symbols.put("-=", MinusEquals);
		symbols.put("--", MinusMinus);
		symbols.put("~", Tilde);
		symbols.put("|", Pipe);
		symbols.put(">", GreaterThan);
		symbols.put("=", Equals);
		symbols.put("<", LessThan);
		symbols.put("+", Plus);
		symbols.put("^", Caret);
		symbols.put("%", Percent);
		symbols.put("&", Ampersand);
		symbols.put("/", Slash);
		symbols.put("*", Star);
		symbols.put("}", RBrace);
		symbols.put("{", LBrace);
		symbols.put("]", RBracket);
		symbols.put("[", LBracket);
		symbols.put(")", RParen);
		symbols.put("(", LParen);
		symbols.put(".", Period);
		symbols.put("!", Bang);
		symbols.put("?", QuestionMark);
		symbols.put(";", Semicolon);
		symbols.put(",", Comma);
		symbols.put("-", Minus);
		symbols.put(":", Colon);
		symbols.put("\\", Backslash);
		symbols.put("@", At);
		symbols.put("#", Hashtag);
	}

	public static String valueForSymbol(TokenType type) {
		for (final var entry : symbols.entrySet()) {
			if (Objects.equals(type, entry.getValue())) {
				return entry.getKey();
			}
		}

		return null;
	}

	private final String sourceText;
	private final int[] codePoints;
	private final ArrayDeque<TemplateLiteralState> templateLiteralStates;
	private final int codePointCount;
	private int index = 0;
	Token latestToken;

	public Lexer(String sourceText) throws SyntaxError {
		final int[] unpaddedCodePoints = sourceText.codePoints().toArray();
		this.templateLiteralStates = new ArrayDeque<>();
		this.codePointCount = unpaddedCodePoints.length;
		this.codePoints = Arrays.copyOf(unpaddedCodePoints, codePointCount + 3); // Padding for branchless lookahead
		this.sourceText = sourceText;
		if (accept('#', '!')) {
			consumeSingleLineComment();
		}
	}

	private Lexer(String sourceText, int[] codePoints, ArrayDeque<TemplateLiteralState> templateLiteralStates, int codePointCount, int index, Token latestToken) {
		this.sourceText = sourceText;
		this.codePoints = codePoints;
		this.templateLiteralStates = templateLiteralStates;
		this.codePointCount = codePointCount;
		this.index = index;
		this.latestToken = latestToken;
	}

	public Lexer copy() {
		final ArrayDeque<TemplateLiteralState> templateLiteralStatesClone = new ArrayDeque<>();
		for (TemplateLiteralState templateLiteralState : templateLiteralStates) {
			templateLiteralStatesClone.add(templateLiteralState.copy());
		}

		return new Lexer(
			sourceText,
			codePoints,
			templateLiteralStatesClone,
			codePointCount,
			index,
			latestToken
		);
	}

	private Token eof() {
		if (inBounds()) throw new ShouldNotHappen("Creating EOF token before consuming all codepoints");
		return new Token(range(index), EOF, null);
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
		return index < codePointCount && codePoints[index] == c;
	}

	private boolean is(int first, int second) {
		return index + 1 < codePointCount && codePoints[index] == first && codePoints[index + 1] == second;
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

	private boolean accept(int first) {
		if (index < codePointCount && codePoints[index] == first) {
			index += 1;
			return true;
		} else {
			return false;
		}
	}

	private boolean accept(int first, int second) {
		if (index + 1 < codePointCount && codePoints[index] == first && codePoints[index + 1] == second) {
			index += 2;
			return true;
		} else {
			return false;
		}
	}

	// Division is not allowed after reserved words
	private boolean slashMeansDivision() {
		if (latestToken == null) return false;
		final TokenType lastTokenType = latestToken.type();
		return lastTokenType == Identifier // However if a reserved word is handled by treatAsIdentifier() (e.g. a.in), it is allowed
			   || lastTokenType == BigIntLiteral
			   || lastTokenType == True
			   || lastTokenType == False
			   || lastTokenType == RBrace
			   || lastTokenType == RBracket
			   || lastTokenType == LineTerminator
			   || lastTokenType == Undefined
			   || lastTokenType == Infinity
			   || lastTokenType == NaN
			   || lastTokenType == MinusMinus
			   || lastTokenType == NullLiteral
			   || lastTokenType == NumericLiteral
			   || lastTokenType == RParen
			   || lastTokenType == PlusPlus
			   || lastTokenType == PrivateIdentifier
			   || lastTokenType == RegexpFlags
			   || lastTokenType == StringLiteral
			   || lastTokenType == TemplateEnd
			   || lastTokenType == This;
	}

	private static final int[] REGEXP_FLAGS = new int[] { 'd', 'g', 'i', 'm', 's', 'u', 'v', 'y' };

	public Token next() throws SyntaxError {
		final Token result = nextToken();
		latestToken = result;
		return result;
	}

	private Token nextToken() throws SyntaxError {
		if (latestToken != null && latestToken.type() == RegexpPattern) {
			final StringBuilder flags = new StringBuilder();
			final int startIndex = index;
			while (isIdentifierPart()) {
				if (flags.indexOf(Character.toString(codePoints[index])) != -1)
					throw new SyntaxError("Duplicate flag %s in regular expression literal".formatted(quoteCodePoint(codePoints[index])), position());
				if (Arrays.binarySearch(REGEXP_FLAGS, codePoints[index]) < 0)
					throw new SyntaxError("Invalid regular expression flag %s".formatted(quoteCodePoint(codePoints[index])), position());
				collect(flags);
			}

			return new Token(range(startIndex), RegexpFlags, flags.toString());
		} else if (latestToken != null && latestToken.type() == NumericLiteral && isIdentifierStart(codePoints[index])) {
			throw new SyntaxError("Identifier starts immediately after numeric literal", position());
		}

		if (notInTemplateSpan()) {
			acceptWhitespace();
			consumeComment();
			acceptWhitespace();
		}

		final int startIndex = index;
		if (accept('`')) {
			if (notInTemplateSpan()) {
				templateLiteralStates.push(new TemplateLiteralState());
				return new Token(range(startIndex), TemplateStart, "`");
			} else {
				templateLiteralStates.pop();
				return new Token(range(startIndex), TemplateEnd, "`");
			}
		}

		if (inTemplateLiteral() && currentTemplateLiteralIsEnding()) {
			return tokenizeTemplateLiteralEnd(startIndex);
		}

		if (inTemplateLiteral() && !templateLiteralStates.getFirst().inExpression) {
			if (!inBounds()) {
				throw new SyntaxError("Unterminated template literal", position());
			}

			if (accept('$', '{')) {
				templateLiteralStates.getFirst().inExpression = true;
				return new Token(range(startIndex), TemplateExpressionStart, "${");
			} else {
				return tokenizeTemplateLiteralSpan(startIndex);
			}
		}

		if (!inBounds()) {
			return eof();
		} else if (isLineTerminator()) {
			consumeLineTerminators();
			return new Token(range(startIndex), LineTerminator, null);
		} else if (isIdentifierStart(codePoints[index])) {
			return tokenizeKeywordOrIdentifier(startIndex, false);
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

	public void treatAsRegexpLiteral() throws SyntaxError {
		index = latestToken.range().startIndex;
		latestToken = tokenizeRegexpLiteral(index);
	}

	public void treatAsIdentifier() throws SyntaxError {
		index = latestToken.range().startIndex;
		latestToken = tokenizeKeywordOrIdentifier(index, true);
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
		if (accept('/', '/')) {
			consumeSingleLineComment();
			return;
		}

		if (accept('/', '*')) {
			while (inBounds()) {
				if (accept('*', '/')) break;
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
		while (inBounds() && (escaped || !is('$', '{'))) {
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
		return templateLiteralStates.getFirst().inExpression && templateLiteralStates.getFirst().bracketCount == 0 && accept('}');
	}

	private Token tokenizeTemplateLiteralEnd(int startIndex) {
		templateLiteralStates.getFirst().inExpression = false;
		return new Token(range(startIndex), TemplateExpressionEnd, "}");
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
		if (accept('\n') || accept('\u2028') || accept('\u2029') || accept('\r', '\n'))
			return;

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
		if (accept('0', 'x') || accept('0', 'X')) radix = 16;
		else if (accept('0', 'b') || accept('0', 'B')) radix = 2;
		else if (accept('0', 'o') || accept('0', 'O')) radix = 8;
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

		final TokenType type = accept('n') ? BigIntLiteral : NumericLiteral;
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

	private Token tokenizeKeywordOrIdentifier(int startIndex, boolean forceIdentifier) throws SyntaxError {
		final StringBuilder builder = new StringBuilder();
		while (isIdentifierPart()) collect(builder);
		final String keyword = builder.toString();
		final TokenType type = forceIdentifier ? Identifier : keywords.getOrDefault(keyword, Identifier);
		return new Token(range(startIndex), type, keyword);
	}

	private Token tokenizeSymbol(int startIndex) throws SyntaxError {
		TokenType type = null;
		String key = null;
		for (int i = 4; i >= 1; i--) {
			key = new String(codePoints, index, i);
			type = symbols.get(key);
			if (type != null) {
				index += i;
				break;
			}
		}

		if (type == null) {
			throw new SyntaxError("Cannot tokenize character %s".formatted(quoteCodePoint(codePoints[index])), position());
		}

		if (!templateLiteralStates.isEmpty() && templateLiteralStates.getFirst().inExpression) {
			if (type == LBrace) {
				templateLiteralStates.getFirst().bracketCount++;
			} else if (type == RBrace) {
				templateLiteralStates.getFirst().bracketCount--;
			}
		}

		return new Token(range(startIndex), type, key);
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

		public TemplateLiteralState() {
		}

		private TemplateLiteralState(boolean inExpression, int bracketCount) {
			this.inExpression = inExpression;
			this.bracketCount = bracketCount;
		}

		public TemplateLiteralState copy() {
			return new TemplateLiteralState(inExpression, bracketCount);
		}
	}
}
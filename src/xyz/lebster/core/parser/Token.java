package xyz.lebster.core.parser;

import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.exception.ShouldNotHappen;
import xyz.lebster.core.node.SourcePosition;

import java.util.Set;

public final class Token {
	public final TokenType type;
	final String value;

	public final SourcePosition position;

	public Token(TokenType type, String value, SourcePosition position) {
		this.type = type;
		this.value = value;
		this.position = position;
	}

	public Token(TokenType type, SourcePosition position) {
		this.type = type;
		this.value = null;
		this.position = position;
	}

	@Override
	public String toString() {
		if (value == null) return String.valueOf(type);
		return "%s (%s)".formatted(StringEscapeUtils.quote(value, true), type);
	}

	@SpecificationURL("https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Operators/Operator_Precedence#table")
	int precedence() {
		return switch (type) {
			case Period, LBracket, LParen, OptionalChain -> 20;
			case New -> 19;
			case PlusPlus, MinusMinus -> 18;
			case Bang, Tilde, Typeof, Void, Delete, Await -> 17;
			case Exponent -> 16;
			case Star, Slash, Percent -> 15;
			case Plus, Minus -> 14;
			case LeftShift, RightShift, UnsignedRightShift -> 13;
			case LessThan, LessThanEqual, GreaterThan, GreaterThanEqual, In, InstanceOf -> 12;
			case LooseEqual, NotEqual, StrictEqual, StrictNotEqual -> 11;
			case Ampersand -> 10;
			case Caret -> 9;
			case Pipe -> 8;
			case NullishCoalescing -> 7;
			case LogicalAnd -> 6;
			case LogicalOr -> 5;
			case QuestionMark -> 4;
			case Equals, PlusEquals, MinusEquals, ExponentEquals, NullishCoalescingEquals, LogicalOrEquals,
				LogicalAndEquals, PipeEquals, CaretEquals, AmpersandEquals, UnsignedRightShiftEquals, RightShiftEquals,
				LeftShiftEquals, PercentEquals, DivideEquals, MultiplyEquals -> 3;
			case Yield -> 2;
			case Comma -> 1;
			default -> throw new ShouldNotHappen("Attempting to get precedence for token type '" + type + "'");
		};
	}

	@SpecificationURL("https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Operators/Operator_Precedence#table")
	Associativity associativity() {
		return switch (type) {
			case Period, LBracket, LParen, OptionalChain, Star, Slash, Percent, Plus, Minus, LeftShift, RightShift,
				UnsignedRightShift, LessThan, LessThanEqual, GreaterThan, GreaterThanEqual, In, InstanceOf, LooseEqual,
				NotEqual, StrictEqual, StrictNotEqual, Typeof, Void, Delete, Await, Ampersand, Caret, Pipe,
				NullishCoalescing, LogicalAnd, LogicalOr, Comma -> Associativity.Left;

			case New, PlusPlus, MinusMinus, Bang, Tilde, Exponent, QuestionMark, Equals, PlusEquals, MinusEquals,
				ExponentEquals, NullishCoalescingEquals, LogicalOrEquals, LogicalAndEquals, PipeEquals, CaretEquals,
				AmpersandEquals, UnsignedRightShiftEquals, RightShiftEquals, LeftShiftEquals, PercentEquals,
				DivideEquals, MultiplyEquals, Yield -> Associativity.Right;

			default -> throw new ShouldNotHappen("Attempting to get associativity for token type '" + type + "'");
		};
	}

	boolean matchIdentifierName() {
		return type == TokenType.Async
			   || type == TokenType.Await
			   || type == TokenType.Break
			   || type == TokenType.Case
			   || type == TokenType.Catch
			   || type == TokenType.Class
			   || type == TokenType.Const
			   || type == TokenType.Continue
			   || type == TokenType.Debugger
			   || type == TokenType.Default
			   || type == TokenType.Delete
			   || type == TokenType.Do
			   || type == TokenType.Else
			   || type == TokenType.Enum
			   || type == TokenType.Export
			   || type == TokenType.Extends
			   || type == TokenType.False
			   || type == TokenType.Finally
			   || type == TokenType.For
			   || type == TokenType.Function
			   || type == TokenType.Identifier
			   || type == TokenType.If
			   || type == TokenType.Import
			   || type == TokenType.In
			   || type == TokenType.InstanceOf
			   || type == TokenType.Let
			   || type == TokenType.New
			   || type == TokenType.Null
			   || type == TokenType.Return
			   || type == TokenType.Static
			   || type == TokenType.Super
			   || type == TokenType.Switch
			   || type == TokenType.This
			   || type == TokenType.Throw
			   || type == TokenType.True
			   || type == TokenType.Try
			   || type == TokenType.Typeof
			   || type == TokenType.Var
			   || type == TokenType.Void
			   || type == TokenType.While
			   || type == TokenType.With
			   || type == TokenType.Yield;
	}

	boolean matchDeclaration() {
		return type == TokenType.Function
			   || type == TokenType.Class
			   || type == TokenType.Let
			   || type == TokenType.Var
			   || type == TokenType.Const;
	}

	boolean matchStatement() {
		return type == TokenType.Return
			   || type == TokenType.Yield
			   || type == TokenType.Do
			   || type == TokenType.If
			   || type == TokenType.Throw
			   || type == TokenType.Try
			   || type == TokenType.While
			   || type == TokenType.With
			   || type == TokenType.For
			   || type == TokenType.LBrace
			   || type == TokenType.Switch
			   || type == TokenType.Break
			   || type == TokenType.Continue
			   || type == TokenType.Var
			   || type == TokenType.Import
			   || type == TokenType.Export
			   || type == TokenType.Debugger
			   || type == TokenType.Semicolon;
	}

	boolean matchPrimaryExpression() {
		if (matchUnaryPrefixedExpression() || matchPrefixedUpdateExpression()) return true;
		return type == TokenType.Async
			   || type == TokenType.Class
			   || type == TokenType.False
			   || type == TokenType.Function
			   || type == TokenType.Identifier
			   || type == TokenType.Infinity
			   || type == TokenType.LBrace
			   || type == TokenType.LBracket
			   || type == TokenType.LParen
			   || type == TokenType.NaN
			   || type == TokenType.New
			   || type == TokenType.Null
			   || type == TokenType.NumericLiteral
			   || type == TokenType.RegexpLiteral
			   || type == TokenType.StringLiteral
			   || type == TokenType.Super
			   || type == TokenType.TemplateStart
			   || type == TokenType.This
			   || type == TokenType.True
			   || type == TokenType.Undefined;
	}

	boolean matchSecondaryExpression(Set<TokenType> forbidden) {
		if (forbidden.contains(type)) return false;
		return type == TokenType.Ampersand
			   || type == TokenType.AmpersandEquals
			   || type == TokenType.Caret
			   || type == TokenType.CaretEquals
			   || type == TokenType.Comma
			   || type == TokenType.DivideEquals
			   || type == TokenType.Equals
			   || type == TokenType.Exponent
			   || type == TokenType.ExponentEquals
			   || type == TokenType.GreaterThan
			   || type == TokenType.GreaterThanEqual
			   || type == TokenType.In
			   || type == TokenType.InstanceOf
			   || type == TokenType.LBracket
			   || type == TokenType.LParen
			   || type == TokenType.LeftShift
			   || type == TokenType.LeftShiftEquals
			   || type == TokenType.LessThan
			   || type == TokenType.LessThanEqual
			   || type == TokenType.LogicalAnd
			   || type == TokenType.LogicalAndEquals
			   || type == TokenType.LogicalOr
			   || type == TokenType.LogicalOrEquals
			   || type == TokenType.LooseEqual
			   || type == TokenType.Minus
			   || type == TokenType.MinusEquals
			   || type == TokenType.MinusMinus
			   || type == TokenType.MultiplyEquals
			   || type == TokenType.NotEqual
			   || type == TokenType.NullishCoalescing
			   || type == TokenType.NullishCoalescingEquals
			   || type == TokenType.Percent
			   || type == TokenType.PercentEquals
			   || type == TokenType.Period
			   || type == TokenType.Pipe
			   || type == TokenType.PipeEquals
			   || type == TokenType.Plus
			   || type == TokenType.PlusEquals
			   || type == TokenType.PlusPlus
			   || type == TokenType.QuestionMark
			   || type == TokenType.RightShift
			   || type == TokenType.RightShiftEquals
			   || type == TokenType.Slash
			   || type == TokenType.Star
			   || type == TokenType.StrictEqual
			   || type == TokenType.StrictNotEqual
			   || type == TokenType.UnsignedRightShift
			   || type == TokenType.UnsignedRightShiftEquals;
	}

	boolean matchPrefixedUpdateExpression() {
		return type == TokenType.PlusPlus ||
			   type == TokenType.MinusMinus;
	}

	boolean matchUnaryPrefixedExpression() {
		return type == TokenType.Bang
			   || type == TokenType.Delete
			   || type == TokenType.Minus
			   || type == TokenType.Plus
			   || type == TokenType.Tilde
			   || type == TokenType.Typeof
			   || type == TokenType.Void;
	}

	boolean matchVariableDeclaration() {
		return type == TokenType.Var ||
			   type == TokenType.Let ||
			   type == TokenType.Const;
	}

	boolean matchClassElementName() {
		return matchIdentifierName()
			   || type == TokenType.LBracket
			   || type == TokenType.NumericLiteral
			   || type == TokenType.PrivateIdentifier
			   || type == TokenType.StringLiteral;
	}
}
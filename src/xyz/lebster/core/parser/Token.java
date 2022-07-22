package xyz.lebster.core.parser;

import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.exception.CannotParse;
import xyz.lebster.core.exception.ShouldNotHappen;
import xyz.lebster.core.node.SourcePosition;
import xyz.lebster.core.node.expression.AssignmentExpression.AssignmentOp;
import xyz.lebster.core.node.expression.BinaryExpression.BinaryOp;
import xyz.lebster.core.node.expression.EqualityExpression.EqualityOp;
import xyz.lebster.core.node.expression.LogicalExpression.LogicOp;
import xyz.lebster.core.node.expression.RelationalExpression.RelationalOp;
import xyz.lebster.core.node.expression.UpdateExpression.UpdateOp;

import java.util.Set;

import static xyz.lebster.core.node.expression.AssignmentExpression.AssignmentOp.*;
import static xyz.lebster.core.node.expression.BinaryExpression.BinaryOp.*;
import static xyz.lebster.core.node.expression.EqualityExpression.EqualityOp.*;
import static xyz.lebster.core.node.expression.LogicalExpression.LogicOp.*;
import static xyz.lebster.core.node.expression.RelationalExpression.RelationalOp.*;
import static xyz.lebster.core.node.expression.UpdateExpression.UpdateOp.*;
import static xyz.lebster.core.parser.TokenType.*;
import static xyz.lebster.core.parser.TokenType.GreaterThan;
import static xyz.lebster.core.parser.TokenType.InstanceOf;
import static xyz.lebster.core.parser.TokenType.LeftShift;
import static xyz.lebster.core.parser.TokenType.LessThan;
import static xyz.lebster.core.parser.TokenType.UnsignedRightShift;

public final class Token {
	public final TokenType type;
	public final SourcePosition position;
	final String value;

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
		return type == Async
			   || type == Await
			   || type == Break
			   || type == Case
			   || type == Catch
			   || type == Class
			   || type == Const
			   || type == Continue
			   || type == Debugger
			   || type == Default
			   || type == Delete
			   || type == Do
			   || type == Else
			   || type == Enum
			   || type == Export
			   || type == Extends
			   || type == False
			   || type == Finally
			   || type == For
			   || type == Function
			   || type == Identifier
			   || type == If
			   || type == Import
			   || type == TokenType.In
			   || type == InstanceOf
			   || type == Let
			   || type == New
			   || type == Null
			   || type == Return
			   || type == Static
			   || type == Super
			   || type == Switch
			   || type == This
			   || type == Throw
			   || type == True
			   || type == Try
			   || type == Typeof
			   || type == Var
			   || type == Void
			   || type == While
			   || type == With
			   || type == Yield;
	}

	boolean matchDeclaration() {
		return type == Function
			   || type == Class
			   || type == Let
			   || type == Var
			   || type == Const;
	}

	boolean matchStatement() {
		return type == Return
			   || type == Yield
			   || type == Do
			   || type == If
			   || type == Throw
			   || type == Try
			   || type == While
			   || type == With
			   || type == For
			   || type == LBrace
			   || type == Switch
			   || type == Break
			   || type == Continue
			   || type == Var
			   || type == Import
			   || type == Export
			   || type == Debugger
			   || type == Semicolon;
	}

	boolean matchPrimaryExpression() {
		if (matchUnaryPrefixedExpression() || matchPrefixedUpdateExpression()) return true;
		return type == Async
			   || type == Class
			   || type == False
			   || type == Function
			   || type == Identifier
			   || type == Infinity
			   || type == LBrace
			   || type == LBracket
			   || type == LParen
			   || type == NaN
			   || type == New
			   || type == Null
			   || type == NumericLiteral
			   || type == RegexpLiteral
			   || type == StringLiteral
			   || type == Super
			   || type == TemplateStart
			   || type == This
			   || type == True
			   || type == Undefined;
	}

	boolean matchSecondaryExpression(Set<TokenType> forbidden) {
		if (forbidden.contains(type)) return false;
		return type == Ampersand
			   || type == AmpersandEquals
			   || type == Caret
			   || type == CaretEquals
			   || type == Comma
			   || type == DivideEquals
			   || type == Equals
			   || type == Exponent
			   || type == ExponentEquals
			   || type == GreaterThan
			   || type == GreaterThanEqual
			   || type == TokenType.In
			   || type == InstanceOf
			   || type == LBracket
			   || type == LParen
			   || type == LeftShift
			   || type == LeftShiftEquals
			   || type == LessThan
			   || type == LessThanEqual
			   || type == LogicalAnd
			   || type == LogicalAndEquals
			   || type == LogicalOr
			   || type == LogicalOrEquals
			   || type == LooseEqual
			   || type == Minus
			   || type == MinusEquals
			   || type == MinusMinus
			   || type == MultiplyEquals
			   || type == NotEqual
			   || type == NullishCoalescing
			   || type == NullishCoalescingEquals
			   || type == Percent
			   || type == PercentEquals
			   || type == Period
			   || type == Pipe
			   || type == PipeEquals
			   || type == Plus
			   || type == PlusEquals
			   || type == PlusPlus
			   || type == QuestionMark
			   || type == RightShift
			   || type == RightShiftEquals
			   || type == Slash
			   || type == Star
			   || type == StrictEqual
			   || type == StrictNotEqual
			   || type == UnsignedRightShift
			   || type == UnsignedRightShiftEquals;
	}

	boolean matchPrefixedUpdateExpression() {
		return type == PlusPlus ||
			   type == MinusMinus;
	}

	boolean matchUnaryPrefixedExpression() {
		return type == Bang
			   || type == Delete
			   || type == Minus
			   || type == Plus
			   || type == Tilde
			   || type == Typeof
			   || type == Void;
	}

	boolean matchVariableDeclaration() {
		return type == Var ||
			   type == Let ||
			   type == Const;
	}

	boolean matchClassElementName() {
		return matchIdentifierName()
			   || type == LBracket
			   || type == NumericLiteral
			   || type == PrivateIdentifier
			   || type == StringLiteral;
	}

	UpdateOp getUpdateOp() throws CannotParse {
		return switch (type) {
			case MinusMinus -> PostDecrement;
			case PlusPlus -> PostIncrement;
			default -> throw new CannotParse(this, "UpdateOp");
		};
	}

	RelationalOp getRelationalOp() throws CannotParse {
		return switch (type) {
			case LessThan -> RelationalOp.LessThan;
			case LessThanEqual -> LessThanEquals;
			case GreaterThan -> RelationalOp.GreaterThan;
			case GreaterThanEqual -> GreaterThanEquals;
			case In -> RelationalOp.In;
			case InstanceOf -> RelationalOp.InstanceOf;
			default -> throw new CannotParse(this, "RelationalOp");
		};
	}

	LogicOp getLogicOp() throws CannotParse {
		return switch (type) {
			case LogicalOr -> Or;
			case LogicalAnd -> And;
			case NullishCoalescing -> Coalesce;
			default -> throw new CannotParse(this, "LogicOp");
		};
	}

	EqualityOp getEqualityOp() throws CannotParse {
		return switch (type) {
			case StrictEqual -> StrictEquals;
			case LooseEqual -> LooseEquals;
			case StrictNotEqual -> StrictNotEquals;
			case NotEqual -> LooseNotEquals;
			default -> throw new CannotParse(this, "EqualityOp");
		};
	}

	BinaryOp getBinaryOp() throws CannotParse {
		return switch (type) {
			case Plus -> Add;
			case Minus -> Subtract;
			case Star -> Multiply;
			case Slash -> Divide;
			case Percent -> Remainder;
			case Exponent -> Exponentiate;
			case Pipe -> BitwiseOR;
			case Ampersand -> BitwiseAND;
			case Caret -> BitwiseXOR;
			case LeftShift -> BinaryOp.LeftShift;
			case RightShift -> SignedRightShift;
			case UnsignedRightShift -> BinaryOp.UnsignedRightShift;
			default -> throw new CannotParse(this, "BinaryOp");
		};
	}

	AssignmentOp getAssignmentOp() throws CannotParse {
		return switch (type) {
			case Equals -> Assign;
			case LogicalAndEquals -> LogicalAndAssign;
			case LogicalOrEquals -> LogicalOrAssign;
			case NullishCoalescingEquals -> NullishCoalesceAssign;
			case MultiplyEquals -> MultiplyAssign;
			case DivideEquals -> DivideAssign;
			case PercentEquals -> RemainderAssign;
			case PlusEquals -> PlusAssign;
			case MinusEquals -> MinusAssign;
			case LeftShiftEquals -> LeftShiftAssign;
			case RightShiftEquals -> RightShiftAssign;
			case UnsignedRightShiftEquals -> UnsignedRightShiftAssign;
			case AmpersandEquals -> BitwiseAndAssign;
			case CaretEquals -> BitwiseExclusiveOrAssign;
			case PipeEquals -> BitwiseOrAssign;
			case ExponentEquals -> ExponentAssign;
			default -> throw new CannotParse(this, "AssignmentOp");
		};
	}
}
package xyz.lebster.core.parser;

import xyz.lebster.core.node.SourcePosition;

import java.util.Set;

public final class Token {
	public final TokenType type;
	public final String value;

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
		return value == null ? String.valueOf(type) : '"' + StringEscapeUtils.escape(value) + "\" (" + type + ")";
	}

	boolean matchIdentifierName() {
		return type == TokenType.Identifier
			   || type == TokenType.Let
			   || type == TokenType.Async
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
			   || type == TokenType.If
			   || type == TokenType.Import
			   || type == TokenType.In
			   || type == TokenType.InstanceOf
			   || type == TokenType.New
			   || type == TokenType.Null
			   || type == TokenType.Return
			   || type == TokenType.Super
			   || type == TokenType.Static
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
		return type == TokenType.Function ||
			   type == TokenType.Class ||
			   type == TokenType.Let ||
			   type == TokenType.Var ||
			   type == TokenType.Const;
	}

	boolean matchStatementOrExpression() {
		return matchPrimaryExpression() ||
			   type == TokenType.Return ||
			   type == TokenType.Yield ||
			   type == TokenType.Do ||
			   type == TokenType.If ||
			   type == TokenType.Throw ||
			   type == TokenType.Try ||
			   type == TokenType.While ||
			   type == TokenType.With ||
			   type == TokenType.For ||
			   type == TokenType.LBrace ||
			   type == TokenType.Switch ||
			   type == TokenType.Break ||
			   type == TokenType.Continue ||
			   type == TokenType.Var ||
			   type == TokenType.Import ||
			   type == TokenType.Export ||
			   type == TokenType.Debugger ||
			   type == TokenType.Semicolon;
	}

	boolean matchPrimaryExpression() {
		return type == TokenType.LParen
			   || type == TokenType.Async
			   || type == TokenType.TemplateStart
			   || type == TokenType.Identifier
			   || type == TokenType.StringLiteral
			   || type == TokenType.NumericLiteral
			   || type == TokenType.Super
			   || type == TokenType.True
			   || type == TokenType.False
			   || type == TokenType.Class
			   || type == TokenType.Function
			   || type == TokenType.LBracket
			   || type == TokenType.LBrace
			   || type == TokenType.This
			   || type == TokenType.RegexpLiteral
			   || type == TokenType.Null
			   || type == TokenType.New
			   || type == TokenType.Infinity
			   || type == TokenType.NaN
			   || type == TokenType.Undefined
			   || matchUnaryPrefixedExpression()
			   || matchPrefixedUpdateExpression();
	}

	boolean matchSecondaryExpression(Set<TokenType> forbidden) {
		if (forbidden.contains(type)) return false;
		return type == TokenType.Plus ||
			   type == TokenType.Minus ||
			   type == TokenType.Star ||
			   type == TokenType.Slash ||
			   type == TokenType.Percent ||
			   type == TokenType.Exponent ||
			   type == TokenType.Pipe ||
			   type == TokenType.Ampersand ||
			   type == TokenType.Caret ||
			   type == TokenType.LeftShift ||
			   type == TokenType.RightShift ||
			   type == TokenType.UnsignedRightShift ||
			   type == TokenType.QuestionMark ||
			   type == TokenType.LParen ||
			   type == TokenType.StrictEqual ||
			   type == TokenType.LooseEqual ||
			   type == TokenType.StrictNotEqual ||
			   type == TokenType.NotEqual ||
			   type == TokenType.LogicalOr ||
			   type == TokenType.LogicalAnd ||
			   type == TokenType.NullishCoalescing ||
			   type == TokenType.Equals ||
			   type == TokenType.LogicalAndEquals ||
			   type == TokenType.LogicalOrEquals ||
			   type == TokenType.NullishCoalescingEquals ||
			   type == TokenType.MultiplyEquals ||
			   type == TokenType.DivideEquals ||
			   type == TokenType.PercentEquals ||
			   type == TokenType.PlusEquals ||
			   type == TokenType.MinusEquals ||
			   type == TokenType.LeftShiftEquals ||
			   type == TokenType.RightShiftEquals ||
			   type == TokenType.UnsignedRightShiftEquals ||
			   type == TokenType.AmpersandEquals ||
			   type == TokenType.CaretEquals ||
			   type == TokenType.PipeEquals ||
			   type == TokenType.ExponentEquals ||
			   type == TokenType.MinusMinus ||
			   type == TokenType.PlusPlus ||
			   type == TokenType.LessThan ||
			   type == TokenType.LessThanEqual ||
			   type == TokenType.GreaterThan ||
			   type == TokenType.GreaterThanEqual ||
			   type == TokenType.In ||
			   type == TokenType.InstanceOf ||
			   type == TokenType.Period ||
			   type == TokenType.LBracket ||
			   type == TokenType.Comma;
	}

	boolean matchPrefixedUpdateExpression() {
		return type == TokenType.PlusPlus ||
			   type == TokenType.MinusMinus;
	}

	boolean matchUnaryPrefixedExpression() {
		return type == TokenType.Bang ||
			   type == TokenType.Tilde ||
			   type == TokenType.Plus ||
			   type == TokenType.Minus ||
			   type == TokenType.Typeof ||
			   type == TokenType.Void ||
			   type == TokenType.Delete;
	}

	boolean matchVariableDeclaration() {
		return type == TokenType.Var ||
			   type == TokenType.Let ||
			   type == TokenType.Const;
	}

	boolean matchClassElementName() {
		return matchIdentifierName()
			   || type == TokenType.StringLiteral
			   || type == TokenType.NumericLiteral
			   || type == TokenType.LBracket
			   || type == TokenType.PrivateIdentifier;
	}
}
package xyz.lebster.core.parser;

public enum TokenType {
	Ampersand, AmpersandEquals, Arrow, At, Async, Await, Backslash, Bang, Break, Caret, CaretEquals,
	Case, Catch, Class, Colon, Comma, Const, Continue, Debugger, Default, Delete, DivideEquals,
	Do, DotDotDot, EOF, Else, Enum, Equals, Exponent, ExponentEquals, Export, Extends, False,
	Finally, For, Function, GreaterThan, GreaterThanEqual, Hashtag, Identifier, If, Import, In,
	Infinity, Instanceof, LBrace, LBracket, LParen, LeftShift, LeftShiftEquals, LessThan,
	LessThanEqual, Let, LineTerminator, LogicalAnd, LogicalAndEquals, LogicalOr, LogicalOrEquals,
	LooseEqual, Minus, MinusEquals, MinusMinus, MultiplyEquals, NaN, New, NotEqual, Null,
	NullishCoalescing, NullishCoalescingEquals, NumericLiteral, OptionalChain, Percent,
	PercentEquals, Period, Pipe, PipeEquals, Plus, PlusEquals, PlusPlus, QuestionMark, RBrace,
	RBracket, RParen, Return, RightShift, RightShiftEquals, Semicolon, Slash, Star, StrictEqual,
	StrictNotEqual, StringLiteral, Super, Switch, This, Throw, Tilde, True, Try, Typeof, Undefined,
	UnsignedRightShift, UnsignedRightShiftEquals, Var, Void, While, With, Yield, RegexpLiteral,

	TemplateStart, TemplateEnd, TemplateSpan, TemplateExpressionStart, BigIntLiteral, PrivateIdentifier, TemplateExpressionEnd,
	Static
}
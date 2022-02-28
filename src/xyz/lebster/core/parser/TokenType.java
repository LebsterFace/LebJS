package xyz.lebster.core.parser;

public enum TokenType {
	Bang, Break, Case, Catch, Class, Comma, Const, Continue, Debugger, Default, Delete, Slash, Do, EOF,
	Else, Equals, Export, Extends, Finally, For, Function, Identifier, If, Import, In, Instanceof, LBrace, LBracket,
	LParen, Let, Minus, Star, New, NumericLiteral, Period, Plus, RBrace, RBracket, RParen, Return, Semicolon,
	StringLiteral, Super, Switch, LineTerminator, This, Throw, Try, Typeof, Var, Void, While, With, Yield, Null, Undefined,
	NaN, UnsignedRightShiftEquals, StrictEqual, LeftShiftEquals, Ampersand, Percent, Caret, LessThan, GreaterThan, Pipe,
	Tilde, MinusMinus, MinusEquals, NotEqual, NullishCoalescing, OptionalChain, Exponent, MultiplyEquals, DivideEquals,
	LogicalAnd, AmpersandEquals, PercentEquals, CaretEquals, PlusPlus, PlusEquals, LeftShift, LessThanEqual, LooseEqual,
	Arrow, GreaterThanEqual, RightShift, PipeEquals, LogicalOr, StrictNotEqual, NullishCoalescingEquals, ExponentEquals,
	LogicalAndEquals, RightShiftEquals, UnsignedRightShift, LogicalOrEquals, Infinity, QuestionMark, Await, Colon,
	Backslash, DotDotDot, Enum, False, At, Hashtag, True
}
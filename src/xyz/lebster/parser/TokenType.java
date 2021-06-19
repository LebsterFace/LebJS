package xyz.lebster.parser;

public enum TokenType {
	Identifier,
	NumericLiteral,
	StringLiteral,
	BooleanLiteral,

	LParen,
	RParen,
	Equals,
	Terminator,
	EOF,
	Semicolon,

	Let,

	Plus,
	Minus,
	Multiply,
	Divide,

	Period
}

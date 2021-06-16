package xyz.lebster.parser;

public enum TokenType {
	Identifier,
	NumericLiteral,
	StringLiteral,
	BooleanLiteral,

	LParen,
	RParen,
	Assign,
	Let,
	Terminator,
	EOF
}

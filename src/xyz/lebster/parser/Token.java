package xyz.lebster.parser;

public record Token(TokenType type, String value, int start, int endPos) { }

package xyz.lebster.cli;

public record ExecutionOptions (
	boolean showAST,
	boolean showLastValue,
	boolean showDebug,
	boolean silent,
	boolean testingMethods,
	boolean showPrompt
) { }

package xyz.lebster.runtime;

public final class ExecutionError extends Error {
	public ExecutionError(String message) {
		super(message);
	}
}
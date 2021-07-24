package xyz.lebster.core.runtime;

public final class ExecutionError extends Error {
	public ExecutionError(String message) {
		super(message);
	}
}
package xyz.lebster.core.runtime;

public final class ExecutionError extends RuntimeException {
	public ExecutionError(String message) {
		super(message);
	}
}
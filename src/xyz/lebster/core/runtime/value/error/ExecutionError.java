package xyz.lebster.core.runtime.value.error;

public final class ExecutionError extends RuntimeException {
	public ExecutionError(String message) {
		super(message);
	}
}
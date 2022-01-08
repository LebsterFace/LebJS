package xyz.lebster.core.exception;

public final class NotImplemented extends RuntimeException {
	public NotImplemented(String message) {
		super(message + " has not been implemented.");
	}
}
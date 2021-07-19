package xyz.lebster.exception;

public final class NotImplemented extends Error {
	public NotImplemented(String message) {
		super(message + " has not been implemented.");
	}
}
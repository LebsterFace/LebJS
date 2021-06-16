package xyz.lebster.core.exception;

public class NotImplementedException extends Error {
	public NotImplementedException(String message) {
		super(message + " has not been implemented.");
	}
}

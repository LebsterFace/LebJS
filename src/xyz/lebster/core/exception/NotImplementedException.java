package xyz.lebster.core.exception;

public class NotImplementedException extends LanguageException {
	public NotImplementedException(String message) {
		super(message + " has not been implemented.");
	}
}

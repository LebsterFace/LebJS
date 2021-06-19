package xyz.lebster.exception;

public class NotImplemented extends LanguageError {
	public NotImplemented(String message) {
		super(message + " has not been implemented.");
	}
}

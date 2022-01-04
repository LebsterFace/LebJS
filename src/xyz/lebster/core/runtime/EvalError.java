package xyz.lebster.core.runtime;

public class EvalError extends LanguageError {
	public EvalError(Throwable e) {
		super(e.getMessage());
	}
}

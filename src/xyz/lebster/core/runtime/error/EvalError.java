package xyz.lebster.core.runtime.error;

public class EvalError extends LanguageError {
	public EvalError(Throwable e) {
		super(e.getMessage());
	}
}

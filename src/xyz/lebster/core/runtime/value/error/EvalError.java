package xyz.lebster.core.runtime.value.error;

public class EvalError extends LanguageError {
	public EvalError(Throwable e) {
		super(e.getMessage());
	}
}

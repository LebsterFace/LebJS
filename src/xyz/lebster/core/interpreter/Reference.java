package xyz.lebster.core.interpreter;

import xyz.lebster.core.NonCompliant;
import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.error.reference.ReferenceError;
import xyz.lebster.core.value.object.Key;
import xyz.lebster.core.value.object.ObjectValue;

import static xyz.lebster.core.interpreter.AbruptCompletion.error;

@SpecificationURL("https://tc39.es/ecma262/multipage#sec-reference-record-specification-type")
public record Reference(ObjectValue base, Key<?> referencedName) {
	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-getvalue")
	public Value<?> getValue(Interpreter interpreter) throws AbruptCompletion {
		if (isResolvable()) {
			return base.get(interpreter, referencedName);
		} else {
			throw error(new ReferenceError(interpreter, referencedName.value + " is not defined"));
		}
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-putvalue")
	@NonCompliant
	public void putValue(Interpreter interpreter, Value<?> newValue) throws AbruptCompletion {
		// 4. If IsUnresolvableReference(V) is true, then
		if (!isResolvable()) {
			// a. If V.[[Strict]] is true, throw a ReferenceError exception.
			if (interpreter.isStrictMode())
				throw error(new ReferenceError(interpreter, referencedName.value + " is not defined"));
			// b. Let globalObj be GetGlobalObject().
			// c. Return ? Set(globalObj, V.[[ReferencedName]], W, false).
			interpreter.globalObject.set(interpreter, referencedName, newValue);
			return;
		}

		// FIXME: Follow spec ( 5. If IsPropertyReference(V)... )

		// a. Let base be V.[[Base]].
		// b. Assert: base is an Environment Record.
		// c. Return ? base.SetMutableBinding(V.[[ReferencedName]], W, V.[[Strict]]) (see 9.1).
		// FIXME: Follow spec (SetMutableBinding)
		base.set(interpreter, referencedName, newValue);
	}

	public boolean isResolvable() {
		return this.base != null;
	}
}
package xyz.lebster.core.interpreter;

import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.runtime.value.Value;
import xyz.lebster.core.runtime.value.error.ReferenceError;
import xyz.lebster.core.runtime.value.object.ObjectValue;
import xyz.lebster.core.runtime.value.primitive.StringValue;

@SpecificationURL("https://tc39.es/ecma262/multipage#sec-reference-record-specification-type")
// FIXME: The `base` 'should' be possible to be a LexicalEnvironment as well as an ObjectLiteral
// 		  Right now, when we need to use a LexicalEnvironment we just pass in the variables
//		  of the frame (Interpreter#getReference resolvable case), but in the future
//		  we may need to pass the entire LexicalEnvironment
public record Reference(ObjectValue base, StringValue referencedName) {
	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-getvalue")
	public Value<?> getValue(Interpreter interpreter) throws AbruptCompletion {
		if (isResolvable()) {
			return base.get(interpreter, referencedName);
		} else {
			throw AbruptCompletion.error(new ReferenceError(referencedName.value + " is not defined"));
		}
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-putvalue")
	public void putValue(Interpreter interpreter, Value<?> newValue) throws AbruptCompletion {
		// 4. If IsUnresolvableReference(V) is true, then
		if (!isResolvable()) {
			// a. If V.[[Strict]] is true, throw a ReferenceError exception.
			if (interpreter.isStrictMode)
				throw AbruptCompletion.error(new ReferenceError(referencedName.value + " is not defined"));
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
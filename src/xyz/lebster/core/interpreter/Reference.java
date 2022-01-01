package xyz.lebster.core.interpreter;

import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.node.value.Dictionary;
import xyz.lebster.core.node.value.StringLiteral;
import xyz.lebster.core.node.value.Value;
import xyz.lebster.core.runtime.ReferenceError;

@SpecificationURL("https://tc39.es/ecma262/multipage#sec-reference-record-specification-type")
// FIXME: The `base` 'should' be possible to be a LexicalEnvironment as well as a dictionary
// 		  Right now, when we need to use a LexicalEnvironment we just pass in the variables
//		  of the frame (Interpreter#getReference resolvable case), but in the future
//		  we may need to pass the entire LexicalEnvironment
public record Reference(Dictionary base, StringLiteral referencedName) {
	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-getvalue")
	public Value<?> getValue(Interpreter interpreter) throws AbruptCompletion {
		if (isResolvable()) {
			return base.get(referencedName).execute(interpreter);
		} else {
			throw AbruptCompletion.error(new ReferenceError(referencedName.value + " is not defined"));
		}
	}

	public void setValue(Interpreter interpreter, Value<?> newValue) throws AbruptCompletion {
		if (isResolvable()) {
			this.base.set(interpreter, referencedName, newValue);
		} else {
			throw AbruptCompletion.error(new ReferenceError(referencedName.value + " is not defined"));
		}
	}

	public boolean isResolvable() {
		return this.base != null;
	}
}
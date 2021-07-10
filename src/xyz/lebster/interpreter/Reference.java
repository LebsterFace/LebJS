package xyz.lebster.interpreter;

import xyz.lebster.node.SpecificationURL;
import xyz.lebster.node.value.Dictionary;
import xyz.lebster.node.value.StringLiteral;
import xyz.lebster.node.value.Value;
import xyz.lebster.runtime.ReferenceError;

@SpecificationURL("https://tc39.es/ecma262/multipage#sec-reference-record-specification-type")
// FIXME: The `base` 'should' be possible to be a LexicalEnvironment as well as a dictionary
// 		  Right now, when we need to use a LexicalEnvironment we just pass in the variables
//		  of the frame (Interpreter#getReference resolvable case), but in the future
//		  we may need to pass the entire LexicalEnvironment
public record Reference(Dictionary base, StringLiteral referencedName) {
	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-getvalue")
	public Value<?> getValue(Interpreter interpreter) throws AbruptCompletion {
		if (isResolvable()) {
			return base.get(referencedName);
		} else {
			throw new AbruptCompletion(new ReferenceError(referencedName.value + " is not defined"), AbruptCompletion.Type.Throw);
		}
	}

	public Value<?> setValue(Interpreter interpreter, Value<?> newValue) throws AbruptCompletion {
		if (isResolvable()) {
			return base.set(referencedName, newValue);
		} else {
			throw new AbruptCompletion(new ReferenceError(referencedName.value + " is not defined"), AbruptCompletion.Type.Throw);
		}
	}

	public boolean isResolvable() {
		return base != null;
	}
}
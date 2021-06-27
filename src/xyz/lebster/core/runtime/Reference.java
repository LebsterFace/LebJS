package xyz.lebster.core.runtime;

import xyz.lebster.core.value.Dictionary;
import xyz.lebster.core.value.StringLiteral;
import xyz.lebster.core.value.Value;

// https://tc39.es/ecma262/multipage/ecmascript-data-types-and-values.html#sec-reference-record-specification-type
// FIXME: Other properties
public record Reference(Dictionary baseObj, StringLiteral referencedName) {
	public Value<?> getValue(Interpreter interpreter) {
//		https://tc39.es/ecma262/multipage/ecmascript-data-types-and-values.html#sec-putvalue
//		FIXME: Environment Record system
//		FIXME: Unresolvable references should be handled here
		return baseObj.get(referencedName);
	}
}

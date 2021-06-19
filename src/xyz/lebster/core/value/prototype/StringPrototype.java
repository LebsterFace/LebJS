package xyz.lebster.core.value.prototype;

import xyz.lebster.core.value.Dictionary;
import xyz.lebster.core.value.NativeFunction;
import xyz.lebster.core.value.StringLiteral;

public class StringPrototype extends Dictionary  {
	public static final StringPrototype instance = new StringPrototype();
	public StringPrototype() {
		set("reverse", new NativeFunction((interpreter, arguments) ->
			new StringLiteral(new StringBuilder(interpreter.thisValue()).reverse().toString()))
		);
	}
}

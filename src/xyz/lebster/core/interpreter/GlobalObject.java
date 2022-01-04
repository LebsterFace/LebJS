package xyz.lebster.core.interpreter;

import xyz.lebster.cli.Testing;
import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.exception.CannotParse;
import xyz.lebster.core.exception.SyntaxError;
import xyz.lebster.core.node.Program;
import xyz.lebster.core.node.value.Dictionary;
import xyz.lebster.core.node.value.NumericLiteral;
import xyz.lebster.core.node.value.Undefined;
import xyz.lebster.core.parser.Lexer;
import xyz.lebster.core.parser.Parser;
import xyz.lebster.core.runtime.ConsoleObject;
import xyz.lebster.core.runtime.EvalError;
import xyz.lebster.core.runtime.MathObject;

@SpecificationURL("https://tc39.es/ecma262/multipage#sec-global-object")
public final class GlobalObject extends Dictionary {
	public GlobalObject() {
		super();

		// 19.1 Value Properties of the Global Object
		put("globalThis", this);
		put("NaN", new NumericLiteral(Double.NaN));
		put("Infinity", new NumericLiteral(Double.POSITIVE_INFINITY));
		put("undefined", Undefined.instance);

		// 19.2 Function Properties of the Global Object
		// FIXME: Follow spec
		setMethod("eval", (interpreter, arguments) -> {
			final String source = arguments.length > 0 ? arguments[0].toStringLiteral(interpreter).value : "undefined";
			try {
				final Program program = new Parser(new Lexer(source).tokenize()).parse();
				return program.execute(interpreter);
			} catch (CannotParse | SyntaxError e) {
				throw AbruptCompletion.error(new EvalError(e));
			}
		});

		put("Math", MathObject.instance);

		put("console", ConsoleObject.instance);
		Testing.addTestingMethods(this);
	}
}
package xyz.lebster.core.value.shadowrealm;

import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.Intrinsics;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.object.ObjectValue;
import xyz.lebster.core.value.primitive.string.StringValue;

import static xyz.lebster.core.node.declaration.Kind.Let;

public final class ShadowRealm extends ObjectValue {
	private final Interpreter interpreter = new Interpreter();

	public ShadowRealm(Intrinsics intrinsics) {
		super(intrinsics.shadowRealmPrototype);
	}

	public Value<?> evaluate(String sourceText) throws AbruptCompletion {
		return interpreter.runtimeParse(sourceText).execute(interpreter);
	}

	public void declare(StringValue name, Value<?> value) throws AbruptCompletion {
		interpreter.declareVariable(Let, name, value);
	}
}

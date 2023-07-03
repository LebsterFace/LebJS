package xyz.lebster.core.interpreter.environment;

import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.function.Executable;
import xyz.lebster.core.value.object.ObjectValue;

// TODO: [[ThisBindingStatus]]
public class FunctionEnvironment extends DeclarativeEnvironment implements ThisEnvironment {
	public final ObjectValue newTarget;
	public final Executable functionObject;
	public Value<?> thisValue;

	public FunctionEnvironment(Environment parent, Value<?> thisValue, ObjectValue newTarget, Executable functionObject) {
		super(parent);
		this.thisValue = thisValue;
		this.newTarget = newTarget;
		this.functionObject = functionObject;
	}

	@Override
	public Value<?> thisValue() {
		return thisValue;
	}
}

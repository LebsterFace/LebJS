package xyz.lebster.core.node.declaration;

import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.node.Dumpable;
import xyz.lebster.core.value.Value;

import java.util.List;

public interface AssignmentTarget extends Dumpable {
	List<BindingPair> getBindings(Interpreter interpreter, Value<?> input) throws AbruptCompletion;
}
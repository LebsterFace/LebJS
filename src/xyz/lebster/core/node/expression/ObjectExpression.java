package xyz.lebster.core.node.expression;

import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.node.SourceRange;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.function.Executable;
import xyz.lebster.core.value.function.Function;
import xyz.lebster.core.value.object.AccessorDescriptor;
import xyz.lebster.core.value.object.Key;
import xyz.lebster.core.value.object.ObjectValue;
import xyz.lebster.core.value.primitive.string.StringValue;

import java.util.ArrayList;

@SpecificationURL("https://tc39.es/ecma262/multipage#sec-object-initializer")
public final class ObjectExpression implements Expression {
	public final ArrayList<ObjectEntryNode> entries = new ArrayList<>();
	public SourceRange range;

	public ObjectExpression() {
	}

	@Override
	public ObjectValue execute(Interpreter interpreter) throws AbruptCompletion {
		final ObjectValue result = new ObjectValue(interpreter.intrinsics);
		for (final ObjectEntryNode entryNode : entries)
			entryNode.insertInto(result, interpreter);
		return result;
	}

	public interface ObjectEntryNode {
		void insertInto(ObjectValue result, Interpreter interpreter) throws AbruptCompletion;
	}

	@Override
	public SourceRange range() {
		return range;
	}

	public record EntryNode(Expression key, Expression value, boolean computed, boolean method) implements ObjectEntryNode {
		@Override
		public void insertInto(ObjectValue result, Interpreter interpreter) throws AbruptCompletion {
			final Key<?> executedKey = this.key.execute(interpreter).toPropertyKey(interpreter);
			final Value<?> executedValue = Executable.namedEvaluation(interpreter, value, executedKey);
			result.put(executedKey, executedValue, true, true, true);
		}
	}

	public record GetterSetterNode(boolean getter, Expression name, FunctionExpression value, boolean computed) implements ObjectEntryNode {
		@Override
		public void insertInto(ObjectValue result, Interpreter interpreter) throws AbruptCompletion {
			final Key<?> key = name.execute(interpreter).toPropertyKey(interpreter);
			final Function function = value.execute(interpreter);
			final String newName = "%s %s".formatted(getter ? "get" : "set", key.toFunctionName().value);
			function.updateName(new StringValue(newName));

			final var existing = result.value.get(key) instanceof AccessorDescriptor A ? A : null;
			final var descriptor = existing == null ? new AccessorDescriptor(null, null, true, true) : existing;
			if (getter) descriptor.setGetter(function);
			else descriptor.setSetter(function);
			if (existing == null) result.value.put(key, descriptor);
		}
	}

	public record ShorthandNode(StringValue key) implements ObjectEntryNode {
		@Override
		public void insertInto(ObjectValue result, Interpreter interpreter) throws AbruptCompletion {
			result.put(key, interpreter.getBinding(key).getValue(interpreter), true, true, true);
		}
	}

	public record SpreadNode(Expression name) implements ObjectEntryNode {
		@Override
		public void insertInto(ObjectValue result, Interpreter interpreter) throws AbruptCompletion {
			final ObjectValue value = name.execute(interpreter).toObjectValue(interpreter);
			for (final var entry : value.value.entrySet()) {
				if (entry.getValue().isEnumerable()) {
					result.put(entry.getKey(), value.get(interpreter, entry.getKey()));
				}
			}
		}
	}
}
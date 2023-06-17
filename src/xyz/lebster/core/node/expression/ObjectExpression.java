package xyz.lebster.core.node.expression;

import xyz.lebster.core.DumpBuilder;
import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.exception.ShouldNotHappen;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.node.Dumpable;
import xyz.lebster.core.node.SourceRange;
import xyz.lebster.core.node.expression.literal.StringLiteral;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.function.Executable;
import xyz.lebster.core.value.function.Function;
import xyz.lebster.core.value.object.AccessorDescriptor;
import xyz.lebster.core.value.object.Key;
import xyz.lebster.core.value.object.ObjectValue;
import xyz.lebster.core.value.primitive.string.StringValue;

import java.util.ArrayList;
import java.util.Iterator;

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

	@Override
	public void dump(int indent) {
		DumpBuilder.begin(indent)
			.self(this)
			.children("Entries", entries);
	}

	@Override
	public void represent(StringRepresentation representation) {
		representation.append("{ ");

		for (final Iterator<ObjectEntryNode> iterator = entries.iterator(); iterator.hasNext(); ) {
			final ObjectEntryNode entry = iterator.next();
			entry.represent(representation);
			if (iterator.hasNext()) representation.append(',');
			representation.append(' ');
		}

		representation.append('}');
	}

	public interface ObjectEntryNode extends Dumpable {
		void insertInto(ObjectValue result, Interpreter interpreter) throws AbruptCompletion;
	}

	private static void displayKey(Expression key, boolean computed, StringRepresentation representation) {
		if (computed) {
			representation.append('[');
			key.represent(representation);
			representation.append("]");
		} else {
			((StringLiteral) key).value().displayForObjectKey(representation);
		}
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

		@Override
		public void dump(int indent) {
			DumpBuilder.begin(indent)
				.self(this)
				.child("Key", key)
				.child("Value", value);
		}

		@Override
		public void represent(StringRepresentation representation) {
			displayKey(key, computed, representation);
			if (method) {
				if (!(value instanceof final FunctionExpression function))
					throw new ShouldNotHappen("Method EntryNode had non-function value");
				function.representCall(representation);
				representation.append(' ');
				function.body().represent(representation);
			} else {
				representation.append(": ");
				value.represent(representation);
			}
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

		@Override
		public void dump(int indent) {
			DumpBuilder.begin(indent)
				.selfParameterized(this, getter ? "Getter" : "Setter")
				.child("Name", name)
				.child("Function", value);
		}

		@Override
		public void represent(StringRepresentation representation) {
			representation.append(getter ? "get " : "set ");
			displayKey(name, computed, representation);
			value.representCall(representation);
			representation.append(' ');
			value.body().represent(representation);
		}
	}

	public record ShorthandNode(StringValue key) implements ObjectEntryNode {
		@Override
		public void insertInto(ObjectValue result, Interpreter interpreter) throws AbruptCompletion {
			result.put(key, interpreter.getBinding(key).getValue(interpreter), true, true, true);
		}

		@Override
		public void dump(int indent) {
			DumpBuilder.begin(indent)
				.selfValue(this, key.value);
		}

		@Override
		public void represent(StringRepresentation representation) {
			representation.append(key.value);
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

		@Override
		public void dump(int indent) {
			DumpBuilder.begin(indent)
				.self(this)
				.container(name);
		}

		@Override
		public void represent(StringRepresentation representation) {
			representation.append("...");
			name.represent(representation);
		}
	}
}
package xyz.lebster.core.node.expression;

import xyz.lebster.core.DumpBuilder;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.node.Dumpable;
import xyz.lebster.core.node.SourceRange;
import xyz.lebster.core.value.Names;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.function.Executable;
import xyz.lebster.core.value.object.ObjectValue;
import xyz.lebster.core.value.string.StringValue;

import java.util.ArrayList;
import java.util.Iterator;

public record ObjectExpression(SourceRange range, ArrayList<ObjectEntryNode> entries) implements Expression {

	public static StaticEntryNode staticEntry(StringValue key, Expression value) {
		return new StaticEntryNode(key, value);
	}

	public static ComputedKeyEntryNode computedKeyEntry(Expression key, Expression value) {
		return new ComputedKeyEntryNode(key, value);
	}

	public static ShorthandEntryNode shorthandEntry(StringValue key) {
		return new ShorthandEntryNode(key);
	}

	public static SpreadEntryNode spreadEntry(Expression name) {
		return new SpreadEntryNode(name);
	}

	@Override
	public ObjectValue execute(Interpreter interpreter) throws AbruptCompletion {
		final ObjectValue result = new ObjectValue(interpreter.intrinsics.objectPrototype);
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

	private record StaticEntryNode(StringValue key, Expression value) implements ObjectEntryNode {
		@Override
		public void insertInto(ObjectValue result, Interpreter interpreter) throws AbruptCompletion {
			final Value<?> executedValue = value.execute(interpreter);
			if (Executable.isAnonymousFunctionExpression(value) && executedValue instanceof final Executable function) {
				function.set(interpreter, Names.name, key);
				function.updateName(key.toFunctionName());
			}

			result.putEnumerable(key, executedValue);
		}

		@Override
		public void dump(int indent) {
			DumpBuilder.begin(indent)
				.self(this)
				.singleChild("Key", key.value)
				.child("Value", value);
		}

		@Override
		public void represent(StringRepresentation representation) {
			key.displayForObjectKey(representation);
			representation.append(": ");
			value.represent(representation);
		}
	}

	private record ComputedKeyEntryNode(Expression key, Expression value) implements ObjectEntryNode {
		@Override
		public void insertInto(ObjectValue result, Interpreter interpreter) throws AbruptCompletion {
			final ObjectValue.Key<?> executedKey = this.key.execute(interpreter).toPropertyKey(interpreter);
			final Value<?> executedValue = value.execute(interpreter);

			if (Executable.isAnonymousFunctionExpression(value) && executedValue instanceof final Executable function) {
				function.set(interpreter, Names.name, executedKey);
				function.updateName(executedKey.toFunctionName());
			}

			result.putEnumerable(executedKey, executedValue);
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
			representation.append('[');
			key.represent(representation);
			representation.append("]: ");
			value.represent(representation);
		}
	}

	private record ShorthandEntryNode(StringValue key) implements ObjectEntryNode {
		@Override
		public void insertInto(ObjectValue result, Interpreter interpreter) throws AbruptCompletion {
			result.putEnumerable(key, interpreter.getBinding(key).getValue(interpreter));
		}

		@Override
		public void dump(int indent) {
			DumpBuilder.begin(indent)
				.selfNamed(this, key.value);
		}

		@Override
		public void represent(StringRepresentation representation) {
			representation.append(key.value);
		}
	}

	private record SpreadEntryNode(Expression name) implements ObjectEntryNode {
		@Override
		public void insertInto(ObjectValue result, Interpreter interpreter) throws AbruptCompletion {
			final ObjectValue value = name.execute(interpreter).toObjectValue(interpreter);
			for (final var entry : value.entries()) {
				if (entry.getValue().isEnumerable()) {
					result.put(entry.getKey(), value.get(interpreter, entry.getKey()));
				}
			}
		}

		@Override
		public void dump(int indent) {
			DumpBuilder.begin(indent)
				.self(this)
				.child("Key", name);
		}

		@Override
		public void represent(StringRepresentation representation) {
			representation.append("...");
			name.represent(representation);
		}
	}
}
package xyz.lebster.core.node.expression;

import xyz.lebster.core.DumpBuilder;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.node.Dumpable;
import xyz.lebster.core.value.Names;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.function.Executable;
import xyz.lebster.core.value.object.ObjectValue;
import xyz.lebster.core.value.string.StringValue;

import java.util.ArrayList;
import java.util.Iterator;

public record ObjectExpression(ArrayList<ObjectEntryNode> entries) implements Expression {
	public ObjectExpression() {
		this(new ArrayList<>());
	}

	public void staticEntry(StringValue key, Expression value) {
		this.entries.add(new StaticEntryNode(key, value));
	}

	public void computedKeyEntry(Expression key, Expression value) {
		this.entries.add(new ComputedKeyEntryNode(key, value));
	}

	public void shorthandEntry(StringValue key) {
		this.entries.add(new ShorthandEntryNode(key));
	}

	public void spreadEntry(Expression name) {
		this.entries.add(new SpreadEntryNode(name));
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
		DumpBuilder.notImplemented(indent, this);
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

	private interface ObjectEntryNode extends Dumpable {
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
			DumpBuilder.begin(indent).self(this)
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

			result.put(executedKey, executedValue);
		}

		@Override
		public void dump(int indent) {
			DumpBuilder.begin(indent).self(this)
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
			result.put(key, interpreter.getBinding(key).getValue(interpreter));
		}

		@Override
		public void dump(int indent) {
			DumpBuilder.begin(indent).self(this)
				.singleChild("Key", key.value);
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
			for (final ObjectValue.Key<?> key : value.value.keySet())
				result.put(key, value.get(interpreter, key));
		}

		@Override
		public void dump(int indent) {
			DumpBuilder.begin(indent).self(this)
				.child("Key", name);
		}

		@Override
		public void represent(StringRepresentation representation) {
			representation.append("...");
			name.represent(representation);
		}
	}
}
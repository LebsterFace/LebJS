package xyz.lebster.core.value;

import xyz.lebster.core.ANSI;
import xyz.lebster.core.exception.ShouldNotHappen;
import xyz.lebster.core.value.array.ArrayObject;
import xyz.lebster.core.value.object.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

public final class JSONDisplayer {
	private final HashMap<ObjectValue, ObjectNode> objects = new HashMap<>();
	private int lastID = 0;
	private int currentIndent = 0;

	private JSONDisplayer() {
	}

	private void indent() {
		currentIndent++;
	}

	private void unindent() {
		currentIndent--;
	}

	private void appendIndent(StringBuilder builder) {
		builder.append("\t".repeat(currentIndent));
	}

	public static void display(StringBuilder builder, ObjectValue object, boolean forceJSON) {
		final DisplayNode rootNode = new JSONDisplayer().buildTreeFromValue(object, forceJSON);
		rootNode.display(builder);
	}

	private DisplayNode buildTreeFromValue(Value<?> current, boolean forceJSON) {
		if (current instanceof final ObjectValue currentObject && (forceJSON || currentObject.displayAsJSON())) {
			final ObjectNode found = objects.get(currentObject);
			if (found != null) {
				if (found.id == -1) found.id = ++lastID;
				return new ReferenceNode(found.id);
			}

			final ObjectNode result = new ObjectNode(currentObject);
			objects.put(currentObject, result);

			for (final Displayable value : currentObject.displayableValues()) {
				final DisplayNode node = buildTree(value);
				result.values.add(node);
			}

			for (final Entry<Key<?>, PropertyDescriptor> entry : currentObject.displayableProperties()) {
				DisplayNode node;
				PropertyDescriptor value = entry.getValue();
				if (value instanceof final DataDescriptor descriptor) {
					node = buildTreeFromValue(descriptor.value(), false);
				} else if (value instanceof final AccessorDescriptor descriptor) {
					node = new DisplayableNode(descriptor);
				} else {
					throw new ShouldNotHappen("Displaying " + value.getClass().getSimpleName());
				}
				result.properties.put(entry.getKey(), node);
			}

			return result;
		} else {
			return new DisplayableNode(current);
		}
	}

	private DisplayNode buildTree(Displayable d) {
		if (d == null) return null;
		if (d instanceof final Value<?> value) return buildTreeFromValue(value, false);
		if (d instanceof final DataDescriptor descriptor) return buildTreeFromValue(descriptor.value(), false);
		return new DisplayableNode(d);
	}

	private sealed interface DisplayNode extends Displayable {
		@Override
		default void display(StringBuilder builder) {
			final var firstAttempt = new StringBuilder();
			display(firstAttempt, true);
			if (ANSI.stripFormatting(firstAttempt.toString()).length() >= 72) {
				display(builder, false);
			} else {
				builder.append(firstAttempt);
			}
		}

		void display(StringBuilder builder, boolean singleLine);
	}

	private record DisplayableNode(Displayable displayable) implements DisplayNode {
		@Override
		public void display(StringBuilder builder, boolean singleLine) {
			displayable.display(builder);
		}
	}

	private record ReferenceNode(int id) implements DisplayNode {
		@Override
		public void display(StringBuilder builder, boolean singleLine) {
			builder.append(ANSI.BRIGHT_CYAN);
			builder.append("[Circular *");
			builder.append(id);
			builder.append(']');
			builder.append(ANSI.RESET);
		}
	}

	private final class ObjectNode implements DisplayNode {
		final ArrayList<DisplayNode> values;
		final HashMap<Key<?>, DisplayNode> properties;
		private final ObjectValue value;
		int id;

		ObjectNode(ObjectValue value) {
			this.value = value;
			this.values = new ArrayList<>();
			this.properties = new HashMap<>();
			this.id = -1;
		}

		private static void displayEmpty(StringBuilder builder, int emptyCount) {
			builder.append(ANSI.BRIGHT_BLACK);
			builder.append(emptyCount == 1 ? "empty" : "empty x " + emptyCount);
			builder.append(ANSI.RESET);
		}

		private static boolean hidePrefix(ObjectValue value) {
			if (value.getPrototype() == null) return false;
			return value.getClass() == ObjectValue.class ||
				   value.getClass() == ArrayObject.class;
		}

		private static void appendDelimiter(StringBuilder builder, boolean singleLine, boolean more) {
			if (more) builder.append(',');
			if (singleLine) builder.append(' ');
			else builder.append('\n');
		}

		@Override
		public void display(StringBuilder builder, boolean singleLine) {
			final boolean isArray = value.getClass() == ArrayObject.class;
			final var valuesIt = values.iterator();
			final var propertiesIt = properties.entrySet().iterator();

			if (this.id != -1) {
				builder.append(ANSI.BRIGHT_CYAN);
				builder.append("<ref *");
				builder.append(this.id);
				builder.append('>');
				builder.append(ANSI.RESET);
				builder.append(' ');
			}

			if (!hidePrefix(value)) {
				if (value.getPrototype() == null) builder.append("[");
				value.displayPrefix(builder);
				if (value.getPrototype() == null) {
					builder.append(": ");
					builder.append(ANSI.RESET);
					builder.append(ANSI.BOLD);
					builder.append("null");
					builder.append(ANSI.RESET);
					builder.append(" prototype]");
				}
				builder.append(' ');
			}

			builder.append(isArray ? '[' : '{');

			if (singleLine) {
				builder.append(' ');
			} else {
				indent();
				builder.append('\n');
			}

			int emptyCount = 0;
			while (valuesIt.hasNext()) {
				final DisplayNode next = valuesIt.next();
				if (next == null) {
					emptyCount++;
					continue;
				}

				if (emptyCount > 0) {
					if (!singleLine) appendIndent(builder);
					displayEmpty(builder, emptyCount);
					appendDelimiter(builder, singleLine, true);
					emptyCount = 0;
				}

				if (!singleLine) appendIndent(builder);
				next.display(builder);
				appendDelimiter(builder, singleLine, valuesIt.hasNext() || propertiesIt.hasNext());
			}

			if (emptyCount > 0) {
				if (!singleLine) appendIndent(builder);
				displayEmpty(builder, emptyCount);
				appendDelimiter(builder, singleLine, propertiesIt.hasNext());
			}

			while (propertiesIt.hasNext()) {
				final var entry = propertiesIt.next();
				if (!singleLine) appendIndent(builder);
				entry.getKey().displayForObjectKey(builder);
				builder.append(ANSI.RESET);
				builder.append(": ");
				entry.getValue().display(builder);
				appendDelimiter(builder, singleLine, propertiesIt.hasNext());
			}

			if (!singleLine) {
				unindent();
				appendIndent(builder);
			}

			builder.append(isArray ? ']' : '}');
		}
	}
}

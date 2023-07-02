package xyz.lebster.core.value;

import xyz.lebster.core.ANSI;
import xyz.lebster.core.exception.ShouldNotHappen;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.value.array.ArrayObject;
import xyz.lebster.core.value.object.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

interface DisplayNode extends Displayable {
	@Override
	default void display(StringRepresentation representation) {
		final var firstAttempt = new StringRepresentation();
		display(firstAttempt, true);
		if (firstAttempt.length() >= 72) {
			display(representation, false);
		} else {
			representation.append(firstAttempt);
		}
	}

	void display(StringRepresentation representation, boolean singleLine);
}

record DisplayableNode(Displayable displayable) implements DisplayNode {
	@Override
	public void display(StringRepresentation representation, boolean singleLine) {
		displayable.display(representation);
	}
}

record ReferenceNode(int id) implements DisplayNode {
	@Override
	public void display(StringRepresentation representation, boolean singleLine) {
		representation.append(ANSI.BRIGHT_CYAN);
		representation.append("[Circular *");
		representation.append(id);
		representation.append(']');
		representation.append(ANSI.RESET);
	}
}

final class ObjectNode implements DisplayNode {
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

	private static void displayEmpty(StringRepresentation representation, int emptyCount) {
		representation.append(ANSI.BRIGHT_BLACK);
		representation.append(emptyCount == 1 ? "empty" : "empty x " + emptyCount);
		representation.append(ANSI.RESET);
	}

	private static boolean hidePrefix(ObjectValue value) {
		if (value.getPrototype() == null) return false;
		return value.getClass() == ObjectValue.class ||
			   value.getClass() == ArrayObject.class;
	}

	private static void appendDelimiter(StringRepresentation representation, boolean singleLine, boolean more) {
		if (more) representation.append(',');
		if (singleLine) representation.append(' ');
		else representation.append('\n');
	}

	@Override
	public void display(StringRepresentation representation, boolean singleLine) {
		final boolean isArray = value.getClass() == ArrayObject.class;
		final var valuesIt = values.iterator();
		final var propertiesIt = properties.entrySet().iterator();

		if (this.id != -1) {
			representation.append(ANSI.BRIGHT_CYAN);
			representation.append("<ref *");
			representation.append(this.id);
			representation.append('>');
			representation.append(ANSI.RESET);
			representation.append(' ');
		}

		if (!hidePrefix(value)) {
			if (value.getPrototype() == null) representation.append("[");
			value.displayPrefix(representation);
			if (value.getPrototype() == null) {
				representation.append(": ");
				representation.append(ANSI.RESET);
				representation.append(ANSI.BOLD);
				representation.append("null");
				representation.append(ANSI.RESET);
				representation.append(" prototype]");
			}
			representation.append(' ');
		}

		representation.append(isArray ? '[' : '{');

		if (singleLine) {
			representation.append(' ');
		} else {
			representation.indent();
			representation.append('\n');
		}

		int emptyCount = 0;
		while (valuesIt.hasNext()) {
			final DisplayNode next = valuesIt.next();
			if (next == null) {
				emptyCount++;
				continue;
			}

			if (emptyCount > 0) {
				if (!singleLine) representation.appendIndent();
				displayEmpty(representation, emptyCount);
				appendDelimiter(representation, singleLine, true);
				emptyCount = 0;
			}

			if (!singleLine) representation.appendIndent();
			next.display(representation);
			appendDelimiter(representation, singleLine, valuesIt.hasNext() || propertiesIt.hasNext());
		}

		if (emptyCount > 0) {
			if (!singleLine) representation.appendIndent();
			displayEmpty(representation, emptyCount);
			appendDelimiter(representation, singleLine, propertiesIt.hasNext());
		}

		while (propertiesIt.hasNext()) {
			final var entry = propertiesIt.next();
			if (!singleLine) representation.appendIndent();
			entry.getKey().displayForObjectKey(representation);
			representation.append(ANSI.RESET);
			representation.append(": ");
			entry.getValue().display(representation);
			appendDelimiter(representation, singleLine, propertiesIt.hasNext());
		}

		if (!singleLine) {
			representation.unindent();
			representation.appendIndent();
		}

		representation.append(isArray ? ']' : '}');
	}
}

public final class JSONDisplayer {
	private final HashMap<ObjectValue, ObjectNode> objects = new HashMap<>();
	private int lastID = 0;

	private JSONDisplayer() {
	}

	public static void display(StringRepresentation representation, ObjectValue object) {
		display(representation, object, false);
	}

	public static void display(StringRepresentation representation, ObjectValue object, boolean forceJSON) {
		final DisplayNode rootNode = new JSONDisplayer().buildTreeFromValue(object, forceJSON);
		rootNode.display(representation);
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
}

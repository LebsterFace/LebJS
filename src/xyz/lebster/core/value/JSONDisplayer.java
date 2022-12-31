package xyz.lebster.core.value;

import xyz.lebster.core.ANSI;
import xyz.lebster.core.exception.ShouldNotHappen;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.value.array.ArrayObject;
import xyz.lebster.core.value.object.AccessorDescriptor;
import xyz.lebster.core.value.object.DataDescriptor;
import xyz.lebster.core.value.object.ObjectValue;
import xyz.lebster.core.value.object.ObjectValue.Key;
import xyz.lebster.core.value.object.PropertyDescriptor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

interface DisplayNode {
	void display(boolean singleLine, StringRepresentation representation);
}

record AccessorNode(AccessorDescriptor descriptor) implements DisplayNode {
	@Override
	public void display(boolean singleLine, StringRepresentation representation) {
		descriptor.display(representation);
	}
}

record ValueNode(Value<?> value) implements DisplayNode {
	@Override
	public void display(boolean singleLine, StringRepresentation representation) {
		value.display(representation);
	}
}

record ReferenceNode(int id) implements DisplayNode {
	@Override
	public void display(boolean singleLine, StringRepresentation representation) {
		representation.append(ANSI.BRIGHT_CYAN);
		representation.append("[Circular *");
		representation.append(id);
		representation.append(']');
		representation.append(ANSI.RESET);
	}
}

final class ObjectNode implements DisplayNode {
	private final ObjectValue value;
	final ArrayList<DisplayNode> values;
	final HashMap<Key<?>, DisplayNode> properties;
	int id;

	ObjectNode(ObjectValue value) {
		this.value = value;
		this.values = new ArrayList<>();
		this.properties = new HashMap<>();
		this.id = -1;
	}

	@Override
	public void display(boolean singleLine, StringRepresentation representation) {
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
			final var next = valuesIt.next();
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
			next.display(singleLine, representation);
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
			entry.getValue().display(singleLine, representation);
			appendDelimiter(representation, singleLine, propertiesIt.hasNext());
		}

		if (!singleLine) {
			representation.unindent();
			representation.appendIndent();
		}

		representation.append(isArray ? ']' : '}');
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
}

public final class JSONDisplayer {
	private final HashMap<ObjectValue, ObjectNode> objects = new HashMap<>();
	private int lastID = 0;

	private JSONDisplayer() {
	}

	public static void display(StringRepresentation representation, ObjectValue object) {
		final var singleLine = new StringRepresentation();
		final JSONDisplayer displayer = new JSONDisplayer();
		final DisplayNode rootNode = displayer.buildTree(object);
		rootNode.display(true, singleLine);
		if (singleLine.length() >= 72) {
			rootNode.display(false, representation);
		} else {
			representation.append(singleLine);
		}
	}

	public static void display(StringRepresentation representation, ObjectValue object, boolean singleLine, boolean forceJSON) {
		final JSONDisplayer displayer = new JSONDisplayer();
		final DisplayNode rootNode = displayer.buildTree(object, forceJSON);
		rootNode.display(singleLine, representation);
	}

	private DisplayNode buildTree(Value<?> current, boolean forceJSON) {
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
				final DisplayNode node = buildTree(entry.getValue());
				result.properties.put(entry.getKey(), node);
			}

			return result;
		} else {
			return new ValueNode(current);
		}
	}

	private DisplayNode buildTree(Displayable d) {
		if (d == null) return null;
		if (d instanceof final Value<?> value) return buildTree(value);
		if (d instanceof final PropertyDescriptor descriptor) return buildTree(descriptor);
		throw new ShouldNotHappen("Attempting to build tree for " + d.getClass().getSimpleName());
	}

	private DisplayNode buildTree(Value<?> current) {
		return buildTree(current, false);
	}

	private DisplayNode buildTree(PropertyDescriptor value) {
		if (value instanceof final DataDescriptor descriptor)
			return buildTree(descriptor.value());
		if (value instanceof final AccessorDescriptor descriptor)
			return new AccessorNode(descriptor);
		throw new ShouldNotHappen("Displaying " + value.getClass().getSimpleName());
	}
}

package xyz.lebster.core.value.set;

import xyz.lebster.core.ANSI;
import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.interpreter.Intrinsics;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.object.DataDescriptor;
import xyz.lebster.core.value.object.ObjectValue;
import xyz.lebster.core.value.object.PropertyDescriptor;
import xyz.lebster.core.value.primitive.number.NumberValue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

@SpecificationURL("https://tc39.es/ecma262/multipage#sec-set-objects")
public final class SetObject extends ObjectValue {
	public final ArrayList<Value<?>> setData;

	public SetObject(Intrinsics intrinsics, ArrayList<Value<?>> setData) {
		super(intrinsics.setPrototype);
		this.setData = setData;
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-get-set.prototype.size")
	NumberValue getSize() {
		// 3. Let entries be the List that is S.[[SetData]].
		// 4. Let count be 0.
		int count = 0;
		// 5. For each element e of entries, do
		for (final Value<?> e : setData) {
			// a. If e is not empty, set count to count + 1.
			if (e != null) count += 1;
		}

		// 6. Return 𝔽(count).
		return new NumberValue(count);
	}

	@Override
	public void displayRecursive(StringRepresentation representation, HashSet<ObjectValue> parents, boolean singleLine) {
		representation.append(ANSI.CYAN);
		representation.append("Set");
		representation.append(ANSI.RESET);
		representation.append('(');
		getSize().display(representation);
		representation.append(") { ");

		parents.add(this);
		if (!singleLine) {
			representation.appendLine();
			representation.indent();
		}

		this.representValues(representation, parents, singleLine);

		if (!singleLine) {
			representation.unindent();
			representation.appendIndent();
		}

		representation.append('}');
	}

	@SuppressWarnings("unchecked")
	private void representValues(StringRepresentation representation, HashSet<ObjectValue> parents, boolean singleLine) {
		final Iterator<Map.Entry<Key<?>, PropertyDescriptor>> propertiesIterator = value.entrySet().iterator();
		final Iterator<Value<?>> elementsIterator = setData.iterator();
		while (elementsIterator.hasNext()) {
			if (!singleLine) representation.appendIndent();
			final Value<?> next = elementsIterator.next();
			if (next == null) continue;

			HashSet<ObjectValue> newParents = (HashSet<ObjectValue>) parents.clone();
			newParents.add(this);
			DataDescriptor.display(next, representation, this, newParents, singleLine);
			representPropertyDelimiter(elementsIterator.hasNext() || propertiesIterator.hasNext(), representation, singleLine);
		}

		representProperties(representation, parents, singleLine, propertiesIterator);
	}

}

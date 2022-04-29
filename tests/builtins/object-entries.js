function expectForObj(O) {
	const entries = Object.entries(O);
	const keys = Object.keys(O);
	const values = Object.values(O);

	Test.expect(2, values.length);
	Test.expect(2, keys.length);
	Test.expect(2, entries.length);

	if (values[0] === "value") {
		Test.expect("numericProperty", values[1]);
	} else if (values[0] === "numericProperty") {
		Test.expect("value", values[1]);
	} else {
		Test.expect("numericProperty or value", values[0]);
	}

	if (keys[0] === "property") {
		Test.expect("1234", keys[1]);
	} else if (keys[0] === "1234") {
		Test.expect("property", keys[1]);
	} else {
		Test.expect("property or 1234", keys[0]);
	}

	if (entries[0][0] === "property") {
		Test.expect("value", entries[0][1]);
		Test.expect("numericProperty", entries[1][1]);
	} else if (entries[0][0] === "1234") {
		Test.expect("numericProperty", entries[0][1]);
		Test.expect("value", entries[1][1]);
	} else {
		Test.expect("property or 1234", entries[0][0]);
		Test.expect("value or numericProperty", entries[0][1]);
	}
}

const exampleObject = { "property": "value", 1234: "numericProperty" };
expectForObj(exampleObject);
const fromEntries = Object.fromEntries(Object.entries(exampleObject));
Test.expect(true, "property" in fromEntries);
Test.expect(true, "1234" in fromEntries);
Test.expect("value", fromEntries.property);
Test.expect("numericProperty", fromEntries[1234]);
expectForObj(fromEntries);
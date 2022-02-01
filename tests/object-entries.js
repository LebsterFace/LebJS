function expectForObj(O) {
	const entries = Object.entries(O);
	const keys = Object.keys(O);
	const values = Object.values(O);

	expect(2, values.length);
	expect(2, keys.length);
	expect(2, entries.length);

	if (values[0] === "value") {
		expect("numericProperty", values[1]);
	} else if (values[0] === "numericProperty") {
		expect("value", values[1]);
	} else {
		expect("numericProperty or value", values[0]);
	}

	if (keys[0] === "property") {
		expect("1234", keys[1]);
	} else if (keys[0] === "1234") {
		expect("property", keys[1]);
	} else {
		expect("property or 1234", keys[0]);
	}

	if (entries[0][0] === "property") {
		expect("value", entries[0][1]);
		expect("numericProperty", entries[1][1]);
	} else if (entries[0][0] === "1234") {
		expect("numericProperty", entries[0][1]);
		expect("value", entries[1][1]);
	} else {
		expect("property or 1234", entries[0][0]);
		expect("value or numericProperty", entries[0][1]);
	}
}

const exampleObject = { "property": "value", 1234: "numericProperty" };
expectForObj(exampleObject);
const fromEntries = Object.fromEntries(Object.entries(exampleObject));
expect(true, "property" in fromEntries);
expect(true, "1234" in fromEntries);
expect("value", fromEntries.property);
expect("numericProperty", fromEntries[1234]);
expectForObj(fromEntries);
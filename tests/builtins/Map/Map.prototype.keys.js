// length
Test.expect(0, Map.prototype.keys.length);

// basic functionality
const original = [
	["a", 0],
	["b", 1],
	["c", 2],
];
const a = new Map(original);
const it = a.keys();
Test.equals({ value: "a", done: false }, it.next());
Test.equals({ value: "b", done: false }, it.next());
Test.equals({ value: "c", done: false }, it.next());
Test.equals({ value: undefined, done: true }, it.next());
Test.equals({ value: undefined, done: true }, it.next());
Test.equals({ value: undefined, done: true }, it.next());
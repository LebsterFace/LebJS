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
Test.expectEqual({ value: "a", done: false }, it.next());
Test.expectEqual({ value: "b", done: false }, it.next());
Test.expectEqual({ value: "c", done: false }, it.next());
Test.expectEqual({ value: undefined, done: true }, it.next());
Test.expectEqual({ value: undefined, done: true }, it.next());
Test.expectEqual({ value: undefined, done: true }, it.next());
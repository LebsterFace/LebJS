// constructor properties
Test.expect(0, Map.length);
Test.expect("Map", Map.name);

// errors
// invalid array iterators
for (const value of [-100, Infinity, NaN, {}]) {
	Test.expectError("TypeError", "is not iterable", () => new Map(value));
}
// invalid iterator entries
Test.expectError("TypeError", "Iterator value 1 is not an entry object", () => new Map([1, 2, 3]));
// called without new
Test.expectError("TypeError", "Map constructor must be called with `new`", () => Map());

// normal behavior
Test.expect("object", typeof new Map());

// constructor with single entries array argument
let a = new Map([
	["a", 0],
	["b", 1],
	["c", 2],
]);
Test.expect(true, a instanceof Map);
Test.expect(3, a.size);
let seen = [false, false, false];
a.forEach(v => seen[v] = true);
Test.expect(true, seen[0] && seen[1] && seen[2]);
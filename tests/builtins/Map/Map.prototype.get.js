// basic functionality
Test.expect(1, Map.prototype.get.length);

const map = new Map([
	["a", 0],
	["b", 1],
	["c", 2],
]);

Test.expect(0, map.get("a"));
Test.expect(undefined, map.get("d"));

// NaN differentiation
map.set(NaN, "a");

Test.expect("a", map.get(0 / 0));
Test.expect("a", map.get(0 * Infinity));
Test.expect("a", map.get(Infinity - Infinity));
// basic functionality
Test.expect(0, Map.prototype.clear.length);

const map = new Map([
	["a", 0],
	["b", 1],
	["c", 2],
]);
Test.expect(3, map.size);
map.clear();
Test.expect(0, map.size);
// length is 1
Test.expect(1, Map.prototype.has.length);

// basic functionality
let map = new Map([
	["a", 0],
	[1, "b"],
	["c", 2],
]);

Test.expect(false, new Map().has());
Test.expect(true, new Map([{}]).has());
Test.expect(true, map.has("a"));
Test.expect(true, map.has(1));
Test.expect(false, map.has("serenity"));
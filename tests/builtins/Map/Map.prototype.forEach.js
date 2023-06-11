// length is 1
Test.expect(1, Map.prototype.forEach.length);

// errors
// requires at least one argument
Test.expectError("TypeError", "undefined is not a function", () => new Map().forEach());

// callback must be a function
Test.expectError("TypeError", "undefined is not a function", () => new Map().forEach(undefined));

// normal behavior
// never calls callback with empty set
{
	let callbackCalled = 0;
	Test.expect(undefined, new Map().forEach(() => callbackCalled++));
	Test.expect(0, callbackCalled);
}

// calls callback once for every item
{
	let callbackCalled = 0;
	const data = [
		["a", 0],
		["b", 1],
		["c", 2],
	];

	Test.expect(undefined, new Map(data).forEach(() => callbackCalled++));
	Test.expect(3, callbackCalled);
}

// callback receives value, key and map
{
	let a = new Map([
		["a", 0],
		["b", 1],
		["c", 2],
	]);

	a.forEach((value, key, map) => {
		Test.expect(true, a.has(key));
		Test.expect(value, a.get(key));
		Test.expect(a, map);
	});
}
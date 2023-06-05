Test.expect(1, Array.prototype.toSorted.length);

// null or undefined this value
Test.expectError("TypeError", "Cannot convert undefined to object", () => Array.prototype.toSorted.call());
Test.expectError("TypeError", "Cannot convert undefined to object", () => Array.prototype.toSorted.call(undefined));
Test.expectError("TypeError", "Cannot convert null to object", () => Array.prototype.toSorted.call(null));

// invalid compare function
Test.expectError("TypeError", "'foo' is not a function", () => [].toSorted("foo"));

// basic functionality
{
	const a = [2, 4, 1, 3, 5];
	const b = a.toSorted();
	Test.expect(false, a === b);
	Test.equals([2, 4, 1, 3, 5], a);
	Test.equals([1, 2, 3, 4, 5], b);
}

// custom compare function
{
	const a = [2, 4, 1, 3, 5];
	const b = a.toSorted(() => 0);
	Test.expect(false, a === b);
	Test.equals([2, 4, 1, 3, 5], a);
	Test.equals([2, 4, 1, 3, 5], b);
}

/* TODO: is unscopable
Test.expect(true, Array.prototype[Symbol.unscopables].toSorted);
const array = [];
with (array) {
	Test.expectError("ReferenceError", "'toSorted' is not defined", () => toSorted);
} */
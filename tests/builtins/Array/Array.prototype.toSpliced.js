Test.expect(2, Array.prototype.toSpliced.length);

// null or undefined this value
Test.expectError("TypeError", "Cannot convert undefined to object", () => Array.prototype.toSpliced.call());
Test.expectError("TypeError", "Cannot convert undefined to object", () => Array.prototype.toSpliced.call(undefined));
Test.expectError("TypeError", "Cannot convert null to object", () => Array.prototype.toSpliced.call(null));

/* FIXME:
{
	const a = { length: 2 ** 53 - 1 };
	Test.expectError("TypeError", "Maximum array size exceeded", () => Array.prototype.toSpliced.call(a, 0, 0, "foo"));
}

{
	const a = { length: 2 ** 32 - 1 };
	Test.expectError("RangeError", "Invalid array length", () => Array.prototype.toSpliced.call(a, 0, 0, "foo"));
} */

// no start or delete count argument
{
	const a = [1, 2, 3, 4, 5];
	const b = a.toSpliced();
	Test.expect(false, a === b);
	Test.equals([1, 2, 3, 4, 5], a);
	Test.equals([1, 2, 3, 4, 5], b);
};

// only start argument
{
	const a = [1, 2, 3, 4, 5];
	const values = [
		[0, []],
		[1, [1]],
		[4, [1, 2, 3, 4]],
		[-1, [1, 2, 3, 4]],
		[999, [1, 2, 3, 4, 5]],
		[Infinity, [1, 2, 3, 4, 5]],
	];
	for (const [start, expected] of values) {
		const b = a.toSpliced(start);
		Test.expect(false, a === b);
		Test.equals([1, 2, 3, 4, 5], a);
		Test.equals(expected, b);
	}
};

// start and delete count argument
{
	const a = [1, 2, 3, 4, 5];
	const values = [
		[0, 5, []],
		[1, 3, [1, 5]],
		[4, 1, [1, 2, 3, 4]],
		[-1, 1, [1, 2, 3, 4]],
		[999, 10, [1, 2, 3, 4, 5]],
		[Infinity, Infinity, [1, 2, 3, 4, 5]],
	];
	for (const [start, deleteCount, expected] of values) {
		const b = a.toSpliced(start, deleteCount);
		Test.expect(false, a === b);
		Test.equals([1, 2, 3, 4, 5], a);
		Test.equals(expected, b);
	}
}

// start, delete count, and items argument
{
	const a = [1, 2, 3, 4, 5];
	const values = [
		[0, 5, ["foo", "bar"], ["foo", "bar"]],
		[1, 3, ["foo", "bar"], [1, "foo", "bar", 5]],
		[4, 1, ["foo", "bar"], [1, 2, 3, 4, "foo", "bar"]],
		[-1, 1, ["foo", "bar"], [1, 2, 3, 4, "foo", "bar"]],
		[999, 10, ["foo", "bar"], [1, 2, 3, 4, 5, "foo", "bar"]],
		[Infinity, Infinity, ["foo", "bar"], [1, 2, 3, 4, 5, "foo", "bar"]],
	];
	for (const [start, deleteCount, items, expected] of values) {
		const b = a.toSpliced(start, deleteCount, ...items);

		Test.expect(false, a === b);
		Test.equals([1, 2, 3, 4, 5], a);
		Test.equals(expected, b);
	}
}

/* TODO: is unscopable
Test.expect(true, Array.prototype[Symbol.unscopables].toSpliced);
const array = [];
with (array) {
	Test.expectError("ReferenceError", "'toSpliced' is not defined", () => toSpliced);
} */
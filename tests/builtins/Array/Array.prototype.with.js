Test.expect(2, Array.prototype.with.length);

// null or undefined this value
Test.expectError("TypeError", "Cannot convert undefined to object", () => Array.prototype.with.call());
Test.expectError("TypeError", "Cannot convert undefined to object", () => Array.prototype.with.call(undefined));
Test.expectError("TypeError", "Cannot convert null to object", () => Array.prototype.with.call(null));

// out of range index
Test.expectError("RangeError", "Index 0 is out of range of array length 0", () => [].with(0, "foo"));
Test.expectError("RangeError", "Index -1 is out of range of array length 0", () => [].with(-1, "foo"));

// basic functionality
const a = [1, 2, 3, 4, 5];
const values = [
    [0, "foo", ["foo", 2, 3, 4, 5]],
    [-5, "foo", ["foo", 2, 3, 4, 5]],
    [4, "foo", [1, 2, 3, 4, "foo"]],
    [-1, "foo", [1, 2, 3, 4, "foo"]],
];

for (const [index, value, expected] of values) {
    const b = a.with(index, value);
    Test.expect(true, b !== a);
    Test.expectEqual([1, 2, 3, 4, 5], a);
    Test.expectEqual(expected, b);
}
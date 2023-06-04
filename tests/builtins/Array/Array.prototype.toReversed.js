Test.expect(0, Array.prototype.toReversed.length);

// null or undefined this value
Test.expectError("TypeError", "Cannot convert undefined to object", () => Array.prototype.toReversed.call());
Test.expectError("TypeError", "Cannot convert undefined to object", () => Array.prototype.toReversed.call(undefined));
Test.expectError("TypeError", "Cannot convert null to object", () => Array.prototype.toReversed.call(null));

// basic functionality
const a = [1, 2, 3, 4, 5];
const b = a.toReversed();
Test.expect(false, b === a);
Test.equals([1, 2, 3, 4, 5], a);
Test.equals([5, 4, 3, 2, 1], b);

/* TODO: is unscopable
Test.expect(true, Array.prototype[Symbol.unscopables].toReversed);
const array = [];
with (array) {
    Test.expectError("ReferenceError", "'toReversed' is not defined", () => toReversed);
} */
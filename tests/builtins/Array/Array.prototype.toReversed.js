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
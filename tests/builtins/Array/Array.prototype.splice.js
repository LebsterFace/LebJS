Test.expect(2, Array.prototype.splice.length);

let array = ["hello", "world", "goodbye", 1, 2];
let removed = array.splice(3);
Test.expectEqual(["hello", "world", "goodbye"], array);
Test.expectEqual([1, 2], removed);

array = ["hello", "world", "goodbye", 1, 2];
removed = array.splice(-2);
Test.expectEqual(["hello", "world", "goodbye"], array);
Test.expectEqual([1, 2], removed);

array = ["hello", "world", "goodbye", 1, 2];
removed = array.splice(-2, 1);
Test.expectEqual(["hello", "world", "goodbye", 2], array);
Test.expectEqual([1], removed);

array = ["goodbye"];
removed = array.splice(0, 0, "hello", "world");
Test.expectEqual(["hello", "world", "goodbye"], array);
Test.expectEqual([], removed);

array = ["goodbye", "world", "goodbye"];
removed = array.splice(0, 1, "hello");
Test.expectEqual(["hello", "world", "goodbye"], array);
Test.expectEqual(["goodbye"], removed);

array = ["foo", "bar", "baz"];
removed = array.splice();
Test.expectEqual(["foo", "bar", "baz"], array);
Test.expectEqual([], removed);

removed = array.splice(0, 123);
Test.expectEqual([], array);
Test.expectEqual(["foo", "bar", "baz"], removed);

array = ["foo", "bar", "baz"];
removed = array.splice(123, 123);
Test.expectEqual(["foo", "bar", "baz"], array);
Test.expectEqual([], removed);

array = ["foo", "bar", "baz"];
removed = array.splice(-123, 123);
Test.expectEqual([], array);
Test.expectEqual(["foo", "bar", "baz"], removed);

array = ["foo", "bar"];
removed = array.splice(1, 1, "baz");
Test.expectEqual(["foo", "baz"], array);
Test.expectEqual(["bar"], removed);

// FIXME
// const obj = { length: Math.pow(2, 32) };
// Test.expectError("RangeError", "Invalid array length", () => Array.prototype.splice.call(obj, 0));
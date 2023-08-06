Test.expect(2, Array.prototype.splice.length);

let array = ["hello", "world", "goodbye", 1, 2];
let removed = array.splice(3);
Test.equals(["hello", "world", "goodbye"], array);
Test.equals([1, 2], removed);

array = ["hello", "world", "goodbye", 1, 2];
removed = array.splice(-2);
Test.equals(["hello", "world", "goodbye"], array);
Test.equals([1, 2], removed);

array = ["hello", "world", "goodbye", 1, 2];
removed = array.splice(-2, 1);
Test.equals(["hello", "world", "goodbye", 2], array);
Test.equals([1], removed);

array = ["goodbye"];
removed = array.splice(0, 0, "hello", "world");
Test.equals(["hello", "world", "goodbye"], array);
Test.equals([], removed);

array = ["goodbye", "world", "goodbye"];
removed = array.splice(0, 1, "hello");
Test.equals(["hello", "world", "goodbye"], array);
Test.equals(["goodbye"], removed);

array = ["foo", "bar", "baz"];
removed = array.splice();
Test.equals(["foo", "bar", "baz"], array);
Test.equals([], removed);

removed = array.splice(0, 123);
Test.equals([], array);
Test.equals(["foo", "bar", "baz"], removed);

array = ["foo", "bar", "baz"];
removed = array.splice(123, 123);
Test.equals(["foo", "bar", "baz"], array);
Test.equals([], removed);

array = ["foo", "bar", "baz"];
removed = array.splice(-123, 123);
Test.equals([], array);
Test.equals(["foo", "bar", "baz"], removed);

array = ["foo", "bar"];
removed = array.splice(1, 1, "baz");
Test.equals(["foo", "baz"], array);
Test.equals(["bar"], removed);

// FIXME
// const obj = { length: Math.pow(2, 32) };
// Test.expectError("RangeError", "Invalid array length", () => Array.prototype.splice.call(obj, 0));
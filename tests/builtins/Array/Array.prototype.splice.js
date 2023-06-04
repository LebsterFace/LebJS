Test.expect(2, Array.prototype.splice.length);

const array = ["hello", "world", "lebjs", 1, 2];
const removed = array.splice(3);
Test.equals(["hello", "world", "lebjs"], array);
Test.equals([1, 2], removed);

array = ["hello", "world", "lebjs", 1, 2];
removed = array.splice(-2);
Test.equals(["hello", "world", "lebjs"], array);
Test.equals([1, 2], removed);

array = ["hello", "world", "lebjs", 1, 2];
removed = array.splice(-2, 1);
Test.equals(["hello", "world", "lebjs", 2], array);
Test.equals([1], removed);

array = ["lebjs"];
removed = array.splice(0, 0, "hello", "world");
Test.equals(["hello", "world", "lebjs"], array);
Test.equals([], removed);

array = ["goodbye", "world", "lebjs"];
removed = array.splice(0, 1, "hello");
Test.equals(["hello", "world", "lebjs"], array);
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

// TODO: Invalid lengths
Test.expect(1, Array.prototype.lastIndexOf.length);

const array = [1, 2, 3, 1, "hello"];
Test.expect(4, array.lastIndexOf("hello"));
Test.expect(4, array.lastIndexOf("hello", 1000));
Test.expect(3, array.lastIndexOf(1));
Test.expect(3, array.lastIndexOf(1, -1));
Test.expect(3, array.lastIndexOf(1, -2));
Test.expect(1, array.lastIndexOf(2));
Test.expect(1, array.lastIndexOf(2, -3));
Test.expect(1, array.lastIndexOf(2, -4));
Test.expect(-1, [].lastIndexOf("hello"));
Test.expect(-1, [].lastIndexOf("hello", 10));
Test.expect(-1, [].lastIndexOf("hello", -10));
Test.expect(-1, [].lastIndexOf());
Test.expect(0, [undefined].lastIndexOf());
Test.expect(2, [undefined, undefined, undefined].lastIndexOf());
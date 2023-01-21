// length is 1
Test.expect(1, Array.prototype.some.length);

// errors
// callback must be a function
Test.expectError("TypeError", "undefined is not a function", () => [].some(undefined));

// basic functionality
const array = ["hello", "friends", 1, 2, false, -42];
Test.expect(true, array.some(value => value === "hello"));
Test.expect(false, array.some(value => value === "leb"));
Test.expect(true, array.some((value, index, arr) => index === 1));
Test.expect(true, array.some(value => value.toString() === "1"));
Test.expect(true, array.some(value => value === 1));
Test.expect(false, array.some(value => value === 13));
Test.expect(true, array.some(value => typeof value !== "string"));
Test.expect(true, array.some(value => typeof value === "boolean"));
Test.expect(true, array.some(value => value > 1));
Test.expect(true, array.some(value => value > 1 && value < 3));
Test.expect(false, array.some(value => value > 100));
Test.expect(true, array.some(value => value < 0));
Test.expect(true, array.some(value => array.pop()));
Test.expect(true, ["", "hello", "friends", "leb"].some(value => value.length === 0));
Test.expect(false, [].some(value => value === 1));

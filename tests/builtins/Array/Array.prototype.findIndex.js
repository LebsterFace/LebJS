Test.expect(1, Array.prototype.findIndex.length);

// callback must be a function
Test.expectError("TypeError", "undefined is not a function", () => { [].findIndex(undefined); });

// basic functionality
const array = ["hello", "friends", 1, 2, false];
Test.expect(0, array.findIndex(value => value === "hello"));
Test.expect(1, array.findIndex((value, index, arr) => index === 1));
// TODO: Loose equality: Test.expect(2, array.findIndex(value => value == "1"));
Test.expect(2, array.findIndex(value => value === 1));
Test.expect(2, array.findIndex(value => typeof value !== "string"));
Test.expect(4, array.findIndex(value => typeof value === "boolean"));
Test.expect(3, array.findIndex(value => value > 1));
Test.expect(3, array.findIndex(value => value > 1 && value < 3));
Test.expect(-1, array.findIndex(value => value > 100));
Test.expect(-1, [].findIndex(value => value === 1));

// never calls callback with empty array
let c = 0;
Test.expect(-1, [].findIndex(() => { c++; }));
Test.expect(0, c);

// calls callback once for every item
Test.expect(-1, [1, 2, 3].findIndex(() => { c++; }));
Test.expect(3, c);

// empty slots are treated as undefined
c = 0;
const holes = [1, , , "foo", , undefined, , ,];
Test.expect(1, holes.findIndex(value => { c++; return value === undefined; }));
Test.expect(2, c);
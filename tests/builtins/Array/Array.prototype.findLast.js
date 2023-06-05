Test.expect(1, Array.prototype.findLast.length);

// callback must be a function
Test.expectError("TypeError", "undefined is not a function", () => { [].findLast(undefined); });

// basic functionality
const array = ["hello", "world", 1, 2, false];
Test.expect("hello", array.findLast(value => value === "hello"));
Test.expect("world", array.findLast((value, index, arr) => index === 1));
// TODO: Loose equality: Test.expect(1, array.findLast(value => value == "1"));
Test.expect(1, array.findLast(value => value === 1));
Test.expect(false, array.findLast(value => typeof value !== "string"));
Test.expect(false, array.findLast(value => typeof value === "boolean"));
Test.expect("world", array.findLast(value => typeof value === "string"));
Test.expect(2, array.findLast(value => value > 1));
Test.expect(2, array.findLast(value => value >= 1));
Test.expect(2, array.findLast(value => value > 1 && value < 3));
Test.expect(undefined, array.findLast(value => value > 100));
Test.expect(undefined, [].findLast(value => value === 1));

// never calls callback with empty array
let c = 0;
Test.expect(undefined, [].findLast(() => { c++; }));
Test.expect(0, c);

// calls callback once for every item
Test.expect(undefined, [1, 2, 3].findLast(() => { c++; }));
Test.expect(3, c);

// empty slots are treated as undefined
c = 0;
const holes = [1, , , "foo", , undefined, , , 6];
Test.expect(undefined, holes.findLast(value => { c++; return value === undefined; }));
Test.expect(2, c);
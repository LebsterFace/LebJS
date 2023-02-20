Test.expect(1, Array.prototype.find.length);

//callback must be a function
Test.expectError("TypeError", "undefined is not a function", () => [].find(undefined));

// basic functionality
const array = ["hello", "world", 1, 2, false];
Test.expect("hello", array.find(value => value === "hello"));
Test.expect("world", array.find((value, index, arr) => index === 1));
// TODO: Loose equality: Test.expect(1, array.find(value => value == "1"));
Test.expect(1, array.find(value => value === 1));
Test.expect(1, array.find(value => typeof value !== "string"));
Test.expect(false, array.find(value => typeof value === "boolean"));
Test.expect(2, array.find(value => value > 1));
Test.expect(2, array.find(value => value > 1 && value < 3));
Test.expect(undefined, array.find(value => value > 100));
Test.expect(undefined, [].find(value => value === 1));

// never calls callback with empty array
let c = 0;
Test.expect(undefined, [].find(() => { c++; }));
Test.expect(0, c);

// calls callback once for every item
Test.expect(undefined, [1, 2, 3].find(() => { c++; }));
Test.expect(3, c);

// empty slots are treated as undefined
c = 0;
const holes = [1, , , "foo", , undefined, , ,];
Test.expect(undefined, holes.find(value => { c++; return value === undefined; }));
Test.expect(2, c);

/* TODO: is unscopable
    Test.expect(true, Array.prototype[Symbol.unscopables].find);
    const array = [];
    with (array) {
        Test.expectError("ReferenceError", "'find' is not defined", () => { find; });
    } */
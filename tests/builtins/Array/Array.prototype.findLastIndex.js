Test.expect(1, Array.prototype.findLastIndex.length);

// callback must be a function
Test.expectError("TypeError", "undefined is not a function", () => { [].findLastIndex(undefined); });

// basic functionality
const array = ["hello", "friends", 1, 2, false];
Test.expect(0, array.findLastIndex(value => value === "hello"));
Test.expect(1, array.findLastIndex((value, index, arr) => index === 1));
// TODO: Loose equality: Test.expect(2, array.findLastIndex(value => value == "1"));
Test.expect(2, array.findLastIndex(value => value === 1));
Test.expect(4, array.findLastIndex(value => typeof value !== "string"));
Test.expect(4, array.findLastIndex(value => typeof value === "boolean"));
Test.expect(1, array.findLastIndex(value => typeof value === "string"));
Test.expect(3, array.findLastIndex(value => value > 1));
Test.expect(3, array.findLastIndex(value => value >= 1));
Test.expect(3, array.findLastIndex(value => value > 1 && value < 3));
Test.expect(-1, array.findLastIndex(value => value > 100));
Test.expect(-1, [].findLastIndex(value => value === 1));

// never calls callback with empty array
let c = 0;
Test.expect(-1, [].findLastIndex(() => { c++; }));
Test.expect(0, c);

// calls callback once for every item
Test.expect(-1, [1, 2, 3].findLastIndex(() => { c++; }));
Test.expect(3, c);

// empty slots are treated as undefined
c = 0;
const holes = [1, , , "foo", , undefined, , , 6];
Test.expect(7, holes.findLastIndex(value => { c++; return value === undefined; }));
Test.expect(2, c);

/* TODO: is unscopable
    expect(Array.prototype[Symbol.unscopables].findLastIndex).toBeTrue();
    const array = [];
    with (array) {
        expect(() => {
            findLastIndex;
        }).toThrowWithMessage(ReferenceError, "'findLastIndex' is not defined");
    } */
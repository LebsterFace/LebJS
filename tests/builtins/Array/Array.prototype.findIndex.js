Test.expect(1, Array.prototype.findIndex.length);

// errors
// callback must be a function
Test.expectError("TypeError", "undefined is not a function", () => {
    [].findIndex(undefined);
});

// normal behavior
// basic functionality
const array = ["hello", "world", 1, 2, false];
Test.expect(0, array.findIndex(value => value === "hello"));
Test.expect(1, array.findIndex((value, index, arr) => index === 1));
Test.expect(2, array.findIndex(value => String(value) === "1"));
Test.expect(2, array.findIndex(value => value === 1));
Test.expect(2, array.findIndex(value => typeof value !== "string"));
Test.expect(4, array.findIndex(value => typeof value === "boolean"));
Test.expect(3, array.findIndex(value => value > 1));
Test.expect(3, array.findIndex(value => value > 1 && value < 3));
Test.expect(-1, array.findIndex(value => value > 100));
Test.expect(-1, [].findIndex(value => value === 1));

// never calls callback with empty array
{
    const callbackCalled = 0;
    Test.expect(-1, [].findIndex(() => {
        callbackCalled++;
    }));
    Test.expect(0, callbackCalled);
}

// calls callback once for every item
{
    const callbackCalled = 0;
    Test.expect(-1, [1, 2, 3].findIndex(() => {
        callbackCalled++;
    }));
    Test.expect(3, callbackCalled);
}

// empty slots are treated as undefined
{
    const callbackCalled = 0;
    Test.expect(1, [1, , , "foo", , undefined, , , ].findIndex(value => {
        callbackCalled++;
        return value === undefined;
    }));
    Test.expect(2, callbackCalled);
}
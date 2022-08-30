{
    // callback must be a function
    Test.expectError("TypeError", "undefined is not a function", () => [].find(undefined));
}

{
    // basic functionality
    const array = ["hello", "world", 1, 2, false];
    Test.expect("hello", array.find(value => value === "hello"));
    Test.expect("world", array.find((value, index, arr) => index === 1));
    // Test.expect(1, array.find(value => value == "1"));
    Test.expect(1, array.find(value => value === 1));
    Test.expect(1, array.find(value => typeof value !== "string"));
    Test.expect(false, array.find(value => typeof value === "boolean"));
    Test.expect(2, array.find(value => value > 1));
    Test.expect(2, array.find(value => value > 1 && value < 3));
    Test.expect(undefined, array.find(value => value > 100));
    Test.expect(undefined, [].find(value => value === 1));
}

{
    // never calls callback with empty array
    var callbackCalled = 0;
    Test.expect(undefined, [].find(() => {
        callbackCalled++;
    }));
    Test.expect(0, callbackCalled);
}

{
    // calls callback once for every item
    var callbackCalled = 0;
    Test.expect(undefined, [1, 2, 3].find(() => {
        callbackCalled++;
    }));
    Test.expect(3, callbackCalled);
}

{
    // empty slots are treated as undefined
    var callbackCalled = 0;
    Test.expect(undefined, [1, , , "foo", , undefined, , , ].find(value => {
        callbackCalled++;
        return value === undefined;
    }));
    Test.expect(2, callbackCalled);
}
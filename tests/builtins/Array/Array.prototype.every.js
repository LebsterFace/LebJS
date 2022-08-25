// callback must be a function
Test.expectError("TypeError", "undefined is not a function", () => {
    [].every(undefined);
})

// normal behavior
// basic functionality
const arrayOne = ["serenity", {
    test: "serenity"
}];
Test.expect(false, arrayOne.every(value => value === "hello"))
Test.expect(false, arrayOne.every(value => value === "serenity"))
Test.expect(true, arrayOne.every((value, index, arr) => index < 2))
Test.expect(false, arrayOne.every(value => typeof value === "string"))
Test.expect(true, arrayOne.every(value => arrayOne.pop()))

const arrayTwo = [true, false, 1, 2, 3, "3"];
Test.expect(false, arrayTwo.every((value, index, arr) => index > 0))
Test.expect(true, arrayTwo.every((value, index, arr) => index >= 0))
Test.expect(false, arrayTwo.every(value => typeof value !== "string"))
Test.expect(false, arrayTwo.every(value => typeof value === "number"))
Test.expect(false, arrayTwo.every(value => value > 0))
Test.expect(true, arrayTwo.every(value => value >= 0 && value < 4))
Test.expect(true, arrayTwo.every(value => arrayTwo.pop()))

Test.expect(true, ["", "hello", "friends", "serenity"].every(value => value.length >= 0))

// empty array
Test.expect(true, [].every(value => value === 1))

// elements past the initial array size are ignored
const array = [1, 2, 3, 4, 5];

Test.expect(true,
    array.every((value, index, arr) => {
        arr.push(6);
        return value <= 5;
    })
);
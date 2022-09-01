// correct behavior
{
    // basic functionality
    let o;

    o = {};
    Test.expect(o.toLocaleString(), o.toString());

    o = { toString: () => 42 };
    Test.expect(42, o.toString());
}

// errors
{
    // toString that throws error
    {
        let o = {
            toString: () => {
                throw new Error();
            },
        };

        Test.expectError("Error", "", () => o.toLocaleString());
    };

    // toString that is not a function
    {
        let o = { toString: "foo" };
        Test.expectError("TypeError", "'foo' is not a function", () => o.toLocaleString());
    };
}

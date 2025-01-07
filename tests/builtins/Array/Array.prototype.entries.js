// length
Test.expect(0, Array.prototype.entries.length);

// basic functionality
{
    const a = ["a", "b", "c"];
    const it = a.entries();
    Test.expectEqual({ value: [0, "a"], done: false }, it.next());
    Test.expectEqual({ value: [1, "b"], done: false }, it.next());
    Test.expectEqual({ value: [2, "c"], done: false }, it.next());
    Test.expectEqual({ value: undefined, done: true }, it.next());
    Test.expectEqual({ value: undefined, done: true }, it.next());
    Test.expectEqual({ value: undefined, done: true }, it.next());
}

// works when applied to non-object
{
    [true, false, 9, 2, Symbol()].forEach(primitive => {
        const it = [].entries.call(primitive);
        Test.expectEqual({ value: undefined, done: true }, it.next());
        Test.expectEqual({ value: undefined, done: true }, it.next());
        Test.expectEqual({ value: undefined, done: true }, it.next());
    });
}

// item added to array before exhaustion is accessible
{
    const a = ["a", "b"];
    const it = a.entries();
    Test.expectEqual({ value: [0, "a"], done: false }, it.next());
    Test.expectEqual({ value: [1, "b"], done: false }, it.next());
    a.push("c");
    Test.expectEqual({ value: [2, "c"], done: false }, it.next());
    Test.expectEqual({ value: undefined, done: true }, it.next());
    Test.expectEqual({ value: undefined, done: true }, it.next());
}

// item added to array after exhaustion is inaccessible
{
    const a = ["a", "b"];
    const it = a.entries();
    Test.expectEqual({ value: [0, "a"], done: false }, it.next());
    Test.expectEqual({ value: [1, "b"], done: false }, it.next());
    Test.expectEqual({ value: undefined, done: true }, it.next());
    a.push("c");
    Test.expectEqual({ value: undefined, done: true }, it.next());
}
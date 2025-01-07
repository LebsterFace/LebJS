// length
Test.expect(0, Array.prototype.values.length);

// basic functionality
{
    const a = [1, 2, 3];
    const it = a.values();
    Test.expectEqual({ value: 1, done: false }, it.next());
    Test.expectEqual({ value: 2, done: false }, it.next());
    Test.expectEqual({ value: 3, done: false }, it.next());
    Test.expectEqual({ value: undefined, done: true }, it.next());
    Test.expectEqual({ value: undefined, done: true }, it.next());
    Test.expectEqual({ value: undefined, done: true }, it.next());
}

// works when applied to non-object
[true, false, 9, 2, Symbol()].forEach(primitive => {
    const it = [].values.call(primitive);
    Test.expectEqual({ value: undefined, done: true }, it.next());
    Test.expectEqual({ value: undefined, done: true }, it.next());
    Test.expectEqual({ value: undefined, done: true }, it.next());
});

// item added to array before exhaustion is accessible
{
    const a = [1, 2];
    const it = a.values();
    Test.expectEqual({ value: 1, done: false }, it.next());
    Test.expectEqual({ value: 2, done: false }, it.next());
    a.push(3);
    Test.expectEqual({ value: 3, done: false }, it.next());
    Test.expectEqual({ value: undefined, done: true }, it.next());
    Test.expectEqual({ value: undefined, done: true }, it.next());
}

// item added to array after exhaustion is inaccessible
{
    const a = [1, 2];
    const it = a.values();
    Test.expectEqual({ value: 1, done: false }, it.next());
    Test.expectEqual({ value: 2, done: false }, it.next());
    Test.expectEqual({ value: undefined, done: true }, it.next());
    a.push(3);
    Test.expectEqual({ value: undefined, done: true }, it.next());
}
// length
Test.expect(0, Array.prototype.values.length);

// basic functionality
{
    const a = [1, 2, 3];
    const it = a.values();
    Test.equals({ value: 1, done: false }, it.next());
    Test.equals({ value: 2, done: false }, it.next());
    Test.equals({ value: 3, done: false }, it.next());
    Test.equals({ value: undefined, done: true }, it.next());
    Test.equals({ value: undefined, done: true }, it.next());
    Test.equals({ value: undefined, done: true }, it.next());
}

// works when applied to non-object
[true, false, 9, 2, Symbol()].forEach(primitive => {
    const it = [].values.call(primitive);
    Test.equals({ value: undefined, done: true }, it.next());
    Test.equals({ value: undefined, done: true }, it.next());
    Test.equals({ value: undefined, done: true }, it.next());
});

// item added to array before exhaustion is accessible
{
    const a = [1, 2];
    const it = a.values();
    Test.equals({ value: 1, done: false }, it.next());
    Test.equals({ value: 2, done: false }, it.next());
    a.push(3);
    Test.equals({ value: 3, done: false }, it.next());
    Test.equals({ value: undefined, done: true }, it.next());
    Test.equals({ value: undefined, done: true }, it.next());
}

// item added to array after exhaustion is inaccessible
{
    const a = [1, 2];
    const it = a.values();
    Test.equals({ value: 1, done: false }, it.next());
    Test.equals({ value: 2, done: false }, it.next());
    Test.equals({ value: undefined, done: true }, it.next());
    a.push(3);
    Test.equals({ value: undefined, done: true }, it.next());
}

/* TODO:
// is unscopable
{
    expect(Array.prototype[Symbol.unscopables].values).toBeTrue();
    const array = [];
    with (array) {
        expect(() => {
            values;
        }).toThrowWithMessage(ReferenceError, "'values' is not defined");
    }
} */
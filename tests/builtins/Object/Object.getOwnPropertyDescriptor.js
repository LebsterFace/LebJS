// TODO: Use Test.equals
{
    let { value, writable, enumerable, configurable } = Object.getOwnPropertyDescriptor({ a: 1 }, 'a');
    Test.expect(1, value);
    Test.expect(true, writable);
    Test.expect(true, enumerable);
    Test.expect(true, configurable);
}

{
    let { value, writable, enumerable, configurable } = Object.getOwnPropertyDescriptor('hello', '3');
    Test.expect('l', value);
    Test.expect(false, writable);
    Test.expect(true, enumerable);
    Test.expect(false, configurable);
}

{
    let { value, writable, enumerable, configurable } = Object.getOwnPropertyDescriptor('hello', 'length');
    Test.expect(5, value);
    Test.expect(false, writable);
    Test.expect(false, enumerable);
    Test.expect(false, configurable);
}

{
    let { value, writable, enumerable, configurable } = Object.getOwnPropertyDescriptor([1, 2], 'length');
    // FIXME: Test.expect(2, value);
    Test.expect(true, writable);
    Test.expect(false, enumerable);
    Test.expect(false, configurable);
}

{
    let { value, writable, enumerable, configurable } = Object.getOwnPropertyDescriptor([1, 2], '0');
    Test.expect(1, value);
    Test.expect(true, writable);
    Test.expect(true, enumerable);
    Test.expect(true, configurable);
}
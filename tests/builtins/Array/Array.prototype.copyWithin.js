Test.expect(2, Array.prototype.copyWithin.length);

// No-op
{
    const array = [1, 2];
    array.copyWithin(0, 0);
    Test.equals([1, 2], array);
}

// basic behavior
{
    const array = [1, 2, 3];
    const b = array.copyWithin(1, 2);
    Test.equals(array, b);
    Test.equals([1, 3, 3], array);

    b = array.copyWithin(2, 0);
    Test.equals(array, b);
    Test.equals([1, 3, 1], array);
}

// start > target
{
    const array = [1, 2, 3];
    const b = array.copyWithin(0, 1);
    Test.equals(array, b);
    Test.equals([2, 3, 3], array);
}

// overwriting behavior
{
    const array = [1, 2, 3];
    const b = array.copyWithin(1, 0);
    Test.equals(array, b);
    Test.equals([1, 1, 2], array);
}

// specify end
{
    const array = [1, 2, 3];
    const b = array.copyWithin(2, 0, 1);
    Test.equals(array, b);
    Test.equals([1, 2, 1], array);
}

/* TODO: is unscopable
Test.expect(true, Array.prototype[Symbol.unscopables].copyWithin);
const array = [];
with (array) {
    Test.expectError("ReferenceError", "'copyWithin' is not defined", () => copyWithin);
} */
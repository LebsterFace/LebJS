Test.expect(0, Array.prototype.flat.length); // length is 0

// normal behavior
// basic functionality
{
    Test.equals([], [].flat());
    const array1 = [1, 2, [3, 4]];
    const array2 = [1, 2, [3, 4, [5, 6]]];
    const array3 = [1, 2, [3, 4, [5, 6]]];
    Test.equals([1, 2, 3, 4], array1.flat());
    Test.equals([1, 2, 3, 4, [5, 6]], array2.flat());
    Test.equals([1, 2, 3, 4, 5, 6], array3.flat(2));
}

// calls depth as infinity
{
    const array1 = [1, 2, [3, 4, [5, 6, [7, 8]]]];
    Test.equals([1, 2, 3, 4, 5, 6, 7, 8], array1.flat(Infinity));
    Test.equals([1, 2, [3, 4, [5, 6, [7, 8]]]], array1.flat(-Infinity));
}

// calls depth as undefined
{
    const array1 = [1, 2, [3, 4, [5, 6, [7, 8]]]];
    Test.equals([1, 2, 3, 4, [5, 6, [7, 8]]], array1.flat(undefined));
}

// calls depth as null
{
    const array1 = [1, 2, [3, 4, [5, 6, [7, 8]]]];
    Test.equals([1, 2, [3, 4, [5, 6, [7, 8]]]], array1.flat(null));
    Test.equals([1, 2, [3, 4, [5, 6, [7, 8]]]], array1.flat(NaN));
}

// calls depth as non integer
{
    const array1 = [1, 2, [3, 4, [5, 6, [7, 8]]]];
    Test.equals([1, 2, [3, 4, [5, 6, [7, 8]]]], array1.flat("depth"));
    Test.equals([1, 2, 3, 4, 5, 6, [7, 8]], array1.flat("2"));
    Test.equals([1, 2, 3, 4, 5, 6, [7, 8]], array1.flat(2.1));
    Test.equals([1, 2, [3, 4, [5, 6, [7, 8]]]], array1.flat(0.7));
    Test.equals([1, 2, 3, 4, 5, 6, [7, 8]], array1.flat([2]));
    Test.equals([1, 2, [3, 4, [5, 6, [7, 8]]]], array1.flat([2, 1]));
    Test.equals([1, 2, [3, 4, [5, 6, [7, 8]]]], array1.flat({}));
    Test.equals([1, 2, [3, 4, [5, 6, [7, 8]]]], array1.flat({ depth: 2 }));
}

/* TODO: is unscopable
{
    Test.expect(true, Array.prototype[Symbol.unscopables].flat);
    const array = [];
    with (array) {
        Test.expectError("ReferenceError", "'flat' is not defined", () => flat);
    }
}); */
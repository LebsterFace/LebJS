Test.expect(0, Array.prototype.flat.length); // length is 0

// normal behavior
// basic functionality
{
    Test.expectEqual([], [].flat());
    const array1 = [1, 2, [3, 4]];
    const array2 = [1, 2, [3, 4, [5, 6]]];
    const array3 = [1, 2, [3, 4, [5, 6]]];
    Test.expectEqual([1, 2, 3, 4], array1.flat());
    Test.expectEqual([1, 2, 3, 4, [5, 6]], array2.flat());
    Test.expectEqual([1, 2, 3, 4, 5, 6], array3.flat(2));
}

// calls depth as infinity
{
    const array1 = [1, 2, [3, 4, [5, 6, [7, 8]]]];
    Test.expectEqual([1, 2, 3, 4, 5, 6, 7, 8], array1.flat(Infinity));
    Test.expectEqual([1, 2, [3, 4, [5, 6, [7, 8]]]], array1.flat(-Infinity));
}

// calls depth as undefined
{
    const array1 = [1, 2, [3, 4, [5, 6, [7, 8]]]];
    Test.expectEqual([1, 2, 3, 4, [5, 6, [7, 8]]], array1.flat(undefined));
}

// calls depth as null
{
    const array1 = [1, 2, [3, 4, [5, 6, [7, 8]]]];
    Test.expectEqual([1, 2, [3, 4, [5, 6, [7, 8]]]], array1.flat(null));
    Test.expectEqual([1, 2, [3, 4, [5, 6, [7, 8]]]], array1.flat(NaN));
}

// calls depth as non integer
{
    const array1 = [1, 2, [3, 4, [5, 6, [7, 8]]]];
    Test.expectEqual([1, 2, [3, 4, [5, 6, [7, 8]]]], array1.flat("depth"));
    Test.expectEqual([1, 2, 3, 4, 5, 6, [7, 8]], array1.flat("2"));
    Test.expectEqual([1, 2, 3, 4, 5, 6, [7, 8]], array1.flat(2.1));
    Test.expectEqual([1, 2, [3, 4, [5, 6, [7, 8]]]], array1.flat(0.7));
    Test.expectEqual([1, 2, 3, 4, 5, 6, [7, 8]], array1.flat([2]));
    Test.expectEqual([1, 2, [3, 4, [5, 6, [7, 8]]]], array1.flat([2, 1]));
    Test.expectEqual([1, 2, [3, 4, [5, 6, [7, 8]]]], array1.flat({}));
    Test.expectEqual([1, 2, [3, 4, [5, 6, [7, 8]]]], array1.flat({ depth: 2 }));
}
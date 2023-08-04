// basic functionality
{
    Test.expect(1, Array.prototype.sort.length);

    var arr = ["c", "b", "d", "a"];
    Test.equals(arr, arr.sort());
    Test.equals(["a", "b", "c", "d"], arr);

    arr = ["aa", "a"];
    Test.equals(arr, arr.sort());
    Test.equals(["a", "aa"], arr);

    arr = [1, 0];
    Test.expect(arr, arr.sort()); // should be exactly same object
    Test.equals([0, 1], arr);

    // numbers are sorted as strings
    arr = [205, -123, 22, 200, 3, -20, -2, -1, 25, 2, 0, 1];
    Test.equals([-1, -123, -2, -20, 0, 1, 2, 200, 205, 22, 25, 3], arr.sort());

    // mix of data, including empty slots and undefined
    arr = ["2", Infinity, null, null, , undefined, 5, , undefined, null, 54, "5"];
    Test.equals([
        "2",
        5,
        "5",
        54,
        Infinity,
        null,
        null,
        null,
        undefined,
        undefined,
        ,
        ,
    ], arr.sort());
    Test.equals(12, arr.length);

    // undefined compare function
    arr = ["2", Infinity, null, null, , undefined, 5n, , undefined, null, 54, "5"];
    Test.equals([
        "2",
        5n,
        "5",
        54,
        Infinity,
        null,
        null,
        null,
        undefined,
        undefined,
        ,
        ,
    ], arr.sort(undefined));
    Test.equals(12, arr.length);

    // numeric data with compare function to sort numerically
    arr = [50, 500, 5, Infinity, -Infinity, 0, 10, -10, 1, -1, 5, 0, 15, Infinity];
    Test.equals([
        -Infinity,
        -10,
        -1,
        0,
        0,
        1,
        5,
        5,
        10,
        15,
        50,
        500,
        Infinity,
        Infinity,
    ], arr.sort((a, b) => a - b));
    Test.equals(14, arr.length);

    // numeric data with compare function to sort reverse numerically
    arr = [50, 500, 5, Infinity, -Infinity, 0, 10, -10, 1, -1, 5, 0, 15, Infinity];
    Test.equals([
        Infinity,
        Infinity,
        500,
        50,
        15,
        10,
        5,
        5,
        1,
        0,
        0,
        -1,
        -10,
        -Infinity,
    ], arr.sort((a, b) => b - a));

    // small/edge cases
    Test.equals([], [].sort());
    Test.equals([5], [5].sort());
    Test.equals([5, 5], [5, 5].sort());
    Test.equals([undefined], [undefined].sort());
    Test.equals([undefined, undefined], [undefined, undefined].sort());
    Test.equals([,], [,].sort());
    Test.equals([, ,], [, ,].sort());
    Test.equals([5, ,], [5, ,].sort());
    Test.equals([5, , ,], [, , 5].sort());

    // sorting should be stable
    arr = [
        { sorted_key: 2, other_property: 1 },
        { sorted_key: 2, other_property: 2 },
        { sorted_key: 1, other_property: 3 },
    ];
    arr.sort((a, b) => a.sorted_key - b.sorted_key);
    Test.expect(1, arr[1].other_property);
    Test.expect(2, arr[2].other_property);
}

// that it makes no unnecessary calls to compare function
{
    const expectNoCallCompareFunction = function (a, b) {
        Test.fail();
    };

    Test.equals([], [].sort(expectNoCallCompareFunction));
    Test.equals([1], [1].sort(expectNoCallCompareFunction));
    Test.equals([1, undefined], [1, undefined].sort(expectNoCallCompareFunction));
    Test.equals([
        undefined,
        undefined,
    ], [undefined, undefined].sort(expectNoCallCompareFunction));
    Test.equals([1, , , ,], [, , 1, ,].sort(expectNoCallCompareFunction));
    Test.equals([
        1,
        undefined,
        undefined,
        ,
        ,
        ,
    ], [undefined, , 1, , undefined, ,].sort(expectNoCallCompareFunction));
}

// that it works on non-arrays
{
    var obj = { length: 0 };
    Test.expect(obj, Array.prototype.sort.call(obj));
    Test.equals({ length: 0 }, obj);

    obj = { 0: 1, length: 0 };
    Test.expect(obj, Array.prototype.sort.call(obj, undefined));
    Test.equals({ 0: 1, length: 0 }, obj);

    obj = { 0: 3, 1: 2, 2: 1, 3: 0, length: 2 };
    Test.expect(obj, Array.prototype.sort.call(obj));
    Test.equals({ 0: 2, 1: 3, 2: 1, 3: 0, length: 2 }, obj);

    obj = { 0: 3, 1: 2, 2: 1, a: "b", hello: "friends!", length: 2 };
    Test.expect(obj, Array.prototype.sort.call(obj));
    Test.equals({ 0: 2, 1: 3, 2: 1, a: 'b', hello: 'friends!', length: 2 }, obj);

    obj = { 0: 2, 1: 3, 2: 1, a: "b", hello: "friends!", length: 2 };
    Test.equals(obj,
        Array.prototype.sort.call(obj, (a, b) => {
            Test.expect(true, a === 2 || a === 3);
            Test.expect(true, b === 2 || b === 3);
            return b - a;
        })
    );
    Test.equals({ 0: 3, 1: 2, 2: 1, a: 'b', hello: 'friends!', length: 2 }, obj);
}

// that it handles abrupt completions correctly
{
    class TestError extends Error {
        constructor() {
            super();
            this.name = "TestError";
        }
    }

    let arr = [1, 2, 3];
    Test.expectError("TestError", "", () =>
        arr.sort((a, b) => {
            throw new TestError();
        })
    );

    class DangerousToString {
        constructor() {}

        toString() {
            throw new TestError();
        }
    }
    arr = [new DangerousToString(), new DangerousToString()];
    Test.expectError("TestError", "", () => arr.sort());
}

// TODO: it does not use deleteProperty unnecessarily
//{
//    var obj = new Proxy(
//        { 0: 5, 1: 4, 2: 3, length: 3 },
//        {
//            deleteProperty: function (target, property) {
//                Test.fail();
//            },
//        }
//    );
//    Array.prototype.sort.call(obj);
//}
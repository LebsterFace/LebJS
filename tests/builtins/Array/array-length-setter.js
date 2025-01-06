// errors
// invalid array length value
let array = [1, 2, 3];
for (const value of [undefined, "foo", -1, Infinity, -Infinity, NaN]) {
    Test.expectError("RangeError", "Invalid array length", () => array.length = value);
    Test.expect(3, array.length);
}

// normal behavior
// extend array by setting length
array = [1, 2, 3];
array.length = 5;
Test.equals([1, 2, 3, , ,], array);

// extend array by setting property
array[9] = 'A';
Test.equals([1, 2, 3, , , , , , , 'A'], array);
Test.expect(10, array.length);

// truncate array by setting length
array = [1, 2, 3];
array.length = 2;
Test.equals([1, 2], array);
array.length = 0;
Test.equals([], array);

// length value is coerced to number if possible
array = [1, 2, 3];
array.length = "42";
Test.expect(42, array.length);
array.length = [];
Test.expect(0, array.length);
array.length = true;
Test.expect(1, array.length);

/* FIXME: setting a huge array length
array = [];
array.length = 0x80000000;
Test.expect(0x80000000, array.length);
array.length = 0x80000001;
Test.expect(0x80000001, array.length); */

/* FIXME: should not remove non-configurable values
array = [1, undefined, 3];
Object.defineProperty(array, 1, { configurable: false, value: 2 });
Test.expect(3, array.length);
Test.expect(1, (array.length = 1));
Test.expect(2, array.length);
Test.expect(2, array[1]); */

// behavior when obj has Array prototype
function ArrExtend() { }
ArrExtend.prototype = [10, 11, 12];

// Has the properties from prototype
array = new ArrExtend();
Test.expect(3, array.length);
Test.expect(10, array[0]);
Test.expect(11, array[1]);
Test.expect(12, array[2]);

// Can override length to any value
for (const value of [null, "Hello", -6, 0]) {
    array = new ArrExtend();
    array.length = value;
    Test.expect(value, array.length);

    // should not wipe high values
    Test.expect(10, array[0]);
    Test.expect(11, array[1]);
    Test.expect(12, array[2]);
}

// Can call array methods
array = new ArrExtend();
array.push(1);
Test.expect(4, array.length);
Test.expect(1, array[3]);

// If length overwritten uses that value
for (const value of [null, "Hello", -6, 0]) {
    array = new ArrExtend();
    array.length = value;
    Test.expect(value, array.length);

    array.push(99);
    Test.expect(1, array.length);
    Test.expect(99, array[0]);

    // should not wipe higher value
    Test.expect(11, array[1]);
    Test.expect(12, array[2]);

    array.push(100);

    Test.expect(2, array.length);
    Test.expect(100, array[1]);

    array.length = 0;
    // should not wipe values since we are not an array
    Test.expect(99, array[0]);
    Test.expect(100, array[1]);
    Test.expect(12, array[2]);
}
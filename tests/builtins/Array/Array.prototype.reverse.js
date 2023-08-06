// length is 0
Test.expect(0, Array.prototype.reverse.length);

// basic functionality
// Odd length array
let array = [1, 2, 3];
Test.equals([3, 2, 1], array.reverse());
Test.equals([3, 2, 1], array);

// Even length array
array = [1, 2];
Test.equals([2, 1], array.reverse());
Test.equals([2, 1], array);

// Empty array
array = [];
Test.equals([], array.reverse());
Test.equals([], array);

// TODO: tests for sparse arrays / non-configurable elements
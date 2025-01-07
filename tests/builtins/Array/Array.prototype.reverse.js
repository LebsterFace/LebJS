// length is 0
Test.expect(0, Array.prototype.reverse.length);

// basic functionality
// Odd length array
let array = [1, 2, 3];
Test.expectEqual([3, 2, 1], array.reverse());
Test.expectEqual([3, 2, 1], array);

// Even length array
array = [1, 2];
Test.expectEqual([2, 1], array.reverse());
Test.expectEqual([2, 1], array);

// Empty array
array = [];
Test.expectEqual([], array.reverse());
Test.expectEqual([], array);

// TODO: tests for sparse arrays / non-configurable elements
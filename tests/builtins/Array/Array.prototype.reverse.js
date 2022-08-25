// Odd length array
let array = [1, 2, 3];
Test.equals(array.reverse(), [3, 2, 1]);
Test.equals(array, [3, 2, 1]);

// Even length array
array = [1, 2];
Test.equals(array.reverse(), [2, 1]);
Test.equals(array, [2, 1]);

// Empty array
array = [];
Test.equals(array.reverse(), []);
Test.equals(array, []);

// TODO: tests for sparse arrays / non-configurable elements
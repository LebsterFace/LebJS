Test.expect(1, Array.prototype.fill.length);

const array = [1, 2, 3, 4];

Test.expectEqual([1, 2, 0, 0], array.fill(0, 2, 4));
Test.expectEqual([1, 5, 5, 5], array.fill(5, 1));
Test.expectEqual([6, 6, 6, 6], array.fill(6));

Test.expectEqual([4, 4, 4], [1, 2, 3].fill(4));
Test.expectEqual([1, 4, 4], [1, 2, 3].fill(4, 1));
Test.expectEqual([1, 4, 3], [1, 2, 3].fill(4, 1, 2));
Test.expectEqual([1, 2, 3], [1, 2, 3].fill(4, 3, 3));
Test.expectEqual([4, 2, 3], [1, 2, 3].fill(4, -3, -2));
Test.expectEqual([1, 2, 3], [1, 2, 3].fill(4, NaN, NaN));
Test.expectEqual([1, 2, 3], [1, 2, 3].fill(4, 3, 5));
Test.expectEqual([4, 4, 4], Array(3).fill(4));
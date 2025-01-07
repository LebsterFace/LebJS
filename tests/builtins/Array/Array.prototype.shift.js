// length is 0
Test.expect(0, Array.prototype.shift.length);

// normal behavior
// array with elements
let a = [1, 2, 3];
Test.expect(1, a.shift());
Test.expectEqual([2, 3], a);
Test.expect(2, a.shift());
Test.expectEqual([3], a);
Test.expect(3, a.shift());
Test.expectEqual([], a);

// empty array
Test.expect(undefined, a.shift());
Test.expectEqual([], a);
Test.expect(undefined, a.shift());
Test.expectEqual([], a);

// array with empty slot
a = [,];
Test.expect(undefined, a.shift());
Test.expectEqual([], a);
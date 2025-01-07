// array with elements
let a = [1, 2, 3];
Test.expect(a.pop(), 3);
Test.expectEqual(a, [1, 2]);
Test.expect(a.pop(), 2);
Test.expectEqual(a, [1]);
Test.expect(a.pop(), 1);
Test.expectEqual(a, []);
Test.expect(a.pop(), undefined);
Test.expectEqual(a, []);

// empty array
a = [];
Test.expect(a.pop(), undefined);
Test.expectEqual(a, []);

a = [,];
Test.expect(a.pop(), undefined);
Test.expectEqual(a, []);

// array with prototype indexed value
Array.prototype[1] = 1;

a = [0];
a.length = 2;
Test.expect(a[1], 1);
Test.expect(a.pop(), 1);

Test.expect(a.length, 1);
Test.expectEqual(a, [0]);
Test.expect(a[1], 1);

delete Array.prototype[1];
Test.expect(Array.prototype[1], undefined)
Test.expect(false, '1' in Array.prototype)

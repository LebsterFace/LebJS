const array = ["hello", "world", 1, 2, false];

Test.expect([].includes(), false);
Test.expect([undefined].includes(), true);
Test.expect(array.includes("hello"), true);
Test.expect(array.includes(1), true);
Test.expect(array.includes(1, -3), true);
Test.expect(array.includes("lebjs"), false);
Test.expect(array.includes(false, -1), true);
Test.expect(array.includes(2, -1), false);
Test.expect(array.includes(2, -100), true);
Test.expect(array.includes("world", 100), false);
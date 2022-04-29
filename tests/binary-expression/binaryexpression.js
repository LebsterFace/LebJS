Test.expect(10, 5 + 5);
Test.expect(10, 5 * 2);
Test.expect(7, 1 + 2 * 3);
Test.expect(9, (1 + 2) * 3);

Test.expect(25, 5 ** 2);
Test.expect(5, 5 ** 1);
Test.expect(1, 5 ** 0);
Test.expect(4, (4))

Test.expect(5, 2 / 1 * 5 / 2 * 1)
Test.expect(36, 6 / 1 * (1 + 2) * 2)
Test.expect(1.5, 1 / 2 * 3 - 4 + 5 / 1 - 2 * 3 + 4 / 8 - 3 * (5 / 2 - 4))
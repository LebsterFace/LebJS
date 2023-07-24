Test.expect(1, Number.isInteger.length);

Test.expect(true, Number.isInteger(0));
Test.expect(true, Number.isInteger(42));
Test.expect(true, Number.isInteger(-10000));
Test.expect(true, Number.isInteger(5));
Test.expect(true, Number.isInteger(5.0));
Test.expect(true, Number.isInteger(5 + 1 / 10000000000000000));
Test.expect(true, Number.isInteger(+2147483647 + 1));
Test.expect(true, Number.isInteger(-2147483648 - 1));
Test.expect(true, Number.isInteger(99999999999999999999999999999999999));

Test.expect(false, Number.isInteger(5 + 1 / 1000000000000000));
Test.expect(false, Number.isInteger(1.23));
Test.expect(false, Number.isInteger(""));
Test.expect(false, Number.isInteger("0"));
Test.expect(false, Number.isInteger("42"));
Test.expect(false, Number.isInteger(true));
Test.expect(false, Number.isInteger(false));
Test.expect(false, Number.isInteger(null));
Test.expect(false, Number.isInteger([]));
Test.expect(false, Number.isInteger(Infinity));
Test.expect(false, Number.isInteger(-Infinity));
Test.expect(false, Number.isInteger(NaN));
Test.expect(false, Number.isInteger());
Test.expect(false, Number.isInteger(undefined));
Test.expect(false, Number.isInteger("foo"));
Test.expect(false, Number.isInteger({}));
Test.expect(false, Number.isInteger([1, 2, 3]));
Test.expect(1, Number.isSafeInteger.length);

Test.expect(true, Number.isSafeInteger(0));
Test.expect(true, Number.isSafeInteger(1));
Test.expect(true, Number.isSafeInteger(2.0));
Test.expect(true, Number.isSafeInteger(42));
Test.expect(true, Number.isSafeInteger(Number.MAX_SAFE_INTEGER));
Test.expect(true, Number.isSafeInteger(Number.MIN_SAFE_INTEGER));

Test.expect(false, Number.isSafeInteger());
Test.expect(false, Number.isSafeInteger("1"));
Test.expect(false, Number.isSafeInteger(2.1));
Test.expect(false, Number.isSafeInteger(42.42));
Test.expect(false, Number.isSafeInteger(""));
Test.expect(false, Number.isSafeInteger([]));
Test.expect(false, Number.isSafeInteger(null));
Test.expect(false, Number.isSafeInteger(undefined));
Test.expect(false, Number.isSafeInteger(NaN));
Test.expect(false, Number.isSafeInteger(Infinity));
Test.expect(false, Number.isSafeInteger(-Infinity));
Test.expect(false, Number.isSafeInteger(Number.MAX_SAFE_INTEGER + 1));
Test.expect(false, Number.isSafeInteger(Number.MIN_SAFE_INTEGER - 1));
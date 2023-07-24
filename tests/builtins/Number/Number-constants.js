Test.expect(2 ** -52, Number.EPSILON);
Test.expect(true, Number.EPSILON > 0);
Test.expect(2 ** 53 - 1, Number.MAX_SAFE_INTEGER);
Test.expect(Number.MAX_SAFE_INTEGER + 2, Number.MAX_SAFE_INTEGER + 1);
Test.expect(-(2 ** 53 - 1), Number.MIN_SAFE_INTEGER);
Test.expect(Number.MIN_SAFE_INTEGER - 2, Number.MIN_SAFE_INTEGER - 1);
Test.expect(Infinity, Number.POSITIVE_INFINITY);
Test.expect(-Infinity, Number.NEGATIVE_INFINITY);
Test.expect(NaN, Number.NaN);
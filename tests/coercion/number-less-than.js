Test.expect(false, Infinity < Infinity);
Test.expect(false, Infinity < -Infinity);
Test.expect(false, Infinity < NaN);
Test.expect(false, Infinity < 0);
Test.expect(false, Infinity < -0);
Test.expect(false, Infinity < 'str');
Test.expect(false, Infinity < 10);
Test.expect(true, -Infinity < Infinity);
Test.expect(false, -Infinity < -Infinity);
Test.expect(false, -Infinity < NaN);
Test.expect(true, -Infinity < 0);
Test.expect(true, -Infinity < -0);
Test.expect(false, -Infinity < 'str');
Test.expect(true, -Infinity < 10);
Test.expect(false, NaN < Infinity);
Test.expect(false, NaN < -Infinity);
Test.expect(false, NaN < NaN);
Test.expect(false, NaN < 0);
Test.expect(false, NaN < -0);
Test.expect(false, NaN < 'str');
Test.expect(false, NaN < 10);
Test.expect(true, 0 < Infinity);
Test.expect(false, 0 < -Infinity);
Test.expect(false, 0 < NaN);
Test.expect(false, 0 < 0);
Test.expect(false, 0 < -0);
Test.expect(false, 0 < 'str');
Test.expect(true, 0 < 10);
Test.expect(true, -0 < Infinity);
Test.expect(false, -0 < -Infinity);
Test.expect(false, -0 < NaN);
Test.expect(false, -0 < 0);
Test.expect(false, -0 < -0);
Test.expect(false, -0 < 'str');
Test.expect(true, -0 < 10);
Test.expect(false, 'str' < Infinity);
Test.expect(false, 'str' < -Infinity);
Test.expect(false, 'str' < NaN);
Test.expect(false, 'str' < 0);
Test.expect(false, 'str' < -0);
Test.expect(false, 'str' < 'str');
Test.expect(false, 'str' < 10);
Test.expect(true, 10 < Infinity);
Test.expect(false, 10 < -Infinity);
Test.expect(false, 10 < NaN);
Test.expect(false, 10 < 0);
Test.expect(false, 10 < -0);
Test.expect(false, 10 < 'str');
Test.expect(false, 10 < 10);
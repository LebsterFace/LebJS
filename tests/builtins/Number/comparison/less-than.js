Test.expect(false, 1 < 1);
Test.expect(true, 1 < 2);
Test.expect(false, 2 < 1);
Test.expect(true, 1 < Infinity);
Test.expect(false, Infinity < 1);
Test.expect(false, 1 < -Infinity);
Test.expect(true, -Infinity < 1);
Test.expect(false, 1 < NaN);
Test.expect(false, NaN < 1);
Test.expect(false, 1 < 0.01);
Test.expect(true, 0.01 < 1);
Test.expect(false, 1 < 0.0001);
Test.expect(true, 0.0001 < 1);
Test.expect(true, 1 < Math.PI);
Test.expect(false, Math.PI < 1);
Test.expect(false, 1 < 0);
Test.expect(true, 0 < 1);
Test.expect(false, 1 < -0);
Test.expect(true, -0 < 1);
Test.expect(false, 2 < 2);
Test.expect(true, 2 < Infinity);
Test.expect(false, Infinity < 2);
Test.expect(false, 2 < -Infinity);
Test.expect(true, -Infinity < 2);
Test.expect(false, 2 < NaN);
Test.expect(false, NaN < 2);
Test.expect(false, 2 < 0.01);
Test.expect(true, 0.01 < 2);
Test.expect(false, 2 < 0.0001);
Test.expect(true, 0.0001 < 2);
Test.expect(true, 2 < Math.PI);
Test.expect(false, Math.PI < 2);
Test.expect(false, 2 < 0);
Test.expect(true, 0 < 2);
Test.expect(false, 2 < -0);
Test.expect(true, -0 < 2);
Test.expect(false, Infinity < Infinity);
Test.expect(false, Infinity < -Infinity);
Test.expect(true, -Infinity < Infinity);
Test.expect(false, Infinity < NaN);
Test.expect(false, NaN < Infinity);
Test.expect(false, Infinity < 0.01);
Test.expect(true, 0.01 < Infinity);
Test.expect(false, Infinity < 0.0001);
Test.expect(true, 0.0001 < Infinity);
Test.expect(false, Infinity < Math.PI);
Test.expect(true, Math.PI < Infinity);
Test.expect(false, Infinity < 0);
Test.expect(true, 0 < Infinity);
Test.expect(false, Infinity < -0);
Test.expect(true, -0 < Infinity);
Test.expect(false, -Infinity < -Infinity);
Test.expect(false, -Infinity < NaN);
Test.expect(false, NaN < -Infinity);
Test.expect(true, -Infinity < 0.01);
Test.expect(false, 0.01 < -Infinity);
Test.expect(true, -Infinity < 0.0001);
Test.expect(false, 0.0001 < -Infinity);
Test.expect(true, -Infinity < Math.PI);
Test.expect(false, Math.PI < -Infinity);
Test.expect(true, -Infinity < 0);
Test.expect(false, 0 < -Infinity);
Test.expect(true, -Infinity < -0);
Test.expect(false, -0 < -Infinity);
Test.expect(false, NaN < NaN);
Test.expect(false, NaN < 0.01);
Test.expect(false, 0.01 < NaN);
Test.expect(false, NaN < 0.0001);
Test.expect(false, 0.0001 < NaN);
Test.expect(false, NaN < Math.PI);
Test.expect(false, Math.PI < NaN);
Test.expect(false, NaN < 0);
Test.expect(false, 0 < NaN);
Test.expect(false, NaN < -0);
Test.expect(false, -0 < NaN);
Test.expect(false, 0.01 < 0.01);
Test.expect(false, 0.01 < 0.0001);
Test.expect(true, 0.0001 < 0.01);
Test.expect(true, 0.01 < Math.PI);
Test.expect(false, Math.PI < 0.01);
Test.expect(false, 0.01 < 0);
Test.expect(true, 0 < 0.01);
Test.expect(false, 0.01 < -0);
Test.expect(true, -0 < 0.01);
Test.expect(false, 0.0001 < 0.0001);
Test.expect(true, 0.0001 < Math.PI);
Test.expect(false, Math.PI < 0.0001);
Test.expect(false, 0.0001 < 0);
Test.expect(true, 0 < 0.0001);
Test.expect(false, 0.0001 < -0);
Test.expect(true, -0 < 0.0001);
Test.expect(false, Math.PI < Math.PI);
Test.expect(false, Math.PI < 0);
Test.expect(true, 0 < Math.PI);
Test.expect(false, Math.PI < -0);
Test.expect(true, -0 < Math.PI);
Test.expect(false, 0 < 0);
Test.expect(false, 0 < -0);
Test.expect(false, -0 < 0);
Test.expect(false, -0 < -0);
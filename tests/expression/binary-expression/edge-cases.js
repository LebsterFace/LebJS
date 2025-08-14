Test.expect(-1n, -1n >> 2n); // fixes bigint
Test.expect(0, 1e22 & 5); // fixes toInt32
Test.expect(5, 5 << 1e22); // fixes toUint32
Test.expect(5, 5 >>> 1e22); // fixes >>>
Test.expect(1, Number.isFinite.length);

Test.expect(true, Number.isFinite(0));
Test.expect(true, Number.isFinite(1.23));
Test.expect(true, Number.isFinite(42));

Test.expect(false, Number.isFinite(""));
Test.expect(false, Number.isFinite("0"));
Test.expect(false, Number.isFinite("42"));
Test.expect(false, Number.isFinite(true));
Test.expect(false, Number.isFinite(false));
Test.expect(false, Number.isFinite(null));
Test.expect(false, Number.isFinite([]));
Test.expect(false, Number.isFinite());
Test.expect(false, Number.isFinite(NaN));
Test.expect(false, Number.isFinite(undefined));
Test.expect(false, Number.isFinite(Infinity));
Test.expect(false, Number.isFinite(-Infinity));
Test.expect(false, Number.isFinite("foo"));
Test.expect(false, Number.isFinite({}));
Test.expect(false, Number.isFinite([1, 2, 3]));
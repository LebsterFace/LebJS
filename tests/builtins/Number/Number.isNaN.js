Test.expect(1, Number.isNaN.length);

Test.expect(false, Number.isNaN(0));
Test.expect(false, Number.isNaN(42));
Test.expect(false, Number.isNaN(""));
Test.expect(false, Number.isNaN("0"));
Test.expect(false, Number.isNaN("42"));
Test.expect(false, Number.isNaN(true));
Test.expect(false, Number.isNaN(false));
Test.expect(false, Number.isNaN(null));
Test.expect(false, Number.isNaN([]));
Test.expect(false, Number.isNaN(Infinity));
Test.expect(false, Number.isNaN(-Infinity));
Test.expect(false, Number.isNaN());
Test.expect(false, Number.isNaN(undefined));
Test.expect(false, Number.isNaN("foo"));
Test.expect(false, Number.isNaN({}));
Test.expect(false, Number.isNaN([1, 2, 3]));

Test.expect(true, Number.isNaN(NaN));
Test.expect(true, Number.isNaN(Number.NaN));
Test.expect(true, Number.isNaN(0 / 0));
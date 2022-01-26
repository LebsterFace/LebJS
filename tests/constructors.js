expect(String(true), 'true');
expect(String(false), 'false');
expect(String(null), 'null');
expect(String(undefined), 'undefined');
expect(String(0), '0');

expect(Number(true), 1);
expect(Number(false), 0);
expect(Number(null), 0);
expect(Number(undefined), NaN);
expect(Number(0), 0);
expect(Number("21"), 21);
expect(Number("21.5"), 21.5);
expect(Number("hello"), NaN);

expect(Boolean(true), true);
expect(Boolean(false), false);
expect(Boolean(null), false);
expect(Boolean(undefined), false);
expect(Boolean(0), false);
expect(Boolean(""), false);
expect(Boolean("hello"), true);
expect(128104, "👨‍👩‍👦".codePointAt(0));
expect(56424, "👨‍👩‍👦".codePointAt(1));
expect(8205, "👨‍👩‍👦".codePointAt(2));
expect(128105, "👨‍👩‍👦".codePointAt(3));
expect(56425, "👨‍👩‍👦".codePointAt(4));
expect(8205, "👨‍👩‍👦".codePointAt(5));
expect(128102, "👨‍👩‍👦".codePointAt(6));
expect(56422, "👨‍👩‍👦".codePointAt(7));
expect(undefined, "ABC".codePointAt(-1));
expect(65, "ABC".codePointAt(0));
expect(66, "ABC".codePointAt(1));
expect(67, "ABC".codePointAt(2));
expect(undefined, "ABC".codePointAt(3));
expect(67197, "𐙽".codePointAt(0));
expect(56957, "𐙽".codePointAt(1));
expect(undefined, "𐙽".codePointAt(2));
expect(70, "Foobar".codePointAt(0));
expect(111, "Foobar".codePointAt(1));
expect(111, "Foobar".codePointAt(2));
expect(98, "Foobar".codePointAt(3));
expect(97, "Foobar".codePointAt(4));
expect(114, "Foobar".codePointAt(5));
expect(undefined, "Foobar".codePointAt(6));
expect(undefined, "Foobar".codePointAt(-1));
expect(70, "Foobar".codePointAt());
expect(70, "Foobar".codePointAt(NaN));
expect(70, "Foobar".codePointAt("foo"));
expect(70, "Foobar".codePointAt(undefined));
expect(128512, "😀".codePointAt(0));
expect(56832, "😀".codePointAt(1));
expect(undefined, "😀".codePointAt(2));
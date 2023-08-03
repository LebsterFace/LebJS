// Exponential
Test.expect(1, 1e0);
Test.expect(10, 1e1);
Test.expect(10, 1e+1);
Test.expect(3210000, 3.21e6);
Test.expect(0.0000006, 6e-7);
Test.expectError("SyntaxError", "Missing exponent", () => Test.parse("1.23e"));

// Radix
Test.expect(3735929054, 0xDEADC0DE);
Test.expect(3735929054, 0xdeadc0de);
Test.expect(255, 0b11111111);
Test.expect(8, 0o10);
Test.expectError("SyntaxError", "Missing binary digits after '0b'", () => Test.parse("0b"));
Test.expectError("SyntaxError", "Missing binary digits after '0b'", () => Test.parse("0b０１０１０"));
Test.expectError("SyntaxError", "Missing hexadecimal digits after '0x'", () => Test.parse("0x"));
Test.expectError("SyntaxError", "Missing hexadecimal digits after '0x'", () => Test.parse("0xｄｅａｄｃ０ｄｅ"));
Test.expectError("SyntaxError", "Missing octal digits after '0o'", () => Test.parse("0o"));
Test.expectError("SyntaxError", "Missing octal digits after '0o'", () => Test.parse("0o７７７"));
Test.expectError("SyntaxError", 'Unexpected token "2"', () => Test.parse("0b102"));
Test.expectError("SyntaxError", 'Unexpected token "8"', () => Test.parse("0o778"));

// Numeric separator
Test.expectError("SyntaxError", "Numeric separators are not allowed immediately after 'e'", () => Test.parse("1.23e_1"));
Test.expectError("SyntaxError", "Numeric separators are not allowed at the end of numeric literals", () => Test.parse("0b0101_"));
Test.expectError("SyntaxError", "Numeric separators are not allowed at the end of numeric literals", () => Test.parse("0o777_"));
Test.expectError("SyntaxError", "Numeric separators are not allowed at the end of numeric literals", () => Test.parse("0xFF_"));
Test.expectError("SyntaxError", "Numeric separators are not allowed at the end of numeric literals", () => Test.parse("1.1_"));
Test.expectError("SyntaxError", "Numeric separators are not allowed at the end of numeric literals", () => Test.parse("1.23e1_"));
Test.expectError("SyntaxError", "Numeric separators are not allowed at the end of numeric literals", () => Test.parse("1_"));
Test.expectError("SyntaxError", "Numeric separators are not allowed at the end of numeric literals", () => Test.parse("1_.1"));
Test.expectError("SyntaxError", "Numeric separators are not allowed at the start of numeric literals", () => Test.parse("0b_0101"));
Test.expectError("SyntaxError", "Numeric separators are not allowed at the start of numeric literals", () => Test.parse("0o_777"));
Test.expectError("SyntaxError", "Numeric separators are not allowed at the start of numeric literals", () => Test.parse("0x_FF"));
Test.expectError("SyntaxError", "Numeric separators are not allowed immediately after '.'", () => Test.parse("0._1"));
Test.expectError("SyntaxError", "Only one underscore is allowed as numeric separator", () => Test.parse("0b010__1"));
Test.expectError("SyntaxError", "Only one underscore is allowed as numeric separator", () => Test.parse("0o77__7"));
Test.expectError("SyntaxError", "Only one underscore is allowed as numeric separator", () => Test.parse("0xF__F"));
Test.expectError("SyntaxError", "Only one underscore is allowed as numeric separator", () => Test.parse("1.23e1__0"));
Test.expectError("SyntaxError", "Only one underscore is allowed as numeric separator", () => Test.parse("1__0"));
Test.expect(10, 1_0);
Test.expect(100, 1_00);
Test.expect(100, 1_00);
Test.expect(1.101, 1.1_0_1);
Test.expect(1e10, 1e1_0);
Test.expect(1234567, 1_2_3_4_5_6_7);

// Leading decimal
Test.expect(0.5, .5);

// Leading zero
Test.expectError("SyntaxError", "Unexpected leading zero", () => Test.parse("00"));
Test.expectError("SyntaxError", "Unexpected leading zero", () => Test.parse("000"));
Test.expectError("SyntaxError", "Unexpected leading zero", () => Test.parse("0123"));
Test.expectError("SyntaxError", "Unexpected leading zero", () => Test.parse("00123"));
Test.expectError("SyntaxError", "Unexpected leading zero", () => Test.parse("0000123"));
Test.expectError("SyntaxError", "Unexpected leading zero", () => Test.parse("00.1"));
Test.expectError("SyntaxError", "Unexpected leading zero", () => Test.parse("01.1"));
Test.expectError("SyntaxError", "Unexpected leading zero", () => Test.parse("001.1"));
Test.expectError("SyntaxError", "Unexpected leading zero", () => Test.parse("00001.1"));
Test.expectError("SyntaxError", "Unexpected leading zero", () => Test.parse("0000.1"));
Test.expectError("SyntaxError", "Unexpected leading zero", () => Test.parse("000.1"));
Test.expectError("SyntaxError", "Unexpected leading zero", () => Test.parse("01"));
Test.expectError("SyntaxError", "Unexpected leading zero", () => Test.parse("01.00"));
Test.expectError("SyntaxError", "Unexpected leading zero", () => Test.parse("01.0"));
Test.expectError("SyntaxError", "Unexpected leading zero", () => Test.parse("00.0"));
Test.parse("0");
Test.parse("0.");
Test.parse("0.1");
Test.parse("0.01");
Test.parse(".001");
Test.parse("0.001");
Test.parse("0.0001");
Test.parse("0.0000");
Test.parse(".0");
Test.parse(".000");
Test.parse(".00");
Test.parse(".1");
Test.parse(".0");
Test.parse("0.0");
Test.parse("0.00");

// The SourceCharacter immediately following a NumericLiteral must not be an IdentifierStart or DecimalDigit.
Test.expectError("SyntaxError", 'Identifier starts immediately after numeric literal', () => Test.parse("3a"));
Test.expectError("SyntaxError", 'Identifier starts immediately after numeric literal', () => Test.parse("3in ['a', 'b', 'c', 'd']"));
Test.expectError("SyntaxError", "Identifier starts immediately after numeric literal", () => Test.parse("0b10g"));
Test.expectError("SyntaxError", "Identifier starts immediately after numeric literal", () => Test.parse("0o77g"));
Test.expectError("SyntaxError", "Identifier starts immediately after numeric literal", () => Test.parse("0xFFg"));
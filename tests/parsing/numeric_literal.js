// Numeric separator
Test.expectError("SyntaxError", "Only one underscore is allowed as numeric separator", () => Test.parse("1__0"));
Test.expectError("SyntaxError", "Numeric separators are not allowed at the end of numeric literals", () => Test.parse("1_.1"));
Test.expectError("SyntaxError", "Numeric separators are not allowed immediately after '.'", () => Test.parse("0._01"));
Test.expect(10, 1_0);
Test.expect(100, 1_00);
Test.expect(100, 1_00);
Test.expect(1.101, 1.1_0_1);
Test.expect(1e10, 1e1_0);
Test.expect(1234567, 1_2_3_4_5_6_7);

// Exponential
Test.expect(1, 1e0);
Test.expect(10, 1e1);
Test.expect(10, 1e+1);
Test.expect(3210000, 3.21e6);
Test.expect(0.0000006, 6e-7);

// Radix
Test.expect(3735929054, 0xDEADC0DE);
Test.expect(255, 0b11111111);
Test.expect(8, 0o10);

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
Test.parse(".1");
Test.parse(".0");
Test.parse("0.0");
Test.parse("0.00");
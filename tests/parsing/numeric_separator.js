Test.expect(10, 1_0)
Test.expect(100, 1_00)
Test.expect(100, 1_00)

Test.expectSyntaxError("Only one underscore is allowed as numeric separator", "1__0");

Test.expect(0.01, 0._01)
Test.expect(1e10, 1e1_0)
Test.expect(1234567, 1_2_3_4_5_6_7)
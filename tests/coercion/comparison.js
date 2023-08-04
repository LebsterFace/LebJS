Test.expect(true, 1 === 1)
Test.expect(false, 1 === 0)

// ========================= LESS THAN =========================

 Test.expect(true, "a" < "b");
 Test.expect(false, "a" < "a");
 Test.expect(false, "a" < "3");

Test.expect(false, "5" < 3);
Test.expect(false, "3" < 3);
Test.expect(true, "3" < 5);

Test.expect(false, "hello" < 5);
Test.expect(false, 5 < "hello");

Test.expect(false, "5" < 3n);
Test.expect(true, "3" < 5n);

Test.expect(false, 5 < 3);
Test.expect(false, 3 < 3);
Test.expect(true, 3 < 5);

Test.expect(false, 5n < 3);
Test.expect(true, 3 < 5n);

Test.expect(false, true < false);
Test.expect(true, false < true);

Test.expect(true, 0 < true);
Test.expect(false, true < 1);

Test.expect(false, null < 0);
Test.expect(true, null < 1);

Test.expect(false, undefined < 3);
Test.expect(false, 3 < undefined);

Test.expect(false, 3 < NaN);
Test.expect(false, NaN < 3);

// ========================= GREATER THAN =========================

 Test.expect(false, "a" > "b");
 Test.expect(false, "a" > "a");
 Test.expect(true, "a" > "3");

Test.expect(true, "5" > 3);
Test.expect(false, "3" > 3);
Test.expect(false, "3" > 5);

Test.expect(false, "hello" > 5);
Test.expect(false, 5 > "hello");

Test.expect(true, "5" > 3n);
Test.expect(false, "3" > 5n);

Test.expect(true, 5 > 3);
Test.expect(false, 3 > 3);
Test.expect(false, 3 > 5);

Test.expect(true, 5n > 3);
Test.expect(false, 3 > 5n);

Test.expect(true, true > false);
Test.expect(false, false > true);

Test.expect(true, true > 0);
Test.expect(false, true > 1);

Test.expect(false, null > 0);
Test.expect(true, 1 > null);

Test.expect(false, undefined > 3);
Test.expect(false, 3 > undefined);

Test.expect(false, 3 > NaN);
Test.expect(false, NaN > 3);

// ========================= LESS THAN OR EQUAL =========================

 Test.expect(true, "a" <= "b");
 Test.expect(true, "a" <= "a");
 Test.expect(false, "a" <= "3");

Test.expect(false, "5" <= 3);
Test.expect(true, "3" <= 3);
Test.expect(true, "3" <= 5);

Test.expect(false, "hello" <= 5);
Test.expect(false, 5 <= "hello");

Test.expect(false, 5 <= 3);
Test.expect(true, 3 <= 3);
Test.expect(true, 3 <= 5);

Test.expect(false, 5n <= 3);
Test.expect(true, 3 <= 3n);
Test.expect(true, 3 <= 5n);

Test.expect(false, true <= false);
Test.expect(true, true <= true);
Test.expect(true, false <= true);

Test.expect(false, true <= 0);
Test.expect(true, true <= 1);

Test.expect(true, null <= 0);
Test.expect(false, 1 <= null);

Test.expect(false, undefined <= 3);
Test.expect(false, 3 <= undefined);

Test.expect(false, 3 <= NaN);
Test.expect(false, NaN <= 3);

// ========================= GREATER THAN OR EQUAL =========================

 Test.expect(false, "a" >= "b");
 Test.expect(true, "a" >= "a");
 Test.expect(true, "a" >= "3");

Test.expect(true, "5" >= 3);
Test.expect(true, "3" >= 3);
Test.expect(false, "3" >= 5);

Test.expect(false, "hello" >= 5);
Test.expect(false, 5 >= "hello");

Test.expect(true, 5 >= 3);
Test.expect(true, 3 >= 3);
Test.expect(false, 3 >= 5);

Test.expect(true, 5n >= 3);
Test.expect(true, 3 >= 3n);
Test.expect(false, 3 >= 5n);

Test.expect(true, true >= false);
Test.expect(true, true >= true);
Test.expect(false, false >= true);

Test.expect(true, true >= 0);
Test.expect(true, true >= 1);

Test.expect(true, null >= 0);
Test.expect(true, 1 >= null);

Test.expect(false, undefined >= 3);
Test.expect(false, 3 >= undefined);

Test.expect(false, 3 >= NaN);
Test.expect(false, NaN >= 3);
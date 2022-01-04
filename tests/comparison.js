// ========================= LESS THAN =========================

 expect(true, "a" < "b");
 expect(false, "a" < "a");
 expect(false, "a" < "3");

expect(false, "5" < 3);
expect(false, "3" < 3);
expect(true, "3" < 5);

expect(false, "hello" < 5);
expect(false, 5 < "hello");

// expect(false, "5" < 3n);
// expect(true, "3" < 5n);

expect(false, 5 < 3);
expect(false, 3 < 3);
expect(true, 3 < 5);

// expect(false, 5n < 3);
// expect(true, 3 < 5n);

expect(false, true < false);
expect(true, false < true);

expect(true, 0 < true);
expect(false, true < 1);

expect(false, null < 0);
expect(true, null < 1);

expect(false, undefined < 3);
expect(false, 3 < undefined);

expect(false, 3 < NaN);
expect(false, NaN < 3);

// ========================= GREATER THAN =========================

 expect(false, "a" > "b");
 expect(false, "a" > "a");
 expect(true, "a" > "3");

expect(true, "5" > 3);
expect(false, "3" > 3);
expect(false, "3" > 5);

expect(false, "hello" > 5);
expect(false, 5 > "hello");

// expect(true, "5" > 3n);
// expect(false, "3" > 5n);

expect(true, 5 > 3);
expect(false, 3 > 3);
expect(false, 3 > 5);

// expect(true, 5n > 3);
// expect(false, 3 > 5n);

expect(true, true > false);
expect(false, false > true);

expect(true, true > 0);
expect(false, true > 1);

expect(false, null > 0);
expect(true, 1 > null);

expect(false, undefined > 3);
expect(false, 3 > undefined);

expect(false, 3 > NaN);
expect(false, NaN > 3);

// ========================= LESS THAN OR EQUAL =========================

 expect(true, "a" <= "b");
 expect(true, "a" <= "a");
 expect(false, "a" <= "3");

expect(false, "5" <= 3);
expect(true, "3" <= 3);
expect(true, "3" <= 5);

expect(false, "hello" <= 5);
expect(false, 5 <= "hello");

expect(false, 5 <= 3);
expect(true, 3 <= 3);
expect(true, 3 <= 5);

// expect(false, 5n <= 3);
// expect(true, 3 <= 3n);
// expect(true, 3 <= 5n);

expect(false, true <= false);
expect(true, true <= true);
expect(true, false <= true);

expect(false, true <= 0);
expect(true, true <= 1);

expect(true, null <= 0);
expect(false, 1 <= null);

expect(false, undefined <= 3);
expect(false, 3 <= undefined);

expect(false, 3 <= NaN);
expect(false, NaN <= 3);

// ========================= GREATER THAN OR EQUAL =========================

 expect(false, "a" >= "b");
 expect(true, "a" >= "a");
 expect(true, "a" >= "3");

expect(true, "5" >= 3);
expect(true, "3" >= 3);
expect(false, "3" >= 5);

expect(false, "hello" >= 5);
expect(false, 5 >= "hello");

expect(true, 5 >= 3);
expect(true, 3 >= 3);
expect(false, 3 >= 5);

// expect(true, 5n >= 3);
// expect(true, 3 >= 3n);
// expect(false, 3 >= 5n);

expect(true, true >= false);
expect(true, true >= true);
expect(false, false >= true);

expect(true, true >= 0);
expect(true, true >= 1);

expect(true, null >= 0);
expect(true, 1 >= null);

expect(false, undefined >= 3);
expect(false, 3 >= undefined);

expect(false, 3 >= NaN);
expect(false, NaN >= 3);
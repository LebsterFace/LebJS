const obj = { property: true };
let x = 1;

Test.expect(true, x === 1 ? true : false);
Test.expect(x, x ? x : 0);
Test.expect(false, 1 < 2 ? false : true);
Test.expect(10, 0 ? 1 : 1 ? 10 : 20);
Test.expect(20, 0 ? (1 ? 1 : 10) : 20);
Test.expect("yes", obj.property ? "yes" : "no");
Test.expect(true, 1 ? obj.property : null);
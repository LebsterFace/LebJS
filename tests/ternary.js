const obj = { property: true };
let x = 1;

expect(true, x === 1 ? true : false);
expect(x, x ? x : 0);
expect(false, 1 < 2 ? false : true);
expect(10, 0 ? 1 : 1 ? 10 : 20);
expect(20, 0 ? (1 ? 1 : 10) : 20);
expect("yes", obj.property ? "yes" : "no");
expect(true, 1 ? obj.property : null);
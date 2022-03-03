function C() {};
function D() {};

let o = new C();
let p = {};

expect(true, o instanceof C);
expect(false, o instanceof D);
expect(false, p instanceof C);

expect(true, p instanceof Object)
expect(true, o instanceof Object)
expect(true, C.prototype instanceof Object)

C.prototype = {};
let o2 = new C();

expect(true, o2 instanceof C);
expect(false, o instanceof C);

D.prototype = new C();
let o3 = new D();

expect(true, o3 instanceof D);
expect(true, o3 instanceof C);
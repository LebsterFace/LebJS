function C() {};
function D() {};

let o = new C();
let p = {};

Test.expect(true, o instanceof C);
Test.expect(false, o instanceof D);
Test.expect(false, p instanceof C);

Test.expect(true, p instanceof Object)
Test.expect(true, o instanceof Object)
Test.expect(true, C.prototype instanceof Object)

C.prototype = {};
let o2 = new C();

Test.expect(true, o2 instanceof C);
Test.expect(false, o instanceof C);

D.prototype = new C();
let o3 = new D();

Test.expect(true, o3 instanceof D);
Test.expect(true, o3 instanceof C);
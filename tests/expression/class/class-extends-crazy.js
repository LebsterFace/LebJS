class ParentClass {
	constructor(a, b, c) {
		this.a = a;
		this.bc = b + c;
	}

	parentMethod(param) {
		return (this.bc + this.a + param);
	};
}
class ChildClass extends ParentClass {
	constructor(a, b) {
		super(a, b, "C");
		this.d = a - b;
	};

	childMethod(param) {
		return this.parentMethod(param - 10);
	};
}

Test.expect(ParentClass, Object.getPrototypeOf(ChildClass));
Test.expect(ParentClass.prototype, Object.getPrototypeOf(ChildClass.prototype));

let c = new ChildClass(0xA, 0xB);

Test.expect('11C10NaN', c.childMethod());
Test.expect('11C10undefined', c.parentMethod());
Test.expect('11C10P', c.parentMethod('P'));
Test.expect('11C10NaN', c.childMethod('p'));
Test.expect('11C', c.bc);
Test.expect(10, c.a);
Test.expect('11C', c.bc);
Test.expect(-1, c.d);
Test.expect('11C10100', c.parentMethod(100));
Test.expect('11C1099', c.childMethod(109));
Test.expect(true, c instanceof ChildClass);
Test.expect(true, c instanceof ParentClass);
Test.expect(true, ChildClass.prototype instanceof ParentClass);
Test.expect(ParentClass.prototype, Object.getPrototypeOf(ChildClass.prototype));
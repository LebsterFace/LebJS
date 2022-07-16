class Parent {
    constructor(a, b) {
        this.a = a;
        this.b = b;
    }
}

class Child extends Parent {
    constructor(a, b, c) {
        super(a, b);
        this.c = c;
    }
}

let c = new Child(1, 2, 3);
Test.expect(c.a, 1);
Test.expect(c.b, 2);
Test.expect(c.c, 3);

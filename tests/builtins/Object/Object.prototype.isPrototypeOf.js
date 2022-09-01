// basic functionality
function A() {}

function B() {}

A.prototype = new B();
const C = new A();

Test.expect(true, A.prototype.isPrototypeOf(C))
Test.expect(true, B.prototype.isPrototypeOf(C))

Test.expect(false, A.isPrototypeOf(C));
Test.expect(false, B.isPrototypeOf(C));

const D = new Object();
Test.expect(true, Object.prototype.isPrototypeOf(D));
Test.expect(true, Function.prototype.isPrototypeOf(D.toString));
Test.expect(true, Array.prototype.isPrototypeOf([]));

{
    function Foo() {}
    function Bar() {}
    Bar.prototype = Object.create(Foo.prototype);
    const bar = new Bar();

    Test.expect(true, Foo.prototype.isPrototypeOf(bar));
    Test.expect(true, Bar.prototype.isPrototypeOf(bar));
}

// Using isPrototypeOf
{
    function Foo() {}
    function Bar() {}
    function Baz() {}

    Bar.prototype = Object.create(Foo.prototype);
    Baz.prototype = Object.create(Bar.prototype);

    const foo = new Foo();
    const bar = new Bar();
    const baz = new Baz();

    // prototype chains:
    // foo: Foo <- Object
    // bar: Bar <- Foo <- Object
    // baz: Baz <- Bar <- Foo <- Object
    Test.expect(true, Baz.prototype.isPrototypeOf(baz));
    Test.expect(false, Baz.prototype.isPrototypeOf(bar));
    Test.expect(false, Baz.prototype.isPrototypeOf(foo));
    Test.expect(true, Bar.prototype.isPrototypeOf(baz));
    Test.expect(false, Bar.prototype.isPrototypeOf(foo));
    Test.expect(true, Foo.prototype.isPrototypeOf(baz));
    Test.expect(true, Foo.prototype.isPrototypeOf(bar));
    Test.expect(true, Object.prototype.isPrototypeOf(baz));
}
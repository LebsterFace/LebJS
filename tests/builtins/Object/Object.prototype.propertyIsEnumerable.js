// basic functionality
const o = {};

o.foo = 1;
expect(true, o.propertyIsEnumerable("foo"));
expect(false, o.propertyIsEnumerable("bar"));
expect(false, o.propertyIsEnumerable());
expect(false, o.propertyIsEnumerable(undefined));

o.undefined = 2;
expect(true, o.propertyIsEnumerable());
expect(true, o.propertyIsEnumerable(undefined));

expect(false, globalThis.propertyIsEnumerable("globalThis"));

{
    const object1 = {};
    const array1 = [];
    object1.property1 = 42;
    array1[0] = 42;

    Test.expect(true, object1.propertyIsEnumerable('property1'));
    Test.expect(true, array1.propertyIsEnumerable(0));
    Test.expect(false, array1.propertyIsEnumerable('length'));
}

// A basic use of propertyIsEnumerable
{
    const o = {};
    const a = [];
    o.prop = 'is enumerable';
    a[0] = 'is enumerable';

    Test.expect(true, o.propertyIsEnumerable('prop'));
    Test.expect(true, a.propertyIsEnumerable(0));
}

// User-defined vs. built-in objects
{
    const a = ['is enumerable'];

    Test.expect(true, a.propertyIsEnumerable(0));
    Test.expect(false, a.propertyIsEnumerable('length'));

    Test.expect(false, Math.propertyIsEnumerable('random'));
    Test.expect(false, this.propertyIsEnumerable('Math'));
}

// Direct vs. inherited properties
{
    const a = [];
    Test.expect(false, a.propertyIsEnumerable('constructor'));

    function firstConstructor() {
        this.property = 'is not enumerable';
    }

    firstConstructor.prototype.firstMethod = function() {};

    function secondConstructor() {
        this.method = function() {
            return 'is enumerable';
        };
    }

    secondConstructor.prototype = new firstConstructor;
    secondConstructor.prototype.constructor = secondConstructor;

    const o = new secondConstructor();
    o.arbitraryProperty = 'is enumerable';

    Test.expect(true, o.propertyIsEnumerable('arbitraryProperty'));
    Test.expect(true, o.propertyIsEnumerable('method'));
    Test.expect(false, o.propertyIsEnumerable('property'));

    o.property = 'is enumerable';

    Test.expect(true, o.propertyIsEnumerable('property'));

    // These return false as they are on the prototype which propertyIsEnumerable does not consider
    // (even though the last two are iterable with for-in)
    Test.expect(false, o.propertyIsEnumerable('prototype'));
    Test.expect(false, o.propertyIsEnumerable('constructor'));
    Test.expect(false, o.propertyIsEnumerable('firstMethod'));
}
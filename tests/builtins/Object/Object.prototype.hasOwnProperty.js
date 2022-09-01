// basic functionality
const o = {};

o.foo = 1;
Test.expect(true, o.hasOwnProperty("foo"));
Test.expect(false, o.hasOwnProperty("bar"));
Test.expect(false, o.hasOwnProperty());
Test.expect(false, o.hasOwnProperty(undefined));

o.undefined = 2;
Test.expect(true, o.hasOwnProperty());
Test.expect(true, o.hasOwnProperty(undefined));

const testSymbol = Symbol("real");
o[testSymbol] = 3;
Test.expect(true, o.hasOwnProperty(testSymbol));
Test.expect(false, o.hasOwnProperty(Symbol("fake")));

{
    const object1 = {};
    object1.property1 = 42;

    Test.expect(true, object1.hasOwnProperty('property1'));
    Test.expect(false, object1.hasOwnProperty('toString'));
    Test.expect(false, object1.hasOwnProperty('hasOwnProperty'));
}

// Using hasOwnProperty to test for an own property's existence
{
    const example = {};
    Test.expect(false, example.hasOwnProperty('prop'));

    example.prop = 'exists';
    Test.expect(true, example.hasOwnProperty('prop'));

    example.prop = null;
    Test.expect(true, example.hasOwnProperty('prop'));

    example.prop = undefined;
    Test.expect(true, example.hasOwnProperty('prop'));
}

// Direct vs. inherited properties
{
    const example = {};
    example.prop = 'exists';

    // `hasOwnProperty` will only return true for direct properties:
    Test.expect(true, example.hasOwnProperty('prop'));
    Test.expect(false, example.hasOwnProperty('toString'));
    Test.expect(false, example.hasOwnProperty('hasOwnProperty'));

    // The `in` operator will return true for direct or inherited properties:
    Test.expect(true, 'prop' in example);
    Test.expect(true, 'toString' in example);
    Test.expect(true, 'hasOwnProperty' in example);
}

// Using hasOwnProperty as a property name
{
    const foo = {
        // FIXME: Use method here
        hasOwnProperty: () => {
            return false;
        },
        bar: 'Here be dragons',
    };

    Test.expect(false, foo.hasOwnProperty('bar'));

    // FIXME: Use Object.hasOwn() method - recommended
    //        Test.expect(true, Object.hasOwn(foo, "bar"));

    // Use the hasOwnProperty property from the Object prototype
    Test.expect(true, Object.prototype.hasOwnProperty.call(foo, 'bar'));

    // Use another Object's hasOwnProperty
    // and call it with 'this' set to foo
    Test.expect(true, ({}).hasOwnProperty.call(foo, 'bar'));
}

// Objects created with Object.create(null)
{
    const foo = Object.create(null);
    foo.prop = 'exists';
    Test.expectError("TypeError", "foo.hasOwnProperty is not a function", () => foo.hasOwnProperty("prop"));
}
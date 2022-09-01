// result for various object types
const customToStringTag = {
    [Symbol.toStringTag]: "Foo",
};

Test.expect("[object Undefined]", Object.prototype.toString.call(undefined));
Test.expect("[object Null]", Object.prototype.toString.call(null));
Test.expect("[object Array]", Object.prototype.toString.call([]));
// FIXME: Test.expect("[object Arguments]", Object.prototype.toString.call(arguments));
Test.expect("[object Function]", Object.prototype.toString.call(function() {}));
Test.expect("[object Error]", Object.prototype.toString.call(new Error()));
Test.expect("[object Error]", Object.prototype.toString.call(new TypeError()));
// FIXME: Test.expect("[object Error]", Object.prototype.toString.call(new AggregateError([])))
Test.expect("[object Boolean]", Object.prototype.toString.call(true));
Test.expect("[object Number]", Object.prototype.toString.call(1));
// FIXME: Test.expect("[object Date]", Object.prototype.toString.call(new Date()));
Test.expect("[object RegExp]", Object.prototype.toString.call(new RegExp()));
Test.expect("[object Object]", Object.prototype.toString.call({}));
// FIXME: Test.expect("[object Array]", Object.prototype.toString.call(arrayProxy))
Test.expect("[object Foo]", Object.prototype.toString.call(customToStringTag));

Test.expect("[object Object]", globalThis.toString());

{
    function Dog(name) {
        this.name = name;
    }

    const dog1 = new Dog('Gabby');

    Dog.prototype.toString = function dogToString() {
        return `${this.name}`;
    };

    Test.expect("Gabby", dog1.toString());
}

{
    const arr = [1, 2, 3];

    Test.expect("1,2,3", arr.toString());
    Test.expect("[object Array]", Object.prototype.toString.call(arr));
}

// Overriding the default toString method
{
    function Dog(name, breed, color, sex) {
        this.name = name;
        this.breed = breed;
        this.color = color;
        this.sex = sex;
    }

    const theDog = new Dog('Gabby', 'Lab', 'chocolate', 'female');
    Test.expect("[object Object]", theDog.toString());

    Dog.prototype.toString = function dogToString() {
        return `Dog ${this.name} is a ${this.sex} ${this.color} ${this.breed}`;
    }

    Test.expect("Dog Gabby is a female chocolate Lab", theDog.toString());
}

// Using toString() to detect object class
{
    const toString = Object.prototype.toString;

    // FIXME: Test.expect("[object Date]", toString.call(new Date));
    Test.expect("[object String]", toString.call(""));
    Test.expect("[object Math]", toString.call(Math));
    Test.expect("[object Undefined]", toString.call(undefined));
    Test.expect("[object Null]", toString.call(null));
}

{
    const myArray = new Array();
    Test.expect("[object Array]", Object.prototype.toString.call(myArray));

    myArray[Symbol.toStringTag] = 'myArray';
    Test.expect("[object myArray]", Object.prototype.toString.call(myArray));

    Array.prototype[Symbol.toStringTag] = 'prototype polluted';
    Test.expect("[object prototype polluted]", Object.prototype.toString.call(new Array()));
}
function myClass() {
    this.property = 42;
}

let obj = new myClass();
Test.expect(42, obj.property)
Test.expect(myClass, obj.constructor)

function otherClass() {
    this.property = 1000;
    return {property: "me!"}
}

let obj2 = new otherClass();
Test.expect("me!", obj2.property)
Test.expect(false, obj2 instanceof otherClass)
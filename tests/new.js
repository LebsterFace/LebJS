function myClass() {
    this.property = 42;
}

let obj = new myClass();
expect(42, obj.property)
expect(myClass, obj.constructor)

function otherClass() {
    this.property = 1000;
    return {property: "me!"}
}

let obj2 = new otherClass();
expect("me!", obj2.property)
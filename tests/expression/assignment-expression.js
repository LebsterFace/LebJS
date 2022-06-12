this.property = 12;
Test.expect(this.property, property)
Test.expect(globalThis.property, property)
let myObj = { key: property }
Test.expect(this.property, myObj.key)
myObj.key = 12345
Test.expect(12345, myObj.key)
this.property = 12;
expect(this.property, property)
expect(globalThis.property, property)
let myObj = createObject("key", property)
expect(this.property, myObj.key)
myObj.key = 12345
expect(12345, myObj.key)
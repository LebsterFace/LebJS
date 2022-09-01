this.property = 12;
Test.expect(this.property, property)
Test.expect(globalThis.property, property)
let myObj = { key: property }
Test.expect(this.property, myObj.key)
myObj.key = 12345
Test.expect(12345, myObj.key)

const proto = Object.create(null);
proto.hello = 'world';
Test.expect('world', proto.hello);

const object_of_proto = Object.create(proto)
Test.expect('world', object_of_proto.hello);

object_of_proto.hello = 1234;
Test.expect(1234, object_of_proto.hello);
Test.expect('world', proto.hello);

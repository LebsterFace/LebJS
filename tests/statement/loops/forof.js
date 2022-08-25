for (const element of [1, 2, 3]) Test.expect(true, element < 4)
for (let element of [1, 2, 3]) Test.expect(true, element < 4)
for (var element of [1, 2, 3]) Test.expect(true, element < 4)

let element;
for (element of [1, 2, 3]) {}
Test.expect(3, element)

const object = { property: null }
for (object.property of [1, 2, 3]) {}
Test.expect(3, object.property)

for (
    const property of [1,1,1,1,1,1,1]
) {}
Test.expectError("ReferenceError", "property is not defined", () => property);

let str = "hello world ❤️👨‍👨‍👧‍👧"
let newstr = ""
for (let char of str) newstr += char;
Test.expect(str, newstr)
for (const element of [1, 2, 3]) Test.expect(true, element < 4)
for (let element of [1, 2, 3]) Test.expect(true, element < 4)
for (var element of [1, 2, 3]) Test.expect(true, element < 4)

let element;
for (element of [1, 2, 3]) {}
Test.expect(3, element)

const object = { property: null }
for (object.property of [1, 2, 3]) {}
Test.expect(3, object.property)

for (const property of [1,1,1,1,1,1,1]) {}
try {
	property
	Test.fail()
} catch (e) {
	Test.expect("property is not defined", e.message)
	Test.expect("ReferenceError", e.name);
}

let str = "hello world ❤️👨‍👨‍👧‍👧"
let newstr = ""
for (let char of str) newstr += char;
Test.expect(str, newstr)
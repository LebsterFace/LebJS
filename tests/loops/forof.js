for (const element of [1, 2, 3]) expect(true, element < 4)
for (let element of [1, 2, 3]) expect(true, element < 4)
for (var element of [1, 2, 3]) expect(true, element < 4)

for (element of [1, 2, 3]) {}
expect(3, element)

const object = { property: null }
for (object.property of [1, 2, 3]) {}
expect(3, object.property)

for (const property of [1,1,1,1,1,1,1]) {}
try {
	property
	expect(false, true)
} catch (e) {
	expect("property is not defined", e.message)
	expect("ReferenceError", e.name);
}

let str = "hello world ❤️👨‍👨‍👧‍👧"
let newstr = ""
for (let char of str) newstr += char;
expect(str, newstr)
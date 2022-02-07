for (const element of [1, 2, 3]) expect(true, element < 4)
for (let element of [1, 2, 3]) expect(true, element < 4)
for (var element of [1, 2, 3]) expect(true, element < 4)

for (element of [1, 2, 3]) {}
expect(3, element)

const object = { property: null }
for (object.property of [1, 2, 3]) {}
expect(3, object.property)
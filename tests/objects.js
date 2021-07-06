let obj = createObject("four", 2+2, "subObj", createObject("subKey", "subValue"))
expect(4, obj.four)
expect("subValue", obj.subObj.subKey)
obj.subObj.recursive = obj
expect(obj, obj.subObj.recursive)
expect(4, obj.subObj.recursive.four)
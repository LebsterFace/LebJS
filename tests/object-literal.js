let obj = {"key": "value", noString: 2 + 2,

      tabs

                    :

3*3};

expect("value", obj.key);
expect(4, obj.noString);
expect(9, obj.tabs);

let obj = { four: 4, subObj: { subKey: "subValue" } };
expect(4, obj.four);
expect("subValue", obj.subObj.subKey);
obj.subObj.recursive = obj;
expect(obj, obj.subObj.recursive);
expect(4, obj.subObj.recursive.four);

let value = 123;
expect(123, ({ value })["value"])

let object = {
    ["string" + "concatenation"]: "example",
}

expect("example", object.stringconcatenation)
let obj = {"key": "value", noString: 2 + 2,

      tabs

                    :

3*3};

Test.expect("value", obj.key);
Test.expect(4, obj.noString);
Test.expect(9, obj.tabs);

obj = { four: 4, subObj: { subKey: "subValue" } };
Test.expect(4, obj.four);
Test.expect("subValue", obj.subObj.subKey);
obj.subObj.recursive = obj;
Test.expect(obj, obj.subObj.recursive);
Test.expect(4, obj.subObj.recursive.four);

let value = 123;
Test.expect(123, ({ value })["value"])

let object = {
    ["string" + "concatenation"]: "example",
}

Test.expect("example", object.stringconcatenation)


const env = {
	...object,
	object,
	charset: 'UTF-8'
};

Test.expect("example", env.stringconcatenation);
Test.expect(object, env.object);
Test.expect("UTF-8", env.charset);
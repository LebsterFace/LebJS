let getNumber = () => 42;
Test.expect(42, getNumber());

let add = (a, b) => a + b;
Test.expect(5, add(2, 3));

let addBlock = (a, b) => {
	let res = a + b;
	return res;
};
Test.expect(9, addBlock(5, 4));

let makeObject = (a, b) => ({a: a, b: b});
let obj = makeObject(33, 44);
Test.expect("object", typeof obj);
Test.expect(33, obj.a);
Test.expect(44, obj.b);

let returnUndefined = () => {};
Test.expect("undefined", typeof returnUndefined());

let makeArray = (a, b) => [a, b];
let array = makeArray("3", null);
Test.expect("3", array[0]);
Test.expect(null, array[1]);

let square = x => x * x;
Test.expect(9, square(3));

let squareBlock = x => {
	return x * x;
};
Test.expect(16, squareBlock(4));

let message = (who => "Hello " + who)("friends!");
Test.expect("Hello friends!", message);

let sum = ((x, y, z) => x + y + z)(1, 2, 3);
Test.expect(6, sum);

let product = ((x, y, z) => {
	let res = x * y * z;
	return res;
})(5, 4, 2);
Test.expect(40, product);

let half = (x => {
	return x / 2;
})(10);
Test.expect(5, half);

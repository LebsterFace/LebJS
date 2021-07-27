let getNumber = () => 42;
expect(42, getNumber());

let add = (a, b) => a + b;
expect(5, add(2, 3));

let addBlock = (a, b) => {
	let res = a + b;
	return res;
};
expect(9, addBlock(5, 4));

let makeObject = (a, b) => ({a: a, b: b});
let obj = makeObject(33, 44);
expect("object", typeof obj);
expect(33, obj.a);
expect(44, obj.b);

let returnUndefined = () => {};
expect("undefined", typeof returnUndefined());

let makeArray = (a, b) => [a, b];
let array = makeArray("3", null);
expect("3", array[0]);
expect(null, array[1]);

let square = x => x * x;
expect(9, square(3));

let squareBlock = x => {
	return x * x;
};
expect(16, squareBlock(4));

let message = (who => "Hello " + who)("friends!");
expect("Hello friends!", message);

let sum = ((x, y, z) => x + y + z)(1, 2, 3);
expect(6, sum);

let product = ((x, y, z) => {
	let res = x * y * z;
	return res;
})(5, 4, 2);
expect(40, product);

let half = (x => {
	return x / 2;
})(10);
expect(5, half);

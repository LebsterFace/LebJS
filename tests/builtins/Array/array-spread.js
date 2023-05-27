let evens = [2, 4, 6, 8];
let odds = [1, 3, 5, 7];

function test(array, string) {
	let joined = "";
	for (const element of array) joined += element.toString();
	Test.expect(joined, string);
}

test([ ...odds, ...evens ], "13572468");
test([ ...evens, ...odds ], "24681357");
test([ ...odds, 5, ...evens ], "135752468");
test([ 5, ...odds, ...evens ], "513572468");

// errors
// cannot spread number in array
Test.expectError("TypeError", "1 is not iterable", () => [...1]);
// cannot spread object in array
Test.expectError("TypeError", "{ } is not iterable", () => [...{}]);

// basic functionality
Test.equals([1, 2, 3, 4], [1, ...[2, 3], 4]);

let a = [2, 3];
Test.equals([1, 2, 3, 4], [1, ...a, 4]);

let obj = { a: [ 2, 3 ] };
Test.equals([1, 2, 3, 4], [1, ...obj.a, 4]);
Test.equals([1, 2, 3, 4], [...[], ...[...[1, 2, 3]], 4]);
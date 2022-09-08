{
	const descriptors = Object.getOwnPropertyDescriptors({ a: 1, b: 2 });
	Test.expect(1, descriptors.a.value);
	Test.expect(true, descriptors.a.writable);
	Test.expect(true, descriptors.a.enumerable);
	Test.expect(true, descriptors.a.configurable);
	Test.expect(2, descriptors.b.value);
	Test.expect(true, descriptors.b.writable);
	Test.expect(true, descriptors.b.enumerable);
	Test.expect(true, descriptors.b.configurable);
}

{
	const descriptors = Object.getOwnPropertyDescriptors('hi');
	Test.expect("h", descriptors["0"].value);
	Test.expect(false, descriptors["0"].writable);
	Test.expect(true, descriptors["0"].enumerable);
	Test.expect(false, descriptors["0"].configurable);
	Test.expect("i", descriptors["1"].value);
	Test.expect(false, descriptors["1"].writable);
	Test.expect(true, descriptors["1"].enumerable);
	Test.expect(false, descriptors["1"].configurable);
	Test.expect(2, descriptors.length.value);
	Test.expect(false, descriptors.length.writable);
	Test.expect(false, descriptors.length.enumerable);
	Test.expect(false, descriptors.length.configurable);
}

{
	const descriptors = Object.getOwnPropertyDescriptors([ 1, 2 ]);
	Test.expect(1, descriptors["0"].value);
	Test.expect(true, descriptors["0"].writable);
	Test.expect(true, descriptors["0"].enumerable);
	Test.expect(true, descriptors["0"].configurable);
	Test.expect(2, descriptors["1"].value);
	Test.expect(true, descriptors["1"].writable);
	Test.expect(true, descriptors["1"].enumerable);
	Test.expect(true, descriptors["1"].configurable);
	// FIXME: Test.expect(2, descriptors.length.value);
	Test.expect(true, descriptors.length.writable);
	Test.expect(false, descriptors.length.enumerable);
	Test.expect(false, descriptors.length.configurable);
}
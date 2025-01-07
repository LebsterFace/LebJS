// length is 1
Test.expect(1, Array.prototype.concat.length);

// normal behavior
const array = ["hello"];

// no arguments
{
    const concatenated = array.concat();
    Test.expect(1, array.length);
    Test.expect(1, concatenated.length);
    Test.expect(false, array === concatenated);
}

// single argument
{
    const concatenated = array.concat("friends");
    Test.expect(1, array.length);
    Test.expect(2, concatenated.length);
    Test.expect("hello", concatenated[0]);
    Test.expect("friends", concatenated[1]);
}

// single array argument
{
    const concatenated = array.concat([1, 2, 3]);
    Test.expect(1, array.length);
    Test.expect(4, concatenated.length);
    Test.expect("hello", concatenated[0]);
    Test.expect(1, concatenated[1]);
    Test.expect(2, concatenated[2]);
    Test.expect(3, concatenated[3]);
}

// multiple arguments
{
    const concatenated = array.concat(false, "world", { name: "lebjs" }, [1, [2, 3]]);
    Test.expect(1, array.length);
    Test.expect(6, concatenated.length);
    Test.expect("hello", concatenated[0]);
    Test.expect(false, concatenated[1]);
    Test.expect("world", concatenated[2]);
    Test.expectEqual({ name: "lebjs" }, concatenated[3]);
    Test.expect(1, concatenated[4]);
    Test.expectEqual([2, 3], concatenated[5]);
}

/* TODO: Proxy is concatenated as array
{
    const proxy = new Proxy([9, 8], {});
    const concatenated = array.concat(proxy);
    expect(array.length).toHaveLengthtoBe1);
    expect(concatenated.length).toHaveLengthtoBe3);
    Test.expect("hello", concatenated[0]);
    Test.expect(9, concatenated[1]);
    Test.expect(8, concatenated[2]);
} */
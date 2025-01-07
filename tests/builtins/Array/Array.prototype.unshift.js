// length is 1
Test.expect(1, Array.prototype.unshift.length);


// normal behavior
// no argument
{
    var a = ["hello"];
    Test.expect(1, a.unshift());
    Test.expectEqual(["hello"], a);
}

// single argument
{
    var a = ["hello"];
    Test.expect(2, a.unshift("friends"));
    Test.expectEqual(["friends", "hello"], a);
}

// multiple arguments
{
    var a = ["friends", "hello"];
    Test.expect(5, a.unshift(1, 2, 3));
    Test.expectEqual([1, 2, 3, "friends", "hello"], a);
}
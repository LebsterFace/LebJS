// basic functionality
{
    Test.expect("", function() {}.name);

    function bar() {}
    Test.expect("bar", bar.name);
    Test.expectError("TypeError", "Cannot assign to read-only property 'name'", () => bar.name = "baz");
    Test.expect("bar", bar.name);
};

// function assigned to variable
{
    let foo = function() {};
    Test.expect("foo", foo.name);
    Test.expectError("TypeError", "Cannot assign to read-only property 'name'", () => foo.name = "bar");
    Test.expect("foo", foo.name);

    let a, b;
    a = b = function() {};
    Test.expect("b", a.name);
    Test.expect("b", b.name);
};

// functions in array assigned to variable
{
    const arr = [function() {}, function() {}, function() {}];
    Test.expect("", arr[0].name);
    Test.expect("", arr[1].name);
    Test.expect("", arr[2].name);
};

// functions in objects
{
    let f;
    let o = { a: function() {} };

    Test.expect("a", o.a.name);
    f = o.a;
    Test.expect("a", f.name);
    Test.expect("a", o.a.name);

    o = { ...o, b: f };
    Test.expect("a", o.a.name);
    Test.expect("a", o.b.name);

    // Member expressions do not get named.
    o.c = function() {};
    Test.expect("", o.c.name);
};

// names of native functions
{
    Test.expect("log", console.log.name);
    Test.expectError("TypeError", "Cannot assign to read-only property 'name'", () => console.log.name = "warn");
    Test.expect("log", console.log.name);
};

// some anonymous functions get renamed
{
    // assignment from variable does not name
    {
        const f1 = function() {};
        let f3 = f1;
        Test.expect("f1", f3.name);
    };

    // assignment via expression does not name
    {
        let f4 = false || function() {};
        Test.expect("", f4.name);
    };

    // direct assignment does name new function expression
    {
        let f1 = (function() {});
        Test.expect("f1", f1.name);
        let f2 = false;
        f2 ||= function () {};
        Test.expect("f2", f2.name);
    };
};
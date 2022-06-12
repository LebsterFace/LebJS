let obj = { a: 'a', b: 'b', c: [1, 2, 3] };
{
    let { a, b, c } = obj;
    Test.expect(a, obj.a);
    Test.expect(b, obj.b);
    Test.expect(c, obj.c);
}

{
    let a, b, c;
    ({ a, b, c } = obj);
    Test.expect(a, obj.a);
    Test.expect(b, obj.b);
    Test.expect(c, obj.c);
}

{
    let { c: [ a, b, c ] } = obj;
    Test.expect(a, obj.c[0]);
    Test.expect(b, obj.c[1]);
    Test.expect(c, obj.c[2]);
}

{
    let a, b, c;
    ({ c: [ a, b, c ] } = obj);
    Test.expect(a, obj.c[0]);
    Test.expect(b, obj.c[1]);
    Test.expect(c, obj.c[2]);
}

{
    try {
        eval("(({ a, b, c }) = obj)");
        Test.fail();
    } catch (e) {
        Test.expect(true, e.message.startsWith("Invalid left-hand side in assignment"));
    }
}
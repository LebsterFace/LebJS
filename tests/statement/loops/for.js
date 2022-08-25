let num = 0;
for (
    let i = 1;
    i < 7;
    i++
)
{
    num += i
}

Test.expectError("ReferenceError", "i is not defined", () => i);

Test.expect(21, num)
num = null;
for (; false;) {
    num = 6000
}

Test.expect(null, num)
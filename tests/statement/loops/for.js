let num = 0;
for (
    let i = 1;
    i < 7;
    i++
)
{
    num += i
}

try {
	i
	Test.fail()
} catch (e) {
	Test.expect("i is not defined", e.message)
	Test.expect("ReferenceError", e.name);
}

Test.expect(21, num)
num = null;
for (; false;) {
    num = 6000
}

Test.expect(null, num)
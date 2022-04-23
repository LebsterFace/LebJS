let num = 0;
for (let i = 1; i < 7; i++) {
    num += i
}

try {
	i
	expect(false, true)
} catch (e) {
	expect("i is not defined", e.message)
	expect("ReferenceError", e.name);
}

expect(21, num)
num = null;
for (; false;) {
    num = 6000
}

expect(null, num)
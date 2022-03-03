let num = 0;
for (let i = 1; i < 7; i++) {
    num += i
}

expect(21, num)
num = null;
for (; false;) {
    num = 6000
}

expect(null, num)
let i = 0;
while
(
    i < 5
) {
    i = i + 1
}

Test.expect(5, i)
i = 0; while (i < 5) i = i + 1
Test.expect(5, i)

i = 0;
while (i < 5) {
    i = i + 1
    if (i === 3) {
        break
    }
}

Test.expect(3, i)

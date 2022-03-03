let i = 0;
while (i < 5) {
    i = i + 1
}

expect(5, i)
i = 0; while (i < 5) i = i + 1
expect(5, i)

i = 0;
while (i < 5) {
    i = i + 1
    if (i === 3) {
        break
    }
}

expect(3, i)

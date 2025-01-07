Test.expect(1, Set.prototype.forEach.length);

// errors
// requires at least one argument
Test.expectError("TypeError", "undefined is not a function", () => { new Set().forEach() });
// callback must be a function
Test.expectError("TypeError", "undefined is not a function", () => new Set().forEach(undefined));

// normal behavior
{
    // never calls callback with empty set
    let callbackCalled = 0;
    Test.expect(undefined, new Set().forEach(() => callbackCalled++));
    Test.expect(0, callbackCalled);

    callbackCalled = 0;
    // calls callback once for every item
    Test.expect(undefined, new Set([1, 2, 3]).forEach(() => callbackCalled++));
    Test.expect(3, callbackCalled);

    // callback receives value twice and set
    let a = new Set([1, 2, 3]);
    a.forEach((value1, value2, set) => {
        Test.expect(true, a.has(value1));
        Test.expect(value2, value1);
        Test.expect(a, set);
    });
}

// modification during iteration
{
    // adding items during forEach also get visited
    {
        const set = new Set([1, 2]);
        const visited = [];
        set.forEach(val => {
            if (val <= 2) set.add(4 * val);

            visited.push(val);
        });
        Test.expect(4, set.size);

        Test.expectEqual([1, 2, 4, 8], visited);
    }

    // removing an item before it is visited means it doesn't get visited
    {
        const set = new Set([1, 2, 3]);
        const visited = [];
        set.forEach(val => {
            visited.push(val);
            if (val === 1) {
                Test.expect(true, set.delete(2));
            } else {
                Test.expect(3, val);
                Test.expect(false, set.delete(2));
            }
        });
        Test.expect(2, set.size);
        Test.expectEqual([1, 3], visited);
    }

    // removing an item after it was visited and adding it again means it gets visited twice
    {
        const set = new Set([1, 2, 3]);
        const visited = [];
        set.forEach(val => {
            visited.push(val);
            if (val === 2) {
                Test.expect(true, set.delete(1));
            } else if (val === 3) {
                Test.expect(2, set.size);
                set.add(1);
                Test.expect(3, set.size);
            }
        });
        Test.expect(3, set.size);
        Test.expectEqual([1, 2, 3, 1], visited);
    }

    // adding a new item and removing it before it gets visited means it never gets visited
    {
        const set = new Set([1, 2]);
        const visited = [];
        set.forEach(val => {
            visited.push(val);
            if (val === 1) {
                set.add(3);
                Test.expect(3, set.size);
            } else if (val === 2) {
                Test.expect(3, set.size);
                Test.expect(true, set.delete(3));
                Test.expect(2, set.size);
            }
            if (val === 3) Test.fail();
        });
        Test.expect(2, set.size);
        Test.expectEqual([1, 2], visited);
    }

    // removing and adding in the same iterations
    {
        const set = new Set([1, 2, 3]);
        const visited = [];
        let first = true;
        set.forEach(val => {
            visited.push(val);
            if (val === 1 && first) {
                Test.expect(true, set.delete(1));
                set.add(1);
            }

            first = false;
        });
        Test.expect(3, set.size);

        Test.expectEqual([1, 2, 3, 1], visited);
    }

    // removing and readding the same item means it can get visited n times
    {
        let n = 3;

        const set = new Set([1, 2]);

        const visited = [];
        set.forEach(val => {
            visited.push(val);
            if (n-- > 0) {
                Test.expect(true, set.delete(val));
                set.add(val);
            }
        });

        Test.expect(2, set.size);
        Test.expectEqual([1, 2, 1, 2, 1], visited);
    }
}

{
    const a = new Set([0, 1, 2]);
    const seen = [false, false, false];
    a.forEach(x => { seen[x] = true; });
    Test.expect(true, seen[0] && seen[1] && seen[2]);
}
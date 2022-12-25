// length is 1
Test.expect(1, Array.from.length);

// normal behavior
// empty array, no mapFn
{
    const a = Array.from([]);
    Test.expect(true, a instanceof Array);
    Test.expect(0, a.length);
}

// empty array, with mapFn, no thisArg
{
    const a = Array.from([], n => n);
    Test.expect(true, a instanceof Array);
    Test.expect(0, a.length);
}


// empty array, with mapFn, with thisArg
{
    const a = Array.from(
        [],
        function(n) {
            return n + this.value;
        }, {
            value: 100
        }
    );
    Test.expect(true, a instanceof Array);
    Test.expect(0, a.length);
}


// empty string, no mapFn
{
    const a = Array.from("");
    Test.expect(true, a instanceof Array);
    Test.expect(0, a.length);
}


// empty string, with mapFn, no thisArg
{
    const a = Array.from("", n => n);
    Test.expect(true, a instanceof Array);
    Test.expect(0, a.length);
}


// empty string, with mapFn, with thisArg
{
    const a = Array.from(
        "",
        function(n) {
            return n + this.value;
        }, {
            value: 100
        }
    );
    Test.expect(true, a instanceof Array);
    Test.expect(0, a.length);
}


// non-empty array, no mapFn
{
    const a = Array.from([5, 8, 1]);
    Test.expect(true, a instanceof Array);
    Test.expect(3, a.length);
    Test.expect(5, a[0]);
    Test.expect(8, a[1]);
    Test.expect(1, a[2]);
}


// non-empty array, with mapFn, no thisArg
{
    const a = Array.from([5, 8, 1], (n, i) => n - i);
    Test.expect(true, a instanceof Array);
    Test.expect(3, a.length);
    Test.expect(5, a[0]);
    Test.expect(7, a[1]);
    Test.expect(-1, a[2]);
}


// non-empty array, with mapFn, with thisArg
{
    const a = Array.from(
        [5, 8, 1],
        function(n, i) {
            return n - i + this.value;
        }, {
            value: 100
        }
    );
    Test.expect(true, a instanceof Array);
    Test.expect(3, a.length);
    Test.expect(105, a[0]);
    Test.expect(107, a[1]);
    Test.expect(99, a[2]);
}


// non-empty string, no mapFn
{
    const a = Array.from("what");
    Test.expect(true, a instanceof Array);
    Test.expect(4, a.length);
    Test.expect("w", a[0]);
    Test.expect("h", a[1]);
    Test.expect("a", a[2]);
    Test.expect("t", a[3]);
}


// non-empty string, with mapFn, no thisArg
{
    const a = Array.from("what", (n, i) => n + n + i);
    Test.expect(true, a instanceof Array);
    Test.expect(4, a.length);
    Test.expect("ww0", a[0]);
    Test.expect("hh1", a[1]);
    Test.expect("aa2", a[2]);
    Test.expect("tt3", a[3]);
}


// non-empty string, with mapFn, with thisArg
{
    const a = Array.from(
        "what",
        function(n, i) {
            return n + i + this.value;
        }, {
            value: "a"
        }
    );
    Test.expect(true, a instanceof Array);
    Test.expect(4, a.length);
    Test.expect("w0a", a[0]);
    Test.expect("h1a", a[1]);
    Test.expect("a2a", a[2]);
    Test.expect("t3a", a[3]);
}


// shallow array copy, no mapFn
{
    const a = [1, 2, 3];
    const b = Array.from([a]);
    Test.expect(true, b instanceof Array);
    Test.expect(1, b.length);
    b[0][0] = 4;
    Test.expect(4, a[0]);
}


// shallow array copy, with mapFn, no thisArg
{
    const a = [1, 2, 3];
    const b = Array.from([a], n => n.map(n => n + 2));
    Test.expect(true, b instanceof Array);
    Test.expect(1, b.length);
    b[0][0] = 10;
    Test.expect(1, a[0]);
    Test.expect(10, b[0][0]);
    Test.expect(4, b[0][1]);
    Test.expect(5, b[0][2]);
}


// shallow array copy, with mapFn, with thisArg
{
    const a = [1, 2, 3];
    const b = Array.from(
        [a],
        function(n, i) {
            return n.map(n => n + 2 + i + this.value);
        }, {
            value: 100
        }
    );
    Test.expect(true, b instanceof Array);
    Test.expect(1, b.length);
    b[0][0] = 10;
    Test.expect(1, a[0]);
    Test.expect(10, b[0][0]);
    Test.expect(104, b[0][1]);
    Test.expect(105, b[0][2]);
}


const rangeIterator = function(begin, end) {
    return {
        [Symbol.iterator]: () => {
            let value = begin - 1;
            return {
                next: () => {
                    if (value < end) {
                        value += 1;
                    }
                    return {
                        value: value,
                        done: value >= end
                    };
                },
            };
        },
    };
};

// from iterator, no mapFn
{
    const a = Array.from(rangeIterator(8, 10));
    Test.expect(true, a instanceof Array);
    Test.expect(2, a.length);
    Test.expect(8, a[0]);
    Test.expect(9, a[1]);
}


// from iterator, with mapFn, no thisArg
{
    const a = Array.from(rangeIterator(8, 10), n => --n);
    Test.expect(true, a instanceof Array);
    Test.expect(2, a.length);
    Test.expect(7, a[0]);
    Test.expect(8, a[1]);
}


// from iterator, with mapFn, with thisArg
{
    const a = Array.from(
        rangeIterator(8, 10),
        function(n, i) {
            return n + i + this.value;
        }, {
            value: 100
        }
    );
    Test.expect(true, a instanceof Array);
    Test.expect(2, a.length);
    Test.expect(108, a[0]);
    Test.expect(110, a[1]);
}
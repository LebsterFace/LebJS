// basic functionality
{
    Test.expect(2, String.prototype.split.length);

    Test.equals(["hello friends"], "hello friends".split());
    Test.equals([
        "h",
        "e",
        "l",
        "l",
        "o",
        " ",
        "f",
        "r",
        "i",
        "e",
        "n",
        "d",
        "s",
    ], "hello friends".split(""));
    Test.equals(["hello", "friends"], "hello friends".split(" "));

    Test.equals(["a", "b", "c", "d"], "a,b,c,d".split(","));
    Test.equals(["", "a", "b", "c", "d"], ",a,b,c,d".split(","));
    Test.equals(["a", "b", "c", "d", ""], "a,b,c,d,".split(","));
    Test.equals(["a", "b", "", "c", "d"], "a,b,,c,d".split(","));
    Test.equals(["", "a", "b", "", "c", "d", ""], ",a,b,,c,d,".split(","));
    Test.equals([",a,b", ",c,d,"], ",a,b,,,c,d,".split(",,"));
};

// limits
{
    Test.equals([], "a b c d".split(" ", 0));
    Test.equals(["a"], "a b c d".split(" ", 1));
    Test.equals(["a", "b", "c"], "a b c d".split(" ", 3));
    Test.equals(["a", "b", "c", "d"], "a b c d".split(" ", 100));
};

// regex split
{
    /* FIXME
    class RegExp1 extends RegExp {
        [Symbol.split](str, limit) {
            const result = RegExp.prototype[Symbol.split].call(this, str, limit);
            return result.map(x => `(${x})`);
        }
    }

    Test.equals(["(2016, "2016-01-02".split(new RegExp1("-")))", "(01)", "(02)"]);
    Test.equals(["2016", "01", "02"], "2016-01-02".split(new RegExp("-")));
    */

    // Test.equals(["a", "b"], /a*?/[Symbol.split]("ab"));
    // Test.equals(["", "b"], /a*/[Symbol.split]("ab"));

    // let captureResult = /<(\/)?([^<>]+)>/[Symbol.split]("A<B>bold</B>and<CODE>coded</CODE>");
    /* Test.equals([
        "A",
        undefined,
        "B",
        "bold",
        "/",
        "B",
        "and",
        undefined,
        "CODE",
        "coded",
        "/",
        "CODE",
        "",
    ], captureResult);*/
};

// UTF-16
{
    var s = "😀";
    Test.equals(["😀"], s.split());
    Test.equals(["", ""], s.split("😀"));
    Test.equals(["", "\ude00"], s.split("\ud83d"));
    Test.equals(["\ud83d", ""], s.split("\ude00"));

    // TODO: Test.equals(["", "\ude00"], s.split(/\ud83d/));
    // TODO: Test.equals(["\ud83d", ""], s.split(/\ude00/));

    s = "😀😀😀";
    // TODO: Test.equals(["", "\ude00", "\ude00", "\ude00"], s.split(/\ud83d/));
    // TODO: Test.equals(["\ud83d", "\ud83d", "\ud83d", ""], s.split(/\ude00/));
};

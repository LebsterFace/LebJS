// basic functionality
{
    Test.expect(2, String.prototype.split.length);

    Test.expectEqual(["hello friends"], "hello friends".split());
    Test.expectEqual([
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
    Test.expectEqual(["hello", "friends"], "hello friends".split(" "));

    Test.expectEqual(["a", "b", "c", "d"], "a,b,c,d".split(","));
    Test.expectEqual(["", "a", "b", "c", "d"], ",a,b,c,d".split(","));
    Test.expectEqual(["a", "b", "c", "d", ""], "a,b,c,d,".split(","));
    Test.expectEqual(["a", "b", "", "c", "d"], "a,b,,c,d".split(","));
    Test.expectEqual(["", "a", "b", "", "c", "d", ""], ",a,b,,c,d,".split(","));
    Test.expectEqual([",a,b", ",c,d,"], ",a,b,,,c,d,".split(",,"));
};

// limits
{
    Test.expectEqual([], "a b c d".split(" ", 0));
    Test.expectEqual(["a"], "a b c d".split(" ", 1));
    Test.expectEqual(["a", "b", "c"], "a b c d".split(" ", 3));
    Test.expectEqual(["a", "b", "c", "d"], "a b c d".split(" ", 100));
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

    Test.expectEqual(["(2016, "2016-01-02".split(new RegExp1("-")))", "(01)", "(02)"]);
    Test.expectEqual(["2016", "01", "02"], "2016-01-02".split(new RegExp("-")));
    */

    // Test.expectEqual(["a", "b"], /a*?/[Symbol.split]("ab"));
    // Test.expectEqual(["", "b"], /a*/[Symbol.split]("ab"));

    // let captureResult = /<(\/)?([^<>]+)>/[Symbol.split]("A<B>bold</B>and<CODE>coded</CODE>");
    /* Test.expectEqual([
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
    Test.expectEqual(["😀"], s.split());
    Test.expectEqual(["", ""], s.split("😀"));
    Test.expectEqual(["", "\ude00"], s.split("\ud83d"));
    Test.expectEqual(["\ud83d", ""], s.split("\ude00"));

    // TODO: Test.expectEqual(["", "\ude00"], s.split(/\ud83d/));
    // TODO: Test.expectEqual(["\ud83d", ""], s.split(/\ude00/));

    s = "😀😀😀";
    // TODO: Test.expectEqual(["", "\ude00", "\ude00", "\ude00"], s.split(/\ud83d/));
    // TODO: Test.expectEqual(["\ud83d", "\ud83d", "\ud83d", ""], s.split(/\ude00/));
};

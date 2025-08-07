{
    function go(a) {
        let i = 0;
        a[i] = a[++i];
    }

    let array = [1, 2, 3];
    go(array);
    Test.expectEqual([2, 2, 3], array);
}

{
    function go(a) {
        let i = 0;
        a[i] |= a[++i];
    }

    let array = [1, 2];
    go(array);
    Test.expectEqual([3, 2], array);
}

{
    let i = 0;
    let array = [1, 2, 3];
    [array[++i]] = [array[++i]];
    Test.expectEqual([1, 2, 2], array);
}

{
    let i = 0;
    let array = [1, 2, 3];
    array[++i] = array[++i];
    Test.expectEqual([1, 3, 3], array);
}
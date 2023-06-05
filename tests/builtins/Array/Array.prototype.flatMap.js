// length is 1
Test.expect(1, Array.prototype.flatMap.length);

// normal behavior
// basic functionality
{
    function identity(i) {
        return i;
    }

    const array1 = [1, 2, [3, 4]];
    const array2 = [1, 2, [3, 4, [5, 6]]];
    Test.equals([1, 2, 3, 4], array1.flatMap(identity));
    // only goes to depth 1
    Test.equals([1, 2, 3, 4, [5, 6]], array2.flatMap(identity));
}

// flattens return values
{
    function double(i) {
        return [i, 2 * i];
    }

    const array1 = [1, 2];
    const array2 = [1, [3]];
    Test.equals([1, 2, 2, 4], array1.flatMap(double));

    // looks weird but it is correct
    Test.equals([1, 2, [3], 6], array2.flatMap(double));
}

// binds this value
{
    let this_ = undefined;
    function callable() {
        this_ = this;
    }
    const this_arg = { "hello?": "always" };
    [0].flatMap(callable, this_arg);
    Test.equals(this_arg, this_);
}

// gives secondary arguments
{
    const found_values = [];
    const found_indices = [];
    const found_array_values = [];
    const found_this_values = [];
    function callable(val, index, obj) {
        found_values.push(val);
        found_indices.push(index);
        found_array_values.push(obj);
        found_this_values.push(this);
    }
    const this_arg = { "hello?": "always" };
    const array = ["a", "b", "c"];
    array.flatMap(callable, this_arg);

    Test.equals(["a", "b", "c"], found_values);
    Test.equals([0, 1, 2], found_indices);
    Test.equals([array, array, array], found_array_values);
    Test.equals([this_arg, this_arg, this_arg], found_this_values);
}

// empty array means no calls
{
    let called = false;
    function callable() {
        called = true;
        Test.fail();
    }
    [].flatMap(callable);
    Test.expect(false, called);
}
let get = undefined;
let set = undefined;

{
    let value = 40;
    get = function() {
        return value;
    }

    set = function(val) {
        value = val;
    }
}

expect(40, get())
let value = "hello!"
expect(40, get())
set(129)
expect(129, get())
expect("hello!", value)
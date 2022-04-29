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

Test.expect(40, get())
let value = "hello!"
Test.expect(40, get())
set(129)
Test.expect(129, get())
Test.expect("hello!", value)
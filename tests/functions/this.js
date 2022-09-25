Test.expect(globalThis, this);
(function(){
    Test.expect(5, this.length);
}).call("Hello");
Test.expect(globalThis, this);

{
    let ol = {
        ok: "this ok",
        op: function() {
            globalThis.ok = 20;
            return () => this.ok
        }
    };

    Test.expect('this ok', ol.op()());
}

{
    let ol = {
        ok: "this ok",
        op: function() {
            globalThis.ok = 20;
            function fun() {
                function run() {
                    return () => this.ok;
                }
                return run();
            }
            return fun()
        }
    };

    Test.expect(20, ol.op()())
}
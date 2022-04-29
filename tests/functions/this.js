Test.expect(globalThis, this);
(function(){
    Test.expect(5, this.length);
}).call("Hello");
Test.expect(globalThis, this);
expect(globalThis, this);
(function(){
    expect(5, this.length);
}).call("Hello");
expect(globalThis, this);
expect(globalThis, this);
bind("Hello");
expect("Hello", this);
unbind();
expect(globalThis, this);
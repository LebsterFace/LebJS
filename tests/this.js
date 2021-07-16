expect(globalThis, this);
bind("Hello");
expect(5, this.length);
unbind();
expect(globalThis, this);
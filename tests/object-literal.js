let obj = {"key": "value", noString: 2 + 2,

      tabs

                    :

3*3};

expect("value", obj.key);
expect(4, obj.noString);
expect(9, obj.tabs);
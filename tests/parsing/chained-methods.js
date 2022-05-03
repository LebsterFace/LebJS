const object = {
    method: () => object
};

Test.expect(object, object
                        .method()
                        .method()
)
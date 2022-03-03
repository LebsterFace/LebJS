expect(15, parseInt('0xF', 16));
expect(15, parseInt('F', 16));
expect(15, parseInt('17', 8));
expect(15, parseInt('015', 10));
expect(15, parseInt(15.99, 10));
expect(15, parseInt('15,123', 10));
expect(15, parseInt('FXX123', 16));
expect(15, parseInt('1111', 2));
expect(15, parseInt('15 * 3', 10));
expect(15, parseInt('15e2', 10));
expect(15, parseInt('15px', 10));
expect(15, parseInt('12', 13));

expect(NaN, parseInt('Hello', 8));
expect(NaN, parseInt('546', 2));

expect(-15, parseInt('-F', 16));
expect(-15, parseInt('-0F', 16));
expect(-15, parseInt('-0XF', 16));
expect(-15, parseInt(-15.1, 10));
expect(-15, parseInt('-17', 8));
expect(-15, parseInt('-15', 10));
expect(-15, parseInt('-1111', 2));
expect(-15, parseInt('-15e1', 10));
expect(-15, parseInt('-12', 13));

expect(4, parseInt(4.7, 10));
expect(4, parseInt(4.7 * 10000000000000000000000, 10));
expect(4, parseInt(0.00000000000434, 10));

expect(1, parseInt(0.0000001,10));
expect(1, parseInt(0.000000123,10));
expect(1, parseInt(0.0000001,10));
expect(1, parseInt(1000000000000000000000,10));
expect(1, parseInt(123000000000000000000000,10));
expect(1, parseInt(1000000000000000000000,10));

expect(224, parseInt('0e0', 16));

const obj = {
    valueOf: function() {
        return 8;
    }
};

expect(9, parseInt('11', obj));
obj.valueOf = function() {
    return 1
};

expect(NaN, parseInt('11', obj));

obj.valueOf = function() {
    return Infinity
};

expect(11, parseInt('11', obj));

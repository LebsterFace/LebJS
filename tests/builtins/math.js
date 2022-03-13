function expectToBeCloseTo(expected, received) {
    let difference = expected - received;
    if (difference < 0) difference = -difference;
    if (difference > 0.000001)
        expect(expected, received)
}

expectToBeCloseTo(Math.E, 2.718281);
expectToBeCloseTo(Math.LN2, 0.693147);
expectToBeCloseTo(Math.LN10, 2.302585);
expectToBeCloseTo(Math.LOG2E, 1.442695);
expectToBeCloseTo(Math.LOG10E, 0.434294);
expectToBeCloseTo(Math.PI, 3.1415926);
expectToBeCloseTo(Math.SQRT1_2, 0.707106);
expectToBeCloseTo(Math.SQRT2, 1.414213);
expect("[object Math]", Math.toString());
expect(1, Math.abs("-1"));
expect(2, Math.abs(-2));
expect(0, Math.abs(null));
expect(0, Math.abs(""));
expect(0, Math.abs([]));
expect(2, Math.abs([2]));
expect(true, isNaN(Math.abs([1, 2])));
expect(true, isNaN(Math.abs({})));
expect(true, isNaN(Math.abs("string")));
expect(true, isNaN(Math.abs()));
expect(true, isNaN(Math.acosh(-1)));
expect(true, isNaN(Math.acosh(0)));
expect(true, isNaN(Math.acosh(0.5)));
expectToBeCloseTo(Math.acosh(1), 0);
expectToBeCloseTo(Math.acosh(2), 1.316957);
expect(0, Math.asin(0));
expect(0, Math.asin(null));
expect(0, Math.asin(""));
expect(0, Math.asin([]));
expect(true, isNaN(Math.asin()));
expect(true, isNaN(Math.asin(undefined)));
expect(true, isNaN(Math.asin([1, 2, 3])));
expect(true, isNaN(Math.asin({})));
expect(true, isNaN(Math.asin("foo")));
expectToBeCloseTo(Math.asinh(0), 0);
expectToBeCloseTo(Math.asinh(1), 0.881373);
expect(0, Math.atan(0));
expect(-0, Math.atan(-0));
expect(true, isNaN(Math.atan(NaN)));
expectToBeCloseTo(Math.atan(-2), -1.1071487177940904);
expectToBeCloseTo(Math.atan(2), 1.1071487177940904);
expectToBeCloseTo(Math.atan(Infinity), Math.PI / 2);
expectToBeCloseTo(Math.atan(-Infinity), -Math.PI / 2);
expectToBeCloseTo(Math.atan(0.5), 0.4636476090008061);
expectToBeCloseTo(Math.atan2(90, 15), 1.4056476493802699);
expectToBeCloseTo(Math.atan2(15, 90), 0.16514867741462683);
expectToBeCloseTo(Math.atan2(0, -0), Math.PI);
expectToBeCloseTo(Math.atan2(-0, -0), -Math.PI);
expect(0, Math.atan2(0, 0));
expect(-0, Math.atan2(-0, 0));
expectToBeCloseTo(Math.atan2(0, -1), Math.PI);
expectToBeCloseTo(Math.atan2(-0, -1), -Math.PI);
expect(0, Math.atan2(0, 1));
expect(-0, Math.atan2(-0, 1));
expectToBeCloseTo(Math.atan2(-1, 0), -Math.PI / 2);
expectToBeCloseTo(Math.atan2(-1, -0), -Math.PI / 2);
expectToBeCloseTo(Math.atan2(1, 0), Math.PI / 2);
expectToBeCloseTo(Math.atan2(1, -0), Math.PI / 2);
expectToBeCloseTo(Math.atan2(1, -Infinity), Math.PI);
expectToBeCloseTo(Math.atan2(-1, -Infinity), -Math.PI);
expect(0, Math.atan2(1, Infinity));
expect(-0, Math.atan2(-1, Infinity));
expectToBeCloseTo(Math.atan2(Infinity, 1), Math.PI / 2);
expectToBeCloseTo(Math.atan2(-Infinity, 1), -Math.PI / 2);
expectToBeCloseTo(Math.atan2(Infinity, -Infinity), (3 * Math.PI) / 4);
expectToBeCloseTo(Math.atan2(-Infinity, -Infinity), (-3 * Math.PI) / 4);
expectToBeCloseTo(Math.atan2(Infinity, Infinity), Math.PI / 4);
expectToBeCloseTo(Math.atan2(-Infinity, Infinity), -Math.PI / 4);
expect(true, isNaN(Math.atanh(-2)));
expect(true, isNaN(Math.atanh(2)));
expect(-Infinity, Math.atanh(-1));
expect(0, Math.atanh(0));
expectToBeCloseTo(Math.atanh(0.5), 0.549306);
expect(Infinity, Math.atanh(1));
expect(true, isNaN(Math.cbrt(NaN)));
expect(-1, Math.cbrt(-1));
expect(-0, Math.cbrt(-0));
expect(-Infinity, Math.cbrt(-Infinity));
expect(1, Math.cbrt(1));
expect(Infinity, Math.cbrt(Infinity));
expect(0, Math.cbrt(null));
expectToBeCloseTo(Math.cbrt(2), 1.259921);
expect(1, Math.ceil(0.95));
expect(4, Math.ceil(4));
expect(8, Math.ceil(7.004));
expect(-0, Math.ceil(-0.95));
expect(-4, Math.ceil(-4));
expect(-7, Math.ceil(-7.004));
expect(true, isNaN(Math.ceil()));
expect(true, isNaN(Math.ceil(NaN)));
expect(1, Math.cos(0));
expect(1, Math.cos(null));
expect(1, Math.cos(""));
expect(1, Math.cos([]));
expect(-1, Math.cos(Math.PI));
expect(true, isNaN(Math.cos()));
expect(true, isNaN(Math.cos(undefined)));
expect(true, isNaN(Math.cos([1, 2, 3])));
expect(true, isNaN(Math.cos({})));
expect(true, isNaN(Math.cos("foo")));
expect(1, Math.cosh(0));
expectToBeCloseTo(Math.cosh(1), 1.5430806348152437);
expectToBeCloseTo(Math.cosh(-1), 1.5430806348152437);
expect(1, Math.exp(0));
expectToBeCloseTo(Math.exp(-2), 0.135335);
expectToBeCloseTo(Math.exp(-1), 0.367879);
expectToBeCloseTo(Math.exp(1), 2.718281);
expectToBeCloseTo(Math.exp(2), 7.389056);
expect(true, isNaN(Math.exp()));
expect(true, isNaN(Math.exp(undefined)));
expect(true, isNaN(Math.exp("foo")));
expect(0, Math.expm1(0));
expectToBeCloseTo(Math.expm1(-2), -0.864664);
expectToBeCloseTo(Math.expm1(-1), -0.63212);
expectToBeCloseTo(Math.expm1(1), 1.718281);
expectToBeCloseTo(Math.expm1(2), 6.389056);
expect(true, isNaN(Math.expm1()));
expect(true, isNaN(Math.expm1(undefined)));
expect(true, isNaN(Math.expm1("foo")));
expect(0, Math.floor(0.95));
expect(4, Math.floor(4));
expect(7, Math.floor(7.004));
expect(-1, Math.floor(-0.95));
expect(-4, Math.floor(-4));
expect(-8, Math.floor(-7.004));
expect(true, isNaN(Math.floor()));
expect(true, isNaN(Math.floor(NaN)));
expect(5, Math.hypot(3, 4));
expectToBeCloseTo(Math.hypot(3, 4, 5), 7.0710678118654755);
expect(0, Math.hypot());
expect(NaN, Math.hypot(NaN));
expect(NaN, Math.hypot(3, 4, "foo"));
expectToBeCloseTo(Math.hypot(3, 4, "5"), 7.0710678118654755);
expect(3, Math.hypot(-3));
expect(NaN, Math.log(-1));
expect(-Infinity, Math.log(0));
expect(0, Math.log(1));
expectToBeCloseTo(Math.log(10), 2.302585092994046);
expectToBeCloseTo(Math.log10(2), 0.3010299956639812);
expect(0, Math.log10(1));
expect(-Infinity, Math.log10(0));
expect(NaN, Math.log10(-2));
expect(5, Math.log10(100000));
expect(true, isNaN(Math.log1p(-2)));
expect(-Infinity, Math.log1p(-1));
expect(0, Math.log1p(0));
expectToBeCloseTo(Math.log1p(1), 0.693147);
expectToBeCloseTo(Math.log2(3), 1.584962500721156);
expect(1, Math.log2(2));
expect(0, Math.log2(1));
expect(-Infinity, Math.log2(0));
expect(NaN, Math.log2(-2));
expect(10, Math.log2(1024));
expect(-Infinity, Math.max());
expect(1, Math.max(1));
expect(2, Math.max(2, 1));
expect(3, Math.max(1, 2, 3));
expect(0, Math.max(-0, 0));
expect(0, Math.max(0, -0));
expect(true, isNaN(Math.max(NaN)));
expect(true, isNaN(Math.max("String", 1)));
expect(1, Math.min(1));
expect(1, Math.min(2, 1));
expect(1, Math.min(1, 2, 3));
expect(-0, Math.min(-0, 0));
expect(-0, Math.min(0, -0));
expect(true, isNaN(Math.min(NaN)));
expect(true, isNaN(Math.min("String", 1)));
expect(1, Math.pow(2, 0));
expect(2, Math.pow(2, 1));
expect(4, Math.pow(2, 2));
expect(8, Math.pow(2, 3));
expect(0.125, Math.pow(2, -3));
expect(9, Math.pow(3, 2));
expect(1, Math.pow(0, 0));
expect(512, Math.pow(2, Math.pow(3, 2)));
expect(64, Math.pow(Math.pow(2, 3), 2));
expect(8, Math.pow("2", "3"));
expect(1, Math.pow("", []));
expect(1, Math.pow([], null));
expect(1, Math.pow(null, null));
expect(1, Math.pow(undefined, null));
expect(true, isNaN(Math.pow(NaN, 2)));
expect(true, isNaN(Math.pow(2, NaN)));
expect(true, isNaN(Math.pow(undefined, 2)));
expect(true, isNaN(Math.pow(2, undefined)));
expect(true, isNaN(Math.pow(null, undefined)));
expect(true, isNaN(Math.pow(2, "foo")));
expect(true, isNaN(Math.pow("foo", 2)));
expect(1, Math.sign(0.0001));
expect(1, Math.sign(1));
expect(1, Math.sign(42));
expect(1, Math.sign(Infinity));
expect(0, Math.sign(0));
expect(0, Math.sign(null));
expect(0, Math.sign(""));
expect(0, Math.sign([]));
expect(-1, Math.sign(-0.0001));
expect(-1, Math.sign(-1));
expect(-1, Math.sign(-42));
expect(-1, Math.sign(-Infinity));
expect(-0, Math.sign(-0));
expect(-0, Math.sign(-null));
expect(-0, Math.sign(-""));
expect(-0, Math.sign(-[]));
expect(true, isNaN(Math.sign()));
expect(true, isNaN(Math.sign(undefined)));
expect(true, isNaN(Math.sign([1, 2, 3])));
expect(true, isNaN(Math.sign({})));
expect(true, isNaN(Math.sign(NaN)));
expect(true, isNaN(Math.sign("foo")));
expect(0, Math.sin(0));
expect(0, Math.sin(null));
expect(0, Math.sin(""));
expect(0, Math.sin([]));
expect(-1, Math.sin((Math.PI * 3) / 2));
expect(1, Math.sin(Math.PI / 2));
expect(true, isNaN(Math.sin()));
expect(true, isNaN(Math.sin(undefined)));
expect(true, isNaN(Math.sin([1, 2, 3])));
expect(true, isNaN(Math.sin({})));
expect(true, isNaN(Math.sin("foo")));
expect(0, Math.sinh(0));
expectToBeCloseTo(Math.sinh(1), 1.1752011936438014);
expect(3, Math.sqrt(9));
expect(0, Math.tan(0));
expect(0, Math.tan(null));
expect(0, Math.tan(""));
expect(0, Math.tan([]));
expect(1, Math.ceil(Math.tan(Math.PI / 4)));
expect(true, isNaN(Math.tan()));
expect(true, isNaN(Math.tan(undefined)));
expect(true, isNaN(Math.tan([1, 2, 3])));
expect(true, isNaN(Math.tan({})));
expect(true, isNaN(Math.tan("foo")));
expect(0, Math.tanh(0));
expect(1, Math.tanh(Infinity));
expect(-1, Math.tanh(-Infinity));
expectToBeCloseTo(Math.tanh(1), 0.7615941559557649);
expect(13, Math.trunc(13.37));
expect(42, Math.trunc(42.84));
expect(0, Math.trunc(0.123));
expect(-0, Math.trunc(-0.123));
expect(true, isNaN(Math.trunc(NaN)));
expect(true, isNaN(Math.trunc("foo")));
expect(true, isNaN(Math.trunc()));
expect(1, Math.round(1.25));
expect(-1, Math.round(-1.25));
expect(2, Math.round(1.5));
expect(-1, Math.round(-1.5));
expect(2, Math.round(1.75));
expect(-2, Math.round(-1.75));
expect(1, Math.round(1));
expect(-1, Math.round(-1));
expect(4294967297, Math.round(4294967296.5));
expect(-4294967296, Math.round(-4294967296.5));
expect(4294967297, Math.round(4294967297));
expect(-4294967297, Math.round(-4294967297));
expect(1, Math.floor(1.25));
expect(-2, Math.floor(-1.25));
expect(1, Math.floor(1.5));
expect(-2, Math.floor(-1.5));
expect(1, Math.floor(1.75));
expect(-2, Math.floor(-1.75));
expect(1, Math.floor(1));
expect(-1, Math.floor(-1));
expect(4294967296, Math.floor(4294967296.5));
expect(-4294967297, Math.floor(-4294967296.5));
expect(4294967297, Math.floor(4294967297));
expect(-4294967297, Math.floor(-4294967297));
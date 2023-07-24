Test.expect(1, Number.prototype.toString.length);

Test.expect('16792940265', (139947 * 119995).toString());
Test.expect("0", (+0).toString());
Test.expect("0", (-0).toString());
Test.expect("Infinity", (Infinity).toString());
Test.expect("-Infinity", (-Infinity).toString());
Test.expect("NaN", (NaN).toString());
Test.expect("12", (12).toString());
Test.expect("93465", (93465).toString());
Test.expect("358000", (358000).toString());
Test.expect("2147483648", (2147483648).toString());
Test.expect("4294967295", (4294967295).toString());
Test.expect("18014398509481984", (18014398509481984).toString());

// decimal radix gets converted to int
Test.expect("30", (30).toString(10.1));
Test.expect("30", (30).toString(10.9));

// errors
// must be called with numeric `this`
Test.expectError("TypeError", "Number.prototype.toString requires that 'this' be a Number", () => Number.prototype.toString.call(true));
Test.expectError("TypeError", "Number.prototype.toString requires that 'this' be a Number", () => Number.prototype.toString.call([]));
Test.expectError("TypeError", "Number.prototype.toString requires that 'this' be a Number", () => Number.prototype.toString.call({}));
Test.expectError("TypeError", "Number.prototype.toString requires that 'this' be a Number", () => Number.prototype.toString.call(Symbol("foo")));
Test.expectError("TypeError", "Number.prototype.toString requires that 'this' be a Number", () => Number.prototype.toString.call("bar"));
// TODO: Test.expectError("TypeError", "Number.prototype.toString requires that 'this' be a Number", () => Number.prototype.toString.call(1n));

// radix RangeError
Test.expectError("RangeError", 'toString() radix argument must be between 2 and 36', () => (0).toString(0));
Test.expectError("RangeError", 'toString() radix argument must be between 2 and 36', () => (0).toString(1));
Test.expectError("RangeError", 'toString() radix argument must be between 2 and 36', () => (0).toString(37));
Test.expectError("RangeError", 'toString() radix argument must be between 2 and 36', () => (0).toString(100));

// radix
Test.expect("11101111110010111100000", (7857632).toString(2));
Test.expect("112210012122102", (7857632).toString(3));
Test.expect("131332113200", (7857632).toString(4));
Test.expect("4002421012", (7857632).toString(5));
Test.expect("440225532", (7857632).toString(6));
Test.expect("123534356", (7857632).toString(7));
Test.expect("35762740", (7857632).toString(8));
Test.expect("15705572", (7857632).toString(9));
Test.expect("7857632", (7857632).toString(10));
Test.expect("4487612", (7857632).toString(11));
Test.expect("276b2a8", (7857632).toString(12));
Test.expect("18216b3", (7857632).toString(13));
Test.expect("10877d6", (7857632).toString(14));
Test.expect("a532c2", (7857632).toString(15));
Test.expect("77e5e0", (7857632).toString(16));
Test.expect("59160b", (7857632).toString(17));
Test.expect("42f5h2", (7857632).toString(18));
Test.expect("335b5b", (7857632).toString(19));
Test.expect("29241c", (7857632).toString(20));
Test.expect("1j89fk", (7857632).toString(21));
Test.expect("1bbkh2", (7857632).toString(22));
Test.expect("151ih4", (7857632).toString(23));
Test.expect("ng9h8", (7857632).toString(24));
Test.expect("k2m57", (7857632).toString(25));
Test.expect("h51ig", (7857632).toString(26));
Test.expect("el5hb", (7857632).toString(27));
Test.expect("clqdk", (7857632).toString(28));
Test.expect("b355o", (7857632).toString(29));
Test.expect("9l0l2", (7857632).toString(30));
Test.expect("8fng0", (7857632).toString(31));
Test.expect("7fpf0", (7857632).toString(32));
Test.expect("6klf2", (7857632).toString(33));
Test.expect("5tv8s", (7857632).toString(34));
Test.expect("589dr", (7857632).toString(35));
Test.expect("4oezk", (7857632).toString(36));

// extremely large number with radix
Test.expect("101111011010000000010001011010101110000101111100111010000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000", 2.2171010912173817e51.toString(2));
Test.expect("122200222200200002020000221212111212020212021222122122201112112001202200202210122011020121220102211210120110", 2.2171010912173817e51.toString(3));
Test.expect("11323100002023111300233213100000000000000000000000000000000000000000000000000000000000", 2.2171010912173817e51.toString(4));
Test.expect("20213333043100433442123113244334300004432332001224240433304410013422100304", 2.2171010912173817e51.toString(5));
Test.expect("550003524220341511224004404200404220224024020444420402442220220420", 2.2171010912173817e51.toString(6));
Test.expect("4235625635040541506562064422461122351652161506062250163366335", 2.2171010912173817e51.toString(7));
Test.expect("573200213256057472000000000000000000000000000000000000000", 2.2171010912173817e51.toString(8));
Test.expect("580880602200855367001288271433120018026801542154811843", 2.2171010912173817e51.toString(9));
Test.expect("2.2171010912173817e+51", 2.2171010912173817e51.toString(10));
Test.expect("20941975227252065997040823726473715831623804aa6726", 2.2171010912173817e51.toString(11));
Test.expect("426276185347a25840488044080484880408844840480440", 2.2171010912173817e51.toString(12));
Test.expect("136bc76718505c84cb00730624426c569759715872b8b70", 2.2171010912173817e51.toString(13));
Test.expect("835c3959100c338282668c622a8a060a24602aa6a66cc", 2.2171010912173817e51.toString(14));
Test.expect("5e1ae5933690a58aad6a9546513bbbd49305ec050549", 2.2171010912173817e51.toString(15));
Test.expect("5ed008b570be7400000000000000000000000000000", 2.2171010912173817e51.toString(16));
Test.expect("7f3da30c676ag43gc65022d17gae829c8790d1d479", 2.2171010912173817e51.toString(17));
Test.expect("dba188ded00ha8a6c2800e808c20aagggg4g4822c", 2.2171010912173817e51.toString(18));
Test.expect("1af846i1f7ge0bfb7b4c3f1abd5a70g3bhd592fb6", 2.2171010912173817e51.toString(19));
Test.expect("40d316h12f73ec848404c8c0g4400gc48c400804", 2.2171010912173817e51.toString(20));
Test.expect("cd59ci682hih768393c4d1kc5757jkgg918b2dc", 2.2171010912173817e51.toString(21));
Test.expect("239f104idd3dikgg6e4eccg66aee26a0kc2kac6", 2.2171010912173817e51.toString(22));
Test.expect("93f4b0317667h2gm0gka6ia4h9emj7b6aef4ig", 2.2171010912173817e51.toString(23));
Test.expect("1lca597424748g080gggg80g08gg8880880g80", 2.2171010912173817e51.toString(24));
Test.expect("abii4g0njm70ejj3c39lajgi5l3bdf6gl25ne", 2.2171010912173817e51.toString(25));
Test.expect("2e8foaj1lec80c86cck264caii282m6m8e6a0", 2.2171010912173817e51.toString(26));
Test.expect("hiqii260pnbeqe894nd351nqpo5nm3j45jj3", 2.2171010912173817e51.toString(27));
Test.expect("4qmgqk0rk788oog4c8k0k0cg8o04cocckckc", 2.2171010912173817e51.toString(28));
Test.expect("1d2nmd4bln2jmf0k72sl2ejhmj7m8rq70flk", 2.2171010912173817e51.toString(29));
Test.expect("d8onrgfc6kaik26a4oqmee4ic0ksi0k0k2o", 2.2171010912173817e51.toString(30));
Test.expect("4b4u5lb8fb8ttacfifj5g1iapimkppcehrq", 2.2171010912173817e51.toString(31));
Test.expect("1fd025le2v7800000000000000000000000", 2.2171010912173817e51.toString(32));
Test.expect("h5mgn3kr83kucw13c6102w7n5loohbfct6", 2.2171010912173817e51.toString(33));
Test.expect("6e0244vn6ba0gw8ucgkmuwgk4oi02g8icq", 2.2171010912173817e51.toString(34));
Test.expect("2g7ohyr3v1t7c0jrq621aw56kflrj71pfj", 2.2171010912173817e51.toString(35));
Test.expect("z03wqcmb7dw00g80wow0sksk484wgswsc", 2.2171010912173817e51.toString(36));
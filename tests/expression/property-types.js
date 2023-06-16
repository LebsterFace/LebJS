const foo = 'FOO';
const bar = 'BAR';
const get = 'GET';
const set = 'SET';

const objects = [
	{ 'foo': bar },
	{ 123: bar },
	{ foo: bar },
	{ [foo]: bar },
	{ foo() { return bar; } },
	{ [foo]() { return bar; } },
	{ get: bar },
	{ set: bar },
	{ get },
	{ set },
	{ foo },
	{ [get]: bar },
	{ [set]: bar },
	{ get foo() { return bar; } },
	{ set foo(bar) { return bar; } },
	{ get 'foo'() { return bar; } },
	{ set 'foo'(bar) { return bar; } },
	{ get 123() { return bar; } },
	{ set 123(bar) { return bar; } },
	{ get [foo]() { return bar; } },
	{ set [foo](baz) { return bar; } },
	{ ...foo },
	{ ...get },
	{ ...set },
];
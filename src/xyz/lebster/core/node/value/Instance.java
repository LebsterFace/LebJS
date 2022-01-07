package xyz.lebster.core.node.value;

public class Instance extends ObjectLiteral {
	private ObjectLiteral prototype;

	public Instance(ObjectLiteral prototype) {
		this.prototype = prototype;
	}

	@Override
	public ObjectLiteral getPrototype() {
		return prototype;
	}

	public void setPrototype(ObjectLiteral prototype) {
		this.prototype = prototype;
	}
}

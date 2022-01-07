package xyz.lebster.core.node.value.object;

public class Instance extends ObjectValue {
	private ObjectValue prototype;

	public Instance(ObjectValue prototype) {
		this.prototype = prototype;
	}

	@Override
	public ObjectValue getPrototype() {
		return prototype;
	}

	public void setPrototype(ObjectValue prototype) {
		this.prototype = prototype;
	}
}

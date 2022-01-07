package xyz.lebster.core.node.value;

public class Instance extends Dictionary {
	private Dictionary prototype;

	public Instance(Dictionary prototype) {
		this.prototype = prototype;
	}

	@Override
	public Dictionary getPrototype() {
		return prototype;
	}

	public void setPrototype(Dictionary prototype) {
		this.prototype = prototype;
	}
}

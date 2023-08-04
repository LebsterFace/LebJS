package xyz.lebster.core.value;


import xyz.lebster.core.ANSI;

public interface Displayable {
	void display(StringBuilder builder);

	default String toDisplayString(boolean stripFormatting) {
		final StringBuilder builder = new StringBuilder();
		this.display(builder);
		final String result = builder.toString();
		return stripFormatting ? ANSI.stripFormatting(result) : result;
	}
}

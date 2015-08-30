package amidst.bytedata.builder;

import java.util.ArrayList;
import java.util.List;

import amidst.bytedata.CCMethod;
import amidst.bytedata.ClassChecker;
import amidst.bytedata.builder.ClassCheckerBuilder.SimpleClassCheckerBuilder;

public class CCMethodBuilder extends SimpleClassCheckerBuilder {
	private String name;
	private List<String> methods = new ArrayList<String>();

	public CCMethodBuilder(String name) {
		this.name = name;
	}

	public CCMethodBuilder method(String val1, String val2) {
		methods.add(val1);
		methods.add(val2);
		return this;
	}

	@Override
	protected ClassChecker get() {
		ensureMethodPresent();
		return new CCMethod(name, methods.toArray(new String[methods
				.size()]));
	}

	private void ensureMethodPresent() {
		if (methods.isEmpty()) {
			throw new IllegalStateException(
					"method matcher must have at least one method");
		}
	}
}
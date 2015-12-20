package amidst.mojangapi.file.json.version;

import amidst.documentation.GsonConstructor;
import amidst.documentation.Immutable;

@Immutable
public class LibraryRuleOsJson {
	private volatile String name;

	@GsonConstructor
	public LibraryRuleOsJson() {
	}

	public LibraryRuleOsJson(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
package amidst.byteclass.finder.prepare;

import java.util.List;

import amidst.byteclass.ByteClass;

public class MultiBCP extends ByteClassPreparer {
	private List<ByteClassPreparer> preparers;

	public MultiBCP(List<ByteClassPreparer> preparers) {
		this.preparers = preparers;
	}

	@Override
	public void prepare(ByteClass byteClass) {
		for (ByteClassPreparer preparer : preparers) {
			preparer.prepare(byteClass);
		}
	}
}
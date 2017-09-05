package testbundle;

import org.eclipse.jdt.ls.core.internal.IParticipant;
import org.eclipse.jdt.ls.core.internal.JavaInitializeResult;

public class TestParticipant implements IParticipant {
	
	public static Object getInstance() {
		return new TestParticipant();
	}

	@Override
	public void initialize(JavaInitializeResult result) {
		result.getParticipantData().put("TEST_KEY", "TEST_VALUE");

	}
}

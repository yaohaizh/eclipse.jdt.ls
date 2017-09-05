package testbundle;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IExecutableExtensionFactory;

public class ParticipantFactory implements IExecutableExtensionFactory {

	@Override
	public Object create() throws CoreException {
		return TestParticipant.getInstance();
	}
}
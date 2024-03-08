package software.xdev.spring.data.eclipse.store.repository.support.copier.copier;

@FunctionalInterface
public interface RegisteringWorkingCopyAndOriginal
{
	boolean register(final Object workingCopy, final Object objectToStore);
}

package software.xdev.spring.data.eclipse.store.exceptions;

public class BinaryHandlerOnlyForCopyingException extends RuntimeException
{
	public BinaryHandlerOnlyForCopyingException()
	{
		super(
			"This handler should only be used for copying objects one time. It shouldn't be used for anything else, "
				+ "yet it looks like you are using it for something else.");
	}
}

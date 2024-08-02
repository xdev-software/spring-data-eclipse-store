package software.xdev.spring.data.eclipse.store.repository.query.antlr;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;


public class HSqlQueryExecutor
{
	private final
	software.xdev.spring.data.eclipse.store.repository.query.antlr.HqlParser parser;
	
	public HSqlQueryExecutor(final String sqlString)
	{
		// Create a lexer that feeds off of input CharStream
		final software.xdev.spring.data.eclipse.store.repository.query.antlr.HqlLexer lexer =
			new software.xdev.spring.data.eclipse.store.repository.query.antlr.HqlLexer(CharStreams.fromString(sqlString));
		
		// Create a buffer of tokens between the lexer and parser
		final CommonTokenStream tokens = new CommonTokenStream(lexer);
		
		// Create a parser that feeds off the tokens buffer
		this.parser =
			new software.xdev.spring.data.eclipse.store.repository.query.antlr.HqlParser(tokens);
	}
	
	public Object execute(final Object[] parameters)
	{
		// TODO
		return null;
	}
}

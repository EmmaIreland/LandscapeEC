package problem.ecc;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.StringReader;

import landscapeEC.problem.ecc.EccParser;

import org.junit.Test;

public class ParseTest {

	@Test
	public void checkParsing() throws IOException {
		EccParser parser = new EccParser();
		
		StringReader reader = new StringReader("numOfBitsPerWord:12\nnumOfCodeWords:24");
		parser.parseProblem(reader);
		assertEquals("Parser did not correctly parse number of bits per word", 12, parser.getBitsPerWord());
		assertEquals("Parser did not correctly parse number of code words", 24, parser.getNumCodeWords());
	}
	
	@Test
	public void checkParsingOutOfOrder() throws IOException {
		EccParser parser = new EccParser();
		
		StringReader reader = new StringReader("numOfCodeWords:24\nnumOfBitsPerWord:12");
		parser.parseProblem(reader);
		assertEquals("Parser did not correctly parse number of bits per word", 12, parser.getBitsPerWord());
		assertEquals("Parser did not correctly parse number of code words", 24, parser.getNumCodeWords());
	}
	
}

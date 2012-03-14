package net.ohloh.ohcount4j.scan;

import org.testng.annotations.Test;

import static net.ohloh.ohcount4j.Entity.*;
import static net.ohloh.ohcount4j.Language.LANG_SMALLTALK;

public class SmalltalkScannerTest extends BaseScannerTest {

	@Test
	public void basic() {
		assertLine(new SmalltalkScanner(), new Line(LANG_SMALLTALK, BLANK),   "\n");
		assertLine(new SmalltalkScanner(), new Line(LANG_SMALLTALK, BLANK),   "     \n");
		assertLine(new SmalltalkScanner(), new Line(LANG_SMALLTALK, BLANK),   "\t\n");
		assertLine(new SmalltalkScanner(), new Line(LANG_SMALLTALK, CODE),    "^Student new name: aPerson name\n");
		assertLine(new SmalltalkScanner(), new Line(LANG_SMALLTALK, COMMENT), "\"Line comment\"\n");
		assertLine(new SmalltalkScanner(), new Line(LANG_SMALLTALK, COMMENT), "\"\"\n");
		assertLine(new SmalltalkScanner(), new Line(LANG_SMALLTALK, CODE),    " y := y + 7. // with comment\n");
	}

	@Test
	public void eofHandling() {
		// Note lack of trailing \n in all cases below
		assertLine(new SmalltalkScanner(), new Line(LANG_SMALLTALK, BLANK),   "     ");
		assertLine(new SmalltalkScanner(), new Line(LANG_SMALLTALK, BLANK),   "\t");
		assertLine(new SmalltalkScanner(), new Line(LANG_SMALLTALK, CODE),    "^Student new name: aPerson name");
		assertLine(new SmalltalkScanner(), new Line(LANG_SMALLTALK, COMMENT), "\"Line comment\"");
		assertLine(new SmalltalkScanner(), new Line(LANG_SMALLTALK, COMMENT), "\"\"");
		assertLine(new SmalltalkScanner(), new Line(LANG_SMALLTALK, CODE),    " y := y + 7. // with comment");
	}

	@Test
	public void sampleTest() {
		String code
			= "\"Simple piece of a Smalltalk\n"
			+ "			Program\n"
			+ "\n"
			+ "\"\n"
			+ "name: aName address: adAddress\n"
			+ "\"Set the receiver's name and address\n"
			+ "to the specified values.\"\n"
			+ "self name: aName.\n"
			+ "self address: anAddress\n";

		Line[] expected = {
			new Line(LANG_SMALLTALK, COMMENT),
			new Line(LANG_SMALLTALK, COMMENT),
			new Line(LANG_SMALLTALK, BLANK),
			new Line(LANG_SMALLTALK, COMMENT),
			new Line(LANG_SMALLTALK, CODE),
			new Line(LANG_SMALLTALK, COMMENT),
			new Line(LANG_SMALLTALK, COMMENT),
			new Line(LANG_SMALLTALK, CODE),
			new Line(LANG_SMALLTALK, CODE)
		};
		assertLines(new SmalltalkScanner(), expected, code);
	}

	@Test
	public void unterminatedBlockCommentCrash() {
		// This minimal case caused an Arrays.copyOfRange() crash
		String code = "\"\n\n\n";

		Line[] expected = {
				new Line(LANG_SMALLTALK, COMMENT),
				new Line(LANG_SMALLTALK, BLANK),
				new Line(LANG_SMALLTALK, BLANK)
			};
		assertLines(new SmalltalkScanner(), expected, code);
	}
}
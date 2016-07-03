package nl.aardbeitje.turing;

import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * Too lazy to add junit.
 */
public class ProgramTest {

	public static void main(String[] args) throws IOException {
		testComment();
		testParseInstruction();
		testParseInstructionWithAdditionalSpace();
		testParseInstructionWithStateNames();
		testProgram();
	}

	private static void testComment() throws IOException {
		String program = "# comment";
		Program p = new Program(new ByteArrayInputStream(program.getBytes()));
		assertThat(p.getInstructions().size() == 0, "expected 0 instructions");
	}

	private static void testParseInstruction() throws IOException {
		String program = "1: 0F2 0F2";
		Program p = new Program(new ByteArrayInputStream(program.getBytes()));
		assertThat(p.getInstructions().size() == 1, "expected 1 instructions");
		Instruction i = p.getInstructions().get("1");
		assertThat(i.isForwardOn0(), "expected forward-on-0");
		assertThat(!i.isWrite1On0(), "expected not write1-on-0");
		assertThat(i.getStateOn0().equals("2"), "expected state-on-0==2");
		assertThat(i.isForwardOn1(), "expected forward-on-1");
		assertThat(!i.isWrite1On1(), "expected not write1-on-1");
		assertThat(i.getStateOn1().equals("2"), "expected state-on-1==2");
		assertThat(i.getSpecial() == null, "expected special==null");
	}

	private static void testParseInstructionWithAdditionalSpace() throws IOException {
		String program = "1:     0F2 \t  0F2  \t";
		Program p = new Program(new ByteArrayInputStream(program.getBytes()));
		assertThat(p.getInstructions().size() == 1, "expected 1 instructions");
		Instruction i = p.getInstructions().get("1");
		assertThat(i.isForwardOn0(), "expected forward-on-0");
		assertThat(!i.isWrite1On0(), "expected not write1-on-0");
		assertThat(i.getStateOn0().equals("2"), "expected state-on-0==2");
		assertThat(i.isForwardOn1(), "expected forward-on-1");
		assertThat(!i.isWrite1On1(), "expected not write1-on-1");
		assertThat(i.getStateOn1().equals("2"), "expected state-on-1==2");
		assertThat(i.getSpecial() == null, "expected special==null");
	}

	private static void testParseInstructionWithStateNames() throws IOException {
		String program = "somestate: 1Botherstate 0Fyetanotherstate special";
		Program p = new Program(new ByteArrayInputStream(program.getBytes()));
		assertThat(p.getInstructions().size() == 1, "expected 1 instructions");
		Instruction i = p.getInstructions().get("somestate");
		assertThat(!i.isForwardOn0(), "expected forward-on-0");
		assertThat(i.isWrite1On0(), "expected not write1-on-0");
		assertThat(i.getStateOn0().equals("otherstate"), "expected state-on-0==otherstate");
		assertThat(i.isForwardOn1(), "expected forward-on-0");
		assertThat(!i.isWrite1On1(), "expected not write1-on-0");
		assertThat(i.getStateOn1().equals("yetanotherstate"), "expected state-on-1==yetanotherstate");
		assertThat(i.getSpecial().equals("special"), "expected special==special");
	}

	private static void testProgram() throws IOException {
		String program = "1: 0F2 0F2 \n2: 1B1 1B1 halt";
		Program p = new Program(new ByteArrayInputStream(program.getBytes()));
		assertThat(p.getInstructions().size() == 2, "expected 2 instructions");
		Instruction i1 = p.getInstructions().get("1");
		assertThat(i1.isForwardOn0(), "expected forward-on-0");
		assertThat(!i1.isWrite1On0(), "expected not write1-on-0");
		assertThat(i1.getStateOn0().equals("2"), "expected state-on-0==2");
		assertThat(i1.isForwardOn1(), "expected forward-on-1");
		assertThat(!i1.isWrite1On1(), "expected not write1-on-1");
		assertThat(i1.getStateOn1().equals("2"), "expected state-on-1==2");
		assertThat(i1.getSpecial() == null, "expected special==null");

		Instruction i2 = p.getInstructions().get("2");
		assertThat(!i2.isForwardOn0(), "expected forward-on-0");
		assertThat(i2.isWrite1On0(), "expected not write1-on-0");
		assertThat(i2.getStateOn0().equals("1"), "expected state-on-0==1");
		assertThat(!i2.isForwardOn1(), "expected forward-on-1");
		assertThat(i2.isWrite1On1(), "expected not write1-on-1");
		assertThat(i2.getStateOn1().equals("1"), "expected state-on-1==1");
		assertThat(i2.getSpecial().equals("halt"), "expected special==halt");
		
		for( String state : p.getInstructions().keySet()) {
			System.out.println(state + ":" + p.getInstructions().get(state));
		}
	}

	private static void assertThat(boolean b, String msg) {
		if (!b) {
			throw new IllegalArgumentException(msg);
		}

	}

}

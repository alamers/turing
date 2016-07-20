package nl.aardbeitje.turing;

import nl.aardbeitje.turing.InstructionPhase.Phase;

public class ProgramExecutor {

	private final Program program;
	private final TuringViewer viewer;
	private final TuringMachine machine;

	public ProgramExecutor(Program program, TuringMachine machine, TuringViewer viewer) {
		this.program = program;
		this.machine = machine;
		this.viewer = viewer;
	}

	public void runProgram() {
		Instruction instruction = program.getInstructions().get(Program.START_STATE);
		for (;;) {
			String newState = executeInstruction(instruction);
			if (Program.HALT.equals(instruction.getSpecial())) {
				viewer.halt();
				return;
			} else {
				instruction = program.getInstructions().get(newState);
			}
		}
	}

	private String executeInstruction(Instruction i) {
		viewer.currentInstruction(i, new InstructionPhase(Phase.READING));
		boolean read1 = machine.readOnly();
		viewer.currentInstruction(i, new InstructionPhase(Phase.DECIDING, read1));
		machine.readReturn();
		viewer.currentInstruction(i, new InstructionPhase(Phase.WRITING, read1));
		if (read1) {
			machine.write(read1, i.isWrite1On1());
		} else {
			machine.write(read1, i.isWrite1On0());
		}
		viewer.currentInstruction(i, new InstructionPhase(Phase.MOVING, read1));
		if (read1) {
			machine.move(i.isForwardOn1());
		} else {
			machine.move(i.isForwardOn0());
		}
		viewer.currentInstruction(i, new InstructionPhase(Phase.CHANGING, true));

		if (read1) {
			return i.getStateOn1();
		} else {
			return i.getStateOn0();
		}
	}

}

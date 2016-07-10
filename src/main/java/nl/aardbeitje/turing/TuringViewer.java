package nl.aardbeitje.turing;

public interface TuringViewer {

	void currentInstruction(Instruction i, InstructionPhase instructionPhase);

	void halt();

}

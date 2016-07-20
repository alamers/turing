package nl.aardbeitje.turing;

public interface TuringMachine {
	/**
	 * Moves the reader in and reads the current value.
	 * 
	 * Note: does not move the reader back, so call readReturn afterwards.
	 * @return the read value
	 */
	boolean readOnly();

	/**
	 * Moves the reader back in.
	 * 
	 * Note: call readOnly() before.
	 */
	void readReturn();

	/**
	 * Does a read. Equivalent with calling readOnly() and readReturn() sequentially.
	 * @return the read value
	 */
	default boolean read() {
		boolean value = readOnly();
		readReturn();
		return value;
	}

	/**
	 * Writes the newValue. If the newValue is equal to the current value this results in a noop.
	 * @param current
	 * @param newValue
	 */
	void write(boolean current, boolean newValue);

	/**
	 * Moves the tape forward (true) or backward (false).
	 */
	void move(boolean forward);

	void readPosition();
}

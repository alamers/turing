package nl.aardbeitje.turing;

import lejos.hardware.lcd.LCD;

public class Test {
	private static TuringMachine tm;

	public static void main(String[] args) {
		LCD.drawString("Turing Machine", 0, 0);
		LCD.drawString("Writing 0-1-0", 0, 2);

		tm = new TuringMachine();
		tm.loadCalibration();
		writeOnesAndZeros();
	}

	private static void writeOnesAndZeros() {
		final int steps = 10;
		for (int i = 0; i < steps; i++) {
			boolean current = tm.read();
			boolean future = i % 2 == 1;

			if (future) {
				tm.write1(current);
			} else {
				tm.write0(current);
			}

			tm.forward();
		}

		for (int i = 0; i < steps; i++) {
			tm.backward();
		}
	}

}

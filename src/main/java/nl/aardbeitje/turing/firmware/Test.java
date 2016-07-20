package nl.aardbeitje.turing.firmware;

import lejos.hardware.BrickFinder;
import lejos.hardware.ev3.EV3;
import lejos.hardware.lcd.LCD;
import nl.aardbeitje.turing.LocalLegoTuringMachine;

public class Test {
	private static LocalLegoTuringMachine tm;

	public static void main(String[] args) {
		LCD.drawString("Turing Machine", 0, 0);
		LCD.drawString("Writing 0-1-0", 0, 2);

		tm = new LocalLegoTuringMachine((EV3) BrickFinder.getLocal());
		tm.loadCalibration();
		writeOnesAndZeros();
	}

	private static void writeOnesAndZeros() {
		final int steps = 10;
		for (int i = 0; i < steps; i++) {
			boolean current = tm.read();
			boolean future = i % 2 == 1;
			tm.write(current, future);
			tm.forward();
		}

		for (int i = 0; i < steps; i++) {
			tm.backward();
		}
	}

}

package nl.aardbeitje.turing;

import lejos.hardware.Button;
import lejos.hardware.lcd.LCD;

public class Calibrate {

	public static void main(String[] args) {
		TuringMachine tm = new TuringMachine();

		LCD.drawString("Turing Machine", 0, 0);
		LCD.drawString("Current pos 0?", 0, 2);
		LCD.drawString("Press OK to start", 0, 3);
		Button.ENTER.waitForPressAndRelease();
		tm.calibrate();
		tm.saveCalibration();
	}
}

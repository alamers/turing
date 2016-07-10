package nl.aardbeitje.turing.firmware;

import lejos.hardware.BrickFinder;
import lejos.hardware.Button;
import lejos.hardware.ev3.EV3;
import lejos.hardware.lcd.LCD;
import nl.aardbeitje.turing.LegoTuringMachine;

public class Calibrate {

	public static void main(String[] args) {
		LegoTuringMachine tm = new LegoTuringMachine((EV3) BrickFinder.getLocal());

		LCD.drawString("Turing Machine", 0, 0);
		LCD.drawString("Current pos 0?", 0, 2);
		LCD.drawString("Press OK to start", 0, 3);
		Button.ENTER.waitForPressAndRelease();
		tm.calibrate();
		tm.saveCalibration();
	}
}

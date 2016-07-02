package nl.aardbeitje.turing;

import lejos.hardware.BrickFinder;
import lejos.hardware.Button;
import lejos.hardware.LED;
import lejos.hardware.ev3.EV3;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.port.Port;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.robotics.filter.LinearCalibrationFilter;
import lejos.robotics.filter.MeanFilter;

public class RemoteTest {
	private static final Port READER = SensorPort.S1;

	public static void main(String[] args) {

		EV3 ev3 = (EV3) BrickFinder.getDefault();

		LED led = ev3.getLED();
		led.setPattern(5);
		
		Port reader = ev3.getPort(READER.getName());
		TextLCD textLCD = ev3.getTextLCD();
		textLCD.clear();
		
		EV3ColorSensor sensor = new EV3ColorSensor(reader);
		for(String m: sensor.getAvailableModes()) {
			System.out.println(m);
		}
		
		int meanPeriod = 10;
		
		MeanFilter mean = new MeanFilter(sensor, meanPeriod);
		sensor.setCurrentMode("Red");
		
		LinearCalibrationFilter c = new LinearCalibrationFilter(mean);
		c.setCalibrationType(LinearCalibrationFilter.OFFSET_AND_SCALE_CALIBRATION);
		c.setScaleCalibration(-1, 1);
		
		c.startCalibration();
		while(Button.ENTER.isUp()) {
			float[] tmp = new float[c.sampleSize()];
			for (int i=0; i<meanPeriod; i++) {
				c.fetchSample(tmp, 0);
			}
			for (int k=0; k<c.getScaleCorrection().length; k++) {
				textLCD.drawString( String.format("c-%d: %2.3f / %2.3f", k, c.getScaleCorrection()[k] , c.getOffsetCorrection()[k]), 0, k+1);
				System.out.println( String.format("c-%d: %2.3f / %2.3f", k, c.getScaleCorrection()[k] , c.getOffsetCorrection()[k]));
			}
		}
		c.stopCalibration();

		led.setPattern(1);
		
		while(Button.ESCAPE.isUp()) {
			float[] tmp = new float[c.sampleSize()];
			for (int i=0; i<meanPeriod; i++) {
				c.fetchSample(tmp, 0);
			}
			for (int k=0; k<tmp.length; k++) {
				textLCD.drawString( String.format("read-%d: %2.3f", k, tmp[k]), 0, k+3);
				System.out.println( String.format("read-%d: %2.3f", k, tmp[k]));
			}
		}
		
		
		sensor.close();
		led.setPattern(0);
		
	}
}

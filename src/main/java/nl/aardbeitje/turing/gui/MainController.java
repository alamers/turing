package nl.aardbeitje.turing.gui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import nl.aardbeitje.turing.Instruction;
import nl.aardbeitje.turing.InstructionPhase;
import nl.aardbeitje.turing.InstructionPhase.Phase;

public class MainController {
	private static final String family = "Helvetica";

	@FXML
	private Label logLabel;

	@FXML
	private TextFlow currentInstructionTextFlow;

	@FXML
	private MenuItem menuTestInstructionPhases;

	private Stage stage;

	public void setStage(Stage stage) {
		this.stage = stage;
	}

	public void initialize() {
		logLabel.setText("Line 1\nLine2");
		menuTestInstructionPhases.setOnAction(e -> testInstructionPhases());
	}

	public void showCurrentInstruction(Instruction i, InstructionPhase ip) {
		Platform.runLater(() -> {

		Text instructionText = new Text(i.toString() + "\n");
		instructionText.setFont(Font.font(family, FontWeight.BOLD, 50));
		instructionText.setFill(Color.BLACK);

		currentInstructionTextFlow.getChildren().clear();
		currentInstructionTextFlow.getChildren().add(instructionText);
		addInstruction(currentInstructionTextFlow, true, i.isWrite1On0(), i.isForwardOn0(), i.getStateOn0(),
				ip.getPhase(), ip.isReadZero());
		addInstruction(currentInstructionTextFlow, false, i.isWrite1On1(), i.isForwardOn1(), i.getStateOn1(),
				ip.getPhase(), ip.isReadZero());
		});
	}

	private void addInstruction(TextFlow textFlow, boolean is0, boolean isWrite1, boolean isForward, String state,
			Phase p, boolean read0) {
		Text a = new Text("On a ");
		a.setFill(Color.DARKGREY);
		a.setFont(Font.font(family, 25));
		a.setUnderline(p == Phase.READING || (is0 == read0 && p == Phase.DECIDING));
		Text b = new Text(is0 ? "0" : "1");
		b.setFill(Color.BLACK);
		b.setFont(Font.font(family, 25));
		b.setUnderline(p == Phase.READING || (is0 == read0 && p == Phase.DECIDING));
		Text c = new Text(", write a ");
		c.setFill(Color.DARKGREY);
		c.setFont(Font.font(family, 25));
		c.setUnderline(p == Phase.WRITING && is0 == read0);
		Text d = new Text(isWrite1 ? "1" : "0");
		d.setFill(Color.BLACK);
		d.setFont(Font.font(family, 25));
		d.setUnderline(p == Phase.WRITING && is0 == read0);
		Text e = new Text(", move tape ");
		e.setFill(Color.DARKGREY);
		e.setFont(Font.font(family, 25));
		e.setUnderline(p == Phase.MOVING && is0 == read0);
		Text f = new Text(isForward ? "FORWARD" : "BACKWARD");
		f.setFill(Color.BLACK);
		f.setFont(Font.font(family, 25));
		f.setUnderline(p == Phase.MOVING && is0 == read0);
		Text g = new Text(", go to line ");
		g.setFill(Color.DARKGREY);
		g.setFont(Font.font(family, 25));
		g.setUnderline(p == Phase.CHANGING && is0 == read0);
		Text h = new Text(state + "\n");
		h.setFill(Color.BLACK);
		h.setFont(Font.font(family, 25));
		h.setUnderline(p == Phase.CHANGING && is0 == read0);

		textFlow.getChildren().addAll(a, b, c, d, e, f, g, h);
	}

	private void testInstructionPhases() {
		Thread t = new Thread() {
			@Override
			public void run() {
				
			Instruction i = new Instruction("0", "0F1", "1B2", "halt");
			showCurrentInstruction(i, new InstructionPhase(Phase.READING));
			pause();
			showCurrentInstruction(i, new InstructionPhase(Phase.DECIDING, true));
			pause();
			showCurrentInstruction(i, new InstructionPhase(Phase.WRITING, true));
			pause();
			showCurrentInstruction(i, new InstructionPhase(Phase.MOVING, true));
			pause();
			showCurrentInstruction(i, new InstructionPhase(Phase.CHANGING, true));
			pause();

			i = new Instruction("1", "1F1", "1B2", "halt");
			showCurrentInstruction(i, new InstructionPhase(Phase.READING));
			pause();
			showCurrentInstruction(i, new InstructionPhase(Phase.DECIDING, true));
			pause();
			showCurrentInstruction(i, new InstructionPhase(Phase.WRITING, true));
			pause();
			showCurrentInstruction(i, new InstructionPhase(Phase.MOVING, true));
			pause();
			showCurrentInstruction(i, new InstructionPhase(Phase.CHANGING, true));
			pause();
			}
		};
		t.start();
	}

	private void pause() {

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}

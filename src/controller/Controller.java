package controller;

import threads.ThreadUpdater;
import java.io.File;
import java.io.FileNotFoundException;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.stage.FileChooser;
import model.Partida;

public class Controller{

	@FXML
	private BorderPane borderPane;
	
	@FXML
    private Pane pane;
	
	private Partida sceneGame;
	
	@FXML
    private Label counterTxt;
	
	@FXML
    private TextField nameTxF;
	
	void paint() {
		for (int i = 0; i < sceneGame.getBall().size(); i++) {
			double radio = sceneGame.getBall().get(i).getRadio();
			double posX = sceneGame.getBall().get(i).getPosX();
			double posY = sceneGame.getBall().get(i).getPosY();
			Arc o = new Arc();
			o.setCenterX(posX);
			o.setCenterY(posY);
			o.setRadiusX(radio);
			o.setRadiusY(radio);
			o.setStartAngle(0);
			o.setLength(360);
			o.setType(ArcType.ROUND);
			o.setFill(Color.DARKBLUE);
			pane.getChildren().add(o);
		}
		
	}
	
	public void actualize() {
		for (int i = 0; i < sceneGame.getBall().size(); i++) {
			double posX = sceneGame.getBall().get(i).getPosX();
			double posY = sceneGame.getBall().get(i).getPosY();
			((Arc) pane.getChildren().get(i)).setCenterX(posX);
			((Arc) pane.getChildren().get(i)).setCenterY(posY);
		}
		counterTxt.setText(sceneGame.getCounter()+"");
		sceneGame.rebound();
	}
	
	@FXML
    void atrapaBall(MouseEvent event) {
		for (int i = 0; i < sceneGame.getBall().size(); i++) {
			double radio = sceneGame.getBall().get(i).getRadio();
			double posX = sceneGame.getBall().get(i).getPosX();
			double posY = sceneGame.getBall().get(i).getPosY();
			if(event.getX() < posX+radio && event.getX() > posX-radio && 
					event.getY() < posY+radio && event.getY() > posY-radio) {
				sceneGame.getBall().get(i).setStatus(true);
			}
		}
    }
	
    @FXML
    void bestScores(ActionEvent event) {
    	sceneGame.loadHallDeLaFama();
    	Alert al = new Alert(Alert.AlertType.INFORMATION);
		al.setContentText(sceneGame.toStringHallDeLaFama());
		al.show();
    }

    @FXML
    void exit(ActionEvent event) {
    	pane.getChildren().clear();
    	sceneGame.getBall().clear();
    	sceneGame.stopBall();
    	sceneGame.saveHallDeLaFama();
    	Platform.exit();
    }

    @FXML
    void startGame(ActionEvent event){
    	pane.getChildren().clear();
    	FileChooser fileCh = new FileChooser();
    	fileCh.setInitialDirectory(new File("./resources"));
    	File f = fileCh.showOpenDialog(null);
    	ThreadUpdater hilo = new ThreadUpdater(this);
    	try{
    		sceneGame.loadGame(f);
			paint();
			sceneGame.movement();
			hilo.start();
		}catch(Exception ioE){
			Alert al = new Alert(Alert.AlertType.INFORMATION);
			al.setTitle("problema");
			al.setContentText("Problemas leyendo el archivo\nEs probable que el formato no sea válido.");
			al.show();
		}
    	
    }
    
    public boolean stop() {
    	boolean stop = false;
    	if(pane.getChildren().isEmpty() || !sceneGame.ballStop()) {
    		stop = true;
    		sceneGame.newScore(nameTxF.getText());
    		sceneGame.loadHallDeLaFama();
    	}
    	return stop;
    }

    @FXML
    void saveGame(ActionEvent event) throws FileNotFoundException {
    	String name = sceneGame.saveGame();
    	Alert al = new Alert(Alert.AlertType.INFORMATION);
		al.setContentText("Se ha guardado la el juego con este nombre: "+name);
		al.show();
    }
	
    @FXML
	void initialize() {
		sceneGame = new Partida();
		pane.setMaxSize(sceneGame.getHeight(), sceneGame.getWidth());
		pane.setStyle("-fx-background-color: white;");
	
	}
}

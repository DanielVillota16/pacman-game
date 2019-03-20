package ui;

import java.io.FileNotFoundException;
import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.BorderPane;
import model.Game;
import threads.PacmanThread;
import threads.Refresh;

public class PacmanController {

	public static final int WIDTH = 800;
	public static final int HEIGHT= 500;
	
    @FXML
    private BorderPane borderPane;

    private GameZone zone;
    private Game game;
    private PacmanThread[] pts;
    private Refresh refresh;
    
    @FXML
    void initialize() {
    	try {
			game = new Game(0, WIDTH, HEIGHT);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			Alert alert = new Alert(AlertType.ERROR);
			alert.setContentText("File not found");
			alert.show();
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setContentText("File not found");
			alert.show();
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setContentText("There was an error trying to load the file which contains all the scores in the hall of fame");
			alert.show();
			e.printStackTrace();
		} catch (IOException e) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setContentText("File not found");
			alert.show();
			e.printStackTrace();
		}
    	zone = new GameZone(
    			game.getPacmans());
    	borderPane.setCenter(zone);
    	refresh = new Refresh(zone);
    	refresh.setDaemon(true);
    	refresh.start();
    	pts = new PacmanThread[game.getPacmans().length];
    	for (int i = 0; i < game.getPacmans().length; i++) {
    		PacmanThread pt = new PacmanThread(game.getPacmans()[i]);
        	pt.setDaemon(true);
        	pt.start();
        	pts[i] = pt;
		}
    }


    @FXML
    void stop(ActionEvent event) {
    	clearThreads();
    	game.updateTotalBounces();
    	System.out.print("Your score was: "+ game.getNumberOfBounces()+ ", Write your name: ");
    	/*BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    	game.updateTotalBounces();
    	System.out.print("Your score was: "+ game.getNumberOfBounces()+ ", Write your name: ");
    	try {
			String name = br.readLine();
			game.addScore(name, game.getNumberOfBounces());
			for (int i = 0; i < game.getHallOfFame().length; i++) {
				if(game.getHallOfFame()[i] != null) {
					System.out.println(game.getHallOfFame()[i].getScore());
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	try {
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
    }
    
    @FXML
    void exit(ActionEvent event) {
    	clearThreads();
    	
    }

    @FXML
    void load(ActionEvent event) {
    	try {
			game.load();
		}catch (FileNotFoundException e) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setContentText("The file with the information about the last game was not found");
			alert.show();
    	}catch (IOException e) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setContentText("It hasn't been possible to load the game, please try again");
			alert.show();
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			
			e.printStackTrace();
		}
    	clearThreads();
    	zone = new GameZone(game.getPacmans());
    	borderPane.setCenter(zone);
    	refresh = new Refresh(zone);
    	refresh.setDaemon(true);
    	refresh.start();
    	pts = new PacmanThread[game.getPacmans().length];
    	for (int i = 0; i < game.getPacmans().length; i++) {
    		PacmanThread pt = new PacmanThread(game.getPacmans()[i]);
        	pt.setDaemon(true);
        	pt.start();
        	pts[i] = pt;
		}
    }

    @FXML
    void save(ActionEvent event) {
    	clearThreads();
    	
    	try {
			game.save();
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setContentText("The game was saved successfully");
			alert.show();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			Alert alert = new Alert(AlertType.ERROR);
			alert.setContentText("File not found");
			alert.show();
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    @FXML
    void viewBestScores(ActionEvent event) {

    }

    @FXML
    void viewGameInfo(ActionEvent event) {

    }
    
    public void clearThreads() {
    	if(refresh != null) {
    		refresh.setStop(true);
    	}
    	for (int i = 0; i < pts.length; i++) {
			if(pts[i] != null) {
				pts[i].setStop(true);
			}
		}
    }

    
	public GameZone getZone() {
		return zone;
	}

	public void setZone(GameZone zone) {
		this.zone = zone;
	}

	public Game getGame() {
		return game;
	}

	public void setGame(Game game) {
		this.game = game;
	}
	
}

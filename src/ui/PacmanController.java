package ui;

import java.awt.Desktop;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.DialogEvent;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import model.Game;
import model.Score;
import threads.PacmanThread;
import threads.Refresh;

public class PacmanController {

	//constants
	public static final int WIDTH = 800-100;
	public static final int HEIGHT= 500-100;
	
	//attributes
    @FXML
    private BorderPane borderPane;

    private GameZone zone;
    private Game game;
    private PacmanThread[] pts;
    private Refresh refresh;
    
    //methods
	@FXML
    void initialize() throws InterruptedException {
		try {
			game = new Game(WIDTH, HEIGHT);
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
    }
	
    @FXML
    void viewBestScores0(ActionEvent event) {
    	showHallOfFame(0);
    }

    @FXML
    void viewBestScores1(ActionEvent event) {
    	showHallOfFame(1);
    }

    @FXML
    void viewBestScores2(ActionEvent event) {
    	showHallOfFame(2);
    }
    
    @FXML
    void loadLevelOneGame(ActionEvent event) {
    	load(1);
    }

    @FXML
    void loadLevelTwoGame(ActionEvent event) {
    	load(2);
    }

    @FXML
    void loadLevelZeroGame(ActionEvent event) {
    	load(0);
    }

    @FXML
    void viewGameInfo(ActionEvent event) {
    	File file = new File("Laboratorio Unidad 3 - Documentación.pdf");
    	if(Desktop.isDesktopSupported()) {
    		new Thread(() -> {
    			try{
    				Desktop.getDesktop().open(file);
    			} catch(IOException e){
    				e.printStackTrace();
    			}
    		}).start();
    	}
    }
	
	@SuppressWarnings("unchecked")
	public void showHallOfFame(int level) {
		Stage stage = new Stage();
    	Scene scene = new Scene(new Group());
    	stage.setTitle("Hall of fame");
        stage.setWidth(450);
        stage.setHeight(500);
        final Label label = new Label("Hall of fame");
        label.setFont(new Font("Arial", 20));
        TableView<Score> tv = new TableView<Score>();
    	final ObservableList<Score> data = FXCollections.observableArrayList();
    	Score[] hallOfFame = level == 0?game.getHallOfFame0():level == 1?game.getHallOfFame1():game.getHallOfFame2();
    	for (int i = 0; i < hallOfFame.length; i++) {
			data.add(hallOfFame[i]);
		}
        tv.setEditable(true);
        
        TableColumn<Score, String> nicknames = new TableColumn<Score, String>("Nickname");
        nicknames.setMinWidth(100);
        nicknames.setCellValueFactory(
                new PropertyValueFactory<Score, String>("name"));
        TableColumn<Score, Integer> scores = new TableColumn<Score, Integer>("Score");
        scores.setCellValueFactory(
        		new PropertyValueFactory<Score, Integer>("score"));
        tv.setItems(data);
        tv.getColumns().addAll(nicknames, scores);
        final VBox vbox = new VBox();
        vbox.setSpacing(5);
        vbox.setPadding(new Insets(10, 0, 0, 10));
        vbox.getChildren().addAll(label, tv);
 
        ((Group) scene.getRoot()).getChildren().addAll(vbox);
 
        stage.setScene(scene);
        stage.show();
	}
	
	public void verifyScore() {
		if(game.verifyScore()) {
			Stage stage = new Stage();
	    	Scene scene = new Scene(new Group());
	    	stage.setTitle("Score register");
	        Label label = new Label("Your score was great!,\n please write your nickname: ");
	        TextField tf = new TextField();
	        tf.setPrefSize(90, 20);
	        tf.setEditable(true);
	        Label conf = new Label("Your score has been registered successfully");
	        conf.setVisible(false);
	        Button b = new Button("Ok");
	        b.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					String nick = tf.getText();
					game.addScore(nick, game.getNumberOfBounces());
					try {
						game.updateHalls();
					} catch (ClassNotFoundException | IOException e) {
						e.printStackTrace();
					}
					conf.setVisible(true);
					b.setOnAction(null);
				}
			});
	        final VBox vbox = new VBox();
	        vbox.setSpacing(5);
	        vbox.setPadding(new Insets(10, 5, 5, 10));
	        vbox.autosize();
	        vbox.getChildren().addAll(label, tf, b, conf);
	        ((Group) scene.getRoot()).getChildren().addAll(vbox);
	        stage.setScene(scene);
	        stage.show();
		} else {
			Alert confirm = new Alert(AlertType.INFORMATION);
			confirm.setContentText("Good game, your score was: " + game.getNumberOfBounces());
			confirm.show();
		}
	}

    public void clearThreads() {
    	if(refresh != null) {
    		refresh.setStop(true);
    	}
    	if(pts != null) {
    		for (int i = 0; i < pts.length; i++) {
    			pts[i].setStop(true);
        	}
    	}
    }
    
    @FXML
    void exit(ActionEvent event) {
    	clearThreads();
    	try {
			game.updateHalls();
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
    	System.exit(0);
    }
    
    @FXML
    void newGame(ActionEvent event) {
    	clearThreads();
    	Stage stage = new Stage();
    	Scene scene = new Scene(new Group());
    	stage.setTitle("New Game");
        Label label = new Label("Choose the level you want to play in:");
        TextField tf = new TextField();
        tf.setEditable(true);
        Button ok = new Button("Continue");
        ok.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				int level = 0;
	    		try {
		        	try {
						level = Integer.parseInt(tf.getText());
						if(level < 0 || level > 2) {
							throw new IllegalArgumentException("The chosen level: " + level + " is invalid");
						}
						initializeNewGame(level);
						stage.close();
					} catch(NumberFormatException e) {
						Alert ex = new Alert(AlertType.ERROR);
						ex.setContentText("Invalid value, please try again");
						ex.show();
					}
				} catch (IllegalArgumentException e) {
					Alert ex = new Alert(AlertType.ERROR);
					ex.setContentText(e.getMessage() + " please try again");
					ex.show();
				}
			}
        });
        final VBox vbox = new VBox();
        vbox.setSpacing(5);
        vbox.setPadding(new Insets(10, 5, 5, 10));
        vbox.autosize();
        vbox.getChildren().addAll(label, tf, ok);
        ((Group) scene.getRoot()).getChildren().addAll(vbox);
        stage.setScene(scene);
        stage.show();
    	
    }
    
    void load(int level) {
    	clearThreads();
    	try {
    		game = new Game(WIDTH, HEIGHT);
			game.load(level);
	    	zone = new GameZone(game);
	    	borderPane.setCenter(zone);
	    	refresh = new Refresh(this);
	    	refresh.setDaemon(true);
	    	refresh.start();
	    	pts = new PacmanThread[game.getPacmans().length];
	    	for (int i = 0; i < game.getPacmans().length; i++) {
	    		PacmanThread pt = new PacmanThread(game.getPacmans()[i]);
	        	pt.setDaemon(true);
	        	pt.start();
	        	pts[i] = pt;
			}
		}catch (FileNotFoundException e) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setContentText("The file with the information about the last game was not found,"
					+ " it's possible that any game hasn't been saved ever,"
					+ " a new game is going to be loaded when this window get closed");
			alert.setOnCloseRequest(new EventHandler<DialogEvent>() {
				@Override
				public void handle(DialogEvent event) {
					initializeNewGame(level);
					alert.close();
				}
			});
			alert.show();
    	}catch (IOException e) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setContentText("It hasn't been possible to load the game, please try again");
			alert.show();
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			System.out.println("Class not found");
			e.printStackTrace();
		}
    }

    @FXML
    void save(ActionEvent event) {
    	clearThreads();
    	if(game.isGameOn()) {
    		try {
    			game.save();
    			Alert alert = new Alert(AlertType.CONFIRMATION);
    			alert.setContentText("The game was saved successfully");
    			alert.show();
    		} catch (FileNotFoundException e) {
    			Alert alert = new Alert(AlertType.ERROR);
    			alert.setContentText("File not found");
    			alert.show();
    			e.printStackTrace();
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
    	} else {
    		Alert alert = new Alert(AlertType.WARNING);
			alert.setContentText("The game is over, it's not possible to save it");
			alert.show();
    	}
    }

    public void initializeNewGame(int level) {
    	try {
			game = new Game(level, WIDTH, HEIGHT);
			game.setGameOn(true);
			zone = new GameZone(game);
	    	borderPane.setCenter(zone);
	    	refresh = new Refresh(this);
	    	refresh.setDaemon(true);
	    	refresh.start();
	    	pts = new PacmanThread[game.getPacmans().length];
	    	for (int i = 0; i < game.getPacmans().length; i++) {
	    		PacmanThread pt = new PacmanThread(game.getPacmans()[i]);
	        	pt.setDaemon(true);
	        	pt.start();
	        	pts[i] = pt;
			}
		} catch (IllegalArgumentException e) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setContentText("File not found");
			alert.show();
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setContentText("Filenotfoundex There was an error trying to load the file which contains all the scores in the hall of fame");
			alert.show();
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setContentText("Classnotfoundex There was an error trying to load the file which contains all the scores in the hall of fame");
			alert.show();
			e.printStackTrace();
		} catch (IOException e) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setContentText("File not found IO");
			alert.show();
			e.printStackTrace();
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

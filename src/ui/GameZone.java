package ui;

import java.io.Serializable;

import javafx.event.EventHandler;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Rectangle;
import model.Game;
import model.Pacman;

public class GameZone extends Canvas implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	//attributes
	private Pacman[] pacmans;
	private Game game;
	private GraphicsContext gc;
	private boolean openMouth;
	private int startAngle;
	private int angleLength;
	
	//constructor
	public GameZone(Game game) {
		super(800, 500);
		this.setGame(game);
		this.pacmans = game.getPacmans();
		gc = super.getGraphicsContext2D();
		gc.setLineWidth(3);
		gc.setFill(Color.YELLOW);
		openMouth = false;
		startAngle = 45;
		angleLength = 270;
		gc.getCanvas().setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				for(int i = 0; i < pacmans.length; i++) {
					if(pacmans[i] != null) {
						Rectangle r1 = new Rectangle(pacmans[i].getPosX(), pacmans[i].getPosY(), pacmans[i].getRadius(), pacmans[i].getRadius());
						if(r1.intersects(event.getX(), event.getY(), 1, 1)) {	
							pacmans[i].setMoving(false);
						}
					}
				}
			}
		});
	}
	
	//methods
	public long getMinimumWaitTime() {
		long min = pacmans[0].getWait();
		for (int i = 0; i < pacmans.length; i++) {
			if(pacmans[i].getWait() < min) {
				min = pacmans[i].getWait();
			}
		}
		return min;
	}

	public void redraw() {
		gc.clearRect(0, 0,  super.getWidth(), super.getHeight());
		startAngle = openMouth? startAngle+5 : startAngle-5;
		angleLength = openMouth? angleLength-10 : angleLength+10;
		if(startAngle==0 || startAngle==45) {
			openMouth = !openMouth;
		}
		for(Pacman pac:pacmans) {
			if(pac.isMoving()) {
				if(pac.getDirection() == Pacman.VER) {
					if(pac.getSubDirection() == 1) {
						gc.fillArc(pac.getPosX(), pac.getPosY(), pac.getRadius(), pac.getRadius(), startAngle+270, angleLength, ArcType.ROUND);
						gc.strokeArc(pac.getPosX(), pac.getPosY(), pac.getRadius(), pac.getRadius(), startAngle+270, angleLength, ArcType.ROUND);
					} else if(pac.getSubDirection() == -1) {
						gc.fillArc(pac.getPosX(), pac.getPosY(), pac.getRadius(), pac.getRadius(), startAngle+90, angleLength, ArcType.ROUND);
						gc.strokeArc(pac.getPosX(), pac.getPosY(), pac.getRadius(), pac.getRadius(), startAngle+90, angleLength, ArcType.ROUND);
					}
				} else if(pac.getDirection() == Pacman.HOR){
					if(pac.getSubDirection() == 1) {
						gc.fillArc(pac.getPosX(), pac.getPosY(), pac.getRadius(), pac.getRadius(), startAngle, angleLength, ArcType.ROUND);
						gc.strokeArc(pac.getPosX(), pac.getPosY(), pac.getRadius(), pac.getRadius(), startAngle, angleLength, ArcType.ROUND);
					} else if(pac.getSubDirection() == -1) {
						gc.fillArc(pac.getPosX(), pac.getPosY(), pac.getRadius(), pac.getRadius(), startAngle+180, angleLength, ArcType.ROUND);
						gc.strokeArc(pac.getPosX(), pac.getPosY(), pac.getRadius(), pac.getRadius(), startAngle+180, angleLength, ArcType.ROUND);
					}
				}
			}
		}
	}
	
	public void verifyBounces() {
		for(int i = 0; i < pacmans.length; i++) {
			if(pacmans[i].isMoving()) {
				for(int j = i+1; j < pacmans.length; j++) {
					if(pacmans[j].isMoving()) {
						Rectangle r1 = new Rectangle(pacmans[i].getPosX(), pacmans[i].getPosY(), Pacman.MIN_RADIUS, Pacman.MIN_RADIUS);
						if(r1.intersects(pacmans[j].getPosX(), pacmans[j].getPosY(), pacmans[j].getRadius(), pacmans[j].getRadius())) {
							pacmans[i].verifyBounce(pacmans[j]);
						}
					}
				}
			}
		}
	}
	
	//getters and setters
	public Game getGame() {
		return game;
	}

	public void setGame(Game game) {
		this.game = game;
	}
	
}
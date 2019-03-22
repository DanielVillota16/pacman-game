package model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;

public class Game {

	//constants
	public static final int TOTAL_SCORES = 10;
	public static final String FILE_LEVEL_0 = "data"+File.separator+"last-game-level-0";
	public static final String FILE_LEVEL_1 = "data"+File.separator+"last-game-level-1";
	public static final String FILE_LEVEL_2 = "data"+File.separator+"last-game-level-2";
	
	public static final String SCORES_PATH_0 = "data"+File.separator+"scores0.dat";
	public static final String SCORES_PATH_1 = "data"+File.separator+"scores1.dat";
	public static final String SCORES_PATH_2 = "data"+File.separator+"scores2.dat";
	
	//attributes
	private int level;
	private Pacman[] pacmans;
	private int numberOfBounces;
	private Score[] hallOfFame0;
	private Score[] hallOfFame1;
	private Score[] hallOfFame2;
	private boolean gameOn;
	
	private double width;
	private double height;
	
	//constructors
	public Game(int level, double width, double height) throws IllegalArgumentException,
		FileNotFoundException, ClassNotFoundException, IOException {
		this.level = level;
		this.width = width;
		this.height = height;
		int np = level==0?4:level==1?6:level==2?8:0;
		if(np == 0) throw new IllegalArgumentException("The chosen level:" + level + " is invalid."); 
		this.pacmans = new Pacman[np];
		this.numberOfBounces = 0;
		this.gameOn = true;
		char[] dirs = {Pacman.HOR, Pacman.VER};
		for(int i = 0; i < pacmans.length; i++) {
			char randomDir = dirs[i%2==0?0:1];
			long randomWait = (long) ((Math.random()*30)+50);
			double radius = (Math.random()*(Pacman.MAX_RADIUS-Pacman.MIN_RADIUS))+Pacman.MIN_RADIUS;
			pacmans[i] = new Pacman(radius, 0, 0, randomDir, 1 , randomWait, 0, true, width, height);
		}
		hallOfFame0=loadHallOfFame(hallOfFame0,SCORES_PATH_0);
		hallOfFame1=loadHallOfFame(hallOfFame1,SCORES_PATH_1);
		hallOfFame2=loadHallOfFame(hallOfFame2,SCORES_PATH_2);
		reubication();
	}
	
	public Game(double width, double height) throws FileNotFoundException, ClassNotFoundException, IOException {
		this.width = width;
		this.height = height;
		this.gameOn = true;
		hallOfFame0 = loadHallOfFame(hallOfFame0,SCORES_PATH_0);
		hallOfFame1 = loadHallOfFame(hallOfFame1,SCORES_PATH_1);
		hallOfFame2 = loadHallOfFame(hallOfFame2,SCORES_PATH_2);
	}
	
	//methods
	public void verifyGameOn() {
		boolean gameOver = true;
		if(pacmans != null) {
			for (int i = 0; i < pacmans.length && gameOver; i++) {
				if(pacmans[i].isMoving()) {
					gameOver = false;
				}
			}
			gameOn = !gameOver;
		}
	}
	
	public int getBiggestRadius(){
		double biggest = pacmans[0].getRadius();
		for (int i = 0; i < pacmans.length; i++) {
			if(pacmans[i].getRadius() > biggest) {
				biggest = pacmans[i].getRadius();
			}
		}
		return (int)biggest;
	}
	
	public long getLowestWaitTime() {
		long lowTime = pacmans[0].getWait();
		for (int i = 0; i < pacmans.length; i++) {
			if(pacmans[i].getWait() < lowTime) {
				lowTime = pacmans[i].getWait();
			}
		}
		return lowTime;
	}
	
	public void reubication() {
		double posX = Math.random()*width;
		double posY = Math.random()*height;
		int space = getBiggestRadius();
		for (int i = 0; i < pacmans.length; i++) {
			boolean located = false;
			while(!located) {
				for (int j = 0; j < pacmans.length; j++) {
					located = true;
					if(posX >= pacmans[j].getPosX()-space && posX <= pacmans[j].getPosX()+space) {
						located = false;
						posX = Math.random()*width;
						if(posY >= pacmans[j].getPosY()-space && posY <= pacmans[j].getPosY()+space) {
							posY = Math.random()*width;
						}
					}
				}
			}
			pacmans[i].setPosX(posX);
			pacmans[i].setPosY(posY);
			posX = Math.random()*width;
			posY = Math.random()*height;
		}
	}
	
	public boolean verifyScore() {
		updateTotalBounces();
		Score[] hallOfFame = level == 0?hallOfFame0:level == 1?hallOfFame1:hallOfFame2;
		return verifyOnSpecificArray(hallOfFame);
	}
	
	public boolean verifyOnSpecificArray(Score[] hallOfFame) {
		for (int i = 0; i < hallOfFame.length; i++) {
			if(hallOfFame[i]!= null) {
				if(numberOfBounces < hallOfFame[i].getScore()) {
					return true;
				}
			} else {
				return true;
			}
		}
		return false;
	}
	
	public void load(int level) throws IOException, ClassNotFoundException {
		int np = level==0?4:level==1?6:level==2?8:0;
		if(np == 0) throw new IllegalArgumentException("The chosen level:" + level + " is invalid.");
		this.pacmans = new Pacman[np];
		String path = level == 0? FILE_LEVEL_0: level == 1 ? FILE_LEVEL_1 : FILE_LEVEL_2;
		File f = new File(path);
		//if(f.exists()) {
		BufferedReader br = new BufferedReader(new FileReader(f));
		String line = br.readLine();
		int counter = 0;
		int index = 0;
		while(line != null) {
			if(counter == 1) {
				this.level = Integer.parseInt(line);
			} else if(counter > 2) {
				String[] pacmanData = line.split(" ");
				double radius = Double.parseDouble(pacmanData[0]) , posX = Double.parseDouble(pacmanData[1]) , posY = Double.parseDouble(pacmanData[2]); 
				long wait = Long.parseLong(pacmanData[3]);
				char dir = (pacmanData[4].equals("UP")) || (pacmanData[4].equals("DOWN"))?Pacman.VER:Pacman.HOR;
				int subDir = (pacmanData[4].equals("DOWN")) || (pacmanData[4].equals("RIGHT"))?1:-1;
				int bounces = Integer.parseInt(pacmanData[5]);
				boolean isMoving = Boolean.parseBoolean(pacmanData[6]);
				pacmans[index] = new Pacman(radius, posX, posY, dir, subDir, wait, bounces, isMoving, width, height);
				index++;
			}
			line = br.readLine();
			counter++;
		}
		br.close();
		gameOn = true;
		//} else {
		//	throw new ClassNotFoundException();
		//}
	}
	
	public void updateHalls() throws FileNotFoundException, ClassNotFoundException, IOException {
		updateSpecificHall(hallOfFame0, SCORES_PATH_0);
		updateSpecificHall(hallOfFame1, SCORES_PATH_1);
		updateSpecificHall(hallOfFame2, SCORES_PATH_2);
	}
	
	public void updateSpecificHall(Score[] hallOfFame, String path) throws FileNotFoundException, ClassNotFoundException, IOException {
		File f = new File(path);
		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(f));
		oos.writeObject(hallOfFame);
		oos.close();
	}
	
	public Score[] loadHallOfFame(Score[] hallOfFame, String path) throws FileNotFoundException, IOException, ClassNotFoundException {
		File f = new File(path);
		if(f.exists()) {
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f));
			Score[] scores = (Score[])ois.readObject();
			hallOfFame = scores;
			ois.close();
		} else {
			f.createNewFile();
			hallOfFame = new Score[TOTAL_SCORES];
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(f));
			oos.writeObject(hallOfFame);
			oos.close();
		}
		return hallOfFame;
	}
	
	public void save() throws IOException {
		String path = level == 0? FILE_LEVEL_0: level == 1 ? FILE_LEVEL_1 : FILE_LEVEL_2;
		PrintWriter pw = new PrintWriter(new File(path));
		System.out.println(level);
		String data = "#level"
				+ "\n"+ level
				+ "\n#radius posX posY wait-time direction bounces is-moving";
		for (int i = 0; i < pacmans.length; i++) {
			String direction = pacmans[i].getDirection() == Pacman.HOR? (pacmans[i].getSubDirection() == 1 ? "RIGHT": "LEFT"): (pacmans[i].getSubDirection() == 1)? "DOWN": "UP";
			data += "\n"+pacmans[i].getRadius() + " " + pacmans[i].getPosX()+ " " +pacmans[i].getPosY()+" " + pacmans[i].getWait()+" "+direction +" "+pacmans[i].getBounces() + " " + pacmans[i].isMoving();
		}
		pw.print(data);
		pw.close();
		
		String path2 = level == 0? SCORES_PATH_0: level == 1 ? SCORES_PATH_1 : SCORES_PATH_2;
		Score[] hallOfFame = level == 0?hallOfFame0:level == 1?hallOfFame1:hallOfFame2;
		File f = new File(path2);
		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(f));
		oos.writeObject(hallOfFame);
		oos.close();
	}
	
	public void updateTotalBounces() {
		for (int i = 0; i < pacmans.length; i++) {
			if(pacmans[i] != null) {
				numberOfBounces+= pacmans[i].getBounces();	
			}
		}
	}
	
	public void addScore(String name, int score) {
		if(level == 0) {
			hallOfFame0 = reubicateScores(hallOfFame0, name, score);
		} else if(level == 1) {
			hallOfFame1 = reubicateScores(hallOfFame1, name, score);
		} else if(level == 2) {
			hallOfFame2 = reubicateScores(hallOfFame2, name, score);
		}
	}
	
	public Score[] reubicateScores(Score[] hallOfFame, String name, int score) {
		int pos = 0;
		boolean done = false;
		for(int i = 0; i < TOTAL_SCORES && !done; i++) {
			if(hallOfFame[i]!=null) {
				if(score < hallOfFame[i].getScore()) {
					pos = i;
				}
			}else {
				pos = i;
				hallOfFame[i] = new Score(name, score);
				done=true;
			}
		}
		for(int i = pos+1; i < TOTAL_SCORES-1; i++) {
			hallOfFame[i+1] = hallOfFame[i];
		}
		hallOfFame[pos] = new Score(name, score);
		return hallOfFame;
	}

	//getters and setters
	public Score[] getHallOfFame0() {
		return hallOfFame0;
	}

	public void setHallOfFame0(Score[] hallOfFame0) {
		this.hallOfFame0 = hallOfFame0;
	}

	public Score[] getHallOfFame1() {
		return hallOfFame1;
	}

	public void setHallOfFame1(Score[] hallOfFame1) {
		this.hallOfFame1 = hallOfFame1;
	}

	public Score[] getHallOfFame2() {
		return hallOfFame2;
	}

	public void setHallOfFame2(Score[] hallOfFame2) {
		this.hallOfFame2 = hallOfFame2;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}
	
	public Pacman[] getPacmans() {
		return pacmans;
	}
	public void setPacmans(Pacman[] pacmans) {
		this.pacmans = pacmans;
	}
	public int getNumberOfBounces() {
		return numberOfBounces;
	}
	public void setNumberOfBounces(int numberOfBounces) {
		this.numberOfBounces = numberOfBounces;
	}

	public boolean isGameOn() {
		return gameOn;
	}
	
	public void setGameOn(boolean gameOn) {
		this.gameOn = gameOn;
	}
	
}

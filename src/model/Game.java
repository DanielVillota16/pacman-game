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

	public static final int TOTAL_SCORES = 10;
	public static final String FILE_PATH = "data"+File.separator+"last-game";
	public static final String SCORES_PATH = "data"+File.separator+"scores.dat";
	
	
	//private File lastGame;
	private int level;
	private Pacman[] pacmans;
	private int numberOfBounces;
	private Score[] hallOfFame;
	private boolean gameOn;
	
	private double width;
	private double height;
	
	public Game(int level, double width, double height) throws IllegalArgumentException,
		FileNotFoundException, ClassNotFoundException, IOException {
		
		this.width = width-300;
		this.height = height-100;
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
		loadHallOfFame();
		reubication();
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
		int [][][] positions = availablePositions();
		
		boolean [][] availablePosX = new boolean[positions.length][positions[0].length];
		boolean [][] availablePosY = new boolean[positions.length][positions[0].length];

		for(int i = 0; i < pacmans.length; i++) {
			int j = (int)(Math.random()*(positions.length-1)), k = (int)(Math.random()*(positions.length-1));
			int l = (int)(Math.random()*(positions.length-1)), m = (int)(Math.random()*(positions[0].length-1));
			int y = positions[j][k][0];
			int x = positions[l][m][1];
			// verification
			while(availablePosX[j][k] && availablePosY[l][m]) {
				y = positions[(int)(Math.random()*(positions.length-1))][(int)(Math.random()*(positions.length-1))][0];
				x = positions[(int)(Math.random()*(positions.length-1))][(int)(Math.random()*(positions[0].length-1))][1];
			}
			pacmans[i].setPosX(x);
			pacmans[i].setPosY(y);
			availablePosX[j][k] = true;
			availablePosY[l][m] = true;
		}
	}
	
	public int[][][] availablePositions(){
		int i1 = (int) (height/getBiggestRadius());
		int j1 = (int) (width/getBiggestRadius());
		System.out.println(i1+" "+j1);
		int radius = getBiggestRadius();
		int[][][] positions = new int[i1][j1][2];
		int lastY = 70;
		for (int i = 0; i < positions.length; i++) {
			int lastX = 0;
			for (int j = 0; j < positions[i].length; j++) {
				int sum = 0;
				if((i == 0 || j == 0) || (i == positions.length-1 || j== positions[i].length)) {
					sum = radius+lastX;
				} else {
					sum = 2*radius+lastX;
				}
				int[] pos = {lastY, sum};
				positions[i][j] = pos;
				lastX = sum;
			}
			lastY+=70;
		}
		return positions;
	}
	
	public void load() throws IOException, ClassNotFoundException {
		BufferedReader br = new BufferedReader(new FileReader(new File(FILE_PATH)));
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
	}
	
	public void loadHallOfFame() throws FileNotFoundException, IOException, ClassNotFoundException {
		File f = new File(SCORES_PATH);
		if(f.exists()) {
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f));
			Score[] scores = (Score[])ois.readObject();
			hallOfFame = scores;
			ois.close();
		} else {
			f.createNewFile();
			hallOfFame = new Score[TOTAL_SCORES];
		}
	}
	
	public void save() throws IOException {
		PrintWriter pw = new PrintWriter(new File(FILE_PATH));
		String data = "#level"
				+ "\n"+ level
				+ "\n#radius posX posY wait-time direction bounces is-moving";
		for (int i = 0; i < pacmans.length; i++) {
			String direction = pacmans[i].getDirection() == Pacman.HOR? (pacmans[i].getSubDirection() == 1 ? "RIGHT": "LEFT"): (pacmans[i].getSubDirection() == 1)? "DOWN": "UP";
			data += "\n"+pacmans[i].getRadius() + " " + pacmans[i].getPosX()+ " " +pacmans[i].getPosY()+" " + pacmans[i].getWait()+" "+direction +" "+pacmans[i].getBounces() + " " + pacmans[i].isMoving();
		}
		pw.print(data);
		pw.close();
		File f = new File(SCORES_PATH);
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
		int pos = 0;
		boolean done = false;
		for(int i = 1; i < TOTAL_SCORES-1 && !done; i++) {
			if(hallOfFame[i]!=null) {
				if(score < hallOfFame[i].getScore()) {
					pos = i;
				}
			}else {
				hallOfFame[i] = new Score(name, score);
				done=true;
			}
		}
		for(int i = pos+1; i < TOTAL_SCORES-1; i++) {
			hallOfFame[i+1] = hallOfFame[i];
		}
		hallOfFame[pos] = new Score(name, score);
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
	public Score[] getHallOfFame() {
		return hallOfFame;
	}
	public void setHallOfFame(Score[] hallOfFame) {
		this.hallOfFame = hallOfFame;
	}
	
	public boolean isGameOn() {
		return gameOn;
	}
	
	public void setGameOn(boolean gameOn) {
		this.gameOn = gameOn;
	}
	
}

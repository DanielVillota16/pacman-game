package model;

import java.io.Serializable;

public class Score implements Serializable {

	private static final long serialVersionUID = 1L;
	
	//attributes
	private String name;
	private int score;
	
	//constructor
	public Score(String name, int score) {
		this.name = name;
		this.score = score;
	}

	//getters and setters
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}
	
	
}

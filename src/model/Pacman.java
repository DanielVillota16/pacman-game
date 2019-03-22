package model;

import java.io.Serializable;

public class Pacman implements Serializable{
	
	//constants
	
	private static final long serialVersionUID = 1L;
	
	public static final char VER = 'v';
	public static final char HOR = 'h';
	public static final double MIN_RADIUS = 40.0;
	public static final double MAX_RADIUS = 90.0;
	public static final double ADVANCE = 3.0;
	
	//attributes
	private double posX;
	private double posY;
	private boolean moving;
	private char direction;
	private int subDirection;
	private long wait;
	private double limitX;
	private double limitY;
	private double radius;
	private int bounces;
	
	//constructor
	public Pacman(double radius, double posX, double posY, char direction, int subDirection, long wait, int bounces, boolean moving, double limitX, double limitY) {
		this.radius = radius;
		this.posX = posX;
		this.posY = posY;
		this.direction = direction;
		this.wait = wait;
		this.limitX = limitX;
		this.limitY = limitY;
		this.subDirection = subDirection;
		this.bounces = bounces;
		this.moving = moving;
		correctPosition();
	}
	
	//methods
	public void move() {
		if(moving) {
			if(direction == HOR) {
				posX += ADVANCE*subDirection;
			} else if(direction == VER) {
				posY += ADVANCE*subDirection;
			}			
			if(bounce()) {
				bounces++;
			}
		}
	}

	public boolean bounce() {
		boolean bounce = false;
		if(direction == HOR) {
			if(posX <= 0 || posX >= limitX) {
				subDirection *= -1;
				bounce = true;
			}
			
		} else if(direction == VER) {
			if(posY <= 0 || posY >= limitY) {
				subDirection *= -1;
				bounce = true;
			}	
		}
		correctPosition();
		return bounce;
	}
	
	public void correctPosition() {
		if(posX < 0) {
			posX = 1;
		} else if(posX > limitX) {
			posX = limitX-1;
		}
		if(posY < 0) {
			posY = 1;
		} else if(posY > limitY) {
			posY = limitY-1;
		}
	}
	
	public void verifyBounce(Pacman pacman) {
		if(pacman != this && pacman != null && this != null) {
			double distance = Math.sqrt(Math.pow(posX - pacman.getPosX(), 2) + Math.pow(posY - pacman.getPosY(), 2));
			if(distance < radius + pacman.getRadius()) {
				subDirection *= -1;
				pacman.setSubDirection(pacman.getSubDirection()*-1);
				bounces++;
				pacman.setBounces(pacman.getBounces()+1);
				double difference = radius + pacman.getRadius() - distance;
				if(direction == HOR) {
					posX += difference/3*subDirection;
				} else {
					posY += difference/3*subDirection;
				}
				if(pacman.getDirection() == HOR) {
					pacman.setPosX(pacman.getPosX() + difference/3*pacman.getSubDirection()); 
				} else {
					pacman.setPosY(pacman.getPosY() + difference/3*pacman.getSubDirection());
				}
			}
		}
	}
	
	//getters and setters
	public double getPosX() {
		return posX;
	}

	public void setPosX(double posX) {
		this.posX = posX;
	}

	public double getPosY() {
		return posY;
	}

	public void setPosY(double posY) {
		this.posY = posY;
	}

	public boolean isMoving() {
		return moving;
	}

	public void setMoving(boolean moving) {
		this.moving = moving;
	}
	
	public char getDirection() {
		return direction;
	}

	public void setDirection(char direction) {
		this.direction = direction;
	}
	
	public int getSubDirection() {
		return subDirection;
	}

	public void setSubDirection(int subDirection) {
		this.subDirection = subDirection;
	}

	public double getRadius() {
		return radius;
	}

	public void setRadius(double radius) {
		this.radius = radius;
	}

	public long getWait() {
		return wait;
	}

	public void setWait(long wait) {
		this.wait = wait;
	}

	public int getBounces() {
		return bounces;
	}

	public void setBounces(int bounces) {
		this.bounces = bounces;
	}
	
}

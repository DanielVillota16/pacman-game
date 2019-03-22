package threads;

import model.Pacman;
public class PacmanThread extends Thread{

	//attributes
	private Pacman pacman;
	private boolean stop;
	
	//constructor
	public PacmanThread(Pacman pacman) {
		this.stop = false;
		this.pacman = pacman;
	}
	
	//run method
	@Override
	public void run() {
		while(!stop) {
			if(pacman!=null) {
				pacman.move();
			}
    		try {
				sleep(pacman.getWait());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	//getters and setters
	public boolean isStop() {
		return stop;
	}

	public void setStop(boolean stop) {
		this.stop = stop;
	}
	
}

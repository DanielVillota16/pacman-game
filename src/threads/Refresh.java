package threads;

import javafx.application.Platform;
import ui.PacmanController;

public class Refresh extends Thread{
	
	//attributes
	private PacmanController pc;
	private boolean stop;
	
	//constructor
	public Refresh(PacmanController pc) {
		this.pc = pc;
		this.stop = false;
	}

	//run method
	@Override
	public void run() {
		long sleepTime = pc.
				getZone().getMinimumWaitTime();
		while(!stop && pc.getZone().getGame().isGameOn()) {
			pc.getZone().redraw();
			pc.getZone().verifyBounces();
			pc.getGame().verifyGameOn();
			try {
				sleep(sleepTime);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				if(!pc.getZone().getGame().isGameOn()) {
					pc.verifyScore();
				}
			}
		});
	}

	//getters and setters
	public PacmanController getPc() {
		return pc;
	}

	public void setPc(PacmanController pc) {
		this.pc = pc;
	}

	public boolean isStop() {
		return stop;
	}

	public void setStop(boolean stop) {
		this.stop = stop;
	}
	
	
}

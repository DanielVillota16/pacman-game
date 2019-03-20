package threads;

import model.Game;

public class GameThread extends Thread{
	
	private Game game;
	private boolean stop;
	
	public GameThread(Game game) {
		this.game = game;
		this.stop = false;
	}
	
	@Override
	public void run() {
		long sleepTime = game.getLowestWaitTime();
		while(!stop) {
			//game.moveAll();
			for (int i = 0; i < game.getPacmans().length; i++) {
				for (int j = i+1; j < game.getPacmans().length; j++) {
					if(game.getPacmans()[i] != null && game.getPacmans()[j] != null) {
						game.getPacmans()[i].verifyBounce(game.getPacmans()[j]);
					}
				}
			}
			try {
				sleep(sleepTime);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void setStop(boolean stop) {
		this.stop = stop;
	}
}

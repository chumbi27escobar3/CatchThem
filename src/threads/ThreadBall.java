package threads;

import model.Ball;

public class ThreadBall extends Thread{
	
	private Ball ball;

	public ThreadBall(Ball ball) {
		this.ball = ball;
	}
	
	/**
	 * 
	 */
	public void run() {
		while(!ball.getStatus()) {
			ball.moved();
			try {
				sleep(ball.getDelay());
			} catch (InterruptedException e) {	
				e.printStackTrace();
			}
		}
	}
}

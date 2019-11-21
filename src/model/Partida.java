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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import threads.ThreadBall;

public class Partida {
	
	private Score[][] hallDeLaFama;
	
	private int level;
	private int counter;
	private double height;
	private double width;
	private ArrayList<Ball> ball;
	
	public final static String PATH_HALL_DE_LA_FAMA = "./resources/HallDeLaFama.txt";
	
	public Partida() {
		ball = new ArrayList<Ball>();
		hallDeLaFama = new Score[10][3];
		height = 500;
		width = 500;
	}
	
	public void loadGame(File file) {
		ball.clear();
		try {
			BufferedReader bf = new BufferedReader(new FileReader(file));
			String line = bf.readLine();
			while(line != null) {
				if(line.charAt(0) != '#' && line.charAt(0) != '\n') {
					String[] l = line.split("\t");
					if (l.length > 1) {
					double radio = Double.parseDouble(l[0]);
					double posX = Double.parseDouble(l[1]);
					double posY = Double.parseDouble(l[2]);
					int espera = Integer.parseInt(l[3]);
					char direction = l[4].charAt(0);
					int rebotes = Integer.parseInt(l[5]);
					boolean estado = Boolean.parseBoolean(l[6]);
					Ball ball1 = new Ball(radio, posX, posY, espera, direction, rebotes, estado);
					ball.add(ball1);
					}else {
						level = Integer.parseInt(l[0]);
					}
				}
				line = bf.readLine();
			}
			bf.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public String toStringHallDeLaFama() {
		String info = " Nivel 0 \t #\t Nivel 1 \t #\t  Nivel 2\t #\t \n";
		for (int i = 0; i < hallDeLaFama.length; i++) {
			for (int j = 0; j < hallDeLaFama[i].length; j++) {
				info += hallDeLaFama[i][j] != null ? hallDeLaFama[i][j].getName()+"\t"+hallDeLaFama[i][j].getRebounds()+"\t" : "\t\t\t";
			}
			info += "\n";
		}
		return info;
	}
	
	public void loadHallDeLaFama() {
		try {
			ObjectInputStream recuperando = new ObjectInputStream(new FileInputStream(PATH_HALL_DE_LA_FAMA));
			hallDeLaFama = (Score[][])recuperando.readObject();
			recuperando.close();
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
	}
	
	
	public boolean ballStop() {
		boolean isStop = false;
		for (Ball ball : ball) {
			isStop = isStop | !ball.getStatus();
		}
		return isStop;
	}
	
	
	public void rebound() {
		for (Ball ball : ball) {
			double r = ball.getRadio();
			double x = ball.getPosX();
			double y = ball.getPosY();
			char d = ball.getDirection();
			if(d == Ball.RIGHT && (width-x <= r)) {
				ball.setDirection();
			}else if(d == Ball.LEFT && (x <= r)) {
				ball.setDirection();
			}else if(d == Ball.UPWARD && (y <= r)) {
				ball.setDirection();
			}else if(d == Ball.DOWNWARD && (height-y <= r)) {
				ball.setDirection();
			}
		}
	}

	
	public String saveGame() {
		PrintWriter writer;
		String name = "";
		try {
			File f = new File("./resources/JuegoGuardado_"+(new SimpleDateFormat("yyyy-MM-dd_hh-mm-ss").format(new Date()))+".txt");
			name = f.getName();
			writer = new PrintWriter(f);
			writer.println("#nivel");
			writer.println(level);
			writer.println("#radio	posX	posY	espera	direct	rebotes	quieto");
			for (Ball ball: ball) {
				writer.println(ball.toString());
			}
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return name;
	}
	
	public void stopBall() {
		for(Ball ball: ball) {
			ball.setStatus(true);
		}
	}
	
	public void saveHallDeLaFama() {
		try {
			ObjectOutputStream guardando = new ObjectOutputStream(new FileOutputStream(PATH_HALL_DE_LA_FAMA));
			guardando.writeObject(hallDeLaFama);
			guardando.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void newScore(String name) {
		boolean agregado = false;
		if(bestScores()) {
			for (int i = 0; i < hallDeLaFama.length & !agregado; i++) {
				if(hallDeLaFama[i][level] == null) {
					hallDeLaFama[i][level] = new Score(name, level, counter);
					agregado = true;
				}
			}
		}
	}
	
	public void movement() {
		ArrayList<ThreadBall> ball1 = new ArrayList<ThreadBall>();
		for (int i = 0; i < ball.size(); i++) {
			ball1.add(new ThreadBall(ball.get(i)));
			ball1.get(i).start();
		}
	}
	
	
	public boolean bestScores() {
		boolean best = false;
		boolean continuar = true;
		for(int i = 0; i < hallDeLaFama.length & continuar; i++) {
			if(hallDeLaFama[i][level] == null) {
				best = true;
				continuar = false;
			}else if(hallDeLaFama[i][level] != null && level == hallDeLaFama[i][level].getLevel() && counter <= hallDeLaFama[i][level].getRebounds()) {
				best = best || true;
			}
			
		}
		return best;
	}
	
	public int getCounter() {
		int count = 0;
		for (int i = 0; i < ball.size(); i++) {
			count += ball.get(i).getRebounds();
		}
		counter = count;
		return counter;
	}
	
	public double getHeight() {
		return height;
	}
	
	public double getWidth() {
		return width;
	}

	public ArrayList<Ball> getBall() {
		return ball;
	}
	
	
	
}

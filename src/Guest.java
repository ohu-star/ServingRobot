//package Algorithm;

import java.awt.Color;
import java.util.Calendar;
import java.util.Random;

public class Guest extends Thread{
	
	int tableNum;		// 1~6
	int[] entryTime = {0, 0};	// guest entered time
	int settingTimer;	// timer count. wait for setting
	int servingTimer;	// timer count. wait for serving
	int timeToCook;		// the time takes to cook (random)
	int timeToStay;		// the time that guest stays (random)
	int[] endCook = {0, 0};		// when cooking is over
	int[] endStay = {0, 0};		// when guest leaves
	int[] temp = {0, 0};
	int satisfaction;	// depends on how long guest wait
	static final int LIMIT = 60;	// limit time for waiting. to hurry.


	public Guest(int num)
	{
		init(num);
	}

	// Set guest(table) Information
	public void init(int tableNum)
	{
		this.tableNum = tableNum+1;	// 1~6
		
		Calendar time = Calendar.getInstance();
		this.entryTime[0] = time.get(Calendar.MINUTE);
		this.entryTime[1] = time.get(Calendar.SECOND);
		
		this.settingTimer = 0;
		this.servingTimer = 0;
		
		Random random = new Random();
		//random.nextInt(max - min) + min; //min ~ max
		
		this.timeToCook = random.nextInt(180 - 60) + 60;	// 2 ~ 4 minutes 
		this.timeToStay = random.nextInt(240 - 120) + 120;	// 5 ~ 7 minutes 
		
		this.timeToCook = timeToCook / 10;	// 10x speed
		this.timeToStay = timeToStay / 10;
		
		this.satisfaction = 10;
	}
	
	public void run() {
		
		
		// push setting to queue
		Queueing.init("setting.", tableNum);
		System.out.println("! push: " + String.valueOf(this.tableNum) + " table setting");
		
		// wait for setting
		settingCountThread t1 = new settingCountThread(this);	// count waiting time
		t1.start();
		
		// endCook calculate
		// add timeToCook to entryTime
		this.endCook[0] = (this.entryTime[0] + (this.timeToCook / 60)) % 60;	//minute
		this.endCook[1] = (this.entryTime[1] + this.timeToCook) % 60;	//second
		if (this.entryTime[1] + this.timeToCook >= 60) {
			this.endCook[0] = (this.endCook[0] + 1) % 60;
		}
		
		// until endCook
		while(true) {
			Calendar now = Calendar.getInstance();
			this.temp[0] = now.get(Calendar.MINUTE);
			this.temp[1] = now.get(Calendar.SECOND);
			
			if(this.temp[0] == this.endCook[0] && this.temp[1] == this.endCook[1]) {
				// push serving to queue
				Queueing.init("serving.", this.tableNum);
				System.out.println("! push: " + String.valueOf(this.tableNum) + " table serving");
				break;
			}
		}
		
		// wait for serving
		servingCountThread t2 = new servingCountThread(this);
		t2.start();
		
		// do noting before serving done
		while(true) {
			if(MainFrame.isServingDone[this.tableNum-1]) {
				break;
			}
			try {
				Thread.sleep(100);
			} catch(InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		// endStay calculate
		// add timeToStay from now
		Calendar afterServe = Calendar.getInstance();
		this.endStay[0] = (afterServe.get(Calendar.MINUTE) + (this.timeToStay / 60)) % 60;
		this.endStay[1] = (afterServe.get(Calendar.SECOND) + this.timeToStay) % 60;
		if (afterServe.get(Calendar.SECOND) + this.timeToStay >= 60) {
			this.endStay[0] = (this.endStay[0] + 1) % 60;
		}
		
		// until endStay
		while(true) {
			Calendar now = Calendar.getInstance();
			this.temp[0] = now.get(Calendar.MINUTE);
			this.temp[1] = now.get(Calendar.SECOND);
			
			if(this.temp[0] == this.endStay[0] && this.temp[1] == this.endStay[1]) {
				// push clean to queue
				MainFrame.haveToClean[this.tableNum - 1] = true;
				MapPane.table[this.tableNum - 1].repaint();
				Queueing.init("clean.", tableNum);
				System.out.println("! push: " + String.valueOf(this.tableNum) + " table clean");
				break;
			}
		}
		
		// calculate satisfaction
		this.settingTimer -= LIMIT;
		this.servingTimer -= LIMIT;
		if(this.settingTimer > 0) {
			this.satisfaction -= this.settingTimer / 5;	// every time it's over 5 seconds, minus
		}
		if(this.servingTimer > 0) {
			this.satisfaction -= this.servingTimer / 5;
		}

		MapPane.state[this.tableNum-1].setText("clean. score: " + this.satisfaction);	// show satisfaction GUI
		
	}
}


class settingCountThread extends Thread{
	
	Guest myG;
	
	public settingCountThread(Guest g)
	{
		myG = g;
	}
	
	public void run() {
		while(true) {
			
			if(MainFrame.isSettingDone[myG.tableNum-1]) {
				MapPane.state[myG.tableNum-1].setForeground(Color.BLACK);	// initialize color
				MapPane.state[myG.tableNum-1].setText("cook done at " + myG.endCook[0] + ":" + myG.endCook[1]);
				break;	// break when setting done
			}
		
			// setting timer ++
			myG.settingTimer = myG.settingTimer + 1;
			MapPane.state[myG.tableNum-1].setText("wait setting..." + myG.settingTimer);	// show timer
			
			if(myG.settingTimer == myG.LIMIT) {	// overed limit
				// let queue know to change priority
				Queueing.message = "setting." + String.valueOf(myG.tableNum);
				Queueing.priority();
				MapPane.state[myG.tableNum-1].setForeground(Color.RED);	// hurry GUI
			}
			
			try {
				Thread.sleep(100);	// 0.1 second (10x speed)
			} catch(InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}

class servingCountThread extends Thread{
	
	Guest myG;
	
	public servingCountThread(Guest g)
	{
		myG = g;
	}
	
	public void run() {
		while(true) {
			
			if(MainFrame.isServingDone[myG.tableNum-1]) {
				try {
					Thread.sleep(500);	// wait for calculating endStay
				} catch(InterruptedException e) {
					e.printStackTrace();
				}
				MapPane.state[myG.tableNum-1].setForeground(Color.BLACK);	// initialize color
				MapPane.state[myG.tableNum-1].setText("stay until " + myG.endStay[0] + ":" + myG.endStay[1]);
				break;	// break when serving done
			}
		
			// serving timer ++
			myG.servingTimer = myG.servingTimer + 1;
			MapPane.state[myG.tableNum-1].setText("wait serving..." + myG.servingTimer);	// show timer
			
			if(myG.servingTimer == myG.LIMIT) {	// overed limit
				// let queue know to change priority
				Queueing.message = "serving." + String.valueOf(myG.tableNum);
				Queueing.priority();
				MapPane.state[myG.tableNum-1].setForeground(Color.RED);	// hurry GUI
			}
			
			try {
				Thread.sleep(100);	// 0.1 second (10x speed)
			} catch(InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}



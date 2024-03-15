
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class MainFrame extends JFrame implements Runnable, ActionListener {
	public static Queueing queue = new Queueing();// add Queueing 
	public static Dijkstra dj = new Dijkstra(); // add Dijkstra 
	public static MapPane mp = new MapPane();// add MapPane 
	public static int[][] visit_xy = null;// add visited nodes
	public static int[] dest = null;// add destination nodes

	public static int[] table_state = new int[6];
	public static boolean[] isSettingDone = new boolean[6];
	public static boolean[] isServingDone = new boolean[6];
	public static boolean[] haveToClean = new boolean[6];
	public static String[] robot_doing = new String[] { "", "" };
	public static int[] robot_table = new int[2];
	public static int running_Robot;

	public static void main(String args[]) throws Exception { 

		//frame setting
		MainFrame frame = new MainFrame();
		JPanel centerPane = new JPanel();
		frame.init();
		centerPane.add(mp);
		mp.setPreferredSize(new Dimension(600, 580));
		centerPane.setBackground(Color.BLACK);
		frame.getContentPane().add(centerPane, BorderLayout.CENTER);
		frame.pack();
		frame.setVisible(true);

		while (true) {
			while (true)// check robot state
			{
				if (mp.isFree() != 0 && Queueing.state() == 0)//if more than one of robot can working and Queue is not empty
					break;
				Thread.sleep(100);
			}

			visit_xy = null;
			dest = null;
			int working_robot;
			int node;
			int[] setting = { 200, 80 };
			int[] serving = { 400, 80 };

			String[] temp_str = queue.out();//get operation to queue
			node = change_node(Integer.parseInt(temp_str[1]), temp_str[0]); //get end node
			working_robot = mp.isFree(); // get operation from queue

			if (temp_str[0].equals("serving") || temp_str[0].equals("setting"))// if "serving" or "setting"
			{
				if (temp_str[0].equals("setting"))// if "setting"
				{
					// Decide which robot to assign an operation to.
					dj.init(1, mp.getInfo(1), mp.getInfo(2), working_robot);
					// store destination node
					dest = dj.dest_num();
					//store  rotue's x,y coordinates
					visit_xy = dj.list_result();
					//what robot do 
					running_Robot = dj.workRobot();

					// clean way of free robot
					if (running_Robot == 1) {
						mp.way1.clear();
					} else if (running_Robot == 2) {
						mp.way2.clear();
					}

					//move robot
					mp.setRobot(running_Robot, visit_xy, dest);

					//Current Location -> setting bar -> end node
					if (running_Robot == 1) {
						dj.init(node, setting, mp.getInfo(2), 1);
						robot_doing[0] = temp_str[0];
						robot_table[0] = Integer.parseInt(temp_str[1]);
					} else if (running_Robot == 2) {
						dj.init(node, mp.getInfo(1), setting, 2);
						robot_doing[1] = temp_str[0];
						robot_table[1] = Integer.parseInt(temp_str[1]);
					}

					// new setting and move robot
					Queueing.dish -=1;
					visit_xy = null;
					visit_xy = dj.list_result();
					dest = dj.dest_num();
					
					mp.setRobot(running_Robot, visit_xy, dest);

				} else if (temp_str[0].equals("serving")) // if "serving"
				{
					// Decide which robot to assign an operation to.
					dj.init(3, mp.getInfo(1), mp.getInfo(2), working_robot);
					// store destination node
					dest = dj.dest_num();
					//store  rotue's x,y coordinates
					visit_xy = dj.list_result();
					//what robot do
					running_Robot = dj.workRobot();

					// clean way of free robot
					if (running_Robot == 1) {
						mp.way1.clear();
					} else if (running_Robot == 2) {
						mp.way2.clear();
					}

					//move robot
					mp.setRobot(running_Robot, visit_xy, dest);

					//Current Location -> serving(kitchen) -> end node
					if (running_Robot == 1) {
						dj.init(node, serving, mp.getInfo(2), 1);
						robot_doing[0] = temp_str[0];
						robot_table[0] = Integer.parseInt(temp_str[1]);
					} else if (running_Robot == 2) {
						dj.init(node, mp.getInfo(1), serving, 2);
						robot_doing[1] = temp_str[0];
						robot_table[1] = Integer.parseInt(temp_str[1]);
					}
					
					// new setting and move robot
					visit_xy = null;
					visit_xy = dj.list_result();
					dest = dj.dest_num();
					
					mp.setRobot(running_Robot, visit_xy, dest);
				}

			} else if (temp_str[0].equals("refull"))// if refull
			{
				// Decide which robot to assign an operation to.
				dj.init(3, mp.getInfo(1), mp.getInfo(2), working_robot);
				// store destination node
				dest = dj.dest_num();

				//store  rotue's x,y coordinates
				visit_xy = dj.list_result();

				//what robot do
				running_Robot = dj.workRobot();

				// clean way of free robot
				if (running_Robot == 1) {
					mp.way1.clear();
				} else if (running_Robot == 2) {
					mp.way2.clear();
				}
				
				//move robot
				mp.setRobot(running_Robot, visit_xy, dest);

				//Current Location -> Setting bar -> end node
				if (running_Robot == 1) {
					dj.init(1, serving, mp.getInfo(2), 1);
					robot_doing[0] = temp_str[0];
					robot_table[0] = Integer.parseInt(temp_str[1]);
				} else if (running_Robot == 2) {
					dj.init(1, mp.getInfo(2), serving, 2);
					robot_doing[1] = temp_str[0];
					robot_table[1] = Integer.parseInt(temp_str[1]);
				}
				
				// new setting and move robot
				visit_xy = null;
				visit_xy = dj.list_result();
				dest = dj.dest_num();

				mp.setRobot(running_Robot, visit_xy, dest);

			} else if (temp_str[0].equals("clean"))// if clean
			{
				// Decide which robot to assign an operation to.
				dj.init(node, mp.getInfo(1), mp.getInfo(2), working_robot);
				//what robot do
				running_Robot = dj.workRobot();

				if (running_Robot == 1) {
					robot_doing[0] = temp_str[0];
					robot_table[0] = Integer.parseInt(temp_str[1]);
				} else if (running_Robot == 2) {
					robot_doing[1] = temp_str[0];
					robot_table[1] = Integer.parseInt(temp_str[1]);
				}
				//robot move
				dest = dj.dest_num();
				visit_xy = dj.list_result();

				// clean way of free robot
				if (running_Robot == 1) {
					mp.way1.clear();
				} else if (running_Robot == 2) {
					mp.way2.clear();
				}

				mp.setRobot(running_Robot, visit_xy, dest);

			}
		}
	}


	public void init() {
		// initialize GUI

		// set minimum size of frame(window)
		double magn = 1080 / Toolkit.getDefaultToolkit().getScreenSize().getHeight();
		double minX = 1000 * magn;
		double minY = 722 * magn; // (580+40+60)+42
		setMinimumSize(new Dimension((int) minX, (int) minY));

		// full screen
		GraphicsEnvironment graphics = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice device = graphics.getDefaultScreenDevice();
		device.setFullScreenWindow(this);

		setTitle("Serving Robot Simulator");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		/* GUI layout */
		emptyPane1 = new JPanel();
		emptyPane2 = new JPanel();

		// timer label at top
		timeTestLabel = new JLabel("00 : 00");
		timeTestLabel.setForeground(Color.WHITE);
		timeTestLabel.setFont(new Font("돋움", Font.BOLD, 20));
		emptyPane1.add(timeTestLabel);
		new Thread(this).start();

		// space for Guest
		guest = new Guest[6];

		// guest entrance button
		guestEntranceBtn = new JButton();
		guestEntranceBtn = new RoundedButton("Accept Guests       ");
		guestEntranceBtn.setPreferredSize(new Dimension(200, 40));
		guestEntranceBtn.addActionListener(this);
		emptyPane2.add(guestEntranceBtn);

		// add empty panel
		emptyPane1.setPreferredSize(new Dimension(1000, 40));
		emptyPane2.setPreferredSize(new Dimension(1000, 60));
		emptyPane1.setBackground(new Color(0, 0, 0));
		emptyPane2.setBackground(new Color(0, 0, 0));
		getContentPane().add(emptyPane1, BorderLayout.NORTH);
		getContentPane().add(emptyPane2, BorderLayout.SOUTH);

		// add MapPane in main()
	}


	// table number -> node number ++++++++++++++++++++++++++++++++++++++++
	private static int change_node(int table, String operation) {
		if (table == 0)
			return 1;
		else if (table == 1)
			return 6;
		else if (operation.equals("setting") && table == 2)
			return 6;
		else if (operation.equals("serving") && table == 2)
			return 7;
		else if (operation.equals("clean") && table == 2) {
			if (mp.getInfo(running_Robot)[0] < 300)
				return 6;
			else
				return 7;
		} else if (table == 3)
			return 7;
		else if (table == 4)
			return 11;
		else if (operation.equals("setting") && table == 5)
			return 11;
		else if (operation.equals("serving") && table == 5)
			return 12;
		else if (operation.equals("clean") && table == 5) {
			if (mp.getInfo(running_Robot)[0] < 300)
				return 11;
			else
				return 12;
		} else if (table == 6)
			return 12;
		return 0;
	}


	// update timer label (mm:ss)
	public void run() {

		while (true) {
			Calendar time = Calendar.getInstance();
			int mm, ss;
			mm = time.get(Calendar.MINUTE);
			ss = time.get(Calendar.SECOND);
			timeTestLabel.setText(mm + ":" + ss);

			try {
				Thread.sleep(1000); // 1 second
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub

		if (e.getSource() == guestEntranceBtn) {
			// check empty table
			for (int i = 0; i < 6; i++) {   // in order 1 to 6
				if (table_state[i] == 0) {   // if table is empty
					guest[i] = new Guest(i); // create guest and initialize 
					guest[i].start(); // guest thread start
					table_state[i] = 1; // table not empty now
					break;
				} else {
					if (i == 5) {   // if restaurant is full, guest can't come.
						JOptionPane.showMessageDialog(null, "The restaurant is full", "alert",
								JOptionPane.WARNING_MESSAGE);
					}
				}
			}
		}
	}

	Guest[] guest;

	JPanel emptyPane1;
	JPanel emptyPane2;
	JLabel timeTestLabel;
	JButton guestEntranceBtn;


}      
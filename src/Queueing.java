
import java.util.LinkedList;
import java.util.Queue;

//main method is placed main class
//this class has 4method operating in the main class
//init(String, int) method is add queue
//out() method is return next operation to main class 
//priority() is  rearrange queues
//state() is check queues state

public class Queueing {

	public static String message = null;
	public static int dish = 5;

	private static Queue<String> Queue = new LinkedList<>(); // third priority Queue
	private static Queue<String> clean_Queue = new LinkedList<>();// second priority Queue
	private static Queue<String> priority_Queue = new LinkedList<>();// first priority Queue

	private static Queue<String> temp_Queue = new LinkedList<>();// temporary stored Queue

	// kind of priority
	// '.' is Delimiter to robot go to where table
	// ex) 'setting.5' is robot should go table number5 to do 'setting' at table 5

	public static int state()// find the queque us empty if empty return 1, if not empty return 0
	{
		if (Queue.isEmpty() && clean_Queue.isEmpty() && priority_Queue.isEmpty())
			return 1;
		else
			return 0;
	}

	// rearrange the Queues according to priority.
	public static void priority() {

		// if all queue is empty so this operation is the lowest priority
		if (dish <= 3 && !Queue.contains("refull.0")) {
			// dish is
			Queue.add("refull.0");
			System.out.println("! push: refull");
		}
		
		// the highest priority "refull", if all dish is zero
		if (dish == 0 && !priority_Queue.isEmpty() && priority_Queue.contains("refull.0")
				&& priority_Queue.element() != "refull.0")// dish is zero then do 1st
		{

			if (priority_Queue.contains("refull.0"))
				priority_Queue.remove("refull.0");
			// if refull is in priority but not first (exceptional case)
			if (dish == 0 && !priority_Queue.isEmpty() && priority_Queue.contains("refull.0")
					&& priority_Queue.element() != "refull.0") {
				if (Queue.contains("refull.0"))
					Queue.remove("refull.0");
				priority_Queue.remove("refull.0");
				priorityQueue();
				priority_Queue.add("refull.0");
				repriorityQueue();
			}
			// if refull is not in priority
			
		}
		else if (dish == 0 && priority_Queue.isEmpty()) {

			if (Queue.contains("refull.0"))
				Queue.remove("refull.0");
			priority_Queue.add("refull.0");
		}	
		

		// setting the 2nd, 3rd priority "setting" or "serving"
		// In one table, Necessarily "setting" > "serving"
		// At different tables,"setting" >= "serving"
		if (message != null) {
			if (Queue.contains(message))
				Queue.remove(message);
			if (priority_Queue.contains(message))
				return;
			else {
				priority_Queue.add(message);
				message = null;// main.message = null;
			}
		}
		
		// for debugging
		System.out.println();
		System.out.println("priority Queue ------- ");
		if (!priority_Queue.isEmpty()) {
			System.out.println("1 " + priority_Queue);
		} 
		if (!clean_Queue.isEmpty()) {
			System.out.println("2 " + clean_Queue);
		}
		if (!Queue.isEmpty()) {
			System.out.println("3 " + Queue);
		}
		System.out.println("---------------------- ");
		System.out.println();
		
	}

	// input operation in queues
	public static void init(String operation, int table) {

		// init() method can put at clean_Queue and Queue
		// pirority_Queue's rearrange or input is priority() method
		if (operation.equals("clean.")) {
			clean_Queue.add(operation + table);
		} else
			Queue.add(operation + table);
	}

	public String[] out() throws Exception {

		String str1;
		String str2;
		String opr = null;

		// Find the following operation in order of priority
		while (true) {
			Queueing.priority();//rearrange queue
			Thread.sleep(10);// if queue is empty other robot get operation then opr == null
								// opr == null, loop (exceptional case)
			if (!priority_Queue.isEmpty()) {
				opr = priority_Queue.poll();
				if (opr == null)
					continue;
				break;
			} else if (!clean_Queue.isEmpty()) {
				opr = clean_Queue.poll();
				if (opr == null)
					continue;
				break;
			} else if (!Queue.isEmpty()) {
				opr = Queue.poll();
				if (opr == null)
					continue;
				break;
			} else
				continue;
		}
		str1 = opr.substring(0, opr.indexOf("."));
		str2 = opr.substring(opr.indexOf(".") + 1);
		String[] str = { str1, str2 };
		return str;// Transfer to MainFrame
	}

	// priority_Queue value -> temp_Queue
	private static void priorityQueue() {
		for (int i = 0; i < priority_Queue.size(); i++)
			temp_Queue.add(priority_Queue.poll());

	}

	// temp_Queue -> priority_Queue value
	private static void repriorityQueue() {
		for (int i = 0; i < temp_Queue.size(); i++)
			priority_Queue.add(temp_Queue.poll());
	}

}
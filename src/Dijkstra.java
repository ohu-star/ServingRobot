
public class Dijkstra {
   private int n = 12; //number of nodes
   private int[][] Graph; //store 1 if the nodes are connected
   private int[] visitList; //visiting order(node number)
   private int[][] visit_xy; //visiting order(x, y)
   private int[] distance; //store total distance
   private boolean[] visited; //store that the node is already visited
   private int end; //endpoint
   private int[] robots; //current location of the robots
   private int r; //store which robot to work for
   private int robot_status; //robot status(working or not)
   public int [][] node_list = { // every nodes coordinates
         {200, 80},
         {300, 80},
         {400, 80},
         {200, 180},
         {400, 180},
         {200, 280},
         {400, 280},
         {200, 380},
         {300, 380},
         {400, 380},
         {200, 480},
         {400, 480}
   };
   
   
   public Dijkstra() {
      super();
   }
   
   //initiallize
   public void init(
         int end, //endpoint
         int[] robot1, //robot1's coordinate
         int[] robot2, //robot2's coordinate
         int robot_status) //robot status
   {
      Graph = new int[n][n];
      visitList = new int[n];
      distance = new int[n];
      visited = new boolean[n];
      this.end = end;
      robots = new int[2];
      robots[0] = change_Num(robot1);
      robots[1] = change_Num(robot2);
      this.robot_status = robot_status;
      
      //run the main function of Dijkstra after initiallize
      Do_Dijkstra();
   }
   
   //return visiting order
   public int[][] list_result(){
      return visit_xy;
   }
   
   //return which robot to work for
   public int workRobot(){
      return r;
   }
   
   //return endpoint's coordinates
   public int[] dest_num(){
      
      int[] node_xy = new int[2];
      node_xy[0] = node_list[end-1][0];
      node_xy[1] = node_list[end-1][1];
      
      return node_xy;
   }
   
   //change coordinates to node number
   public int change_Num(int[] xy) {
      
      int nodeNum = 0;
      
      //returns the index of a match coordinates in the list
      for(int i = 0; i<n; i++) {
         if(node_list[i][0]==xy[0] && node_list[i][1]==xy[1]) {
            nodeNum = i;
         }
      }
      
      return nodeNum;
   }

   
   //store the connected nodes
   public void get_Graph(int a, int b) {
      a--;
      b--;
      
      //store 1 because distances between nodes are all the same
      Graph[a][b] = Graph[a][b] = 1;
   }
   
   //Set the distance to the largest integer
   private void set_distance() {
      for(int i = 0; i < n ; i++) {
         distance[i] = Integer.MAX_VALUE;
      }
   }
   
   //store connected nodes' distance
   private void neighbor_dist(int start) {
      
      for(int i = 0; i<n; i++) {
         
         //if the node is not visited, and connected, save distance
         if(!visited[i] && Graph[start][i]!=0) {
            distance[i] = 1;
            visitList[i] = start;
         }
      }
   }
   
   //update if there is a more efficient route
   private void update_dist(int start) {
      
      
      for(int i = 0; i<n-1 ; i++) {
         
         int min = Integer.MAX_VALUE;
         int minNode = -1;
         
         //find the min value of distance
         for(int j = 0; j<n; j++) {
            
            if(!visited[j] && distance[j]<Integer.MAX_VALUE) {
               if(min>distance[j]) {
                  min = distance[j];
                  minNode = j;
               }
            }
         }
         
         //visit that node
         visited[minNode] = true;
         
         int k;
         
         //the value stored endpoint to k is bigger than a value passing through minNode, update
         for(k = 0; k<n; k++) {
            
            if(!visited[k] && Graph[minNode][k]!=0) {
               
               if(distance[k] > distance[minNode]+Graph[minNode][k]) {
                  distance[k] = distance[minNode]+Graph[minNode][k];
                  visitList[k] = minNode;
               }
            }
         }
         
         
      }
      
   }
   
   //return which robot to work for
   private int robot_num(int robot1, int robot2, int robot_status) {
      
      // 1 means robot1 is not working now
      // 2 means robot2 is not working now
      if(robot_status == 1 || robot_status == 2) {
         return robot_status-1;
      }

      else {
         //return the closer robot's number
         if(distance[robot1]> distance[robot2]) {
             return 1;
          }
          else
             return 0;
      }
      
   }
   
   //change the list stored by node number to coordinate value
   private int[][] get_location_list(int[] list, int length){
      
      int [][] location_list = new int[length][2];
      
      for(int i = 0; i<length ; i++) {
         location_list[i][0] = node_list[list[i]][0];
         location_list[i][1] = node_list[list[i]][1];
      } 
      
      return location_list;
   }
   
   //store what nodes are connected
   private void set_Graph() {
      get_Graph(1,2);
      get_Graph(1,4);
      get_Graph(2,1);
      get_Graph(2,3);
      get_Graph(3,2);
      get_Graph(3,5);
      get_Graph(4,1);
      get_Graph(4,6);
      get_Graph(5,3);
      get_Graph(5,7);
      get_Graph(6,4);
      get_Graph(6,8);
      get_Graph(7,5);
      get_Graph(7,10);
      get_Graph(8,6);
      get_Graph(8,9);
      get_Graph(8,11);
      get_Graph(9,8);
      get_Graph(9,10);
      get_Graph(10,7);
      get_Graph(10,9);
      get_Graph(10,12);
      get_Graph(11,8);
      get_Graph(12,10);
   }
   
   //do dijkstra
   public void Do_Dijkstra() {
      
      int start = end; //do dijkstra from endpoint to robots
      int robot1 = robots[0];
      int robot2 = robots[1];
      
      start--;
      
      //save map
      set_Graph();
      
      //set distance list
      set_distance();
      
      distance[start] = 0;
      visited[start] = true;
      visitList[start] = start;
      
      //store connected nodes' distance
      neighbor_dist(start);
      
      //update if there is a more efficient route
      update_dist(start);
      
      int Robot = robot_num(robot1, robot2, robot_status);
      r = Robot+1;
      
      //from the endpoint to the other nodes, store route in the list upside down
      for(int i=0; i<n; i++) {
         int[] route = new int[n];
         for(int k = 0 ; k<n ;k++) {
            route[k] = 0;
         }
         int index = i;
         
         route[0] = robots[Robot];
         int j = 1;
         while(true) {
            
            route[j]= visitList[index];
            index = visitList[index]; 
            if(visitList[index] == index) break; 
            j++;
         }
         
         //if the working robot's node, 
         if(i == robots[Robot]) {
            
            int[][] location_list = new int[j][2];
            
            //change the list stored by node number to coordinate value
            location_list = get_location_list(route, j+1);
            
            visit_xy = location_list;
            
            break;
         }
      }
      
   }
   
}
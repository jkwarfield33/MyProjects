/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cs.pkg420.project.pkg1;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import static java.lang.System.in;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;
import static java.lang.System.out;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author Jacob
 * 
 * This is a program written for class. It is an AI that solves 8-tile puzzles.
 * It determines whether or not the given puzzle can be solved, and runs two different
 * heuristics to compare them.
 */
public class CS420Project1{
    public static boolean solved=false;
    //--------------------------------------------------------------------------
    public static void main(String[] args) throws FileNotFoundException, IOException {
       // out.println(1%8);
            boolean done=false;

            Scanner ausculta= new Scanner(in);
            int[] board;
            final int n=3;
            int type;
            
            
            while(!done){
                ArrayList boards=new ArrayList();
                out.println("What would you like me to do?");
                out.println("1: solve puzzles");
                out.println("2: quit");
                int input=ausculta.nextInt();

                if(input==2){
                    done=true;
                }
                else{

                out.println("How would you like to set up the puzzles");
                out.println("1: Input puzzle");
                out.println("2: Generate 200 Test cases");
                input=ausculta.nextInt();
                ausculta.nextLine();
                switch(input){
                    case 1:
                        board= new int[n*n];
                        out.println("Please enter the values in one line");
                        char[] input2=ausculta.nextLine().toCharArray();
                        
                        for(int i=0;i<board.length; i++){
                            board[i]=Integer.parseInt(""+input2[i]); 
                        }
                        out.println("board saved");
                        boards.add(board);
                        break;
                    //...........................................................
                    case 2:
                        
                        board= new int[n*n];
                        out.println("Please Enter the name of the file with the data:");
                        String filename=ausculta.nextLine();
                        
                      FileReader reader = new FileReader(filename);
                    BufferedReader buffer= new BufferedReader(reader);
                    String line=buffer.readLine();
                    
                    while(line!=null){
                        out.println(line);
                        input2=line.toCharArray();
                        for(int i=0;i<board.length; i++){
                            board[i]=Integer.parseInt(""+input2[i]); 
                        }
                        
                        boards.add(board);
                        line=buffer.readLine();
                    } 
                    
                        break;
                    //..........................................................
                    default:


                }
                out.println("Do you wish to run hueristic 1 or 2?");
                type=ausculta.nextInt();
                
                int avgNumNode=0;
                long avgTime=0;
                
                for(Object o: boards){
                    
                    if(solvable((int[])o)){ 
                        if(!solved){ 
                            out.println(input);
                            
                            Graph g= new Graph((int[])o, n,input,type); 
                            
                            avgNumNode=avgNumNode+g.numofNodes;
                            avgTime=avgTime+g.time;
                        }
                        else{
                            solved=false; //reset for the next board
                        }
                            
                    }
                    else{
                        out.println("The configuration has no solution");
                    }
                }
                if(input==2){
                    out.println("Avg # of Nodes: "+avgNumNode/boards.size());
                    out.println("Avg time(ns): "+avgTime/boards.size());
                }

            }
        }
    }
    //--------------------------------------------------------------------------
    public static boolean solvable(int[] board){ //not working 
        int pairsum=0;
        int count=0;
        
        for(int i=0; i<board.length; i++){
            if(i==board[i]){
                count++;
            }
        }
        if(count==board.length){
            out.println("The puzzle is already solved!");
            solved=true;
            return true;
        }
        for(int i=0; i<board.length; i++){
            if(board[i]!=0){
            for(int j=i+1; j<board.length;j++){
                if(board[j]!=0){
                if(board[j]<board[i]){
                    pairsum++;
                }
                }
            }
            }
        }
       out.println("pairsum="+pairsum);
        if(pairsum%2==0){
            return true;
        }
        return false;
    }
}
//******************************************************************************
//******************************************************************************
class Graph{
    protected Node root;
    protected Node current;
    protected int n;
    protected HashMap explored= new HashMap();
    protected PriorityQueue<Node> frontier = new PriorityQueue<Node>();
    protected Boolean done=false;
    
    private boolean checksmall=false;
    private int smallestdepth=0;
    private Node best=null;
    private ArrayList solution=new ArrayList();
    
    public int input;
    public int numofNodes=1;
    public long time;
    public Graph(int[] b, int n,int input,final int type){
        //type is used to determine which hueristic we run. Since we can't use both
        //at the same time. 
        this.n=n;
        current= new Node(1,b,n, null,type);
        best=current;
        time=System.nanoTime();
        out.println(input);
        
        if(!current.solved){
            current.breed();  
            frontier=new PriorityQueue<Node>();

            for(Node nd: current.children){
                frontier.add(nd);
                numofNodes++;
            }
            while(!done){
                if(frontier.size()==0){
                    done=true;
                }
                else{
                choose();
                
                if(checksmall){
                    done=(current.depth>smallestdepth);
                }
                
                else if(current.solved){
                    boolean finished=true;
                    best=current;
                    smallestdepth=current.depth;
                    
                    
                    frontier.poll();
                         for(Node nd: frontier){
                             if(type==1){
                               if(nd.depth+nd.h1 < current.depth){
                                   finished=false;
                                   break;
                               }  
                             }
                             else{
                                if(nd.depth+nd.h2<current.depth){
                                    finished=false;
                                    break;
                                } 
                             }
                         }
                
                    done=finished;
                }
                }
                
            }
            //Once we are out of the while loop, we should have the node of the
            //best solution save in best
            time=System.nanoTime()-time;
            current=best;

            while(current.parent!=null){
                solution.add(current.board);
                current=current.parent;
            }   
        }
        solution.add(current.board);
        if(input!=2){
        print();
        }
        else{
            out.println(numofNodes);
            out.println(time);
        }        
    }
    //--------------------------------------------------------------------------
    //--------------------------------------------------------------------------
    public void choose(){
        
        explored.put(current.boardString,current);
        if(frontier.size()>0){
        current=(Node)frontier.poll(); 
        current.breed(); 
        }

        for(Node n: current.children){
            if(!explored.containsKey(n.boardString)){
                frontier.add(n); //adds any of the children that have not yet been explored.
                numofNodes++;
            }
        }

    }
    //--------------------------------------------------------------------------
    //--------------------------------------------------------------------------
    public void print(){

        String scaledString = "-----\n";
        
        out.println("Solution size: "+solution.size());
        for(int i=solution.size()-1; i>=0; i--){
            int k=0;
            for(int m=0; m<n-1;m++){
                for(int j=0; j<n-1; j++){
                    out.print(((int[])solution.get(i))[k]+"|");
                    k++;
                }
                out.print(((int[])solution.get(i))[k]+"\n");
                k++;
                out.print(scaledString);
            }
            for(int m=0; m<n-1; m++){
                out.print(((int[])solution.get(i))[k]+"|");
                k++;
            }
        out.print(((int[])solution.get(i))[k]+"\n");
        out.println("");
        }
        //}
    }
}
//******************************************************************************
//******************************************************************************
class Node implements Comparable<Node>{
    public int h1;//hueristic 1's cost
    public int h2;//hueristic 2's cost
    public int depth; //our node's depth and real cost
    public int[] board; //our current state
    public String boardString=""; //for use as hashmap key
    public ArrayList<Node> children; //our children
    public Node parent;
    public boolean solved;
    public int type=0;
    
    private int zx;
    private int zy; //for when we make kiddies
    protected int n;
    protected List solution; // a list of strings. Our final output.
    
    public Node(int d, int[] b, int n, Node p,int t){
        depth=d;
        board=b;
        type=t;
        
        for(int i: board){
            boardString+=i;
        }
        
        h1=0;
        h2=0; 
        parent=p;
        this.n=n;
        solved=solved();
        if(!solved){
            //......................................................................
            int index=0;
            int cy=0;
            int cx=0;
            int fy=0;
            int fx=0;

            for(int i: board){
                if(i!=index){
                    h1++;
                }
                if(cx==n){
                    cx=0;
                }
                else{
                    cx++;
                }
                
                cy=(int)(index/n);//where it is
                cx=index-(cy*n); //not right.

                if(i==0){
                 zy=cy;
                 zx=cx;
                }

                fy=(int)(i/n);//where it should be
                fx=i-(fy*n); 
                
                
                
                h2+=(Math.abs(fx-cx)+Math.abs(fy-cy));
                index++;
            }//that's the hueristics set.
        }
    }
    //--------------------------------------------------------------------------
    //--------------------------------------------------------------------------
    public boolean solved(){ //maybe I should just cram this into the h for loop
        for(int i=0; i<board.length; i++){
            if(board[i]!=i){
                return false;
            }
        }
        return true;
    }
    //--------------------------------------------------------------------------
    //--------------------------------------------------------------------------
    public void breed(){
        
        children=new ArrayList(4);
       
        //Remember i=x+yn
        int temp=0;
        int left=this.zx-1;
        int right=this.zx+1;
        int up=this.zy-1;
        int down=this.zy+1;

        int[] savel=new int[board.length];
        int[] saver=new int[board.length];
        int[] saveu=new int[board.length];
        int[] saved=new int[board.length];
        /*
        Yes I know it's excessive, but I'm sick of my Arrays sharing when
        I don't want them to.
        */
        
        if(left>=0){
           for(int i=0;i<savel.length;i++){
               savel[i]=this.board[i];
           }
           
           temp=savel[left+(this.zy*n)];
           savel[left+(this.zy*n)]=0;
           savel[this.zx+(this.zy*n)]=temp;
           children.add(new Node(depth+1,savel,n,this,type));
        }
        //.....................................................
        if(right<n){
           for(int i=0;i<saver.length;i++){
               saver[i]=this.board[i];
           }

           temp=saver[right+(this.zy*n)];
           saver[right+(this.zy*n)]=0;
           saver[this.zx+(this.zy*n)]=temp;
           children.add(new Node(depth+1,saver,n,this,type));
        }
        //....................................................
        if(up>=0){
           for(int i=0;i<saveu.length;i++){
               saveu[i]=this.board[i];
           }

           temp=saveu[this.zx+(up*n)];
           saveu[this.zx+(up*n)]=0;
           saveu[this.zx+(this.zy*n)]=temp;
           children.add(new Node(depth+1,saveu,n,this,type));
        }
        //...................................................
        if(down<n){
           for(int i=0;i<saved.length;i++){
               saved[i]=this.board[i];
           }

           temp=saved[this.zx+(down*n)];
           saved[this.zx+(down*n)]=0;
           saved[this.zx+(this.zy*n)]=temp;
           children.add(new Node(depth+1,saved,n,this,type));
        }
    }
    //--------------------------------------------------------------------------
    //--------------------------------------------------------------------------
    public int compareTo(Node b){
                    int acost;
                    int bcost;
                    if(type==1){
                        acost=this.depth+this.h1;
                        bcost=b.depth+b.h1;
                    }
                    else{
                        acost=this.depth+this.h2;
                        bcost=b.depth+b.h2;
                    }
                    if(acost>bcost){
                        return 1;
                    }
                    if(acost<bcost){
                        return -1;
                    }
                    return 0;
                }
}

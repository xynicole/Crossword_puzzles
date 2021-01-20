import java.io.*;
import java.util.*;

public class CrosswordB {
	private DictInterface dict;
	private static int boardSize;
	private static char[][] board;
	private static StringBuilder[] rowStr;
	private static StringBuilder[] colStr;
	private int[] minusPosRow; // track of the position of the last encountered  minus in each row and each column
	private int[] minusPosCol;
	private static int[] score = new int[]{1,3,3,2,1,4,2,4,1,8,5,1,3,1,1,3,10,1,1,1,1,4,4,8,4,10};
	private static long startTime,endTime; // for timing
	static final long MILLISEC_PER_SEC = 1000;
	public static int solutions = 0;// number of total solutions found
	private static String testType; // dictionary type 
	private static int maxScore = 0; // max score
	private static String[] maxSolution; // hold highest score solution
	
	
	
	
	public static void main(String[] args) throws IOException{
		startTime= System.currentTimeMillis();
		testType = args[0];
		String dictionary = args[1];
		String testBoard = args[2];
		
		new CrosswordB(testType, dictionary, testBoard);
		
	}

	public CrosswordB(String testType, String dictionary, String testBoard) throws IOException{	
		// Have the type of the DictInterface object  DLB or dictionary
	    if (testType.equals("DLB")){
			dict = new DLB();
		}else{
			dict = new MyDictionary();
		}
		
		
	    // read board file
	    Scanner boardReader = new Scanner (new FileInputStream(testBoard));
	  	boardSize = Integer.parseInt(boardReader.nextLine());
	  	
	 // read the dictionary
	    Scanner dictScan = new Scanner(new FileInputStream(dictionary));
	    String st;
	    while(dictScan.hasNext()){
	    	st = dictScan.nextLine();
	    	if (st.length() <= boardSize) { //prune the dict that words will fit on the board
	    		dict.add(st);
	    	}
	    	
	    }
	    dictScan.close();
	  	
	  	
	  //initialize board with size x size
	  	board = new char[boardSize][boardSize];
	  	colStr = new StringBuilder[boardSize];
	  	rowStr = new StringBuilder[boardSize];
	  	minusPosCol = new int[boardSize];
	    minusPosRow = new int[boardSize];
	    maxSolution  = new String[boardSize];;
	    
	    
	    //initialize string builders
	    for (int i = 0; i < boardSize; i++) {
	    	colStr[i] = new StringBuilder();	
	    	rowStr[i] = new StringBuilder();	
	    	maxSolution[i] = new String();
		}
	    
	    //initialize position to -1
	    for(int i=0; i<boardSize; i++){
	    	minusPosCol[i] = -1;
	    	minusPosRow[i] = -1;
	    }
	    
	    
	  //create crossword square from file
	    for(int i = 0; i < boardSize; i++){
	    	String rowString = boardReader.nextLine();
	    	for(int j = 0; j < boardSize; j++){
	    		board[i][j] = rowString.charAt(j);
	      }
	    }
	    //boardReader.close();
	    
	   
	    
	    System.out.println("Solution Found:"); 
	    
	   
		//call solve  recursively     	
	  	solve(0, 0);

	  	
  		// if is DLB print the highest score along with the solution
  		if(testType.equals("DLB")){
  			for(int i=0; i < boardSize; i++){
  				System.out.println(maxSolution[i]);
  			}
  			System.out.println("Highest Score: " + maxScore);
  			System.out.println("Total solutions: " + solutions);
  			endTime =  System.currentTimeMillis(); // for timing
  		    System.out.println("Runtime = " + (endTime-startTime)/MILLISEC_PER_SEC + "seconds");
  			System.exit(0);
  		}
	  		
	  		
  		//if solve returns to the main, no solution found
  		else{
  			System.out.println("No Solution");
  			endTime =  System.currentTimeMillis(); // for timing
  		    System.out.println("Runtime = " + (endTime-startTime)/MILLISEC_PER_SEC + "seconds");
  			System.exit(0);
  		}
	      
	}
	
	private boolean isValid(char c, int row, int col){
		rowStr[row].append(c);
	    colStr[col].append(c);
	    int rowResult = dict.searchPrefix(rowStr[row], minusPosRow[row]+1, rowStr[row].length()-1);
	    int colResult = dict.searchPrefix(colStr[col], minusPosCol[col]+1, colStr[col].length()-1);
	    
	  // if row is a prefix =>  not an end index and the next c not -
	    if(col != boardSize -1 && board[row][col+1] != '-'){
	    	if(rowResult !=1 && rowResult !=3) {
	    		rowStr[row].deleteCharAt(rowStr[row].length()-1);//removes the last char from row
	    		colStr[col].deleteCharAt(colStr[col].length()-1);
	    		return false;
	        }
	    	
	    	//if row is a word => an end index
	      }else{   
	    	if(rowResult !=2 && rowResult !=3) {
	    		rowStr[row].deleteCharAt(rowStr[row].length()-1);
	    		colStr[col].deleteCharAt(colStr[col].length()-1);
	    		return false;
	        }
	      }
	      // if col is a prefix =>  not an end index and the next c not -
	     if(row != board.length-1 && board[row+1][col] != '-'){
	    	 if(colResult !=1 && colResult !=3) {
	    		 rowStr[row].deleteCharAt(rowStr[row].length()-1);
	    		 colStr[col].deleteCharAt(colStr[col].length()-1);
	    		 return false;
	        }
	    	 
	    	// if col is a word
	      }else{
	    	  if(colResult !=2 && colResult !=3) {
	    		  rowStr[row].deleteCharAt(rowStr[row].length()-1);
	    		  colStr[col].deleteCharAt(colStr[col].length()-1);
	    		  return false;
	    	  }
	      }
	     
	      //none of above false conditions which means case is valid, delete appended char return true
	      rowStr[row].deleteCharAt(rowStr[row].length()-1);
	      colStr[col].deleteCharAt(colStr[col].length()-1);
	      return true;
	}
	
	private void solve(int row, int col) {
		if(board[row][col]=='+'){
	        for(char c = 'a'; c <= 'z'; c++){
	          if(isValid(c, row, col)){
	            rowStr[row].append(c);
	            colStr[col].append(c);
	            //maxSolution[row].append(c);
	            // recurse call if needed if end the word, print solution exist for 1 or continue for test 2
	            // undo deleting the last char in both string builders
	            //recures next square
	            
	            if(row == boardSize -1 && col == boardSize -1) {
	            	if(testType.equals("DLB")){
						solutions++;
						printDLB();

					}else{ // myDict type
						printDitc();
					}
	            	
	            	 
	            }else if(row != boardSize -1 && col == boardSize -1 ){
	              solve(row+1, 0);
	                            
	            }else if(col != boardSize -1){
	              solve(row, col+1);
	            }
	            //undo
	            rowStr[row].deleteCharAt(rowStr[row].length()-1); //removes the last char from row
	            colStr[col].deleteCharAt(colStr[col].length()-1);  //removes the last char from col
	         }
	       }
		}else if(board[row][col] == '-'){   //board contains -
			// update row to be col next, col to row
			int temp1;
			int temp2;
			temp1 = minusPosRow[row];
	        temp2 = minusPosCol[col];
	        minusPosRow[row] = col;
	        minusPosCol[col] = row;
	        rowStr[row].append('-');
	        colStr[col].append('-');
	          
	        if(row == boardSize -1 && col == boardSize -1) {
	        	if(testType.equals("DLB")){
					solutions++;
					printDLB();
				
				}else{ // myDict type
					printDitc();
				}     	          
	            
            }else if(row != boardSize -1 && col == boardSize -1){
              solve(row+1, 0);	          
                           
            }else if(col != boardSize -1){
              solve(row, col+1);
            }
	        rowStr[row].deleteCharAt(rowStr[row].length()-1);
            colStr[col].deleteCharAt(colStr[col].length()-1);
            minusPosRow[row] = temp1;
            minusPosCol[col] = temp2;
	        
		}else{ // if is not +/- is a preset letter
			if(isValid(board[row][col], row, col)){
	          rowStr[row].append(board[row][col]);
	          colStr[col].append(board[row][col]);
	          
	          if(row == boardSize -1 && col == boardSize -1) {
	        	  if(testType.equals("DLB")){
						solutions++;
						printDLB();
						
					}else{ // myDict type
						printDitc();
					}
	            	   
	          }else if(row != boardSize -1 && col == boardSize -1){
	              solve(row+1, 0);
  
	          }else if(col != boardSize -1){
	              solve(row, col+1);
	          }
	          
	          rowStr[row].deleteCharAt(rowStr[row].length()-1);
              colStr[col].deleteCharAt(colStr[col].length()-1);
	       }
	    }       
	}
	
	//get score 
	private static int score(){
		int v = 0;
		for(int i=0; i< boardSize; i++){
			for(int j=0; j < boardSize; j++){
				if(board[i][j] != '-'){
					int ind = rowStr[i].charAt(j)-'a';
					v += score[ind];
				}
			}
		}
		return v;
    }
	
	// print dictionary 
	private static void printDitc(){
		for(int i=0; i < boardSize; i++){
            System.out.println(rowStr[i]);
          }
    	
    	int score = score();
        System.out.println("Score: " + score);
        endTime =  System.currentTimeMillis(); // for timing
		System.out.println("Runtime = " + (endTime-startTime)/MILLISEC_PER_SEC + "seconds");
        System.exit(0);

	}
	// print DLB
	private static void printDLB(){
		int score = score();
		if (score > maxScore){
			maxScore = score;
			
			for(int i=0; i < boardSize; i++){
				maxSolution[i] = rowStr[i].toString();
			}
		}
	}
}
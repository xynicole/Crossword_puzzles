import java.util.*;

public class DLB implements DictInterface{
  private static final char TERMINATOR = '$';
  private Node root;

  public DLB(){
	  root = new Node();
  }
  
  private class Node{
	  private char letter; 
	  private Node sibling;//right
      private Node child; //down
      
  }
  
  public boolean add(String s){
    if (s == null) throw new IllegalArgumentException("calls add() with a null key");
    
    s =  s + TERMINATOR;
    root = add(root, s, 0);
    return true;
  }

  public Node add(Node x, String s, int pos){
	  Node result = x;  
	  if(x == null){
		  result = new Node();
		  result.letter  = s.charAt(pos);
		  if(pos < s.length()-1){
			  result.child = add(result.child,s,pos+1); //Recurse on the child node
		  }
	  }else if(x.letter == s.charAt(pos)){
		  if(pos < s.length()-1){
			  result.child=add(result.child,s,pos+1);
		  }
	  }else{
		  result.sibling =add(result.sibling,s,pos);//Recurse on the sibling node
	  }
	  return result;
  }
  
  
  public int searchPrefix(StringBuilder s){

	  return searchPrefix(s, 0, s.length()-1);
  }
  
  /*	0 : s is not a word or prefix = not found
	 	1 : s is a prefix but not a valid word
	 	2 : s is a word  but not a prefix 
	 	3 : s is a word and a prefix
   */

  public int searchPrefix(StringBuilder s, int start, int end){  
	  if (root != null){
		  char key = s.charAt(start);
		  Node x = root; 
		
		  while(true){
			  if (key == x.letter) {
				  if (start < end) {
					  start++; 
					  x = x.child; 
					  key = s.charAt(start);
	    			
				  }else if (x.child.letter == TERMINATOR){
					  if (x.child.sibling != null){ 
						  return 3;
					  }
					  return 2;
					
				  }else { 	
					  return 1; 
				  }
			  }else if (key > x.letter){
				  if(x.sibling != null) {
					  x = x.sibling;
				  }else {
					  return 0; //not found
				  }
			  }else {
				  return 0; 
			  }
		}
	}	
	  	return 0; 	
  }
 /* 
  public void delete(String s) {
      root = delete(root, s 0);
  }

  public Node delete(Node x, String s, int pos){
	  //if BLD is empty 
      if (x == null) return null;
      if (pos == s.length()) x = null;
      x.letter = s.charAt(pos);
      if(pos >s.length()-1) { 
          x.child = delete(x.child, s, pos+1);
      }else if(x.letter == s.charAt(pos)) {
    	  x.child = delete(x.child, s, pos+1);
      }
      else {
    	  x.sibling = delete(x.sibling,s, pos);
      }
      
    
  }*/
  
}

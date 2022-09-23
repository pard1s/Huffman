//pardis ghavami - 9717023147
import java.util.* ;          //importing Scanner , PriorityQueue, Comparator classes
import java.io.*;             //importing File, FileWriter, FileReader,FileOutputStream, FileIntputStream ...
//****************************************************************************************************************//
class Node{                   //Huffman nodes
    int freq;
    char c;
    Node left;
    Node right;
    Node(char ch , int i , Node l, Node r){
        c = ch;
        freq = i;
        left = l;
        right=r;
    }
    Node(char ch  , Node l, Node r){
        c = ch;
        left = l;
        right=r;
    }
}
//****************************************************************************************************************//
class Compare_Nodes implements Comparator<Node>{   
    public int compare(Node x , Node y){               //comparing Nodes on the basis of data values of the nodes.
        return x.freq - y.freq;
    }      

}
//****************************************************************************************************************//
class Huffman{
    static Scanner input = new Scanner(System.in);
    static File f = null;
    static FileWriter myWriter =null;
    static FileReader reader = null;
    public static void main( String args[]) {
        System.out.println("\t  MENU OPTIONS  \t");
        System.out.println("-----------------------------------");
        System.out.println("1- Encode");
        System.out.println("2- Decode");
        System.out.println("-----------------------------------");
        int op = input.nextInt();
        if(op==1)
            encode();
        else if(op ==2)
            decode();
        else
            System.out.println("INVALID INPUT");
    
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static void encode(){
        System.out.println("Enter the path and name of your file without '.txt' prefix, example : Desktop\\Filename");
        input.nextLine();
        String file = input.nextLine();
        String s="";
        int n =63,i;
        char[] ch = new char[n];
        int[] fr = new int[n];
        String[] str = new String[n];
        String code ="";
        
        for(i=0 ; i<26 ; i++){   // line[41-50] -> initializing "ch" array
            ch[i] =(char)( 97 + i);
        }
        for(i=0 ; i<26 ; i++){
            ch[i+26] =(char)( 65 + i);
        }
        for(i=0 ; i<10 ; i++){
            ch[i+52] =(char)( 48 + i);
        }
        ch[62] = (char)(32);

        for(i=0 ; i<n ; i++){  // initilizing "fr" array = 0
            fr[i]=0;
        }
        try{
            reader =new FileReader(file+".txt");
            Scanner in = new Scanner(reader);
            while(in.hasNext()) {
                s=s+in.nextLine();
            }
            reader.close();
            in.close();
        }catch(IOException e) {
            e.printStackTrace();
        } 
        

        for(i=0 ; i<s.length() ; i++){  // fr[i] means how many times ch[i] has been repeated in the text file
            fr[new String(ch).indexOf(s.charAt(i))]++;
        }
        
        PriorityQueue<Node> q = new PriorityQueue<Node>(n,new Compare_Nodes());  //creating a min-priority queue 
        
        for(i=0 ; i<n ; i++){  // add the letters as leaves to q
            if(fr[i] !=0 ){
                Node h = new Node(ch[i],fr[i],null,null);
               
                q.add(h);
            }
        }
        Node root = q.peek(); //root of huffman tree 
        while(q.size() > 1){  // creating huffman tree 
            Node left = q.peek();
            q.poll();
            Node right = q.peek();
            q.poll();
            Node z = new Node('*',left.freq +right.freq , left , right); 
            q.add(z);
            root = z;
        }
        try {   // traversing the tree and storing it's data  in "file.table"
            f = new File(file+".table");
            myWriter = new FileWriter(file+".table");
            if(root.left==null && root.right == null)
                printCode(root, "1" ,str ,ch); 
            else
                printCode(root, "" ,str ,ch);
            myWriter.close();
        }catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        for(i=0 ; i<s.length() ; i++){
            code = code + str[new String(ch).indexOf(s.charAt(i))];
        }
        byte[] b;
        if(code.length()%7 == 0 || code.length()<7)
            b = new byte[code.length()/7 +1];
        else
            b = new byte[code.length()/7 +2];
        int k=0;
        String temp ="";
        int len=7 ;
        for(i=0 ; i<=code.length()/7;i++){
            if((7*i)>=code.length())
                break;
            temp = "";
            if(7*(i+1) < code.length()){
                temp =temp + code.substring(i*7, (i+1)*7);
            }
            else{
                temp = temp+ code.substring(i*7);
                len = temp.length();
            }
            
            b[k++] = Byte.parseByte(temp,2);
            
        }
        String l = len + "";
        b[k] = Byte.parseByte(l,10);
        try {   
            f = new File(file+".Huffman");
            FileOutputStream fos = new FileOutputStream(file+".Huffman") ;  //Storing b[] in File.huffman
                fos.write(b);
                fos.close();
            }catch (IOException e) {
                System.out.println("An error occurred.");
                e.printStackTrace();
               
        }
        
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static void decode(){
        System.out.println("Enter the path and name of your file without '.txt' prefix, example : Desktop\\Filename");
        input.nextLine();
        String file = input.nextLine();
        String code ="";
        int i=0;
        String temp,st;
        String text="";
        try{
            FileInputStream is =new FileInputStream(file+".Huffman");
            byte[] b = is.readAllBytes();
            is.close();
            
            for(i=0 ; i<b.length-2 ; i++){
                temp = String.format("%7s", Integer.toBinaryString(b[i] & 0xFF)).replace(' ', '0');
                code = code + temp;
            }
            int len = b[b.length -1];
            temp = String.format("%s", Integer.toBinaryString(b[b.length-2]).replace(' ', '0'));
            for(i=0 ; i< len-temp.length() ; i++)
                code=code+"0";
            code = code + temp;
          //  System.out.println(code);
        }catch(IOException e) {
            System.out.println("File Not Found!");
            e.printStackTrace();
        }
        char ch; 
        Node root = new Node('*',null,null);
        try{                                 //reading File.table and recreating Huffman Tree
            reader =new FileReader(file+".table");
            Scanner in = new Scanner(reader);
            while(in.hasNext()) {
                st =in.nextLine();
                ch = st.charAt(0);
                temp= st.substring(2,st.length());
              //  System.out.print(temp);
                create_Tree(root, temp, ch);
            }
            reader.close();
            in.close();
        }catch(IOException e) {
            e.printStackTrace();
        }
       Node t=root ;
       for(i=0 ; i< code.length(); i++){    //decoding using huffman tree
            if(t== null)
                break;
           
            if(code.charAt(i)=='0'){
                t=t.left;
           }
            else if(code.charAt(i)=='1'){
                t=t.right;
        }
            if(t.left == null && t.right == null && t.c !='*'){
                text = text + t.c ;
                t=root;
            }  
       }
       System.out.println(text);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static void create_Tree(Node root , String str ,char ch){
        if(str.length()<=1){
            if(str.charAt(0)=='0'){
                root.left = new Node(ch,null,null );
            }
            else{
                root.right = new Node(ch,null,null );
            }
            return ;
        }

        if(str.charAt(0)== '0'){
            if(root.left==null)
                root.left = new Node('*',null,null );
            create_Tree(root.left, str.substring(1), ch);
        }
        else if(str.charAt(0)=='1'){
            if(root.right==null)
                root.right = new Node('*',null,null );
            create_Tree(root.right, str.substring(1), ch);
        }
        return ;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static void printCode(Node root, String s ,String[] str, char[] ch) 
    { 
  
        if (root.left == null && root.right == null && root.c != '*') { 
            
            str[new String(ch).indexOf(root.c)] = s;
            try {
                myWriter.write(root.c+" "+s+"\n");
                } catch (IOException e) {
                System.out.println("An error occurred.");
                e.printStackTrace();
                }
            return; 
        } 
       
       printCode(root.left, s + "0" ,str ,ch); 
       printCode(root.right, s + "1" , str , ch);
           
    } 
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

   
}
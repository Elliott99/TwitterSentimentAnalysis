/**
  Class to support search of Twitter's tweets using twitter4j API.
  Concatenates tweets to a file.
  author: Elliott Goldstein (I worked alone)
 */

import twitter4j.*;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Collections;
import java.util.Scanner;
import java.util.StringTokenizer;

/**
 * Constructing a SearchTweets object will perform a single search
 * and store them in an List.
 */
public class AnalyzeSentiment {
    private static final int MAXTWEETS = 100; // max tweets at once
    private  List<Status> tweets; // tweets from query
    private Query query; // query that generated the tweets
  private static RedBlackBST<String,Double> sentiments;
  private  static String[] words;
    /**
     * Generate tweets based on typical Twitter search string.
     * @param querystring (See https://dev.twitter.com/docs/using-search )
     */

public AnalyzeSentiment(){

}

/**
My AnalyzeSentiment constructor allows me to have a parameter filename that creates an object instance
of AnalyzeSentiment. The parameter is the file for which I will populate the tweets list with. Creating
this constructor allows me to have object instances of the analyzeTweets method so I don't always 
return the same sentiment values. If i had not made the constructor, I would have been forced to 
make analyzeTweets a static method which would always return the same sentiment values

public AnalyzeSentiment(String filename){
  try{tweets=loadintolist(filename);
}
catch (IOException ex){
         System.err.println("Error: locations file not found.");
       }

}
   
    

    /**
     * Retrieve tweets based on typical Twitter search string.
     * @param query valid Query object
     * @param count number of tweets to get (max 100)
     * @param loc  Central point of tweets (null to get all tweets)
     * @param radius Radius (in km) of locations searched
     * @param type type of tweets to get
     */
    
/**
@param the filename from where I will append from to get my RedBlackBST containing the sentiments
and their value. I do this by scanning in from the file given by the client, and then I split the 
strings into a string array (this will separate the key and value, or word and sentiment, values). I 
use my Sentiment class to create a sentiment which consists of the word and it's value, and then 
use my Sentiment methods to get the word and sentiment value to input into my RedBlackBST
*/
  public static RedBlackBST<String,Double> loadSentiments(String fname){
  sentiments = new RedBlackBST<String,Double>();
  //RedBlackBST<Value> value = new RedBlackBST<Value>();
  File infile = new File(fname);
  Scanner scanner = null;
  try{
    scanner = new Scanner(infile);

  }catch (FileNotFoundException ex){
    System.err.println("Error: locations file not found.");
    return null;
  }
while(scanner.hasNextLine()){
  String line = scanner.nextLine();
  StringTokenizer tokenizer = new StringTokenizer(line);
  String[] field = line.split(",");
  double score = Double.parseDouble(field[1]);
  String word=field[0];
  Sentiment sent = new Sentiment(field[0],score);
  sentiments.put(sent.getWord(), sent.getWordValue());
}
  scanner.close();
  return sentiments;
 
}
   
  
/**
Where I analyze my tweet. I use my loadintolist method to load in my file from which my tweets arraylist 
will be populated with tweets from.I check my arraylist words against my words from my RBTree (which
I use to get my words and their sentiment values). I add up my sentiments value and divide it by the length
of my words array(the words array is the result of splitting each word from my tweet so it is the length of
the tweet). I can analyze my different files by changing the parameter of my constructor
*/
public  Double analyzeTweets(){
  


  

 System.out.println("The num of elements is:" + tweets != null);
System.out.println("size of tweets "+tweets.size());
    double count = 0.0;
  double ave = 0.0;
    double getvalue = 0.0;
  for(int m = 0; m < tweets.size(); m++){
  Status t = tweets.get(m);
  String s = t.getText();
  //System.out.println(s);
  words = s.toLowerCase().replaceAll("^\\w]","").split("\\s+");
  for(int i = 0; i<words.length; i++){
    //getvalue = sentiments.get(words[i]);
    //System.out.println(getvalue + " ");
    if(sentiments.get(words[i]) != null){
      count += sentiments.get(words[i]);
        ave = count/words.length;
  System.out.println(ave+ " ");

      
    }
  }

 }
 return ave;
}


    
    
    public int numTweets() {
  if (tweets == null) return 0;
  return tweets.size();
    }


     
    public void print() {
  if (tweets == null) return;
  // Print out info about all tweets
  for (Status tweet : tweets) {
      System.out.println("\n\n@" + tweet.getUser().getScreenName() +
             " - " + tweet.getText());
      System.out.println("date: " + tweet.getCreatedAt());
      System.out.println("Reply to: " + tweet.getInReplyToScreenName());
  }
    }

    
    
  
      
      
        
    /**
    My loadintolist method: It's very similar to the load method except in this case I am adding on the new
    tweets to an ArrayList so I can utilize analyzeSentiment.
    @param filename the file to be loaded from
    @return an ArrayList<Status> of tweets
    */
     private  List<Status> loadintolist (String filename)throws IOException{
      List<Status> list1=new ArrayList();
       try{
      FileInputStream fis = new FileInputStream(filename);
      ObjectInputStream ois = new ObjectInputStream(fis);
      Object obj1 = ois.readObject();
      while(obj1 != null){
      Boolean same = false;
      for(Status t : list1){
         if(t.getText().equals(((Status)obj1).getText()) && t.getUser().equals(((Status)obj1).getUser()))       
          same = true;
        break;
      }
      if(same == false)
        list1.add((Status)obj1);
        obj1 = ois.readObject();

      

      
   
      
        }
        ois.close();
      fis.close();
       } catch(IOException ex){
         System.err.println("Error: locations file not found.");
       }catch(ClassNotFoundException e){
        System.err.println("Error:Class not found");
       }

    return list1;
    }

       
      

  
    /**
     * Load location data from a file.
     * @param fname File from which to load data
     * @return Arraylist of all cities in the file.
     */
    public static ArrayList<Location> loadLocations(String fname) {
  ArrayList<Location> locations = new ArrayList<Location>();
  File infile = new File(fname);
  Scanner scanner = null;
  try {
      scanner = new Scanner(infile);
  } catch (FileNotFoundException ex) {
      System.err.println("Error: locations file not found.");
      return null;
  }
  // Loop over lines and split into fields we keep.
  while (scanner.hasNextLine()) {
      String line = scanner.nextLine();
      StringTokenizer tokenizer = new StringTokenizer(line);
      String[] fields = line.split(",");
      long pop = Long.parseLong(fields[4]);
      if (pop > 100000) { // Ignore small towns.
        // See Location.java to figure out these fields.
    Location loc = new Location(fields[0],fields[1],
              Double.parseDouble(fields[2]),Double.parseDouble(fields[3]),pop);
    locations.add(loc);
    //System.out.println("Added " + loc.toString());
      }
      
  }
  scanner.close();
  return locations;
    }

   
  

   

    /**
     * Usage: java twitter4j.examples.search.SearchTweets [query]
     *
     * @param args
     */
    public static void main(String[] args) {
  if (args.length != 0) {
      System.out.println("java SearchTweets");
      System.exit(-1);
  }

loadSentiments("sentiments.csv");
  
AnalyzeSentiment s1=new AnalyzeSentiment("mytweets.ser");
System.out.println("Tweets for Pizza");
s1.analyzeTweets();
System.out.println("Tweets for Indian Food");
AnalyzeSentiment s2=new AnalyzeSentiment("indianfood.ser");
s2.numTweets();
s2.analyzeTweets();
AnalyzeSentiment s3=new AnalyzeSentiment("sushi.ser");
System.out.println("Sushi Tweets");
s3.analyzeTweets();
AnalyzeSentiment s4=new AnalyzeSentiment("mexicanfood.ser");
System.out.println("Mexican food tweets");
s4.analyzeTweets();
AnalyzeSentiment s5=new AnalyzeSentiment("chinesefood.ser");
System.out.println("Chinese food");
s5.analyzeTweets();


 
    }
    }
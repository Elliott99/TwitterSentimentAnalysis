/*
 /* Class to support search of Twitter's tweets using twitter4j API.
 * Concatenates tweets to a file.
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
import java.util.Date;

/**
 * Constructing a SearchTweets object will perform a single search
 * and store them in a List.
 */
public class SearchTweets {
    private static final int MAXTWEETS = 101; // max tweets at once
    private  List<Status> tweets; // tweets from query
    private Query query; // query that generated the tweets
    Date d1=new Date(); 
    /**
     * Generate tweets based on typical Twitter search string.
     * @param querystring (See https://dev.twitter.com/docs/using-search )
     */
    public SearchTweets(String querystring) {
    this(new Query(querystring),20,null,0.0,Query.ResultType.recent);
    }

    public SearchTweets(){

    }



    /**
     * Retrieve tweets based on typical Twitter search string.
     * @param query valid Query object
     * @param count number of tweets to get (max 100)
     * @param loc  Central point of tweets (null to get all tweets)
     * @param radius Radius (in km) of locations searched
     * @param type type of tweets to get
     */
    public SearchTweets(Query query,int count, GeoLocation loc,
            double radius, Query.ResultType type) {
    this.query = query;
    if (count > 0 && count < MAXTWEETS) // limit tweets we get
        query.setCount(count);
    query.setResultType(type);
    if (loc != null) query.setGeoCode(loc,radius,Query.KILOMETERS);
    Twitter twitter = new TwitterFactory().getInstance();
    QueryResult result = null;
    try {
        result = twitter.search(query);
        tweets = result.getTweets(); // store retrieved tweets
    }
    catch (TwitterException te) {
        tweets = null;
        te.printStackTrace();
        System.out.println("Failed to search tweets: " + te.getMessage());
    }
    }

  
   
  


    /**
     * @return number of tweets in this object
     */
    public int numTweets() {
    if (tweets == null) return 0;
    return tweets.size();
    }

    /**
     * Add a method to return an arraylist consisting of every userid and the
     * number of tweets by that user.
     */
    public ArrayList<UserData> getCounts() {
    if (tweets == null) return null;
    ArrayList<String> results = new ArrayList<String>();
    // get info about all tweets
    for (Status tweet : tweets) {
        String name = tweet.getUser().getScreenName();
        results.add(name);
    }
    Collections.sort(results);
    ArrayList<UserData> userdata = new ArrayList<UserData>();

    // Count tweets by username, assume userdata is sorted by name.
    for (int i = 0; i < results.size(); i++) {
        String name = results.get(i);
        int count = 0;
        // loop over all of same name, update count
        while (i < results.size() && name.equals(results.get(i))) {
        i++;
        count++;
        }
        userdata.add(new UserData(name,count));
        i--;
    }
    return userdata;
    }

    /**
     * Print all tweets to standard output.
     */
    public void print() {
    if (tweets == null) return;
    // Print out info about all tweets
    for (Status tweet : tweets) {
        System.out.println("\n\n@" + tweet.getUser().getScreenName() +
                   " - " + tweet.getText());
        System.out.println("date: " + tweet.getCreatedAt());
        //System.out.println("User: " + tweet.getUser());
        System.out.println("Reply to: " + tweet.getInReplyToScreenName());
    }
    }

      /**
      @param the filename where the files will be saved
      I save my files by declaring a fileoutputstream which reads from the file given as 
      a parameter by the client. I then add the data from the file by doing a for each loop
      on the tweets list where each iteration I use the writeObject method which will add a tweet

      */
   public void save(String filename) {
      
      try {
      
      FileOutputStream fis = new FileOutputStream(filename);
      ObjectOutputStream oos = new ObjectOutputStream(fis);
      for(Status t : tweets){
        oos.writeObject(t);
      }
      
      oos.close();
      fis.close();
  }catch(EOFException ef){
    System.err.println("Error: locations file not found.");
  }
   catch (IOException ex) {
      System.err.println("Error: locations file not found.");
     
  }
  
      
    }
  
      
    

    /**
    @param a filename where I will load the tweets from
    Much like the save method, I do a for each loop in which I will loop over each Status object in my tweets
    list. I avoid adding duplicate tweets by checking the equality of the tweet's text and usernames.
    I add new tweets by saving them to a new file which I denote by adding the date.getTime() method. By
    doing this, after running my do.gather script I will have many increasingly large files and I can tell
    which is the largest by picking which filename is the most recent time. 
     */
   public void load(String filename) throws IOException {
      try{
      FileInputStream fis = new FileInputStream(filename);
      ObjectInputStream oos = new ObjectInputStream(fis);
      Object obj2 = oos.readObject();
      while(obj2 != null){
      Boolean same = false;
      for(Status t : tweets){
         if(t.getText().equals(((Status)obj2).getText()) && t.getUser().equals(((Status)obj2).getUser()))       
          same = true;
      }
      if(same == false)
        tweets.add((Status)obj2);
        obj2 = oos.readObject();
        save(filename+d1.getTime());
      

      
      
        }
        oos.close();
      fis.close();
       } catch(IOException ex){
         System.err.println("Error: locations file not found.");
       }catch(ClassNotFoundException e){
        System.err.println("Error:Class not found");
       }

  // TODO
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
     * Collect tweets and add all resulting tweets to a file.
     * @param numtweets Max number of tweets to gather
     * @param searchwords String of all words that will form twitter searches
     * @param loc Location to search for tweets
     * @param radius Radius (kilometers) about loc to search
     * @param type  Type of results to find
     */
    public static void collectTweets(int numtweets, String searchwords, GeoLocation loc,
             double radius, Query.ResultType type, String outfile) {

  StringTokenizer searchtok = new StringTokenizer(searchwords);
  while (searchtok.hasMoreTokens()) {
      String word = searchtok.nextToken();
      SearchTweets tweets= new SearchTweets(new Query(word),numtweets,loc,radius, type);
      System.out.println("Num Tweets: " + tweets.numTweets());
      tweets.print();
      tweets.save(outfile);       
  }
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

   /*
   * TODO: Right now this just searches for these words in big cities.
   */
  
  int maxnum = 100; // max num tweets to get at one time
  final double radius = 60; // radius around location to search

  GeoLocation gl = new GeoLocation(40.7128,-74.006); //NYC
  String mexican="mexican food";
  Query m1=new Query(mexican);
  // 10 tweets within 20 Kilometers
  SearchTweets tweets= new SearchTweets(new Query("pizza"),99,gl,20,Query.ResultType.recent);
  SearchTweets mexicanfood=new SearchTweets(m1,99,gl,radius,Query.ResultType.recent);
  SearchTweets indianfood=new SearchTweets(new Query("indian food"),99,gl,radius,Query.ResultType.recent);
  SearchTweets chinesefood=new SearchTweets(new Query("chinese food"),99,gl,radius,Query.ResultType.recent);
  SearchTweets sushi=new SearchTweets(new Query("sushi"),99,gl,radius,Query.ResultType.recent);
  //System.out.println("Num Tweets: " + tweets.numTweets());
  //System.out.println("Num tweets miami" + miamitweets.numTweets());

  
 
    tweets.save("mytweets.ser");
    chinesefood.save("chinesefood.ser");
     mexicanfood.save("mexicanfood.ser");
     indianfood.save("indianfood.ser");
     sushi.save("sushi.ser");
    



   
  //tweets.print();
 sushi.print();
}

}
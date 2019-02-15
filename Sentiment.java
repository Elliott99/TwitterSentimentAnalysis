public class Sentiment{
	private String word;
	private double wordvalue;

	public Sentiment(String word, double wordvalue){
		this.word=word;
		this.wordvalue=wordvalue;
	} 

	public String getWord(){
		return word;
	}

	public double getWordValue(){
		return wordvalue;
	}
}

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class Dictionary {
	
	private String name;                                    //Name of this dictionary
	private String filePath;                                //File path of dictionary data
	private HashMap<String, String> data = new HashMap<>(); //Store dictionary data
	private ArrayList<String> wordList = new ArrayList<>(); //Store words to search
	private boolean isModified = false;                     //Check if the dictionary is modified
	
	public Dictionary() {}
	
	/*
	 * Read data from file and create new dictionary
	 */
	public Dictionary(String name, String filePath) {
		this.name = name;
		this.filePath = filePath;
		
		//Import dictionary data
		importData();
	}
	
	//Return number of words in dictionary
	public int size() { return wordList.size(); }
	
	//Check if dictionary is empty
	public boolean isEmpty() { return wordList.isEmpty(); }
	
	//Add new word with two strings: key, value(with html tags)
	public void addNewWord(String word, String meaning) {
		data.put(word, meaning);
		wordList.add(word);
	}
	
	//Add new word from file dictionary data
	//string with word and meaning + full html tags
	public void addWord(String string) {
		int wordEndPos = string.indexOf("</b>");
		String word = string.substring(3, wordEndPos);
		String meaning = string.substring(wordEndPos + 4);
		//Add new word to data and 
		data.put(word, meaning);
		wordList.add(word);
	}
	
	//Return name of this dictionary
	public String getName() { return name; }
	
	//Set modify condition
	public void setModify(boolean isModified) {
		this.isModified = isModified;
	}
	public boolean isModified() { return isModified; }
	
	//Search the meaning of the word in the dictionary
	public String search(String string) {
		return data.get(string);
	}
	
	//Return words array in the dictionary
	public ArrayList<String> getWordList() {
		return wordList;
	}
	
	//Remove word from the dictionary by key
	public void remove(String word) {
		data.remove(word);
		wordList.remove(word);
	}
	
	//Remove word from dictionary by key and value
	public void remove(String word, String meaning) {
		data.remove(word, meaning);
		wordList.remove(word);
	}
	
	//Export data to file
	//Write all dictionary data to file
	public void exportData() {
		update();
		
		try {
			FileOutputStream fos = new FileOutputStream(this.filePath);
	        OutputStreamWriter writer = new OutputStreamWriter(fos, "utf-8");
	        
	        for(String word: wordList) {
	        	writer.write("<b>" + word + "</b>" + data.get(word) + "\n");
	        }
	        
	        writer.close();
		} catch(FileNotFoundException e) {
			//
		} catch(IOException e) {
			//
		}
	}
	
	//Import data from file
	public void importData() {
		//Clear all old data
		data.clear();
		wordList.clear();
		
		try {
			FileInputStream fis = new FileInputStream(this.filePath);
	        InputStreamReader isr = new InputStreamReader(fis, "utf-8");
	        BufferedReader reader = new BufferedReader(isr);
			
			while(reader.ready()) {
				String line = reader.readLine();
				if(!line.equals("")) {
					addWord(line);
				}
			}
			reader.close();
		} catch(FileNotFoundException e) {
			//
		} catch(IOException e) {
			//
		}
	}
	
	//Update word list
	//Sort ArrayList words alphabetically 
	public void update() {
		Collections.sort(wordList, String.CASE_INSENSITIVE_ORDER);
	}

}

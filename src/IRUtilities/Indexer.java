package IRUtilities;


import jdbm.RecordManager;
import jdbm.htree.HTree;
import jdbm.helper.FastIterator;
import java.io.IOException;


public class Indexer {
	private RecordManager recman;
	private HTree hashtable1;
	private HTree hashtable2;
	private int lastIndex;

	public Indexer(RecordManager recman1, String objectname) throws IOException {
		recman = recman1;
		long recid1 = recman.getNamedObject(objectname+"1");
		long recid2 = recman.getNamedObject(objectname+"2");
		long recid3 = recman.getNamedObject(objectname+"Size"); 
		if (recid1 != 0){ // If the object has already been recorded in record
						// manager;
			hashtable1 = HTree.load(recman, recid1);
			hashtable2 = HTree.load(recman, recid2);
			lastIndex = (Integer)recman.fetch(recid3); 
		}else // If not, create a new hashtable;
		{
			hashtable1 = HTree.createInstance(recman);
			recman.setNamedObject(objectname+"1", hashtable1.getRecid());
			hashtable2 = HTree.createInstance(recman);
			recman.setNamedObject(objectname+"2", hashtable2.getRecid());
			long recid4 = recman.insert(new Integer(0));
			recman.setNamedObject(objectname+"Size",recid4); 
		}
	}
	
	public int getLastIndex(){
		return lastIndex;
	}

	public void finalize() throws IOException {
		recman.commit();
		recman.close();
	}
	
	public boolean isContain(String word) throws IOException{
		return hashtable1.get(word)!=null;
	}

	public int addEntry(String word, String word2) throws IOException {
		// Add a "docX Y" entry for the key "word" into hashtable
		// ADD YOUR CODES HERE
		// Test if a term has already existed in the inverted index file. If so, stop insertion and return directly.
		// If not, insert to the inverted file;
		// If there is no the following "if" block, the posting lists will accumulate after several time's running the code.
		if (hashtable1.get(word)!=null)
		{
			return getIndexNumber(word);
		}

		hashtable1.put(word, word2);
		hashtable2.put(word2, word);
		lastIndex++;
	    //recman.commit();
	    return  Integer.parseInt(word2);
	}
	
	

	public void delEntry(String word) throws IOException {
		// Delete the word and its list from the hashtable
		// ADD YOUR CODES HERE
		if (hashtable1.get(word)!=null)lastIndex--;{
			hashtable2.remove(hashtable1.get(word));
			hashtable1.remove(word);
			
		}

	}
	
	public String getIndex(String word) throws IOException{
		if(hashtable1.get(word) == null){
			return "-1";
		}
		return String.valueOf(hashtable1.get(word));
	}
	
	public int getIndexNumber(String word) throws IOException{
		if(hashtable1.get(word) == null){
			return -1;
		}
		return new Integer((String)hashtable1.get(word));
	}
	
	public String getValue(String index) throws IOException{
		if(hashtable2.get(index) == null){
			return "-1";
		}
		return String.valueOf(hashtable2.get(index));
	}
	
	public int getValueNumber(String index) throws IOException{
		if(hashtable2.get(index) == null){
			return -1;
		}
		return new Integer((String)hashtable2.get(index));
	}

	public void printAll() throws IOException {
		// Print all the data in the hashtable
		// ADD YOUR CODES HERE
		FastIterator iter = hashtable1.keys();
		String key;
		while ((key = (String) iter.next()) != null) {
			System.out.println(key + ": " + hashtable1.get(key));
		}

	}
}
package IRUtilities;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import jdbm.RecordManager;
import jdbm.RecordManagerFactory;
import jdbm.helper.FastIterator;

import org.htmlparser.util.ParserException;

import IRUtilities.*;


public class spider {
	private static final int MAX = 300;
	private static int numOfPage = 0;
	private static StopStem stopStem = new StopStem("stopwords.txt");
	private static Vector<String> TaskList = new Vector<String>();
	private static Vector<String> DoneList = new Vector<String>();
	private static Indexer PageIndexer;
	private static Indexer WordIndexer;
	private static Indexer TitleIndexer;
	private static InvertedIndex inverted;
	private static InvertedIndex ForwardIndex;
	private static InvertedIndex ChildParent;
	private static RecordManager recman;
	private static InvertedIndex ParentChild;
	private static PageProperty Pageppt;
	private static Indexer maxTermFreq;
	private static InvertedIndex termWth;
	
	public static void main(String[] args) throws IOException {
		
		try
		{
			recman = RecordManagerFactory.createRecordManager("/Users/KatrinaKo/Documents/workspace_java2/4321/testRM.db");
			//recman = RecordManagerFactory.createRecordManager("/Users/JasonPoon/Documents/workspaceForJ2EE/final/database");
			PageIndexer = new Indexer(recman, "page");
			WordIndexer = new Indexer(recman, "word");
			TitleIndexer = new Indexer(recman, "title");
			inverted = new InvertedIndex(recman, "invertedIndex");
			ForwardIndex = new InvertedIndex(recman, "ForwardIndex");
			ChildParent = new InvertedIndex(recman, "ParentChild");
			ParentChild = new InvertedIndex(recman, "PC");
			Pageppt  = new PageProperty(recman, "PPT");
			maxTermFreq = new Indexer(recman, "maxTermFreq");
			termWth = new InvertedIndex(recman, "termWth");
			System.out.println("loading...");
			fetchPages("http://www.cse.ust.hk");
			while(!TaskList.isEmpty() && numOfPage < MAX){
				if(DoneList.contains(TaskList.firstElement())){
					TaskList.removeElementAt(0);
					continue;
				}
				fetchPages(TaskList.firstElement());
				TaskList.removeElementAt(0);
			}
			
			//calculate termWeight
			FastIterator iter =  inverted.AllKey();
			String key;
			while ((key = (String) iter.next()) != null) {
				int df = inverted.numOfElement(key);
				for(int i = 0; i < df ; i++){
					String[] temp = inverted.getElement(key, i).split(":");
					int maxTF = maxTermFreq.getIndexNumber(temp[0]);
					int tf = Integer.parseInt((temp[1]));
					double weight = termWeight(tf, maxTF, df, MAX);
					termWth.addEntry2(key, temp[0]+":"+weight);
				}
			}
			
			recman.commit();
			recman.close();
			System.out.println("\nDone");
		}
		catch (ParserException e)
		{
			e.printStackTrace ();
		}
	}
	
	public static double termWeight(double tf, double maxTf, double numOfDoc, double maxOfDoc){
		double idf = Math.log(maxOfDoc/numOfDoc)/Math.log(2);
		return (tf*idf)/maxTf;
	}
	
	public static void fetchPages(String url) throws ParserException, IOException{
		System.out.println(url);
		DoneList.add(url);
		numOfPage++;
		
		//crawler
		Crawler crawler = new Crawler(url);
		Vector<String> links = crawler.extractLinks();
		for(int i = 0; i < links.size(); i++){
			if(!DoneList.contains(links.elementAt(i))){
				TaskList.add(links.elementAt(i));
			}else{
				links.removeElementAt(i);
			}
		}
		int pageIndex;
		
		//check contain or not
		if(PageIndexer.isContain(url) && Pageppt.isContain(PageIndexer.getIndex(url))){
			pageIndex = PageIndexer.getIndexNumber(url);
			String date = crawler.lastUpdate();
			String date2 = Pageppt.getLastDate(Integer.toString(pageIndex));
			if(date.compareTo(date2)==0){
				System.out.println("Same...");
				return;
			}else{
				System.out.println("update...");
				//update if last modification date are not same
				String text = ForwardIndex.getValue(Integer.toString(pageIndex));
				String[] temp = text.split(" ");
				for(int i = 0; i < temp.length; i++){
					System.out.println(temp[i]);
				}
				ForwardIndex.delEntry(Integer.toString(pageIndex));
				Pageppt.delEntry(Integer.toString(pageIndex));
			}
		}else{
			System.out.println("NewPage...");
			pageIndex = PageIndexer.addEntry(url, Integer.toString(PageIndexer.getLastIndex()));
		}
		
		//extract word
		Vector<String> words = crawler.extractWords();
		Hashtable<Integer, Integer> map = new Hashtable<Integer,Integer>(); 
		for(int i = 0; i < words.size(); i++){
			if (!stopStem.isStopWord(words.get(i))){
				String temp = stopStem.stem(words.get(i));
				int index = WordIndexer.addEntry(temp, Integer.toString(WordIndexer.getLastIndex()));
				//Inverted-file index
				if(!map.containsKey(index)){
					map.put(index, 1);
				}else{
					
					map.put(index, map.get(index) + 1);
				}
				//forward index
				ForwardIndex.addEntry2(pageIndex+"", temp);
			}
		}
		Set<Integer> set = map.keySet();
	    Iterator<Integer> itr = set.iterator();
	    int max = 0;
	    while (itr.hasNext()) {
	      int index = itr.next();
	      int num = map.get(index);
	      inverted.addEntry(index+"", pageIndex, num);
	      max = num > max ? num : max;
	    }
	    maxTermFreq.addEntry(Integer.toString(pageIndex), Integer.toString(max));
	    
	    
		//extract title
		//StopStem stopStem = new StopStem("/Users/KatrinaKo/Documents/workspace_java2/comp4321/src/IRUtilities/stopwords.txt");
		Vector<String> titleWords = crawler.extractTitle();
		String title = "";
		for(int i = 0; i < titleWords.size(); i++){
			title += titleWords.elementAt(i);
			if (!stopStem.isStopWord(titleWords.get(i))){
				int index = TitleIndexer.addEntry(stopStem.stem(titleWords.get(i)), Integer.toString(TitleIndexer.getLastIndex()));
			}
		}
		
		//extract last update
		String date = crawler.lastUpdate();
		
		//extract pagesize
		int pageSize = crawler.pageSize();
		Pageppt.addEntry(Integer.toString(pageIndex), title, url, date, pageSize);
		for(int i = 0; i < links.size(); i++){
			int pageId = PageIndexer.addEntry(links.elementAt(i),Integer.toString(PageIndexer.getLastIndex()) );
			ChildParent.addEntry2(Integer.toString(pageId),Integer.toString(pageIndex));
			ParentChild.addEntry2(Integer.toString(pageIndex), Integer.toString(pageId));
		}
		TaskList.addAll(links);
	}
}

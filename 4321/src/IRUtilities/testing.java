package IRUtilities;
import java.io.IOException;

import org.htmlparser.util.ParserException;

import IRUtilities.*;


import java.io.*;
import java.util.Vector;

import jdbm.RecordManager;
import jdbm.RecordManagerFactory;


public class testing {
	private static final int MAX = 30;
	private static int numOfPage = 0;
	private static StopStem stopStem = new StopStem("stopwords.txt");
	private static Vector<String> TaskList = new Vector<String>();
	private static Vector<String> DoneList = new Vector<String>();
	private static Indexer PageIndexer;
	private static Indexer WordIndexer;
	private static Indexer TitleIndexer;
	private static InvertedIndex inverted;
	private static InvertedIndex ForwardIndex;
	private static InvertedIndex pagePro;
	private static InvertedIndex ChildParent;
	private static FileWriter fstream;
	private static BufferedWriter out;
	private static RecordManager recman;
	private static InvertedIndex ParentChild;
	private static PageProperty Pageppt;
	private static Indexer maxTermFreq;
	private static InvertedIndex termWth;
	private static InvertedIndex titleForwardIndex;
	private static InvertedIndex titleInverted;
	private static Indexer titleMaxTermFreq;
	
	
	public static void main(String[] args) throws IOException, ParserException {
		recman = RecordManagerFactory.createRecordManager("/Users/KatrinaKo/Documents/workspace_java2/4321/testRM.db");
		//recman = RecordManagerFactory.createRecordManager("/Users/JasonPoon/Documents/workspaceForJ2EE/final/database");
		
		PageIndexer = new Indexer(recman, "page");
		WordIndexer = new Indexer(recman, "word");
		TitleIndexer = new Indexer(recman, "title");
		
		//word index
		inverted = new InvertedIndex(recman, "invertedIndex");
		ForwardIndex = new InvertedIndex(recman, "ForwardIndex");
		ChildParent = new InvertedIndex(recman, "ParentChild");
		ParentChild = new InvertedIndex(recman, "PC");
		Pageppt  = new PageProperty(recman, "PPT");
		fstream = new FileWriter("spider_result.txt");
		out = new BufferedWriter(fstream);
		maxTermFreq = new Indexer(recman, "maxTermFreq");
		termWth = new InvertedIndex(recman, "termWth");
		titleForwardIndex = new InvertedIndex(recman, "titleFI");
		titleInverted = new InvertedIndex(recman, "titleI");
		titleMaxTermFreq = new Indexer(recman, "titleMaxTermFreq");
		

		//ForwardIndex.printAll();
		
		Vector<String> keywords = new Vector<String>();
		keywords.add("aveng");
		keywords.add("news");
		//SearchEngine se = new SearchEngine();
		//Vector<Link> result = se.search(keywords);
		
		
		/*for(int i = 0; i < result.size(); i++){
			System.out.println(result.elementAt(i));
		}*/
		
		
		/*
		 //
		for(int i = 0; i < 30; i++){
			fetch(PageIndexer.getValue(String.valueOf(i)));
		}
		//
		*/		
		
		//Close the output stream
		out.close();
		recman.close();
	}
	
	public static void fetch(String url) throws IOException{
		int index = PageIndexer.getIndexNumber(url);
		
		System.out.println(Pageppt.getTitle(Integer.toString(index)));
		out.append(Pageppt.getTitle(Integer.toString(index))+"\n");
		System.out.println(Pageppt.getUrl(Integer.toString(index)));
		out.append(Pageppt.getUrl(Integer.toString(index))+"\n");
		System.out.println(Pageppt.getLastDate(Integer.toString(index))+","+Pageppt.getPageSize(Integer.toString(index)));
		out.append(Pageppt.getLastDate(Integer.toString(index))+","+Pageppt.getPageSize(Integer.toString(index))+"\n");

		
		String WordList = ForwardIndex.getValue(String.valueOf(index));
		String[] temp = WordList.split(" ");
		for(int i = 0; i < temp.length;i++){
			System.out.print(temp[i]+" ");
			out.append(temp[i]+" ");
			String tempID = WordIndexer.getIndex(temp[i]);
			String str = inverted.getValue(tempID);
			String[] temp2 = str.split(" ");
			for(int j = 0 ; j < temp2.length;j++){
				String[] temp3 = temp2[j].split(":");
				int num = Integer.parseInt(temp3[0]);
				if(index == num){
					System.out.print(" "+temp3[1]+"; ");
					out.append(" "+temp3[1]+"; ");
					break;
				}
			}
		}
		System.out.println();
		out.append("\n");
		
		String child = ParentChild.getValue(String.valueOf(index));
		temp = child.split(" ");
		for(int i = 0; i < temp.length;i++){
			System.out.println(PageIndexer.getValue(temp[i]));
			out.append(PageIndexer.getValue(temp[i])+"\n");
		}
		
		System.out.println("-------------------------------------------------------------------------------------------");
		out.append("-------------------------------------------------------------------------------------------\n");
	}
}

package IRUtilities;
/* --
COMP4321 Lab2 Exercise
Student Name: Poon Ka Hang
Student ID: 10462347
Section: L1LA1
Email: khpoon@ust.hk
*/
import java.text.*;
import java.util.*;

import org.htmlparser.beans.StringBean;
import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.*;
import org.htmlparser.nodes.TagNode;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
import org.htmlparser.util.SimpleNodeIterator;

import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.htmlparser.beans.LinkBean;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.htmlparser.tags.*;

public class Crawler
{
	private String url;
	public Crawler(String _url)
	{
		url = _url;
	}
	
	public int pageSize() throws IOException{
		URL yahoo = new URL(url);
        URLConnection yc = yahoo.openConnection();
        BufferedReader in = new BufferedReader(
                                new InputStreamReader(
                                yc.getInputStream()));
        String inputLine;
        String temp="";
        while ((inputLine = in.readLine()) != null){
        	temp+=inputLine;
        }
        in.close();
        return temp.length();
	}
	
	public String lastUpdate() throws IOException{
		String[] temp = url.split("://");
		URL hp = new URL("http", temp[1], 80, "/"); 
		URLConnection hpCon = hp.openConnection();
		DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
		Date date = new Date(hpCon.getLastModified());
		return dateFormat.format(date);
	}
	
	public Vector<String> extractTitle() throws ParserException{
		Parser parser = new Parser (url);
		NodeFilter filter = new NodeClassFilter(TitleTag.class);
		NodeList nodeList = parser.parse(filter);
		Node[] nodes = nodeList.toNodeArray();
		String line ="";
		for(int i=0;i<nodes.length;i++){
		   Node node = nodes[i];
		   if(node instanceof TitleTag)
		   {
			   TitleTag titlenode = (TitleTag) node;
			   line = titlenode.getTitle();
		   }
		}
		String[] str = line.split(" ");
		Vector<String> vec = new Vector<String>();
		for(int i = 0; i < str.length;i++){
			vec.add(str[i]);
		}
		return vec;
	}
	
	public Vector<String> extractWords() throws ParserException

	{
		// extract words in url and return them
		// use StringTokenizer to tokenize the result from StringBean
		// ADD YOUR CODES HERE
		StringBean sb;
        sb = new StringBean ();
        sb.setLinks (false);
        sb.setURL (url);
        
        StringTokenizer st = new StringTokenizer(sb.getStrings ());
        Vector<String> vec = new Vector<String>();
        
        while (st.hasMoreTokens()) {
            vec.add(st.nextToken());
        }
		return vec;
	}
	public Vector<String> extractLinks() throws ParserException

	{
		// extract links in url and return them
		// ADD YOUR CODES HERE
        Vector<String> vec = new Vector<String>();
	    LinkBean lb = new LinkBean();
	    lb.setURL(url);
	    URL[] URL_array = lb.getLinks();
	    for(int i=0; i<URL_array.length; i++){
	    	vec.add(URL_array[i].toString());
	    }
	    return vec;
	}
	
	public static void main (String[] args)
	{
		try
		{
			Crawler crawler = new Crawler("http://www.cse.ust.hk");

			Vector<String> words = crawler.extractWords();		
			
			System.out.println("Words in "+crawler.url+":");
			for(int i = 0; i < words.size(); i++)
				System.out.print(words.get(i)+" ");
			System.out.println("\n\n");
			

	
			Vector<String> links = crawler.extractLinks();
			System.out.println("Links in "+crawler.url+":");
			for(int i = 0; i < links.size(); i++)		
				System.out.println(links.get(i));
			System.out.println("");
			
		}
		catch (ParserException e)
        {
            e.printStackTrace ();
        }

	}
}
	

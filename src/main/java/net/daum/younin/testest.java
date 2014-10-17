package net.daum.younin;

import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
 
public class testest{
	public static void main(String[] args) throws IOException {
		
//		Document docs = (Document) Jsoup.connect("http://www.twayair.com/International/Default/searchDepartureArrival.aspx?DOMINT=D")
//			    .data("cmd", "login","username", "xxxx","password", "yyyyy")
//			    .referrer("http://www.twayair.com/International/Default/searchDepartureArrival.aspx?DOMINT=D").method(Method.POST).execute().parse();
//		
		
//		Document docs = Jsoup.connect("http://www.twayair.com/International/Default/searchDepartureArrival.aspx?DOMINT=D")
//		        .data("cmd", "login","username", "xxxx","password", "yyyyy")
//		        .referrer("http://www.twayair.com/International/Default/searchDepartureArrival.aspx?DOMINT=D").post();
		
		String city = "일본날씨";
		String url = "http://search.daum.net/search?w=tot&DA=YZR&t__nil_searchbox=btn&sug=&o=&q=" + city;
		System.out.println(url);
		Document doc = Jsoup.connect(url).get();
		Elements titles = doc.select(".list_city");
		System.out.println(titles);
		System.out.println(doc);
 
		//print all titles in main page
		for(Element e: titles){
			System.out.println("text: " +e.text());
			System.out.println("html: "+ e.html());
		}	
 
		//print all available links on page
		Elements links = doc.select("li");
		for(Element l: links){
			System.out.println(l.attr("abs:href"));
		}
 
	}
}

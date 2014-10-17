package net.daum.younin;

import java.io.IOException;
import java.net.URL;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
 
 
public class XmlParsingUsingJsoup  {
    public static void main(final String[] args) throws IOException{
        Document doc = Jsoup.parse(
                new URL("http://weather.media.daum.net/?pageId=2005").openConnection().getInputStream(), 
                "UTF-8", 
                "http://127.0.0.1:8080/");
        
        Elements elems = doc.select("REGION[NAME=서울/경기]");
//        System.out.println(elems);
        Elements findtemp = elems.select("TEMP");
        String temp = findtemp.html();
        System.out.println("temp: "+temp);
        
//        Elements findsensetemp = elems.select("SENSETEMP");
//        String sensetemp = findsensetemp.html();
//        System.out.println("sensetemp: "+sensetemp);
        
        Elements findweather = elems.select("ICON");
        String weather = findweather.html();
        System.out.println("weather: "+weather);
        
        Elements findrain = elems.select("RAIN");
        String rain = findrain.html();
        System.out.println("rain: "+rain);
        
        Elements findwinddir = elems.select("WIND_DIR");
        String winddir = findwinddir.html();
        System.out.println("winddir: "+winddir);
        
        Elements findwindspeed = elems.select("WIND_SPEED");
        String windspeed = findwindspeed.html();
        System.out.println("windspeed: "+windspeed);
        
        Elements findhumidity = elems.select("HUMIDITY");
        String humidity = findhumidity.html();
        System.out.println("humidity: "+humidity);
        
        Elements findamtemp = elems.select("AMTEMP");
        String amtemp = findamtemp.html();
        System.out.println("amtemp: "+amtemp);
        
        Elements findpmtemp = elems.select("PMTEMP");
        String pmtemp = findpmtemp.html();
        System.out.println("pmtemp: "+pmtemp);

    }

}
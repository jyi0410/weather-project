package net.daum.younin;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/api")
public class FlightAPIController {

	@RequestMapping(value = "/weather/flight.json", method = RequestMethod.GET, produces = "application/json; charset=utf8")
	public Model flightinfo(Model model,
			@RequestParam("district1") String district1,
			@RequestParam("district2") String district2)
			throws MalformedURLException, IOException {

		HashMap<String, String> viewData = new HashMap<String, String>();

		List<String> airport = new ArrayList<String>();
		airport.add("인천");
		airport.add("청주");
		airport.add("대구");
		airport.add("포항");
		airport.add("울산");
		airport.add("제주");

		System.out.println(airport);
		System.out.println(district1);
		System.out.println(district2);
		viewData.put("district1", district1);
		viewData.put("district2", district2);

		java.util.Date d = new java.util.Date();
		System.out.println("date: " + d);
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		String yymmdd = df.format(d);
		System.out.println(yymmdd);

		Document doc = Jsoup.parse(new URL(
				"http://weather.media.daum.net/?pageId=2006").openConnection()
				.getInputStream(), "UTF-8", "http://127.0.0.1:8080/");

		float temp, temp2;
		float rain, rain2;
		String state, state2;
		String winddir, winddir2;
		float windspeed, windspeed2;
		int humidity, humidity2;

		String[] value1 = JsoupParsing(doc, district1, yymmdd);
		temp = Float.parseFloat(value1[0]);
		state = value1[1];
		if (value1[2].equals("-")) {
			rain = (float) 1.0;
		} else {
			rain = Float.parseFloat(value1[2]);
		}
		winddir = value1[3];
		windspeed = Float.parseFloat(value1[4]);
		humidity = Integer.parseInt(value1[5]);

		String[] value2 = JsoupParsing(doc, district2, yymmdd);
		if (value2[0].equals("") || value2[0].equals("-")) {
			temp2 = (float) 20.0;
		}
		temp2 = Float.parseFloat(value2[0]);
		state2 = value2[1];
		if (value2[2].equals("-")) {
			rain2 = (float) 1.0;
		} else {
			rain2 = Float.parseFloat(value2[2]);
		}
		winddir2 = value2[3];
		windspeed2 = Float.parseFloat(value2[4]);
		humidity2 = Integer.parseInt(value2[5]);

		String DAment1 = "<< 출발 "+ district1 + " 공항 날씨 >>  " ;
		String DAment2 = "1. 기온은 " + temp + "도입니다.";
		String DAment3 = "2. 강수량은 " + rain + "%입니다.";
		String DAment4 = "3. 날씨는 " + state + "입니다.";
		viewData.put("DAment1", DAment1);
		viewData.put("DAment2", DAment2);
		viewData.put("DAment3", DAment3);
		viewData.put("DAment4", DAment4);

		String AAment1 = "<< 도착 "+district2 + " 공항 날씨 >>  ";
		String AAment2 = "1. 기온은 " + temp2 + "도입니다.";
		String AAment3 = "2. 강수량은 " + rain2 + "%입니다.";
		String AAment4 = "3. 날씨는 " + state2 + "입니다.";
		viewData.put("AAment1", AAment1);
		viewData.put("AAment2", AAment2);
		viewData.put("AAment3", AAment3);
		viewData.put("AAment4", AAment4);

		String direction = "";

		if (district1.equals("인천")) {
			if (district2.equals("청주")) {
				direction = "북서";
			} else if (district2.equals("대구")) {
				direction = "북서";
			} else if (district2.equals("포항")) {
				direction = "북서";
			} else if (district2.equals("울산")) {
				direction = "북서";
			} else if (district2.equals("제주")) {
				direction = "북";
			}
		} else if (district1.equals("청주")) {
			if (district2.equals("인천")) {
				direction = "북서"; // 남서 북동
			} else if (district2.equals("대구")) {
				direction = "북서";
			} else if (district2.equals("포항")) {
				direction = "북서";
			} else if (district2.equals("울산")) {
				direction = "북서";
			} else if (district2.equals("제주")) {
				direction = "북"; // 동 서
			}
		} else if (district1.equals("대구")) {
			if (district2.equals("인천")) {
				direction = "북서"; // 남서 북동
			} else if (district2.equals("청주")) {
				direction = "북서"; // 남서 북동
			} else if (district2.equals("포항")) {
				direction = "서"; // 북 남
			} else if (district2.equals("울산")) {
				direction = "북서";
			} else if (district2.equals("제주")) {
				direction = "북";
			}
		} else if (district1.equals("포항")) {
			if (district2.equals("인천")) {
				direction = "북서"; // 남서 북동
			} else if (district2.equals("청주")) {
				direction = "북서"; // 남서 북동
			} else if (district2.equals("대구")) {
				direction = "서"; // 북 남
			} else if (district2.equals("울산")) {
				direction = "북";
			} else if (district2.equals("제주")) {
				direction = "북";
			}
		} else if (district1.equals("울산")) {
			if (district2.equals("인천")) {
				direction = "북서"; // 남서 북동
			}
			if (district2.equals("청주")) {
				direction = "북서"; // 남서 북동
			}
			if (district2.equals("대구")) {
				direction = "북서"; // 남서 북동
			}
			if (district2.equals("포항")) {
				direction = "북";
			}
			if (district2.equals("제주")) {
				direction = "북동"; // 북서 남동
			}
		}

		System.out.println("direction: " + direction);

		String flightwinddir = "";

		if (direction.equals("북서")) {
			if (winddir.equals("남서") || winddir.equals("북동")) {
				flightwinddir = "측풍";
			} else {
				flightwinddir = "측풍아님";
			}
		} else if (direction.equals("북")) {
			if (winddir.equals("동") || winddir.equals("서")) {
				flightwinddir = "측풍";
			} else {
				flightwinddir = "측풍아님";
			}
		} else if (direction.equals("서")) {
			if (winddir.equals("북") || winddir.equals("남")) {
				flightwinddir = "측풍";
			} else {
				flightwinddir = "측풍아님";
			}
		} else if (direction.equals("북동")) {
			if (winddir.equals("북서") || winddir.equals("남동")) {
				flightwinddir = "측풍";
			} else {
				flightwinddir = "측풍아님";
			}
		}
		System.out.println("flightwinddir: " + flightwinddir);

		int flightcancelpercent = 0;

		if (flightwinddir.equals("측풍")) {
			if (windspeed > 1.5 && windspeed < 3.4) {
				flightcancelpercent = 10;
			} else if (windspeed > 3.3 && windspeed < 5.5) {
				flightcancelpercent = 20;
			} else if (windspeed > 5.4 && windspeed < 8.0) {
				flightcancelpercent = 30;
			} else if (windspeed > 7.9 && windspeed < 10.8) {
				flightcancelpercent = 45;
			} else if (windspeed > 10.7 && windspeed < 13.9) {
				flightcancelpercent = 60;
			} else if (windspeed > 13.8 && windspeed < 17.2) {
				flightcancelpercent = 70;
			} else if (windspeed > 17.1 && windspeed < 20.8) {
				flightcancelpercent = 80;
			} else if (windspeed > 20.7 && windspeed < 24.5) {
				flightcancelpercent = 90;
			} else if (windspeed > 24.4) {
				flightcancelpercent = 100;
			}
		}
		System.out.println("flightcancelpercent: " + flightcancelpercent);

		int flightcancelpercent2 = 0;

		if ((humidity > 49 && humidity < 61)
				|| (humidity2 > 49 && humidity2 < 61)) {
			flightcancelpercent2 = 60;
		} else if ((humidity > 59 && humidity < 71)
				|| (humidity2 > 59 && humidity2 < 71)) {
			flightcancelpercent2 = 70;
		} else if ((humidity > 69 && humidity < 81)
				|| (humidity2 > 69 && humidity2 < 81)) {
			flightcancelpercent2 = 80;
		} else if ((humidity > 79 && humidity < 91)
				|| (humidity2 > 79 && humidity2 < 91)) {
			flightcancelpercent2 = 90;
		} else if ((humidity > 89 && humidity < 101)
				|| (humidity2 > 89 && humidity2 < 101)) {
			flightcancelpercent2 = 100;
		}
		System.out.println("flightcancelpercent2: " + flightcancelpercent2);
		String flightstate = "";
		String percent = "";
		int sum = flightcancelpercent + flightcancelpercent2;
		if (flightcancelpercent > 69 || flightcancelpercent2 > 80) {
			flightstate = "결항";
			System.out.println(yymmdd + "비행기는 결항되었습니다.");
			percent = String.valueOf(sum / 2);
			System.out.println("결항될 확률 :" + percent);
		} else {
			flightstate = "운항";
		}
		System.out.println("flightstate : " + flightstate);

		viewData.put("flightstate", flightstate);
		viewData.put("percent", percent);
		
		System.out.println(viewData);
		model.addAttribute("viewData", viewData);
		return model;
	}

	public String[] JsoupParsing(Document doc, String district, String day)
			throws MalformedURLException, IOException {

		String[] returnvalue = new String[6];

		String temp;
		String rain;
		String winddir;
		String state;
		String windspeed;
		String humidity;

		Elements elems2 = doc.select("REGION[NAME=" + district + "]");
		Elements elems = elems2.select("NOW");

		Elements findtemp = elems.select("SENSTEMP");
		temp = findtemp.html();
		System.out.println("temp: " + temp);
		returnvalue[0] = temp;

		Elements findstate = elems.select("WTEXT");
		state = findstate.html();
		System.out.println("state: " + state);
		returnvalue[1] = state;

		Elements findrain = elems.select("RAIN");
		rain = findrain.html();
		System.out.println("rain: " + rain);
		returnvalue[2] = rain;

		Elements findwinddir = elems.select("WIND_DIR");
		winddir = findwinddir.html();
		System.out.println("winddir: " + winddir);
		returnvalue[3] = winddir;

		Elements findwindspeed = elems.select("WIND_SPEED");
		windspeed = findwindspeed.html();
		System.out.println("windspeed: " + windspeed);
		returnvalue[4] = windspeed;

		Elements findhumidity = elems.select("HUMIDITY");
		humidity = findhumidity.html();
		System.out.println("humidity: " + humidity);
		returnvalue[5] = humidity;

		return returnvalue;
	}
}

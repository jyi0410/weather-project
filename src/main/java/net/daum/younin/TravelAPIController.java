package net.daum.younin;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
public class TravelAPIController {

	@RequestMapping(value = "/weather/travel", method = RequestMethod.GET, produces = "application/json; charset=utf8")
	public Model traveltip(Model model,
			@RequestParam("district1") String district1,
			@RequestParam("district2") String district2,
			@RequestParam("region1") String region1,
			@RequestParam("region2") String region2,
			@RequestParam("startDate") String startDate,
			@RequestParam("endDate") String endDate)
			throws MalformedURLException, IOException {

		List<String> region = new ArrayList<String>();
		region.add("서울/경기");
		region.add("서해5도");
		region.add("강원영서");
		region.add("강원영동");
		region.add("충청북도");
		region.add("충청남도");
		region.add("경상북도");
		region.add("경상남도");
		region.add("울릉도/독도");
		region.add("전라북도");
		region.add("전라남도");
		region.add("제주도");

		System.out.println("**********");
		System.out.println(region);

		System.out.println(region1);
		System.out.println(district1);
		System.out.println(region2);
		System.out.println(district2);

		System.out.println("startDate " + startDate);
		System.out.println("endDate " + endDate);

		String startdate = startDate;
		String enddate = endDate;

		java.util.Date d = new java.util.Date();
		System.out.println("date: " + d);
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		String yymmdd = df.format(d);
		System.out.println(yymmdd);

		// SimpleDateFormat의 형식을 선언한다.
		SimpleDateFormat original_format = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat new_format = new SimpleDateFormat("yyyyMMdd");
		String new_date1 = "";
		String new_date2 = "";
		// 날짜 형식 변환시 파싱 오류를 try.. catch..로 체크한다.
		try {
			// 문자열 타입을 날짜 타입으로 변환한다.
			Date original_date1 = original_format.parse(startdate);
			Date original_date2 = original_format.parse(enddate);
			// 날짜 형식을 원하는 타입으로 변경한다.
			new_date1 = new_format.format(original_date1);
			new_date2 = new_format.format(original_date2);
			// 결과를 출력한다.
			System.out.println("new_date1: " + new_date1);
			System.out.println("new_date2: " + new_date2);

		} catch (ParseException e) {
			e.printStackTrace();
		}

		int startdaynum = Integer.parseInt(new_date1);
		int enddaynum = Integer.parseInt(new_date2);
		int daygap = enddaynum - startdaynum;
		model.addAttribute("daygap",daygap);

		String[] futureday = new String[daygap + 1];
		String tomorrow = startdate;

		for (int i = 0; i < daygap + 1; i++) {
			long chStart = 0;
			// 내일 날짜 구하기
			try {
				chStart = original_format.parse(startdate).getTime(); // 스트링형 date를 long형의 함수로 컨버트
				futureday[i] = tomorrow; 
				chStart += 86400000 * (i + 1); // 24*60*60*1000 하루치의 숫자를 더준다
				Date day = new Date(chStart); // 다시 날짜형태로 바꿔주고
				tomorrow = df.format(day); // 바꿔준 날짜를 yyyyMMdd형으로 변환
				System.out.println("futureday "+futureday[i]);
				model.addAttribute("futureday",futureday);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		float temp, temp2;
		int rain, rain2;
		String state, state2;
		String[] tip1 = new String[daygap + 1];
		String[] tip2 = new String[daygap + 1];
		

		Document doc = Jsoup.parse(new URL(
				"http://weather.media.daum.net/?pageId=2006").openConnection()
				.getInputStream(), "UTF-8", "http://127.0.0.1:8080/");
		
		for (int i = 0; i < daygap + 1; i++) {
			String[] value1 = JsoupParsing(doc,region1, district1,
					futureday[i]);
			temp = Float.parseFloat(value1[0]);
			state = value1[1];
			rain = Integer.parseInt(value1[2]);
			
			String[] value2 = JsoupParsing(doc,region2, district2,
					futureday[i]);
			temp2 = Float.parseFloat(value2[0]);
			state2 =value2[1];
			rain2 = Integer.parseInt(value2[2]);

			if (temp < temp2) {
				tip1[i] = district1 + " 보다 " + district2 + "이(가) 더울것으로 예상됩니다."
						+ "<br>" + "반팔과 반바지를 챙기세요 !!";
			} else if (temp > temp2) {
				tip1[i] = district1 + " 보다 " + district2 + "이(가) 선선할것으로 예상됩니다."
						+ "<br>" + "밤낮 기온차를 대비하여 가디건을 준비하세요 !!";
			} else{
				tip1[i] = district1 + "와(과)"+ district2 + "의 온도는 비슷합니다."
						+"<br>" + "오늘의" +district1 +"옷차림처럼 입으시면 됩니다 !! ";
			}

			if (rain2 > 50) {
				tip2[i] = district2 + " : 비올 확률이 높습니다." + "<br>"
						+ "작은 우산 하나를 가지고 가도록 하세요 !! ";
			} else {
				tip2[i] = district2 + " : 비올 확률이 낮습니다." + "<br>"
						+ "우산은 신경 쓰시지 않아도 됩니다.";
			}
			System.out.println(tip1[i]);
			System.out.println(tip2[i]);
			model.addAttribute("tip1", tip1);
			model.addAttribute("tip2", tip2);
		}
		return model;
	}

	public String[] JsoupParsing(Document doc,String region, String district, String day)
			throws MalformedURLException, IOException {

		String[] returnvalue = new String[3];

		String temp;
		float temp2;
		int rain, rain2;
		String winddir, winddir2;
		String state, state2;
		float windspeed, windspeed2;
		int humidity, humidity2;

		Elements elems11 = doc.select("METRO[NAME=" + region + "]");
		Elements elems12 = elems11.select("REGION[NAME=" + district + "]");
		Elements elems13 = elems12.select("FORECAST");
		Elements elems14 = elems13.select("DAY[DATE=" + day + "]");
		System.out.println(district + day);
		Elements findtemp = elems14.select("PMTEMP");
		String stringtemp = findtemp.html();
		if (stringtemp.equals("-")) {
			stringtemp = "20";
		}
		 System.out.println("temp: " + stringtemp);
		returnvalue[0] = stringtemp;

		Elements findstate = elems14.select("PMWTEXT");
		state = findstate.html();
		 System.out.println("state: "+state);
		returnvalue[1] = state;

		Elements findrain = elems14.select("PMRAIN");
		String stringrain = findrain.html();
		if (stringrain.equals("-")) {
			stringrain = "10";
		}
		 System.out.println("rain: " + stringrain);
		returnvalue[2] = stringrain;

		return returnvalue;
	}
}
package net.daum.younin;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping(value = "/weather")
public class WeatherController {

	int minDegree = 99;

	@RequestMapping(value = "/travel", method = RequestMethod.GET)
	public String getWeather(
			Model model,
			@RequestParam(value = "district1", defaultValue = "제주도") String region1,
			@RequestParam(value = "district2", defaultValue = "제주") String region2) throws MalformedURLException, IOException {

		ArrayList region = new ArrayList();
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

		System.out.println(region);
		System.out.println(region1);
		System.out.println(region2);

		model.addAttribute("region1", region1);
		model.addAttribute("region2", region2);
		
		java.util.Date d = new java.util.Date();
		System.out.println("date: " + d);
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		String yymmdd = df.format(d);
		System.out.println(yymmdd);
		model.addAttribute("yymmdd",yymmdd);
		
		
		
		Document doc = Jsoup.parse(new URL(
				"http://weather.media.daum.net/?pageId=2006").openConnection()
				.getInputStream(), "UTF-8", "http://127.0.0.1:8080/");
		double[] avg = new double[12];

		Elements elems1 = doc.select("METRO[NAME=" + region1 + "]");
		Elements elems2 = elems1.select("REGION[NAME=" + region2 + "]");

		Elements elems = elems2.select("NOW");

		Elements findtemp = elems.select("SENSTEMP");
		String temp = findtemp.html();
		System.out.println("temp: " + temp);
		model.addAttribute("temp", temp);

		Elements findstate = elems.select("WTEXT");
		String state = findstate.html();
		model.addAttribute("state", state);

//		model.addAttribute("image", image(state));
		model.addAttribute("WeatherMent", WeatherMent(state));

		Elements findrain = elems.select("RAIN");
		String rain = findrain.html();
		System.out.println("rain: " + rain);

		Elements findwinddir = elems.select("WIND_DIR");
		String winddir = findwinddir.html();
		System.out.println("winddir: " + winddir);

		Elements findwindspeed = elems.select("WIND_SPEED");
		String windspeed = findwindspeed.html();
		System.out.println("windspeed: " + windspeed);

		Elements findhumidity = elems.select("HUMIDITY");
		String humidity = findhumidity.html();
		System.out.println("humidity: " + humidity);

		// 오늘 날씨
		Elements factor1 = elems2.select("FORECAST");
		Elements factor2 = factor1.select("DAY[DATE=" + yymmdd + "]");

		Elements findamtext = factor2.select("AMCOMMENT");
		String amtext = findamtext.html();
		System.out.println("amtext: " + amtext);

		Elements findpmtext = factor2.select("PMCOMMENT");
		String pmtext = findpmtext.html();
		System.out.println("pmtext: " + pmtext);
		model.addAttribute("pmtext", pmtext);

		Elements findamtemp = factor2.select("AMTEMP");
		String amtemp = findamtemp.html();
		// int am = Integer.parseInt(amtemp);
		System.out.println("amtemp: " + amtemp);

		Elements findpmtemp = factor2.select("PMTEMP");
		String pmtemp = findpmtemp.html();
		int pm = 0;
		if (pmtemp.equals("-")) {
			pm = 22;
		} else {
			pm = Integer.parseInt(pmtemp);
		}
		System.out.println("pmtemp: " + pmtemp);

		String[] adviceList = {
				"여행을 통해 인간은 겸손해진다. 세상에서 인간이라는 존재가 차지하는 비중이 얼마나 하찮은가를 절실히 깨닫게 해주기 때문이다.",
				"이 세상은 책이다. 여행을 하지 않는 사람은 한페이지만을 계속 보는 사람과 같다.",
				"어두운 들녘에 나가 혼자 사 보라 달빛과 별이 왜 홀로 스스로 빛나는지 생각해 보라.",
				"낯선 땅이란 없다. 단지 여행자가 낯설 뿐이다.", "여행과 장소의 변화는 정신에 활력을 준다.",
				"낯선 마을에서 홀로 깨어난다는 건 세상에서 가장 기분좋은 기분이다.", "여행은 끝났는데 길은 시작됐다.",
				"진정한 여행의 발견은 새로운 풍경을 보는것이 아니라 새로운 눈을 갖는 것이다.",
				"여행은 언제나 돈의 문제가 아니라 용기의 문제다.", "바보는 방황하고 현명한 사람은 여행을 한다.",
				"당신의 인생에서 가장 중요한 여행은 여행중 사람을 만나는 여행이다.",
				"또 다른 세상에서, 또 다른 나를 만나, 또 다른 삶을 꿈꾸다.", "행복을 원한다면 때때로 여행을 떠나라.",
				"여행과 변화를 사랑하는 사람은 생명이 있는 사람이다." };

		int random = (int) (Math.random() * adviceList.length);
		String advice = adviceList[random];
		System.out.println(advice);
		model.addAttribute("advice", advice);

		return "/weather/travel";
	}

	@RequestMapping(value = "/flight", method = RequestMethod.GET)
	public String getFlight(
			Model model,
			@RequestParam(value = "district1", defaultValue = "인천") String region1,
			@RequestParam(value = "district2", defaultValue = "제주") String region2) {

		ArrayList airport = new ArrayList();
//		airport.add("김포");
		airport.add("인천");
//		airport.add("원주");
//		airport.add("양양");
//		airport.add("예천");
		airport.add("청주");
//		airport.add("군산");
		airport.add("대구");
		airport.add("포항");
//		airport.add("광주");
//		airport.add("목포");
//		airport.add("여수");
//		airport.add("사천");
		airport.add("울산");
//		airport.add("김해");
		airport.add("제주");

		System.out.println(region1);
		System.out.println(region2);
		
		java.util.Date d = new java.util.Date();
		System.out.println("date: " + d);
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		String yymmdd = df.format(d);
		System.out.println(yymmdd);
		model.addAttribute("yymmdd",yymmdd);
		
		Calendar cal = Calendar.getInstance();
		int year = cal.get(cal.YEAR);
		// System.out.println(year + "년");
		int month = cal.get(cal.MONTH) + 1;
		// System.out.println(month + "월");
		int date = cal.get(cal.DATE);
		// System.out.println(date + "일");
		
		String[] weeks = { "일", "월", "화", "수", "목", "금", "토" };
		cal.set(year, month - 1, date);
		int today = cal.get(cal.DAY_OF_WEEK);
		System.out.println("오늘은 " + weeks[today - 1] + "요일");
		model.addAttribute("day",weeks[today - 1]);

		return "/weather/flight";
	}

	@RequestMapping(value = "/custom", method = RequestMethod.GET)
	public String Weather(Model model) {
		java.util.Calendar cal = java.util.Calendar.getInstance();
		int year = cal.get(cal.YEAR);
		int month = cal.get(cal.MONTH) + 1;
		int date = cal.get(cal.DATE);
		// System.out.println("몇월? : "+month);
		if (month == 1 || (month >= 11 && month <= 12)) {
			String checkweather = "Q. 지금 춥나요 ?? ";
			model.addAttribute("checkweather", checkweather);
		} else {
			String checkweather = "Q. 지금 덥나요 ?? ";
			model.addAttribute("checkweather", checkweather);
		}

		return "/weather/weather";
	}

	@RequestMapping(value = "/home", method = RequestMethod.GET)
	public String Home(
			Model model,
			@RequestParam(value = "district1", defaultValue = "서울/경기") String district1,
			@RequestParam(value = "district2", defaultValue = "서울") String district2,
			@RequestParam(value = "hot", defaultValue = "3") String hot)
			throws IOException {

		ArrayList region = new ArrayList();
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

		System.out.println(region);
		System.out.println(district1);
		System.out.println(district2);

		model.addAttribute("district1", district1);
		model.addAttribute("district2", district2);

		// int districtNum = region.indexOf(district);

		java.util.Date d = new java.util.Date();
		System.out.println("date: " + d);
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		String yymmdd = df.format(d);
		System.out.println(yymmdd);

		Document doc = Jsoup.parse(new URL(
				"http://weather.media.daum.net/?pageId=2006").openConnection()
				.getInputStream(), "UTF-8", "http://127.0.0.1:8080/");

		Elements elems1 = doc.select("METRO[NAME=" + district1 + "]");
		Elements elems2 = elems1.select("REGION[NAME=" + district2 + "]");
		Elements elems = elems2.select("NOW");

		Elements findtemp = elems.select("SENSTEMP");
		String temp = findtemp.html();
		System.out.println("temp: " + temp);
		model.addAttribute("temp", temp);

		Elements findstate = elems.select("WTEXT");
		String state = findstate.html();
		model.addAttribute("state", state);

		if (state.contains("맑음")) {
			String image = "http://cfile25.uf.tistory.com/image/14233F214A961E025AB87E";
			model.addAttribute("image", image);
			String WeatherMent = "오늘은 맑은 날씨가 예상됩니다.";
			model.addAttribute("WeatherMent", WeatherMent);

		} else if (state.contains("비") || state.contains("소나기")
				|| state.contains("천둥번개")) {
			String image = "http://cfile234.uf.daum.net/image/120ABE3F4FF65CCA17170F";
			model.addAttribute("image", image);
			String WeatherMent = "오늘은 비가 올것 같네요.";
			model.addAttribute("WeatherMent", WeatherMent);
		} else if (state.contains("흐") || state.contains("안개")) {
			String image = "http://cfile7.uf.tistory.com/image/2630B84853BEBC6135B52E";
			model.addAttribute("image", image);
			String WeatherMent = "오늘은 흐린 날씨가 예상됩니다.";
			model.addAttribute("WeatherMent", WeatherMent);
		} else if (state.contains("구름")) {
			String image = "http://www.thecontest.co.kr/common/download.php?ftype=content&fid=7285";
			model.addAttribute("image", image);
			String WeatherMent = "오늘은 구름이 조금 예상됩니다.";
			model.addAttribute("WeatherMent", WeatherMent);
		} else if (state.contains("눈")) {
			String image = "http://termcoord.files.wordpress.com/2013/03/snow.jpg";
			model.addAttribute("image", image);
			String WeatherMent = "오늘은 눈이 내릴것 같네요";
			model.addAttribute("WeatherMent", WeatherMent);
		} else {
			String image = "http://images.iop.org/objects/phw/news/thumb/17/9/7/clouds.jpg";
			model.addAttribute("image", image);
			String WeatherMent = "오늘은 맑은 날씨가 예상됩니다.";
			model.addAttribute("WeatherMent", WeatherMent);
		}

		System.out.println("state: " + state);

		Elements findrain = elems.select("RAIN");
		String rain = findrain.html();
		System.out.println("rain: " + rain);

		Elements findwinddir = elems.select("WIND_DIR");
		String winddir = findwinddir.html();
		System.out.println("winddir: " + winddir);

		Elements findwindspeed = elems.select("WIND_SPEED");
		String windspeed = findwindspeed.html();
		System.out.println("windspeed: " + windspeed);

		Elements findhumidity = elems.select("HUMIDITY");
		String humidity = findhumidity.html();
		System.out.println("humidity: " + humidity);

		// 오늘 날씨
		Elements factor1 = elems2.select("FORECAST");
		Elements factor2 = factor1.select("DAY[DATE=" + yymmdd + "]");

		Elements findamtext = factor2.select("AMCOMMENT");
		String amtext = findamtext.html();
		System.out.println("amtext: " + amtext);

		Elements findpmtext = factor2.select("PMCOMMENT");
		String pmtext = findpmtext.html();
		System.out.println("pmtext: " + pmtext);
		model.addAttribute("pmtext", pmtext);

		Elements findamtemp = factor2.select("AMTEMP");
		String amtemp = findamtemp.html();
		// int am = Integer.parseInt(amtemp);
		System.out.println("amtemp: " + amtemp);

		Elements findpmtemp = factor2.select("PMTEMP");
		String pmtemp = findpmtemp.html();
		int pm = 0;
		if (pmtemp.equals("-")) {
			pm = 22;
		} else {
			pm = Integer.parseInt(pmtemp);
		}
		System.out.println("pmtemp: " + pmtemp);

		// forecast
		Calendar cal = Calendar.getInstance();
		String todaydate = df.format(cal.getTime());
		String tomorrow = "";
		int[] tmpm = new int[6];
		int[] tmam = new int[6];
		String[] amstate = new String[6];
		String[] pmstate = new String[6];

		for (int i = 0; i < 6; i++) {
			long chStart = 0;
			// 내일 날짜 구하기
			try {
				chStart = df.parse(todaydate).getTime(); // 스트링형 date를 long형의
															// 함수로
															// 컨버트
				chStart += 86400000 * (i + 1); // 24*60*60*1000 하루치의 숫자를 더준다
				Date day = new Date(chStart); // 다시 날짜형태로 바꿔주고
				tomorrow = df.format(day); // 바꿔준 날짜를 yyyyMMdd형으로 변환
				System.out.println(tomorrow);

			} catch (Exception ex) {
				ex.printStackTrace();
			}

			Elements tmfactor1 = elems2.select("FORECAST");
			Elements tmfactor2 = tmfactor1.select("DAY[DATE=" + tomorrow + "]");

			// AMTEMP , PMTEMP (오늘 / 내일)
			if (i == 0) {
				Elements findtmpmtemp = tmfactor2.select("PMTEMP");
				String tmpmtemp = findtmpmtemp.html();
				if (tmpmtemp.equals("-")) {
					continue;
				} else {
					tmpm[i] = Integer.parseInt(tmpmtemp);
				}
				// System.out.println("tmpm: " + tmpm[i]);
			} else {
				Elements findmaxtemp = tmfactor2.select("MAXTEMP");
				String maxtemp = findmaxtemp.html();
				if (maxtemp.equals("-")) {
					continue;
				} else {
					tmpm[i] = Integer.parseInt(maxtemp);
				}
				// System.out.println("maxtemp: " + tmpm[i]);

			}

			if (i == 0) {
				if (pm == tmpm[i]) {
					String ment = "내일은 오늘과 기온이 같음";
					model.addAttribute("ment", ment);
					// System.out.println(ment);
				} else if (pm < tmpm[i]) {
					int gap = (tmpm[i] - pm);
					String ment = "내일은 오늘보다 " + gap + "도 높음";
					model.addAttribute("ment", ment);
					// System.out.println(ment);
				} else {
					int gap = (pm - tmpm[i]);
					String ment = "내일은 오늘보다 " + gap + "도 낮음";
					model.addAttribute("ment", ment);
					// System.out.println(ment);
				}
			}
			model.addAttribute("tmpm", tmpm);

			Elements findamstate = tmfactor2.select("AMWTEXT");
			amstate[i] = findamstate.html();
			// System.out.println("amstate : " + amstate[i]);

			Elements findpmstate = tmfactor2.select("PMWTEXT");
			pmstate[i] = findpmstate.html();
			// System.out.println("pmstate : " + pmstate[i]);

			model.addAttribute("amstate", amstate);
			model.addAttribute("pmstate", pmstate);

		}

		int year = cal.get(cal.YEAR);
		// System.out.println(year + "년");
		int month = cal.get(cal.MONTH) + 1;
		// System.out.println(month + "월");
		int date = cal.get(cal.DATE);
		// System.out.println(date + "일");

		int[] dateArray = new int[7];
		dateArray[0] = date;
		dateArray[1] = date + 1;
		dateArray[2] = date + 2;
		dateArray[3] = date + 3;
		dateArray[4] = date + 4;
		dateArray[5] = date + 5;
		dateArray[6] = date + 6;
		model.addAttribute("date", dateArray);

		String[] weeks = { "일", "월", "화", "수", "목", "금", "토" };
		cal.set(year, month - 1, date);
		int today = cal.get(cal.DAY_OF_WEEK);
		System.out.println("오늘은 " + weeks[today - 1] + "요일");
		System.out.println(today - 1);

		String[] DayArray = new String[7];
		int[] array = new int[7];
		for (int i = 0; i < 7; i++) {
			cal.set(year, month - 1, date + (i));
			array[i] = cal.get(cal.DAY_OF_WEEK);
			// System.out.println(weeks[array[i] - 1] + "요일");
			DayArray[i] = weeks[array[i] - 1];
			model.addAttribute("day", DayArray);
		}

		String[] iconArray = new String[6];
		String[] amicon = new String[6];
		String[] pmicon = new String[6];
		iconArray[0] = "https://d30y9cdsu7xlg0.cloudfront.net/png/2660-84.png"; // 맑음
																				// 아이콘
		iconArray[1] = "https://d30y9cdsu7xlg0.cloudfront.net/png/2621-84.png"; // 비
																				// 아이콘
		iconArray[2] = "https://d30y9cdsu7xlg0.cloudfront.net/png/7739-84.png"; // 흐림
																				// 아이콘
		iconArray[3] = "https://d30y9cdsu7xlg0.cloudfront.net/png/2632-84.png"; // 구름
																				// 조금
																				// 아이콘
		iconArray[4] = "https://d30y9cdsu7xlg0.cloudfront.net/png/11697-84.png"; // 눈
																					// 아이콘
		iconArray[5] = "https://d30y9cdsu7xlg0.cloudfront.net/png/9519-84.png";// 구름
																				// 많음
																				// 아이콘

		if (state.contains("맑")) {
			String icon = iconArray[0];
			model.addAttribute("todayicon", icon);
		} else if (state.contains("비") || state.contains("소나기")
				|| state.contains("천둥번개")) {
			String icon = iconArray[1];
			model.addAttribute("todayicon", icon);
		} else if (state.contains("흐") || state.contains("안개")) {
			String icon = iconArray[2];
			model.addAttribute("todayicon", icon);
		} else if (state.contains("조금")) {
			String icon = iconArray[3];
			model.addAttribute("todayicon", icon);
		} else if (state.contains("눈")) {
			String icon = iconArray[4];
			model.addAttribute("todayicon", icon);
		} else if (state.contains("많음")) {
			String icon = iconArray[5];
			model.addAttribute("todayicon", icon);
		}

		for (int i = 0; i < 6; i++) {
			if (amstate[i].contains("맑")) {
				String icon = iconArray[0];
				amicon[i] = icon;
			} else if (amstate[i].contains("비") || amstate[i].contains("소나기")
					|| amstate[i].contains("천둥번개")) {
				String icon = iconArray[1];
				amicon[i] = icon;
			} else if (amstate[i].contains("흐") || amstate[i].contains("안개")) {
				String icon = iconArray[2];
				amicon[i] = icon;
			} else if (amstate[i].contains("조금")) {
				String icon = iconArray[3];
				amicon[i] = icon;
			} else if (amstate[i].contains("비")) {
				String icon = iconArray[4];
				amicon[i] = icon;
			} else if (amstate[i].contains("많음")) {
				String icon = iconArray[5];
				amicon[i] = icon;
			}
			// System.out.println(amicon[i]);
		}
		model.addAttribute("amicon", amicon);

		for (int i = 0; i < 6; i++) {
			if (pmstate[i].contains("맑")) {
				String icon = iconArray[0];
				pmicon[i] = icon;
			} else if (pmstate[i].contains("비") || pmstate[i].contains("소나기")
					|| pmstate[i].contains("천둥번개")) {
				String icon = iconArray[1];
				pmicon[i] = icon;
			} else if (pmstate[i].contains("흐") || pmstate[i].contains("안개")) {
				String icon = iconArray[2];
				pmicon[i] = icon;
			} else if (pmstate[i].contains("조금")) {
				String icon = iconArray[3];
				pmicon[i] = icon;
			} else if (pmstate[i].contains("비")) {
				String icon = iconArray[4];
				pmicon[i] = icon;
			} else if (pmstate[i].contains("많음")) {
				String icon = iconArray[5];
				pmicon[i] = icon;
			}
			// System.out.println(pmicon[i]);
		}
		model.addAttribute("pmicon", pmicon);

		return "weather/home";
	}

	@RequestMapping({"/parallax", "/"})
	public String parallax(
			Model model,
			@RequestParam(value = "district1", defaultValue = "제주도") String district1,
			@RequestParam(value = "district2", defaultValue = "제주") String district2)
			throws IOException {

		ArrayList region = new ArrayList();
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

		System.out.println(region);
		System.out.println(district1);
		System.out.println(district2);

		model.addAttribute("district1", district1);
		model.addAttribute("district2", district2);

		// int districtNum = region.indexOf(district);

		java.util.Date d = new java.util.Date();
		System.out.println("date: " + d);
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		String yymmdd = df.format(d);
		System.out.println(yymmdd);

		Document doc = Jsoup.parse(new URL(
				"http://weather.media.daum.net/?pageId=2006").openConnection()
				.getInputStream(), "UTF-8", "http://127.0.0.1:8080/");
		double[] avg = new double[12];
		for (int i = 0; i < 12; i++) {
			double sum = 0;
			int[] count = new int[12];
			Object regionname = region.get(i);
			System.out.println(regionname);

			Elements metro = doc.select("METRO[NAME=" + regionname + "]");
			Elements city = metro.select("REGION");
			count[i] = city.size();

			for (int j = 0; j < count[i]; j++) {
				Element test = city.get(j);
				String cityname = test.attr("NAME");
				Elements thiscity = metro.select("REGION[NAME=" + cityname
						+ "]");
				Elements now = thiscity.select("FORECAST");
				Elements date = now.select("DAY[DATE =" + yymmdd + "]");
				
				Elements sensetemp = date.select("PMTEMP");
				String temp = sensetemp.html();
				int temp2;
				if (temp.equals("-") || temp.equals("")) {
					temp2 = 22;
				} else {
					temp2 = Integer.parseInt(temp);
				}
				sum += temp2;
			}
			avg[i] = (int) (sum / count[i]);
			System.out.println("평균 기온: " + avg[i]);
		}
		model.addAttribute("avg", avg);

		Elements elems1 = doc.select("METRO[NAME=" + district1 + "]");
		Elements elems2 = elems1.select("REGION[NAME=" + district2 + "]");

		Elements elems = elems2.select("NOW");
		Elements notexist = elems2.select("FORECAST");
		Elements notexist2 = notexist.select("DAY[DATE =" + yymmdd + "]");

		Elements findtemp = elems.select("SENSTEMP");
		String temp = findtemp.html();
		if(temp.contains("-")||temp.equals("")){
			Elements findtemp2 = notexist2.select("PMTEMP");
			temp = findtemp2.html();
		}
		System.out.println("temp: " + temp);
		model.addAttribute("temp", temp);

		Elements findstate = elems.select("WTEXT");
		String state = findstate.html();
		if(state.contains("-")||state.equals("")){
			Elements findstate2 = notexist2.select("PMWTEXT");
			state = findstate2.html();
		}
		System.out.println("state: "+ state);
		model.addAttribute("state", state);
		
		//배경 이미지 선택 !! 
		int[] randomNumber = new int[100];
		randomNumber[0] = -1;
		int count = 0;
		count += 1;
		model.addAttribute("image", image(randomNumber,count,state));
		
		model.addAttribute("WeatherMent", WeatherMent(state));

		Elements findrain = elems.select("RAIN");
		String rain = findrain.html();
		System.out.println("rain: " + rain);

		Elements findwinddir = elems.select("WIND_DIR");
		String winddir = findwinddir.html();
		System.out.println("winddir: " + winddir);

		Elements findwindspeed = elems.select("WIND_SPEED");
		String windspeed = findwindspeed.html();
		System.out.println("windspeed: " + windspeed);

		Elements findhumidity = elems.select("HUMIDITY");
		String humidity = findhumidity.html();
		System.out.println("humidity: " + humidity);

		// 오늘 날씨
		Elements factor1 = elems2.select("FORECAST");
		Elements factor2 = factor1.select("DAY[DATE=" + yymmdd + "]");

		Elements findamtext = factor2.select("AMCOMMENT");
		String amtext = findamtext.html();
		System.out.println("amtext: " + amtext);

		Elements findpmtext = factor2.select("PMCOMMENT");
		String pmtext = findpmtext.html();
		System.out.println("pmtext: " + pmtext);
		model.addAttribute("pmtext", pmtext);

		Elements findamtemp = factor2.select("AMTEMP");
		String amtemp = findamtemp.html();
		// int am = Integer.parseInt(amtemp);
		System.out.println("amtemp: " + amtemp);

		Elements findpmtemp = factor2.select("PMTEMP");
		String pmtemp = findpmtemp.html();
		int pm = 0;
		if (pmtemp.equals("-")) {
			pm = 22;
		} else {
			pm = Integer.parseInt(pmtemp);
		}
		System.out.println("pmtemp: " + pmtemp);

		// forecast
		Calendar cal = Calendar.getInstance();
		String todaydate = df.format(cal.getTime());
		String tomorrow = "";
		int[] tmpm = new int[6];
		int[] tmam = new int[6];
		String[] amstate = new String[6];
		String[] pmstate = new String[6];
		String[] futureimage = new String[6];
		String[] futurement = new String[6];

		for (int i = 0; i < 6; i++) {
			long chStart = 0;
			// 내일 날짜 구하기
			try {
				chStart = df.parse(todaydate).getTime(); // 스트링형 date를 long형의
															// 함수로
															// 컨버트
				chStart += 86400000 * (i + 1); // 24*60*60*1000 하루치의 숫자를 더준다
				Date day = new Date(chStart); // 다시 날짜형태로 바꿔주고
				tomorrow = df.format(day); // 바꿔준 날짜를 yyyyMMdd형으로 변환
				System.out.println(tomorrow);

			} catch (Exception ex) {
				ex.printStackTrace();
			}

			Elements tmfactor1 = elems2.select("FORECAST");
			Elements tmfactor2 = tmfactor1.select("DAY[DATE=" + tomorrow + "]");

			Elements findweatherment = tmfactor2.select("PMWTEXT");
			String weatherment = findweatherment.html();
			count+=1;
			futureimage[i] = image(randomNumber,count,weatherment);
			futurement[i] = WeatherMent(weatherment);

			// AMTEMP , PMTEMP (오늘 / 내일)
			if (i == 0) {
				Elements findtmpmtemp = tmfactor2.select("PMTEMP");
				String tmpmtemp = findtmpmtemp.html();
				if (tmpmtemp.equals("-")) {
					continue;
				} else {
					tmpm[i] = Integer.parseInt(tmpmtemp);
				}
				// System.out.println("tmpm: " + tmpm[i]);
			} else {
				Elements findmaxtemp = tmfactor2.select("MAXTEMP");
				String maxtemp = findmaxtemp.html();
				if (maxtemp.equals("-")) {
					continue;
				} else {
					tmpm[i] = Integer.parseInt(maxtemp);
				}
				// System.out.println("maxtemp: " + tmpm[i]);

			}

			if (i == 0) {
				if (pm == tmpm[i]) {
					String ment = "내일은 오늘과 기온이 같음";
					model.addAttribute("ment", ment);
					// System.out.println(ment);
				} else if (pm < tmpm[i]) {
					int gap = (tmpm[i] - pm);
					String ment = "내일은 오늘보다 " + gap + "도 높음";
					model.addAttribute("ment", ment);
					// System.out.println(ment);
				} else {
					int gap = (pm - tmpm[i]);
					String ment = "내일은 오늘보다 " + gap + "도 낮음";
					model.addAttribute("ment", ment);
					// System.out.println(ment);
				}
			}
			model.addAttribute("tmpm", tmpm);

			Elements findamstate = tmfactor2.select("AMWTEXT");
			amstate[i] = findamstate.html();
			// System.out.println("amstate : " + amstate[i]);

			Elements findpmstate = tmfactor2.select("PMWTEXT");
			pmstate[i] = findpmstate.html();
			// System.out.println("pmstate : " + pmstate[i]);

			model.addAttribute("amstate", amstate);
			model.addAttribute("pmstate", pmstate);
			model.addAttribute("futureimage", futureimage);
			model.addAttribute("futurement", futurement);
		}

		int year = cal.get(cal.YEAR);
		// System.out.println(year + "년");
		int month = cal.get(cal.MONTH) + 1;
		// System.out.println(month + "월");
		int date = cal.get(cal.DATE);
		// System.out.println(date + "일");

		int[] dateArray = new int[7];
		dateArray[0] = date;
		dateArray[1] = date + 1;
		dateArray[2] = date + 2;
		dateArray[3] = date + 3;
		dateArray[4] = date + 4;
		dateArray[5] = date + 5;
		dateArray[6] = date + 6;
		model.addAttribute("date", dateArray);

		String[] weeks = { "일", "월", "화", "수", "목", "금", "토" };
		cal.set(year, month - 1, date);
		int today = cal.get(cal.DAY_OF_WEEK);
		System.out.println("오늘은 " + weeks[today - 1] + "요일");
		System.out.println(today - 1);

		String[] DayArray = new String[7];
		int[] array = new int[7];
		for (int i = 0; i < 7; i++) {
			cal.set(year, month - 1, date + (i));
			array[i] = cal.get(cal.DAY_OF_WEEK);
			// System.out.println(weeks[array[i] - 1] + "요일");
			DayArray[i] = weeks[array[i] - 1];
			model.addAttribute("day", DayArray);
		}

		String[] iconArray = new String[6];
		String[] amicon = new String[6];
		String[] pmicon = new String[6];
		iconArray[0] = "https://d30y9cdsu7xlg0.cloudfront.net/png/2660-84.png"; // 맑음
																				// 아이콘
		iconArray[1] = "https://d30y9cdsu7xlg0.cloudfront.net/png/2621-84.png"; // 비
																				// 아이콘
		iconArray[2] = "https://d30y9cdsu7xlg0.cloudfront.net/png/7739-84.png"; // 흐림
																				// 아이콘
		iconArray[3] = "https://d30y9cdsu7xlg0.cloudfront.net/png/2632-84.png"; // 구름
																				// 조금
																				// 아이콘
		iconArray[4] = "https://d30y9cdsu7xlg0.cloudfront.net/png/11697-84.png"; // 눈
																					// 아이콘
		iconArray[5] = "https://d30y9cdsu7xlg0.cloudfront.net/png/9519-84.png";// 구름
																				// 많음
																				// 아이콘

		if (state.contains("맑")) {
			String icon = iconArray[0];
			model.addAttribute("todayicon", icon);
		} else if (state.contains("비") || state.contains("소나기")
				|| state.contains("천둥번개")) {
			String icon = iconArray[1];
			model.addAttribute("todayicon", icon);
		} else if (state.contains("흐") || state.contains("안개")) {
			String icon = iconArray[2];
			model.addAttribute("todayicon", icon);
		} else if (state.contains("조금")) {
			String icon = iconArray[3];
			model.addAttribute("todayicon", icon);
		} else if (state.contains("눈")) {
			String icon = iconArray[4];
			model.addAttribute("todayicon", icon);
		} else if (state.contains("많음")) {
			String icon = iconArray[5];
			model.addAttribute("todayicon", icon);
		}

		for (int i = 0; i < 6; i++) {
			if (amstate[i].contains("맑")) {
				String icon = iconArray[0];
				amicon[i] = icon;
			} else if (amstate[i].contains("비") || amstate[i].contains("소나기")
					|| amstate[i].contains("천둥번개")) {
				String icon = iconArray[1];
				amicon[i] = icon;
			} else if (amstate[i].contains("흐") || amstate[i].contains("안개")) {
				String icon = iconArray[2];
				amicon[i] = icon;
			} else if (amstate[i].contains("조금")) {
				String icon = iconArray[3];
				amicon[i] = icon;
			} else if (amstate[i].contains("비")) {
				String icon = iconArray[4];
				amicon[i] = icon;
			} else if (amstate[i].contains("많음")) {
				String icon = iconArray[5];
				amicon[i] = icon;
			}
			// System.out.println(amicon[i]);
		}
		model.addAttribute("amicon", amicon);

		for (int i = 0; i < 6; i++) {
			if (pmstate[i].contains("맑")) {
				String icon = iconArray[0];
				pmicon[i] = icon;
			} else if (pmstate[i].contains("비") || pmstate[i].contains("소나기")
					|| pmstate[i].contains("천둥번개")) {
				String icon = iconArray[1];
				pmicon[i] = icon;
			} else if (pmstate[i].contains("흐") || pmstate[i].contains("안개")) {
				String icon = iconArray[2];
				pmicon[i] = icon;
			} else if (pmstate[i].contains("조금")) {
				String icon = iconArray[3];
				pmicon[i] = icon;
			} else if (pmstate[i].contains("비")) {
				String icon = iconArray[4];
				pmicon[i] = icon;
			} else if (pmstate[i].contains("많음")) {
				String icon = iconArray[5];
				pmicon[i] = icon;
			}
			// System.out.println(pmicon[i]);
		}
		model.addAttribute("pmicon", pmicon);

		return "/weather/index";
	}

	public String image(int[] randomNumber,int count,String a) {
		
		String image = "";
		
		ArrayList image1 = new ArrayList();
		image1.add("http://www.kepco.pe.kr/photo01/3092124.jpg");
		image1.add("http://cfile25.uf.tistory.com/image/14233F214A961E025AB87E");
		image1.add("http://cfile8.uf.tistory.com/image/2613FD4652526675228EB7");
		image1.add("http://3.bp.blogspot.com/-xQjX0_ivcQA/UlGDq6NZ7JI/AAAAAAAAAHM/Xa5KCXeAROc/s1600/%EA%B0%80%EC%9D%84%ED%95%98%EB%8A%982.jpg");
		image1.add("http://cfile27.uf.tistory.com/image/2558D138523FB4C716DDD7");
		image1.add("http://cfile23.uf.tistory.com/image/1249554B4FB20A4227E69A");
		image1.add("http://cfile3.uf.tistory.com/image/275F434C5350B5B53F82CA");
		image1.add("http://cfile3.uf.tistory.com/image/13177A385061BFDA2D17FB");
		image1.add("http://cfs9.tistory.com/upload_control/download.blog?fhandle=YmxvZzk3NjA2QGZzOS50aXN0b3J5LmNvbTovYXR0YWNoLzIvMjc3LmpwZw%3D%3D");
		image1.add("http://cfile9.uf.tistory.com/image/2311824651B184CB1D0C99");
		
		ArrayList image2 = new ArrayList();
		image2.add("http://cfile28.uf.tistory.com/image/2716CF45532DE907039EF3");
		image2.add("http://cfile24.uf.tistory.com/image/170AF835515FA8B3255978");
		image2.add("http://cfile1.uf.tistory.com/image/24513E4B535FA7840F648A");
		image2.add("http://cfs10.tistory.com/image/21/tistory/2008/12/04/10/50/49373767edca8");
		image2.add("http://wow.seoul.go.kr/upload/wowkit/2013/09/11/20130911_2235_48567_logo.JPG");
		image2.add("http://cfile2.uf.tistory.com/image/2055FB214C842FD16F7B98");
		image2.add("http://cfile6.uf.tistory.com/image/172AB52E4CC060A96356AC");
		image2.add("http://cfile23.uf.tistory.com/image/1677064F4EA7A53D25B0FE");
		image2.add("http://cfile9.uf.tistory.com/image/226B5D4B5428EC7C0FB27E");
		image2.add("http://blog.joins.com/usr/w/h/whitebee1/8/200707_693.jpg");
		
		ArrayList image3 = new ArrayList();
		image3.add("http://cfile7.uf.tistory.com/image/2630B84853BEBC6135B52E");
		image3.add("http://cfs5.blog.daum.net/image/7/blog/2007/06/12/13/51/466e2665ec267&filename=SG101824b.jpg");
		image3.add("http://cfile233.uf.daum.net/image/236E2C4552633E731FF648");
		image3.add("http://www.mybesthome.com/gallery2/main.php?g2_view=core.DownloadItem&g2_itemId=2364&g2_serialNumber=2");
		image3.add("http://toohappy.cafe24.com/attach/1/6605499904.jpg");
		image3.add("http://cfile27.uf.tistory.com/image/202EBD4C50BD53E23C7C45");
		image3.add("http://blog.joins.com/usr/z/e/zeonkt/10/08-nov-s-cloudy002.jpg");
		image3.add("http://c.ask.nate.com/imgs/qrsi.php/6266382/11282633/0/1/A/%ED%9D%90%EB%A6%B0%EB%82%A0.jpg");
		image3.add("http://cfile10.uf.tistory.com/image/162A200C4C2F69502B7607");
		image3.add("http://img2.dcinside.com/viewimage.php?id=ptztm&no=29bcc427b69d3fa762b3d3a70f9e3429495b258b12c8a297a4e5e89393939c2e141ca82f38ba421365c88e37f66287bb92c2f0a5e0175e3e27421e924062&f_no=3dbcc22ff6816ce864afd1");

		ArrayList image4 = new ArrayList();
		image4.add("http://scontent-a.cdninstagram.com/hphotos-xaf1/outbound-distilleryimage5/t0.0-17/OBPTH/02c8b10aae5911e3a3c71275c3100ced_8.jpg");
		image4.add("http://www.free5.kr/files/attach/images/8131/316/153/f93dbe96607fe6947935216103180bcf.jpg");
		image4.add("http://sinbd.com/index/_sys/_upload/image/201204/04/133351810682.jpg");
		image4.add("http://cfile1.uf.tistory.com/image/1957424D5009157335155C");
		image4.add("http://cfile24.uf.tistory.com/image/136CFA38502CD0581913FE");
		image4.add("http://cfile217.uf.daum.net/image/136738354FFD727323AB70");
		image4.add("http://cfile10.uf.tistory.com/image/113F4033501CE5E1313341");
		image4.add("http://cfile10.uf.tistory.com/image/22514F3D51DFED761BAAF9");
		image4.add("http://www.thecontest.co.kr/common/download.php?ftype=content&fid=4261");
		image4.add("http://www.thecontest.co.kr/common/download.php?ftype=content&fid=4265");
		
		ArrayList image5 = new ArrayList();
		image5.add("http://termcoord.files.wordpress.com/2013/03/snow.jpg");
		image5.add("https://encrypted-tbn2.gstatic.com/images?q=tbn:ANd9GcQXJDIFcbrmS0YGOIkEjyKLsGLI-Dl4ZZrpsuKpZ3YEaiycbulw6g");
		image5.add("http://cfile5.uf.tistory.com/image/22704D3752846D64141CE4");
		image5.add("http://cfile29.uf.tistory.com/image/185C82414D3C897109C554");
		image5.add("http://cfile227.uf.daum.net/image/12095404493BB540141399");
		image5.add("http://www.joysf.com/files/attach/images/2705510/2851643/Snowy.jpg");
		image5.add("http://mini-files.thinkpool.com/files/mini/2006/04/06/F1120025HH.jpg");
		image5.add("http://cfile240.uf.daum.net/image/226C2A455289C727036059");
		image5.add("http://cfile21.uf.tistory.com/image/163A5740500006E91951E9");
		image5.add("http://www.wikitree.co.kr/webdata/editor/201311/28/img_20131128114032_ec7eafc5.jpg");
		
		int randomNum = (int)(Math.random() * 10);
		
//		System.out.println("count : "+ count);
//		System.out.println("이전 번호 :" +randomNumber[count-1] );
//		System.out.println("현재 번호 :" + randomNum);
		if(randomNumber[count-1] == randomNum){
//			System.out.println("*********");
			while(randomNumber[count-1] == randomNum){
//				System.out.println("^^^^^^^^");
				randomNum = (int)(Math.random() * 5);
			}
			randomNumber[count] = randomNum;
//			System.out.println("그래서 : " + randomNumber[count]);
		}
		else{
			randomNumber[count] = randomNum;
		}
//		System.out.println("현재 번호: " + randomNumber[count]);
		for(int i=0; i<count+1; i++){
			System.out.println("<<< randomNumber :" + randomNumber[i]);
		}
		
		if (a.contains("맑음")) {
			image = (String) image1.get(randomNum);
		} else if (a.contains("비") || a.contains("소나기") || a.contains("천둥번개")) {
			image = (String) image2.get(randomNum);
		} else if (a.contains("흐") || a.contains("안개") || a.contains("박무")) {
			image = (String) image3.get(randomNum);
		} else if (a.contains("구름")) {
			image = (String) image4.get(randomNum);
		} else if (a.contains("눈")) {
			image = (String) image5.get(randomNum);
		} else {
			image = "http://images.iop.org/objects/phw/news/thumb/17/9/7/clouds.jpg";
		}
		
		System.out.println("image: "+image);
		
		
		return image;
	}
	
	public String WeatherMent(String b) {
		String WeatherMent = "";
		if (b.contains("맑음")) {
			WeatherMent = "맑은 날씨가 예상됩니다.";
		} else if (b.contains("비") || b.contains("소나기") || b.contains("천둥번개")) {
			WeatherMent = "비가 올것 같네요.";
		} else if (b.contains("흐") || b.contains("안개") || b.contains("박무")) {
			WeatherMent = "흐린 날씨가 예상됩니다.";
		} else if (b.contains("구름")) {
			WeatherMent = "구름이 조금 예상됩니다.";
		} else if (b.contains("눈")) {
			WeatherMent = "눈이 내릴것 같네요";
		} else {
			WeatherMent = "맑은 날씨가 예상됩니다.";
		}

		return WeatherMent;
	}

	@RequestMapping(value = "/auto", method = RequestMethod.GET)
	public String Home(Model model) {

		return "/weather/auto";
	}
}

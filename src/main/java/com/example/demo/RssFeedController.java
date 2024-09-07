package com.example.demo;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.RssFeedResult;
import com.example.demo.model.SubCategory;
import com.example.demo.model.MainCategory;
import com.example.demo.service.EmailService;
import com.example.demo.service.LineNotifyService;
import com.example.demo.service.RssFeedService;
import com.example.demo.service.SMSAppService;
import com.example.demo.service.WhatsAppService;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/rss")
public class RssFeedController {

    @Autowired
    private RssFeedService rssFeedService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private WhatsAppService whatsAppService; // Inject WhatsAppService

    @Autowired
    private SMSAppService smsAppService; // Inject SMSAppService

    @Autowired
    private LineNotifyService lineNotifyService;

    private List<MainCategory> mainCategories; // List of all main categories

    public RssFeedController() {
        mainCategories = new ArrayList<>();

        // // Example of adding a main category with its respective subjects
        // MainCategory localNews = new MainCategory("local_news", "地方新聞");
        // localNews.addSubCategory("taipei", "台北市",
        // "https://www.gov.taipei/OpenData.aspx?SN=7DEC7150E6BAD606");
        // localNews.addSubCategory("taichung", "台中市",
        // "https://www.taichung.gov.tw/10179/564770/rss?nodeId=9962");
        // localNews.addSubCategory("tycg", "桃園市",
        // "https://news.tycg.gov.tw/OpenData.aspx?SN=65C6B1AA38BDD145");
        // // Add the main category to the list
        // mainCategories.add(localNews);

        MainCategory TheNewest = new MainCategory("TheNewest", "最新");
        TheNewest.addSubCategory("TheNewLens", "關鍵評論網", "https://feeds.feedburner.com/TheNewsLens");
        TheNewest.addSubCategory("Cnn", "CNN", "http://rss.cnn.com/rss/money_topstories.rss");
        mainCategories.add(TheNewest);
        MainCategory Weather = new MainCategory("Weather", "天氣");
        String weather_head = "https://www.cwa.gov.tw/rss/forecast/36_";
        String weather_tail = ".xml";
        Weather.addSubCategory("KeelungCity", "基隆市", weather_head + "03" + weather_tail);
        Weather.addSubCategory("TaipeiCity", "台北市", weather_head + "01" + weather_tail);
        Weather.addSubCategory("NewTaipeiCity", "新北市", weather_head + "04" + weather_tail);
        Weather.addSubCategory("TaoyuanCity", "桃園市", weather_head + "05" + weather_tail);
        Weather.addSubCategory("HsinchuCity", "新竹市", weather_head + "14" + weather_tail);
        Weather.addSubCategory("HsinchuCounty", "新竹縣", weather_head + "06" + weather_tail);
        Weather.addSubCategory("MiaoliCounty", "苗栗縣", weather_head + "07" + weather_tail);
        mainCategories.add(Weather);
        MainCategory WeatherWarning = new MainCategory("WeatherWarning", "警報特報");
        WeatherWarning.addSubCategory("WeatherWarning", "警報特報", "https://www.cwa.gov.tw/rss/Data/cwa_warning.xml");
        mainCategories.add(WeatherWarning);
        MainCategory GovInfo = new MainCategory("GovInfo", "政府資訊");
        GovInfo.addSubCategory("Taipei", "台北市政府", "https://www.gov.taipei/OpenData.aspx?SN=D33B55D537402BAA");
        GovInfo.addSubCategory("Taoyuan", "桃園市政府", "https://www.tycg.gov.tw/OpenData.aspx?SN=65D106AB5A9DB388");
        GovInfo.addSubCategory("Taichung", "台中市政府", "https://www.taichung.gov.tw/10179/564770/rss?nodeId=9962");
        mainCategories.add(GovInfo);
        MainCategory TwStocks = new MainCategory("TwStocks", "台股");
        String tw_stk_head = "https://tw.stock.yahoo.com/rss?s=";
        String tw_stk_tail = ".TW";
        TwStocks.addSubCategory("00878", "國泰永續高股息", tw_stk_head + "00878" + tw_stk_tail);
        TwStocks.addSubCategory("2002", "中鋼", tw_stk_head + "2002" + tw_stk_tail);
        TwStocks.addSubCategory("2330", "台積電", tw_stk_head + "2330" + tw_stk_tail);
        TwStocks.addSubCategory("0056", "元大高股息", tw_stk_head + "0056" + tw_stk_tail);
        TwStocks.addSubCategory("00940", "元大台灣價值高息", tw_stk_head + "00940" + tw_stk_tail);
        TwStocks.addSubCategory("2883", "開發金", tw_stk_head + "2883" + tw_stk_tail);
        TwStocks.addSubCategory("2303", "聯電", tw_stk_head + "2303" + tw_stk_tail);
        TwStocks.addSubCategory("00919", "群益台灣精選高息", tw_stk_head + "00919" + tw_stk_tail);
        TwStocks.addSubCategory("2317", "鴻海", tw_stk_head + "2317" + tw_stk_tail);
        TwStocks.addSubCategory("00929", "復華台灣科技優息", tw_stk_head + "00929" + tw_stk_tail);
        TwStocks.addSubCategory("0050", "元大台灣50", tw_stk_head + "0050" + tw_stk_tail);
        mainCategories.add(TwStocks);
        MainCategory UsStocks = new MainCategory("UsStocks", "美股");
        String us_stk_head = "https://www.nasdaq.com/feed/rssoutbound?symbol=";
        UsStocks.addSubCategory("Apple", "蘋果 Apple", us_stk_head + "aapl");
        UsStocks.addSubCategory("Amazon", "亞馬遜 Amazon", us_stk_head + "AMZN");
        UsStocks.addSubCategory("Facebook", "臉書 Facebook", us_stk_head + "FB");
        UsStocks.addSubCategory("Tesla", "特斯拉 Tesla", us_stk_head + "TSLA");
        UsStocks.addSubCategory("AMD", "超微 AMD", us_stk_head + "AMD");
        UsStocks.addSubCategory("Nvidia", "輝達 Nvidia", us_stk_head + "NVDA");
        mainCategories.add(UsStocks);
        MainCategory Sport = new MainCategory("Sport", "運動");
        Sport.addSubCategory("TheCentralNewsAgency", "中央通訊社", "https://feeds.feedburner.com/rsscna/sport");
        Sport.addSubCategory("LibertyTimesNews", "自由時報", "https://news.ltn.com.tw/rss/sports.xml");
        Sport.addSubCategory("TsnaProfessional", "TSNA體育", "https://news.tsna.com.tw/rss.php?num=11");
        String fox_head = "https://api.foxsports.com/v2/content/optimized-rss?partnerKey=MB0Wehpmuj2lUhuRhQaafhBjAJqaPU244mlTDK1i&size=30&tags=fs/";
        Sport.addSubCategory("FoxMlb", "FOX體育-MLB", fox_head + "mlb");
        Sport.addSubCategory("FoxNba", "FOX體育-NBA", fox_head + "nba");
        mainCategories.add(Sport);
    }

    // API endpoint to get all categories
    // Method to retrieve categories, 取得大分類列表
    @GetMapping("/mainCategories")
    public List<MainCategory> getMainCategories() {
        return mainCategories;
    }

    // API endpoint to get subjects by category code
    // Method to retrieve subjects, 丟入大分類的 String code, 取得小分類
    @GetMapping("/mainCategories/{subCategory}/")
    public MainCategory getSubjectsByCategory(
            @PathVariable String subCategory) {
        for (MainCategory mainCategory : mainCategories) {
            if (mainCategory.getCode().equals(subCategory)) {
                return mainCategory;
            }
        }
        return null;
    }

    private String getRssUri(String inputSubCategory) {
        for (MainCategory mainCategory : mainCategories) {
            for (SubCategory subCategory : mainCategory.getSubCategories()) {
                if (subCategory.getCode().equalsIgnoreCase(inputSubCategory)) {
                    return subCategory.getRssUri();
                }
            }
        }
        return "SubCategory not found"; // Handle cases where the subject does not exist
    }

    // 寄送通知 api
    @PostMapping("/send-subject/")
    public ResponseEntity<String> sendSubject(
        @RequestParam("subject") String subject, 
        @RequestParam("noticeMethod") String noticeMethod, 
        @RequestParam("recipient") String recipient) {   

        String rssUrl = getRssUri(subject);
        System.out.println(rssUrl);

        if (rssUrl == null) {
            return new ResponseEntity<>("Invalid subject", HttpStatus.BAD_REQUEST);
        }

        RssFeedResult rssFeedResult = rssFeedService.fetchAndFormatRssFeed(rssUrl);

        switch (noticeMethod) {
            // email
            case "email":
                if (recipient == null || !isValidEmail(recipient)) {
                    return new ResponseEntity<>("Invalid or missing email address", HttpStatus.BAD_REQUEST);
                }

                emailService.sendEmailWithRssContent(rssFeedResult, recipient);
                return new ResponseEntity<>("RSS feed sent via email successfully", HttpStatus.OK);

            // line
            case "line":
                lineNotifyService.sendNotification(rssFeedResult, recipient);
                return new ResponseEntity<>("RSS feed sent via LINE Notify successfully", HttpStatus.OK);

            // whatsapp
            case "whatsapp":
                if (recipient == null || !isValidPhoneNumber(recipient)) {
                    return new ResponseEntity<>("Invalid or missing phone number", HttpStatus.BAD_REQUEST);
                }

                whatsAppService.sendNotification(rssFeedResult, recipient);
                return new ResponseEntity<>("RSS feed sent via whatsapp successfully", HttpStatus.OK);

            // 三竹需要有統編後跟簡訊商開通才能夠串接 api 成功
            // sms, 要串台灣簡訊商 三竹 等才可以寄送(需再申請對應api, token 等)
            // https://www.twsms.com/
            // 三竹簡訊 https://msg.mitake.com.tw/SMS/Home.jsp?t=1725121253337
            case "sms":
                if (recipient == null || !isValidPhoneNumber(recipient)) {
                    return new ResponseEntity<>("Invalid or missing phone number", HttpStatus.BAD_REQUEST);
                }

                smsAppService.sendNotification(rssFeedResult, recipient);
                return new ResponseEntity<>("RSS feed sent via sms successfully", HttpStatus.OK);

            default:
                return new ResponseEntity<>("Invalid notification method", HttpStatus.BAD_REQUEST);

        }
    }

    // Basic validation for email addresses
    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$";
        return email.matches(emailRegex);
    }

    // Basic validation for phone numbers (e.g., digits only, 7-15 characters)
    private boolean isValidPhoneNumber(String phoneNumber) {
        String phoneRegex = "^\\d{7,15}$";
        return phoneNumber.matches(phoneRegex);
    }
}
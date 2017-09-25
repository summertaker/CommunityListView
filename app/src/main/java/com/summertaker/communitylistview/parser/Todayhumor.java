package com.summertaker.communitylistview.parser;

import com.summertaker.communitylistview.data.ArticleData;
import com.summertaker.communitylistview.common.BaseParser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.ArrayList;

public class Todayhumor extends BaseParser {

    public void parseList(String response, ArrayList<ArticleData> memberList) {
        /*
        <a href="view.php?table=bestofbest&no=363965&page=1">
            <div class="listLineBox list_tr_sisa" mn='754830'>
                <div class="list_iconBox">
                        <div class='board_icon_mini sisa' style='align-self:center'></div>
                </div>
                <div>
                    <span class="list_no">363965</span>
                    <span class="listDate">2017/09/22 11:45</span>
                    <span class="list_writer" is_member="yes">carryon</span>
                </div>
                <div>
                    <h2 class="listSubject" >네이버를 조져야됨..<span class="list_comment_count"> <span class="memo_count">[3]</span></span></h2>
                </div>
                <div>
                    <span class="list_viewTitle">조회:</span><span class="list_viewCount">1374</span>	            <span class="list_okNokTitle">추천:</span><span class="list_okNokCount">53</span>
                    <span class="list_iconWrap">
                    </span>
                </div>
            </div>
        </a>
        */

        //Log.d(mTag, response);

        if (response == null || response.isEmpty()) {
            return;
        }

        Document doc = Jsoup.parse(response);
        Element root = doc.select("#remove_favorite_alert_div").first();

        if (root != null) {

            for (Element row : doc.select("a")) {
                String title;
                String like;
                String url;

                Element el = row.select(".listSubject").first();
                if (el == null) {
                    continue;
                }
                title = el.text();
                //title = title.replaceAll("[0-9]", "").replace("[]", "");

                //Element a = row; //row.select("a").first();
                url = row.attr("href");
                url = "http://m.todayhumor.co.kr/" + url;

                el = row.select(".list_okNokCount").first();
                like = el.text();

                //Log.d(mTag, title + " / " + like);

                ArticleData data = new ArticleData();
                data.setTitle(title);
                data.setLike(like);
                data.setUrl(url);
                memberList.add(data);
            }
        }
    }

    public String parseDetail(String response) {
        String result = "";

        Document doc = Jsoup.parse(response); // http://jsoup.org/

        Element root = doc.select(".viewContent").first();
        if (root != null) {
            result = root.html();
        }

        return result;
    }
}
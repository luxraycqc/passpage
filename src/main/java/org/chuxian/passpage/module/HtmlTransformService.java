package org.chuxian.passpage.module;

import cn.hutool.core.util.ReUtil;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HtmlTransformService {

    public String constructTargetHtml(String title, String content) {
        String beforeTitle = "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta content=\"width=device-width,initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0\" name=\"viewport\"/>\n" +
                "    <title>";
        String afterTitleBeforeHead = "</title>\n" +
                "</head>\n" +
                "<body>\n" +
                "<h2>";
        String afterHeadBeforeContent = "</h2>\n" +
                "<p>";
        String afterContent = "</p>\n" +
                "</body>\n" +
                "</html>";
        return beforeTitle + title + afterTitleBeforeHead + title + afterHeadBeforeContent + content + afterContent;
    }

    public String displayHead(String rawHtml) {
        String title = ReUtil.get("<title.*?>(.*?)</title>", rawHtml, 1).replaceAll("<.*?>|\\s{2,}", "");
        StringBuilder content = new StringBuilder();
        List<String> paragraphs = ReUtil.findAll("<p.*?>(.*?)</p>", rawHtml, 1);
        int index = 0;
        for (String paragraph : paragraphs) {
            if (content.length() < 220) {
                if (index > 1) {
                    content.append(paragraph.replaceAll("<.*?>|\\s{2,}", ""));
                }
                index++;
            } else {
                break;
            }
        }
        return constructTargetHtml(title, content.toString());
    }

}

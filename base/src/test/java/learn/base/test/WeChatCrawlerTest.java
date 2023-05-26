package learn.base.test;

import org.apache.commons.collections4.CollectionUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Random;

/**
 * @author Zephyr
 * @since 2021-3-31.
 */
public class WeChatCrawlerTest {


    // @Test
    public void testCrawlingWeChat() throws IOException {
        String url = "https://mp.weixin.qq.com/s/dSXqy5C5MIUbHB1VZDeiHw";
        URL htmlUrl2 = new URL(url);
        Document document2 = Jsoup.parse(htmlUrl2, 5000);
        final Element content = document2.getElementById("page-content");
        final Element jsContent = content.getElementById("js_content");
        final String style = jsContent.attr("style");

        for (Element element : content.select("[data-type]")) {
            // 去除视频号
            if ("mpvideosnap".equals(element.tagName()) && "video".equals(element.attr("data-type"))) {
                final Element brother = element.parent().previousElementSibling();
                element.remove();
                if (brother != null && brother.nodeName().equals("p")) {
                    brother.remove();
                }
                System.out.println();
            }

            // 图片转存
            if (element.tagName().equals("img")) {
                String src = element.attr("data-src");
                String suffix = getPostfix(src);
                String fileName = System.currentTimeMillis() + "" + (new Random().nextInt(9999) + 1000) + "." + suffix;
                System.out.println(fileName);
            }
        }

        // a标签去掉超链接
        for (Element a : content.getElementsByTag("a")) {
            replaceATag(a);
            //System.out.println(a);
        }
        String html2 = content.outerHtml();
        //System.out.println(html2);
    }


    private void replaceATag(Element a) {
        final List<Node> childNodes = a.childNodes();
        if ("a".equals(a.tagName()) && CollectionUtils.isEmpty(childNodes)) {
            a.remove();
        } else {
            //String href = a.attr("href");
            a.clearAttributes().tagName("div");
            for (Node node : childNodes) {
                if (node instanceof Element && "a".equals(((Element) node).tagName())) {
                    replaceATag((Element) node);
                }
            }
        }
    }

    public static String getPostfix(String filename) {
        return filename.substring(filename.lastIndexOf("=") + 1);
    }
}

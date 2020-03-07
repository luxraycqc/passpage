package org.chuxian.passpage.module;

import cn.hutool.core.io.file.FileWriter;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.db.DbUtil;
import cn.hutool.db.Entity;
import cn.hutool.http.HttpRequest;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import org.chuxian.passpage.utils.SqlUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.io.File;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class DecoyPageService {
    @Autowired
    Config config;
    @Autowired
    DataSource dataSource;
    @Autowired
    HtmlTransformService htmlTransformService;

    Log log = LogFactory.get();

    public void createDirectories(String domain, String[] loginModes) {
        for (String loginMode : loginModes) {
            File filePath = new File(config.getPageLibraryPath() + domain + loginMode);
            filePath.mkdirs();
        }
    }

    @Scheduled(cron = "0 1 0,12 * * ?")
    public void crawlPages() {
        crawlSohuPages();
        crawlZhihuPages();
    }

    public int crawlSohuPages() {
        int count = 0;
        String domain = "www.sohu.com";
        String[] loginModes = new String[]{"/displayRaw", "/displayHead"};
        try {
            log.info("开始爬取" + domain + "上的所有网页");
            String homepageHtml = HttpRequest.get("https://" + domain)
                    .setReadTimeout(10000)
                    .setConnectionTimeout(10000)
                    .setMaxRedirectCount(2)
                    .execute().body();
            List<String> linkUrls = ReUtil.findAll("(www.sohu.com/a/.*?)\"", homepageHtml, 1);
            log.info("主页和链接获取成功");
            Connection connection = dataSource.getConnection();
            log.info("数据库连接获取成功");
            for (String linkUrl : linkUrls) {
                String linkHtml = HttpRequest.get("https://" + linkUrl)
                        .setReadTimeout(10000)
                        .setConnectionTimeout(10000)
                        .setMaxRedirectCount(2)
                        .execute().body();
                String title = ReUtil.get("<h1.*?>(.*?)</h1>", linkHtml, 1);
                if (title != null) {
                    title = title.replaceAll("<.*?>|\\s{2,}", "");
                    if (title.length() > 0) {
                        String fileName = IdUtil.simpleUUID() + ".html";
                        for (String loginMode : loginModes) {
                            File file = new File(config.getPageLibraryPath() + domain + loginMode, fileName);
                            FileWriter writer = new FileWriter(file);
                            if ("/displayRaw".equals(loginMode)) {
                                writer.write(linkHtml);
                            } else if ("/displayHead".equals(loginMode)) {
                                writer.write(htmlTransformService.displayHead(linkHtml));
                            }
                        }
                        SqlUtil.execute(connection, "INSERT INTO decoy_page(domain,file_name,title,added_time) VALUES(?,?,?,?)", domain, fileName, title, new Date());
                        count++;
                    }
                }
            }
            log.info("结束爬取" + domain + "上的所有网页，一共爬了" + count + "个");
            deleteOldPages(connection, domain, loginModes);
            DbUtil.close(connection);
        } catch (Exception e) {
            log.error(e);
        }
        return count;
    }
    public static String cookie = "tgw_l7_route=79c5a098af080bf343c0c50ae917961f; _zap=e75a2022-c2e9-45bc-8162-dfa05b27060c; _xsrf=mgMtHbiKqmKuiuC3ZvQ3DMkyegzQ9OAY; d_c0=\"AFBjhh6QjBCPTsv5TCSR5uOPv6nVkDbOOis=|1577090927\"; Hm_lvt_98beee57fd2ef70ccdd5ca52b9740c49=1577090928; capsion_ticket=\"2|1:0|10:1577090932|14:capsion_ticket|44:NmUzMzA0N2JkMzAzNGZhYmIwMzM5YWE2YWUzNTMxMjM=|9479d3c5b06dee7ca3549dff3c5e0a5c9e6a315f4be03883c41588970d1bdc01\"; z_c0=\"2|1:0|10:1577090935|4:z_c0|92:Mi4xMlU2OEJRQUFBQUFBVUdPR0hwQ01FQ1lBQUFCZ0FsVk5kODN0WGdBTi1EdHp6aXhoakpoVEtSZC0zZGhJc050NnpB|86081c954cfda97285d99b575cadddb06d784412420e5b8d48ee7eb5b2f73bdd\"; tst=r; Hm_lpvt_98beee57fd2ef70ccdd5ca52b9740c49=1577091161";
    public int crawlZhihuPages() {
        int count = 0;
        String domain = "www.zhihu.com";
        String[] loginModes = new String[]{"/displayRaw", "/displayHead"};
        try {
            log.info("开始爬取" + domain + "上的所有网页");
            String homepageHtml = HttpRequest.get("https://" + domain + "/explore")
                    .cookie(cookie)
                    .setReadTimeout(10000)
                    .setConnectionTimeout(10000)
                    .setMaxRedirectCount(2)
                    .execute().body();
            List<String> linkUrls = ReUtil.findAll("(/question/\\d+)\"", homepageHtml, 1);
            log.info("主页和链接获取成功");
            Connection connection = dataSource.getConnection();
            log.info("数据库连接获取成功");
            for (String linkUrl : linkUrls) {
                String linkHtml = HttpRequest.get("https://" + domain + linkUrl)
                        .cookie(cookie)
                        .setReadTimeout(10000)
                        .setConnectionTimeout(10000)
                        .setMaxRedirectCount(2)
                        .execute().body();
                String title = ReUtil.get("<h1.*?>(.*?)</h1>", linkHtml, 1);
                if (title != null) {
                    title = title.replaceAll("<.*?>|\\s{2,}", "");
                    if (title.length() > 0) {
                        String fileName = IdUtil.simpleUUID() + ".html";
                        for (String loginMode : loginModes) {
                            File file = new File(config.getPageLibraryPath() + domain + loginMode, fileName);
                            FileWriter writer = new FileWriter(file);
                            if ("/displayRaw".equals(loginMode)) {
                                writer.write(linkHtml);
                            } else if ("/displayHead".equals(loginMode)) {
                                writer.write(htmlTransformService.displayHead(linkHtml));
                            }
                        }
                        SqlUtil.execute(connection, "INSERT INTO decoy_page(domain,file_name,title,added_time) VALUES(?,?,?,?)", domain, fileName, title, new Date());
                        count++;
                    }
                }
            }
            log.info("结束爬取" + domain + "上的所有网页，一共爬了" + count + "个");
            DbUtil.close(connection);
        } catch (Exception e) {
            log.error(e);
        }
        return count;
    }

    private void deleteOldPages(Connection connection, String domain, String[] loginModes) throws Exception {
        long maxThresholdTime = System.currentTimeMillis() / 1000 - 259200;
        long minThresholdTime = System.currentTimeMillis() / 1000 - 864000;
        List<Entity> oldPageEntities = SqlUtil.query(connection, "select file_name from decoy_page where domain = ? and unix_timestamp(added_time) < ? and unix_timestamp(added_time) > ? and rand() < 0.25", domain, maxThresholdTime, minThresholdTime);
        ArrayList<String> deletedFileNames = new ArrayList<>();
        for (Entity oldPageEntity : oldPageEntities) {
            String fileName = oldPageEntity.getStr("file_name");
            deletedFileNames.add("'" + fileName + "'");
            for (String loginMode : loginModes) {
                File filePath = new File(config.getPageLibraryPath() + domain + loginMode + "/" + fileName);
                filePath.delete();
            }
        }
        if (deletedFileNames.size() > 0) {
            int count = SqlUtil.execute(connection, "delete from decoy_page where domain = ? and file_name in " + deletedFileNames.toString().replaceAll("\\[", "(").replaceAll("\\]", ")"), domain);
            log.info("随机删除3天前10天内的网页" + count + "个");
        }
    }

    public static void main(String[] args) {
        String domain = "www.sohu.com";
        String homepageHtml = HttpRequest.get("https://" + domain)
                .setReadTimeout(10000)
                .setConnectionTimeout(10000)
                .setMaxRedirectCount(2)
                .execute().body();
        System.out.println(homepageHtml);
        List<String> linkUrls = ReUtil.findAll("(www.sohu.com/a/.*?)\"", homepageHtml, 1);
        for (String linkUrl : linkUrls) {
            System.out.println(linkUrl);
        }
    }

}

package org.chuxian.passpage.utils;

import cn.hutool.log.LogFactory;
import org.springframework.core.io.ClassPathResource;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {
    public static String readFileContentAsString(String filePath) {
        String fileContent;
        try {
            InputStream is = new ClassPathResource(filePath).getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line;
            StringBuilder sb = new StringBuilder();
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
            fileContent = sb.toString();
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
            LogFactory.get().error(e);
            return null;
        }
        return fileContent;
    }
    public static List<String> readFileContentAsStringList(String filePath) {
        ArrayList<String> fileContent = new ArrayList<>();
        try {
            InputStream is = new ClassPathResource(filePath).getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = br.readLine()) != null) {
                fileContent.add(line);
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
            LogFactory.get().error(e);
            return null;
        }
        return fileContent;
    }
}

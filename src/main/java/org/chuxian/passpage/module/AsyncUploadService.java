package org.chuxian.passpage.module;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import org.chuxian.passpage.message.UploadPageLogRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.concurrent.Future;

@Service
public class AsyncUploadService {
    @Autowired
    Config config;
    @Autowired
    HtmlTransformService htmlTransformService;
    @Autowired
    LogService logService;

    Log log = LogFactory.get();
    public static HashMap<String, String> tempTitleMap = new HashMap<>();

    @Async
    public Future<Integer> uploadPageLogAsync(UploadPageLogRequest uploadPageLogRequest) {
        String fileName = uploadPageLogRequest.getFileName();
        Integer[] timeArray = uploadPageLogRequest.getTimeArray();
        try {
            String title = tempTitleMap.get(fileName);
            if (title != null) {
                int totalTime = 0;
                for (Integer time : timeArray) {
                    totalTime += time;
                }
                tempTitleMap.remove(fileName);
                logService.uploadPageLog(fileName, totalTime, timeArray.length, ArrayUtil.toString(timeArray));
            }
            return new AsyncResult<>(0);
        } catch (Exception e) {
            log.error(e);
            return new AsyncResult<>(1);
        }
    }
}

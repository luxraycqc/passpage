package org.chuxian.passpage.module;

import cn.hutool.db.DbUtil;
import cn.hutool.db.Entity;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import org.chuxian.passpage.utils.SqlUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.sql.DataSource;
import java.sql.Connection;

@Controller
public class UserStudyController {
    @Autowired
    Config config;
    @Autowired
    DataSource dataSource;
    Log log = LogFactory.get();

    @RequestMapping(value="/getVolunteerCount", method = RequestMethod.GET)
    @ResponseBody
    public int getVolunteerCount() {
        try {
            Connection connection = dataSource.getConnection();
            int result = SqlUtil.queryOne(connection, "select count(*) as count from user_account").getInt("count");
            DbUtil.close(connection);
            return result;
        } catch (Exception e) {
            log.error(e);
            return 0;
        }
    }

    @RequestMapping(value="/userPreStudy", method = RequestMethod.POST)
    @ResponseBody
    public int addUserPreStudy(String sex, int age, String profession) {
        try {
            Connection connection = dataSource.getConnection();
            SqlUtil.execute(connection, "insert into user_pre_study(sex,age,profession) values(?,?,?)", sex, age, profession);
            DbUtil.close(connection);
            return 0;
        } catch (Exception e) {
            log.error(e);
            return 1;
        }
    }

    @RequestMapping(value="/getSignupTime/{username}", method = RequestMethod.GET)
    @ResponseBody
    public long getSignupTime(@PathVariable String username) {
        try {
            Connection connection = dataSource.getConnection();
            Entity result = SqlUtil.queryOne(connection, "select added_time from user_account where username=?", username);
            DbUtil.close(connection);
            if (result != null) return result.getDate("added_time").getTime();
            else return 1;
        } catch (Exception e) {
            log.error(e);
            return 1;
        }
    }

    @RequestMapping(value="/feedback", method = RequestMethod.POST)
    @ResponseBody
    public int feedback(String answer) {
        try {
            String[] msgs = answer.split("-");
            Connection connection = dataSource.getConnection();
            SqlUtil.execute(connection, "insert into feedback(answer1,answer2,answer3,answer4,answer5,answer6,answer7) values(?,?,?,?,?,?,?)", msgs[0], msgs[1], msgs[2], msgs[3], msgs[4], msgs[5], msgs[6]);
            DbUtil.close(connection);
            return 0;
        } catch (Exception e) {
            log.error(e);
            return 1;
        }
    }
}

package com.theone.weblog.weborigin;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Set;


public class WebLogParser {

    public static SimpleDateFormat df1 = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss", Locale.US);
    public static SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);

    //194.237.142.21 - - [18/Sep/2013:06:49:18 +0000] "GET /wp-content/uploads/2013/07/rstudio-git3.png HTTP/1.1" 304 0 "-" "Mozilla/4.0 (compatible;)"
    public static WebLogBean parser(String line) {
        WebLogBean webLogBean = new WebLogBean();
        String[] arr = line.split(" ");
        // 判断搜集的数据是否有处理清洗价值
        if (arr.length > 11) {
            webLogBean.setRemote_addr(arr[0]);
            webLogBean.setRemote_user(arr[1]);
            String time_local = formatDate(arr[3].substring(1));
            if (null == time_local || "".equals(time_local)) time_local = "-invalid_time-";
            webLogBean.setTime_local(time_local);
            webLogBean.setRequest(arr[6]);
            webLogBean.setStatus(arr[8]);
            webLogBean.setBody_bytes_sent(arr[9]);
            webLogBean.setHttp_referer(arr[10]);

            //如果useragent元素较多，拼接useragent
            if (arr.length > 12) {
                StringBuilder sb = new StringBuilder();
                for (int i = 11; i < arr.length; i++) {
                    sb.append(arr[i]);
                }
                webLogBean.setHttp_user_agent(sb.toString());
            } else {
                webLogBean.setHttp_user_agent(arr[11]);
            }

            if (Integer.parseInt(webLogBean.getStatus()) >= 400) {// 大于400，HTTP错误
                webLogBean.setValid(false);
            }

            if ("-invalid_time-".equals(webLogBean.getTime_local())) {
                webLogBean.setValid(false);
            }
        } else {
            webLogBean = null;
        }

        return webLogBean;
    }

    public static void filtStaticResource(WebLogBean bean, Set<String> pages) {
        if (!pages.contains(bean.getRequest())) {
            bean.setValid(false);
        }
    }

    //格式化时间方法
    public static String formatDate(String time_local) { //18/Sep/2013:06:49:18
        try {
            //  Date parse(String source) 从给定字符串的开始解析文本，以生成一个日期
            return df2.format(df1.parse(time_local));//dfs2=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        } catch (ParseException e) {
            return null;
        }

    }

}

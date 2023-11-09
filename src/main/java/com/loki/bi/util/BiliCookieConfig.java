package com.loki.bi.util;

import cn.hutool.core.util.StrUtil;
import org.apache.logging.log4j.util.Strings;
import org.springframework.http.HttpHeaders;

/**
 * @author : loki
 * @version V1.0
 * @Project: HomeAndStock
 * @Package com.loki.bi
 * @Description: TODO
 * @date Date : 2023 年 10月 04 日 18:50
 */
public interface BiliCookieConfig {

    public static final String cookie =  "buvid3=EFD0AEFB-1AA6-BF85-C9CF-EDABDA699DBC75397infoc; b_nut=1690532075; i-wanna-go-back=-1; b_ut=7; _uuid=510E2103101-88F6-57A10-A10ED-DC2E7DA6CD1778320infoc; FEED_LIVE_VERSION=V8; buvid4=0BEF8685-0FE9-20D6-850D-EC609EB9B97027629-022012703-SV44yNkYjlEFOZn8tCspzjBEAdpTkZTzErCqqEiIRN%2FQsy4V2xBzaQ%3D%3D; rpdid=|(umJkJRlmkm0J'uYm|lJkuJk; header_theme_version=CLOSE; LIVE_BUVID=AUTO8816964241305623; buvid_fp_plain=undefined; hit-dyn-v2=1; dy_spec_agreed=1; enable_web_push=DISABLE; home_feed_column=5; fingerprint=97ca5a656e24586e0542793908101340; buvid_fp=042f5ebd3ee7945c578228e459e89292; browser_resolution=1552-827; bili_ticket=eyJhbGciOiJIUzI1NiIsImtpZCI6InMwMyIsInR5cCI6IkpXVCJ9.eyJleHAiOjE2OTk3NDA5NzMsImlhdCI6MTY5OTQ4MTcxMywicGx0IjotMX0.zUtfAZ8B4qAM4fpUgekqRvr5a_8gkVHN7B0-GyVpEKo; bili_ticket_expires=1699740913; CURRENT_BLACKGAP=0; CURRENT_FNVAL=4048; bp_video_offset_14191381=861946953613705303; b_lsid=A231F3D6_18BB4608472; innersign=0; csrf_state=ce91c0dc6f06fec8ba384b08028f1ea6; SESSDATA=f388a528%2C1715091855%2Cd7b1b%2Ab2CjCMppNXl9p5Z5h0k9yl0R_RJYnV2e0FFRFIZsmhUQ4S5mhQCItGYU8QpB4TxVrF9xoSVmZBZXVYM2xyTHROamdWMHFETEMwQVpRVnk0R25xWFVjWXduSHVGamVsZl9ydmV0ZzYwdlQzYjl1c1I3R0RUYlR2d0FaTnlfbnJNWVV2TWVKVXBINnJ3IIEC; bili_jct=2943499c26cfe38a6977274e75165e74; DedeUserID=14191381; DedeUserID__ckMd5=8787da597f8e7ec4; sid=6unylycx; PVID=2";
    
    public static String getCSRF(){
        String[] values = StrUtil.splitToArray(cookie,";");
        for(String val : values){
            if(val.indexOf("bili_jct=")>-1) return val.split("=")[1];
        }
        return StrUtil.EMPTY;
    }

}

import org.springframework.util.DigestUtils;

import com.pibigstar.common.Constant;

public class MD5Factory {
    private static final String slat = "&%5123***&&%%$$#@";
    /**
     * 生成md5
     * @param seckillId
     * @return
     */
    public static String getMD5(String str) {
        String base = str +"/"+slat;
        String md5 = DigestUtils.md5DigestAsHex(base.getBytes());
        return md5;
    }

}
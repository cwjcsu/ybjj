package com.cwjcsu.common.util;

import java.security.SecureRandom;
import java.util.Date;

/**
 * Created by lidonghao on 15/8/7.
 */
public final class RandomUtil {
    private static final String[] l = {
            "0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
            "a", "b", "c", "d", "e", "f", "g", "h", "i", "j",
            "k", "l", "m", "n", "o", "p", "q", "r", "s", "t",
            "u", "v", "w", "x", "y", "z",
            "A", "B", "C", "D", "E", "F", "G", "H", "I", "J",
            "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T",
            "U", "V", "W", "X", "Y", "Z"};

    private static int count = 100;

    private static int getCount() {
        if (count > 999){
            count = 100;
        }
        return count++;
    }

    /**
     * 把十进制的数字转换为其它进制
     * @param value 被转换的十进制数字
     * @param number 几进制（2-62之间）
     * @return
     */
    private static String convertTenToN(long value, int number) {
        if (number <= 1 || number > l.length) {
            throw new RuntimeException("Faild");
        }
        //负数处理
        if (value < 0) {
            return "-" + convertTenToN(0 - value, number);
        }
        if (value < number) {
            return l[(int) value];
        } else {
            long n = value % (long) number;
            return (convertTenToN(value / number, number) + l[(int) n]);
        }
    }

    /**
     * 获取随机的名称，在高并发下有可能重复
     * @return
     */
    public static String getRandomName(){
        return convertTenToN(System.currentTimeMillis(), 62) + convertTenToN((long) getCount(), 62);
    }

    public static final SecureRandom RANDOM = new SecureRandom();
    public static String generateSN(Date d) {
        int id = RANDOM.nextInt(1000000);
        return String.format("%1$tY%1$tm%1$td%2$06d", d, id);
    }

}

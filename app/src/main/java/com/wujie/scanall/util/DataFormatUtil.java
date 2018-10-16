package com.wujie.scanall.util;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by wujie
 * on 2018/10/15/015.
 */
public class DataFormatUtil {
    public static String getMac(String paramString1, String paramString2)
    {
        byte[] arrayOfByte = new byte[32];
        arrayOfByte[0] = 87;
        arrayOfByte[1] = -77;
        arrayOfByte[2] = 119;
        arrayOfByte[3] = 94;
        arrayOfByte[4] = -103;
        arrayOfByte[5] = -72;
        arrayOfByte[6] = -114;
        arrayOfByte[7] = -108;
        arrayOfByte[8] = 104;
        arrayOfByte[9] = -97;
        arrayOfByte[10] = -11;
        arrayOfByte[11] = -84;
        arrayOfByte[12] = 103;
        arrayOfByte[13] = -59;
        arrayOfByte[14] = -1;
        arrayOfByte[15] = -21;
        arrayOfByte[16] = 113;
        arrayOfByte[17] = -68;
        arrayOfByte[18] = -96;
        arrayOfByte[19] = -57;
        arrayOfByte[20] = -122;
        arrayOfByte[21] = 117;
        arrayOfByte[22] = 73;
        arrayOfByte[24] = -16;
        arrayOfByte[25] = -69;
        arrayOfByte[26] = -79;
        arrayOfByte[27] = -77;
        arrayOfByte[28] = 80;
        arrayOfByte[29] = -98;
        arrayOfByte[30] = -99;
        arrayOfByte[31] = 54;
        String str = "";
        paramString1 = str;
        try
        {
            Mac localMac = Mac.getInstance("HmacSHA256");
            paramString1 = str;
            byte[] paramString2byte = paramString2.getBytes("ASCII");
            paramString1 = str;
            localMac.init(new SecretKeySpec(arrayOfByte, "HMACSHA256"));
            paramString1 = str;
            paramString2 = toHex(localMac.doFinal(paramString2byte));
            paramString1 = paramString2;
            paramString2 = paramString2.toUpperCase();
            return paramString2;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return paramString1;
    }

    public static String toHex(byte[] paramArrayOfByte)
    {
        StringBuffer localStringBuffer1 = new StringBuffer(paramArrayOfByte.length * 2);
        StringBuffer localStringBuffer2 = new StringBuffer(paramArrayOfByte.length);
        int i = 0;
        for (;;)
        {
            if (i >= paramArrayOfByte.length) {
                return localStringBuffer1.toString();
            }
            localStringBuffer1.append(Character.forDigit((paramArrayOfByte[i] & 0xF0) >> 4, 16));
            localStringBuffer1.append(Character.forDigit(paramArrayOfByte[i] & 0xF, 16));
            localStringBuffer2.append(paramArrayOfByte[i]);
            i += 1;
        }
    }

}

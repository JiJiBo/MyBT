package com.rulerbug.bt;

import android.content.Context;
import android.content.Intent;

public class BluetoothUtils {
    /**
     * 用于建立十六进制字符的输出的大写字符数组
     */
    private static final char[] DIGITS_UPPER = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
    /**
     * 用于建立十六进制字符的输出的小写字符数组
     */
    private static final char[] DIGITS_LOWER = {'0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    public static String BytesToString(byte[] inHex, int nOff, int nLen) {
        int i;
        StringBuilder strResult = new StringBuilder();
        strResult.append("");
        for (i = 0; i < nLen; i++) {
            strResult.append(String
                    .valueOf(DIGITS_UPPER[(0xF0 & inHex[nOff + i]) >>> 4]));
            strResult.append(String
                    .valueOf(DIGITS_UPPER[inHex[nOff + i] & 0x0F]));
        }
        return strResult.toString();
    }

    public static void CloseBluetooth(Context mc) {
        Intent intent = new Intent();
        intent.setAction(BluetoothService.CLOSE_BLUETOOTH);
        mc.sendBroadcast(intent);
    }
}

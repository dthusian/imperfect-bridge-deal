package dev.wateralt.mc.bridgeforge;

import java.io.IOException;
import java.io.InputStream;

public class Util {
    public static byte[] intToByteArray(int value) {
        return new byte[] {
            (byte)value,
            (byte)(value >>> 8),
            (byte)(value >>> 16),
            (byte)(value >>> 24)};
    }

    public static int byteArrayToInt(byte[] bytes) {
        return ((int)bytes[0]) | ((int)bytes[1] << 8) | ((int)bytes[2] << 16) | ((int)bytes[3] << 24);
    }

    public static void readBlocking(InputStream stream, byte[] output) throws IOException {
        int readSoFar = 0;
        while (readSoFar < output.length) {
            int bytesRead = stream.read(output, readSoFar, output.length - readSoFar);
            if (bytesRead < 0) {
                throw new IOException("Unexpected EOF");
            }
            readSoFar += bytesRead;
        }
    }
}

package tests.javax.sound.sampled;

public class SampleUtils {

    public static void toFrame(byte[] buf, double leftAmp, double rightAmp) {
        long wordValue = (long) (leftAmp * 32767d);
        buf[0] = (byte) (wordValue >> 8);
        buf[1] = (byte) (wordValue & 0xFF);
        wordValue = (long) (rightAmp * 32767d);
        buf[2] = (byte) (wordValue >> 8);
        buf[3] = (byte) (wordValue & 0xFF);
    }

    public static void toFrame(byte[] buf, int offset, double leftAmp, double rightAmp) {
        long wordValue = (long) (leftAmp * 32767d);
        buf[offset + 0] = (byte) (wordValue >> 8);
        buf[offset + 1] = (byte) (wordValue & 0xFF);
        wordValue = (long) (rightAmp * 32767d);
        buf[offset + 2] = (byte) (wordValue >> 8);
        buf[offset + 3] = (byte) (wordValue & 0xFF);
    }

}

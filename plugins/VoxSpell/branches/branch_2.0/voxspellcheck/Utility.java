
package voxspellcheck;

import java.io.DataInput;
import java.io.RandomAccessFile;
import java.io.IOException;
import java.io.DataInputStream;
import java.io.ByteArrayInputStream;

import java.nio.ByteBuffer;

public class Utility
{
    protected static int convert_msb(byte data_[], int offset, int length)
    {
        // getc, used in vimspell, returns an unsigned char cast to int. We
        // mimic that here by padding 1 byte reads to 2 bytes to make them
        // unsigned.
        int adjusted_length = 1;
        while (adjusted_length < length)
            adjusted_length = adjusted_length << 1;
        
        byte data[] = new byte[adjusted_length];
        java.util.Arrays.fill(data, (byte)0);
        System.arraycopy(data_, offset, data, adjusted_length - length, length);
        ByteBuffer buffer = ByteBuffer.wrap(data, 0, adjusted_length);
        buffer.order(java.nio.ByteOrder.BIG_ENDIAN);
        switch (adjusted_length) {
        case 2:
            return buffer.getShort();
        case 4:
            return buffer.getInt();
        default:
            System.out.println("ERROR: Invalid length passed to convert_msd");
            System.exit(1);
        }
        return 0;
    }
    
    protected static int convert_msb(DataInput input, int length) throws IOException
    {
        // getc, used in vimspell, returns an unsigned char cast to int. We
        // mimic that here by padding 1 byte reads to 2 bytes to make them
        // unsigned.
        int adjusted_length = 2;
        while (adjusted_length < length)
            adjusted_length = adjusted_length << 1;
        
        byte data[] = new byte[adjusted_length];
        java.util.Arrays.fill(data, (byte)0);
        
        for (int i = (adjusted_length - length); i < adjusted_length; ++i) {
            data[i] = input.readByte();
        }
        ByteBuffer buffer = ByteBuffer.wrap(data, 0, adjusted_length);
        buffer.order(java.nio.ByteOrder.BIG_ENDIAN);
        switch (adjusted_length) {
        case 2:
            return buffer.getShort();
        case 4:
            return buffer.getInt();
        default:
            System.out.println("ERROR: Invalid length passed to convert_msd");
            System.exit(1);
        }
        return 0;
    }
}
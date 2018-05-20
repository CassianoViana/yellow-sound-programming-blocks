package com.viana.soundprogramming.sound.stream;

import java.io.*;

/**
 * Replacement for a DataInputStream that provides both little and big endian reading capabilities for convenience without need to implement a ByteBuffer
 *
 * https://stackoverflow.com/questions/11000933/using-androids-audiotrack-to-combine-bytes-of-sound-samples-produces-noise
 * https://stackoverflow.com/questions/8028094/java-datainputstream-replacement-for-endianness
 *
 * @author Bill (unspecified.specification@gmail.com)
 */
public class EndianInputStream extends InputStream implements DataInput {
    private DataInputStream dataInStream;
    private InputStream inStream;
    private byte byteBuffer[];

    /**
     * Constructor to wrap InputStream for little and big endian data
     *
     * @param refInStream Inputstream to wrap
     */
    public EndianInputStream(InputStream refInStream) {
        inStream = refInStream;
        dataInStream = new DataInputStream(inStream);
        byteBuffer = new byte[8]; // Largest data type is 64-bits (8 bytes)
    }

    @Override
    public int available() throws IOException {
        return dataInStream.available();
    }

    @Override
    public final int read(byte refBuffer[], int offset, int readLen) throws IOException {
        return inStream.read(refBuffer, offset, readLen);
    }

    @Override
    public int read() throws IOException {
        return inStream.read();
    }

    @Override
    public final int readUnsignedByte() throws IOException {
        return dataInStream.readUnsignedByte();
    }

    @Deprecated
    @Override
    public final String readLine() throws IOException {
        return dataInStream.readLine();
    }

    @Override
    public final String readUTF() throws IOException {
        return dataInStream.readUTF();
    }

    @Override
    public final void close() throws IOException {
        dataInStream.close();
    }

    @Override
    public final void readFully(byte refBuffer[]) throws IOException {
        dataInStream.readFully(refBuffer, 0, refBuffer.length);
    }

    @Override
    public final void readFully(byte refBuffer[], int offset, int readLen) throws IOException {
        dataInStream.readFully(refBuffer, offset, readLen);
    }

    @Override
    public final int skipBytes(int n) throws IOException {
        return dataInStream.skipBytes(n);
    }

    @Override
    public final boolean readBoolean() throws IOException {
        return dataInStream.readBoolean();
    }

    @Override
    public final byte readByte() throws IOException {
        return dataInStream.readByte();
    }

    @Override
    public final float readFloat() throws IOException {
        return Float.intBitsToFloat(readInt());
    }

    @Override
    public final double readDouble() throws IOException {
        return Double.longBitsToDouble(readLong());
    }

    @Override
    public final short readShort() throws IOException {
        return dataInStream.readShort();
    }

    @Override
    public final int readUnsignedShort() throws IOException {
        return dataInStream.readUnsignedShort();
    }

    @Override
    public final long readLong() throws IOException {
        return dataInStream.readLong();
    }

    @Override
    public final char readChar() throws IOException {
        return dataInStream.readChar();
    }

    @Override
    public final int readInt() throws IOException {
        return dataInStream.readInt();
    }

    /**
     * Reads floating point type stored in little endian (see readFloat() for big endian)
     *
     * @return float value translated from little endian
     * @throws IOException if an IO error occurs
     */
    public final float readLittleFloat() throws IOException {
        return Float.intBitsToFloat(readLittleInt());
    }

    /**
     * Reads double precision floating point type stored in little endian (see readDouble() for big endian)
     *
     * @return double precision float value translated from little endian
     * @throws IOException if an IO error occurs
     */
    public final double readLittleDouble() throws IOException {
        return Double.longBitsToDouble(readLittleLong());
    }

    /**
     * Reads short type stored in little endian (see readShorts() for big endian)
     *
     * @return short value translated from little endian
     * @throws IOException if an IO error occurs
     */
    public final short readLittleShort() throws IOException {
        dataInStream.readFully(byteBuffer, 0, 2);
        return (short) ((byteBuffer[1] & 0xff) << 8 | (byteBuffer[0] & 0xff));
    }

    /**
     * Reads char (16-bits) type stored in little endian (see readChar() for big endian)
     *
     * @return char value translated from little endian
     * @throws IOException if an IO error occurs
     */
    public final char readLittleChar() throws IOException {
        dataInStream.readFully(byteBuffer, 0, 2);
        return (char) ((byteBuffer[1] & 0xff) << 8 | (byteBuffer[0] & 0xff));
    }

    /**
     * Reads integer type stored in little endian (see readInt() for big endian)
     *
     * @return integer value translated from little endian
     * @throws IOException if an IO error occurs
     */
    public final int readLittleInt() throws IOException {
        dataInStream.readFully(byteBuffer, 0, 4);
        return (byteBuffer[3]) << 24 | (byteBuffer[2] & 0xff) << 16 |
                (byteBuffer[1] & 0xff) << 8 | (byteBuffer[0] & 0xff);
    }

    /**
     * Reads long type stored in little endian (see readLong() for big endian)
     *
     * @return long value translated from little endian
     * @throws IOException if an IO error occurs
     */
    public final long readLittleLong() throws IOException {
        dataInStream.readFully(byteBuffer, 0, 8);
        return (long) (byteBuffer[7]) << 56 | (long) (byteBuffer[6] & 0xff) << 48 |
                (long) (byteBuffer[5] & 0xff) << 40 | (long) (byteBuffer[4] & 0xff) << 32 |
                (long) (byteBuffer[3] & 0xff) << 24 | (long) (byteBuffer[2] & 0xff) << 16 |
                (long) (byteBuffer[1] & 0xff) << 8 | (long) (byteBuffer[0] & 0xff);
    }

    /**
     * Reads unsigned short type stored in little endian (see readUnsignedShort() for big endian)
     *
     * @return integer value representing unsigned short value translated from little endian
     * @throws IOException if an IO error occurs
     */
    public final int readLittleUnsignedShort() throws IOException {
        dataInStream.readFully(byteBuffer, 0, 2);
        return ((byteBuffer[1] & 0xff) << 8 | (byteBuffer[0] & 0xff));
    }
}

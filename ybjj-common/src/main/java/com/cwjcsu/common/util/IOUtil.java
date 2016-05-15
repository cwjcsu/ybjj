package com.cwjcsu.common.util;

import java.io.*;

/**
 * @author atlas
 * @date 2012-10-9
 */
public final class IOUtil {

    private IOUtil() {

    }

    public static void copy(InputStream is, OutputStream os) throws IOException {
        copy(is, os, -1);
    }

    /**
     * 读取的数据如果超过max bytes，则抛出异常
     *
     * @param is
     * @param os
     * @param max
     * @throws IOException
     */
    public static void copy(InputStream is, OutputStream os, long max)
            throws IOException {
        byte[] buffer = new byte[1024];
        int read = 0;
        long readSum = 0;
        while ((read = is.read(buffer)) != -1) {
            os.write(buffer, 0, read);
            if (max > 0) {
                readSum += read;
                if (readSum > max) {
                    throw new IOException("exceed max size :" + max);
                }
            }
        }
    }

    public static void copy(File from, File to) throws IOException {
        if (from.equals(to)) {
            return;
        }
        if (from.exists()) {
            throw new IOException("Source file \"" + from + "\" not exists");
        }
        if (to.isDirectory()) {
            throw new IOException("Target file \"" + to + "\" is a directory");
        }
        if(!to.exists()) {
            File dir = to.getParentFile();
            if (!dir.exists() && !dir.mkdirs() && !dir.isDirectory()) {
                throw new IOException("Unable to create directory  \"" + dir + "\"");
            }
            try {
                to.createNewFile();
            } catch (Exception e){
                throw new IOException("Unable to create target file  \"" + to + "\"");
            }
        }
        InputStream is = null;
        OutputStream os = null;
        try {
            is = new FileInputStream(from);
            os = new FileOutputStream(to);
            copy(is, os);
        } catch (IOException e) {
            throw e;
        } finally {
            closeQuietly(is);
            closeQuietly(os);
        }
    }

    public static void closeQuietly(Closeable c) {
        try {
            if (c != null)
                c.close();
        } catch (IOException e) {
        }
    }

    public static String getStreamContent(InputStream is, String charset)
            throws IOException {
        return getStreamContent(is, charset, -1);
    }

    public static String getStreamContent(InputStream is, String charset,
                                          int buffsize, long max) throws IOException {
        if (charset == null) {
            charset = "UTF-8";
        }
        if (buffsize <= 0) {
            buffsize = 10240;
        }
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream(buffsize);
            copy(is, os, max);
            return os.toString(charset);
        } catch (IOException e) {
            throw e;
        }
    }

    public static String getStreamContent(InputStream is, String charset,
                                          int buffsize) throws IOException {
        return getStreamContent(is, charset, buffsize, -1);
    }

    public static String getStreamContent(InputStream is) throws IOException {
        return getStreamContent(is, null, -1);
    }

    public static String getStreamContent(InputStream is, int buffsize)
            throws IOException {
        return getStreamContent(is, null, buffsize);
    }

    public static void copyWriter(BufferedReader input, StringWriter writer)
            throws IOException {
        char[] cbuf = new char[1024];
        int read = 0;
        while ((read = input.read(cbuf)) != -1) {
            writer.write(cbuf, 0, read);
        }
    }
}

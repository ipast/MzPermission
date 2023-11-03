package com.ipast.permission.utils;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

public class CloseUtils {

    public static void close(Closeable... closeables) throws IOException {
        if (closeables != null) {
            int i = closeables.length;
            for (int j = 0; j < i; j++) {
                Closeable closeable = closeables[j];
                if (closeable == null)
                    continue;
                closeable.close();
            }
        }
    }

    public static void closeQuietly(Closeable... closeables) {
        try {
            close(closeables);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}

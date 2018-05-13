package com.intrix.social.chat.networking;


import com.intrix.social.chat.abstractions.ProgressListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import retrofit.mime.TypedFile;

/**
 * Created by yarolegovich on 8/6/15.
 */
public class ProgressiveTypedFile extends TypedFile {

    private static final int BUFFER_SIZE = 4096;

    private static ProgressListener sListener;

    public ProgressiveTypedFile(String mimeType, File file) {
        super(mimeType, file);
    }

    public static void setListener(ProgressListener listener) {
        sListener = listener;
    }

    @Override
    public void writeTo(OutputStream out) throws IOException {
        byte[] buffer = new byte[BUFFER_SIZE];
        FileInputStream in = new FileInputStream(super.file());
        int total = 0;
        try {
            int read;
            while ((read = in.read(buffer)) != -1) {
                total += read;
                out.write(buffer, 0, read);
                if (sListener != null)
                    sListener.transferred(total);
            }
        } finally {
            in.close();
        }
    }
}
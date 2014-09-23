/*
 * Copyright (C) 2014 Alex Korovyansky.
 */
package com.alexkorovyansky.wearpomodoro.helpers;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class PomodoroUtils {

    public static byte[] readRawResourceBytes(Resources resources, int rawId) {
        InputStream input = resources.openRawResource(rawId);
        try {
            return toByteArray(input);
        } catch (IOException e) {
            return null;
        } finally {
            closeQuietly(input);
        }
    }

    public static float dipToPixels(Context context, float dipValue) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics);
    }

    private static byte[] toByteArray(final InputStream input) throws IOException {
        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        copy(input, output);
        return output.toByteArray();
    }

    private static long copy(final InputStream input, final OutputStream output) throws IOException {
        final byte[] buffer = new byte[1024 * 4];
        long count = 0;
        int n;
        int EOF = -1;
        while (EOF != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }

    private static void closeQuietly(Closeable stream) {
        if (stream != null) {
            try {
                stream.close();
            } catch (IOException e) {
                // do nothing
            }
        }
    }

    private PomodoroUtils() {

    }
}

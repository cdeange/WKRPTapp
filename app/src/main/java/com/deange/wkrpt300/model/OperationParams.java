package com.deange.wkrpt300.model;

import android.util.Pair;

public interface OperationParams {

    public static class Get {

    }

    public static class Post {
        public final int rawResourceId;
        public final String fileName;

        public Post(final int rawResourceId, final String fileName) {
            this.rawResourceId = rawResourceId;
            this.fileName = fileName;
        }
    }

    public static class Multipart {
        public final Pair formField;
        public final String fileName;
        public final int rawResourceId;

        public Multipart(final Pair formField, final String fileName, final int rawResourceId) {
            this.formField = formField;
            this.fileName = fileName;
            this.rawResourceId = rawResourceId;
        }
    }

    public static class Image {

    }

}

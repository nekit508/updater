package com.nekit508.updater;

import arc.files.Fi;

import java.io.InputStream;
import java.net.URL;

public class RemoteFile {
    public URL url;
    public String relativePath;

    public RemoteFile(URL u, String rp) {
        url = u;
        relativePath = rp;
    }

    public Fi getFi(Fi root) {
        return root.child(relativePath);
    }

    public InputStream stream() {
        try {
            return url.openStream();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

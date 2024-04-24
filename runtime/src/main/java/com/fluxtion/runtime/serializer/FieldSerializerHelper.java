package com.fluxtion.runtime.serializer;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public interface FieldSerializerHelper {

    static URI buildUri(String uriString) {
        try {
            return new URI(uriString);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    static URL buildUrl(String uriString) {
        try {
            return new URI(uriString).toURL();
        } catch (MalformedURLException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}

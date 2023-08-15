package com.fluxtion.compiler.generation.serialiser;

import com.fluxtion.runtime.serializer.FieldSerializerHelper;
import lombok.SneakyThrows;
import org.apache.commons.text.StringEscapeUtils;

import java.io.File;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URL;

public interface IoSerializer {

    static String fileToSource(FieldContext<File> fieldContext) {
        fieldContext.getImportList().add(File.class);
        File file = fieldContext.getInstanceToMap();
        return "new File(\"" + StringEscapeUtils.escapeJava(file.getPath()) + "\")";
    }

    @SneakyThrows
    static String uriToSource(FieldContext<URI> fieldContext) {
        fieldContext.getImportList().add(URI.class);
        fieldContext.getImportList().add(FieldSerializerHelper.class);
        URI uri = fieldContext.getInstanceToMap();
        return "FieldSerializerHelper.buildUri(\"" + StringEscapeUtils.escapeJava(uri.toString()) + "\")";
    }

    @SneakyThrows
    static String urlToSource(FieldContext<URL> fieldContext) {
        fieldContext.getImportList().add(URL.class);
        fieldContext.getImportList().add(FieldSerializerHelper.class);
        URL url = fieldContext.getInstanceToMap();
        return "FieldSerializerHelper.buildUrl(\"" + StringEscapeUtils.escapeJava(url.toString()) + "\")";
    }

    @SneakyThrows
    static String inetSocketAddressToSource(FieldContext<InetSocketAddress> fieldContext) {
        fieldContext.getImportList().add(InetSocketAddress.class);
        InetSocketAddress inetSockeAddress = fieldContext.getInstanceToMap();
        return "InetSocketAddress.createUnresolved(" +
                "\"" + StringEscapeUtils.escapeJava(inetSockeAddress.getHostString()) + "\", "
                + inetSockeAddress.getPort() + ")";
    }

}

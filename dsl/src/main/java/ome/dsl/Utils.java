package ome.dsl;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;

public class Utils {
    public static Collection<File> getFilesInRes(String directory, String wildCard) {
        URL url = Utils.class.getClassLoader().getResource(directory);
        if (url != null) {
            try {
                File dir = new File(url.toURI());
                return FileUtils.listFiles(dir, new WildcardFileFilter(wildCard), null);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
        return Collections.emptyList();
    }
}

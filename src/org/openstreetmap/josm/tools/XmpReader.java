package org.openstreetmap.josm.tools;

import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.imaging.jpeg.JpegProcessingException;
import com.drew.metadata.Metadata;
import com.drew.metadata.xmp.XmpDirectory;
import org.openstreetmap.josm.data.coor.LatLon;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import static java.lang.Double.parseDouble;
import static java.util.Collections.singleton;

public class XmpReader {
    public static LatLon readLatLon(File filename) {
        try {
            final Metadata metadata = JpegMetadataReader.readMetadata(filename, singleton(new com.drew.metadata.xmp.XmpReader()));
            final XmpDirectory dirGps = metadata.getFirstDirectoryOfType(XmpDirectory.class);
            return readLatLon(dirGps);
        } catch (JpegProcessingException | IOException e) {
            Logging.error(e);
        }
        return null;
    }

    public static LatLon readLatLon(XmpDirectory dirXmp) {
        if (dirXmp != null) {
            Map<String, String> props = dirXmp.getXmpProperties();
            Double lat = readAxis(props, "exif:GPSLatitude", 'S');
            Double lon = readAxis(props, "exif:GPSLongitude", 'W');
            if (lat != null && lon != null) {
                return new LatLon(lat, lon);
            }
        }
        return null;
    }

    private static Double readAxis(Map<String, String> props, String prop, char negSuffix) {
        String val = props.get(prop);
        if (val != null && val.length() >= 2) {
            String degStr;
            String minStr = "0";
            String secStr = "0";
            char suffix = val.charAt(val.length() - 1);
            int minIdx = val.indexOf(',');
            if (minIdx != -1) {
                degStr = val.substring(0, minIdx);
                int secIdx = val.indexOf(',', minIdx + 1);
                if (secIdx != -1) {
                    minStr = val.substring(minIdx + 1, secIdx);
                    secStr = val.substring(secIdx + 1, val.length() - 1);
                } else {
                    minStr = val.substring(minIdx + 1, val.length() - 1);
                }
            } else {
                degStr = val.substring(0, val.length() - 1);
            }
            double v = parseDouble(secStr)/3600 + parseDouble(minStr)/60 + parseDouble(degStr);
            if (suffix == negSuffix) {
                v = -v;
            }
            return v;
        }
        return null;
    }
}

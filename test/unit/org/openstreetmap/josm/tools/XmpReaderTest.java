// License: GPL. For details, see LICENSE file.
package org.openstreetmap.josm.tools;

import static org.junit.Assert.*;
import static org.openstreetmap.josm.data.coor.conversion.AbstractCoordinateFormat.newUnlocalizedDecimalFormat;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.junit.Rule;
import org.junit.Test;
import org.openstreetmap.josm.TestUtils;
import org.openstreetmap.josm.data.coor.LatLon;
import org.openstreetmap.josm.data.coor.conversion.AbstractCoordinateFormat;
import org.openstreetmap.josm.testutils.JOSMTestRules;

import java.io.File;
import java.text.DecimalFormat;

/**
 * XMP metadata extraction test
 */
public class XmpReaderTest {
    /**
     * Set the timezone and timeout.
     */
    @Rule
    @SuppressFBWarnings(value = "URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD")
    public JOSMTestRules test = new JOSMTestRules();

    /**
     * Test file with no XMP data in it
     */
    @Test
    public void testNoXmp() {
        File file = new File("data_nodist/exif-example_orientation=6.jpg"); // no xmp
        assertNull(XmpReader.readLatLon(file));
    }

    /**
     * Test file with broken XMP data which should be ignored
     */
    @Test
    public void testBrokenXmp() {
        File file = new File("data_nodist/exif-example_orientation=3.jpg"); // got xmp but it's bad so renders empty xmp directory
        assertNull(XmpReader.readLatLon(file));
    }

    /**
     * Test coordinates extraction from file with XMP data
     */
    @Test
    public void testXmpCoords() {
        File file = new File(TestUtils.getTestDataRoot() + "20190913_005052-betterxmp.jpg"); // valid xmp with better coords in xmp
        LatLon latlon = XmpReader.readLatLon(file);
        assertNotNull(latlon);

        assertEquals("43°37'38.3370\"", degreesMinutesSecondsCmPrecsion(latlon.lat()));
        assertEquals("10°17'19.0458\"", degreesMinutesSecondsCmPrecsion(latlon.lon()));
    }

    /**
     * Copy-pasted from @{link {@link AbstractCoordinateFormat} to get sub-cm formatting precision.
     */
    public static String degreesMinutesSecondsCmPrecsion(double pCoordinate) {
        DecimalFormat DMS_MINUTE_FORMATTER = newUnlocalizedDecimalFormat("00");
        DecimalFormat DMS_SECOND_FORMATTER = newUnlocalizedDecimalFormat("00.0000");
        String DMS60 = DMS_SECOND_FORMATTER.format(60.0);
        String DMS00 = DMS_SECOND_FORMATTER.format(0.0);

        double tAbsCoord = Math.abs(pCoordinate);
        int tDegree = (int) tAbsCoord;
        double tTmpMinutes = (tAbsCoord - tDegree) * 60;
        int tMinutes = (int) tTmpMinutes;
        double tSeconds = (tTmpMinutes - tMinutes) * 60;

        String sDegrees = Integer.toString(tDegree);
        String sMinutes = DMS_MINUTE_FORMATTER.format(tMinutes);
        String sSeconds = DMS_SECOND_FORMATTER.format(tSeconds);

        if (DMS60.equals(sSeconds)) {
            sSeconds = DMS00;
            sMinutes = DMS_MINUTE_FORMATTER.format(tMinutes+1L);
        }
        if ("60".equals(sMinutes)) {
            sMinutes = "00";
            sDegrees = Integer.toString(tDegree+1);
        }

        return sDegrees + '\u00B0' + sMinutes + '\'' + sSeconds + '\"';
    }
}

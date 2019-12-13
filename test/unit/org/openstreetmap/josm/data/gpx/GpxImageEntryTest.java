// License: GPL. For details, see LICENSE file.
package org.openstreetmap.josm.data.gpx;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.openstreetmap.josm.tools.XmpReaderTest.degreesMinutesSecondsCmPrecsion;

import java.io.File;

import org.junit.Rule;
import org.junit.Test;
import org.openstreetmap.josm.TestUtils;
import org.openstreetmap.josm.data.coor.LatLon;
import org.openstreetmap.josm.testutils.JOSMTestRules;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

/**
 * Unit tests of {@link GpxImageEntry} class.
 */
public class GpxImageEntryTest {

    /**
     * Setup test.
     */
    @Rule
    @SuppressFBWarnings(value = "URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD")
    public JOSMTestRules test = new JOSMTestRules();

    /**
     * Unit test of methods {@link GpxImageEntry#equals} and {@link GpxImageEntry#hashCode}.
     */
    @Test
    public void testEqualsContract() {
        TestUtils.assumeWorkingEqualsVerifier();
        EqualsVerifier.forClass(GpxImageEntry.class).usingGetClass()
            .suppress(Warning.NONFINAL_FIELDS)
            .withPrefabValues(GpxImageEntry.class, new GpxImageEntry(new File("foo")), new GpxImageEntry(new File("bar")))
            .verify();
    }

    /**
     * Test that coordinate extraction works for file with no XMP coordinates in it, but with valid EXIF coordinates
     */
    @Test
    public void testCoordNoXmpValidExif() {

        GpxImageEntry entry = new GpxImageEntry(new File("data_nodist/exif-example_orientation=6.jpg")); // no xmp, valid exif
        entry.extractExif();
        LatLon latlon = entry.getExifCoor();
        assertNotNull(latlon);
        assertEquals("51°03'05.8273\"", degreesMinutesSecondsCmPrecsion(latlon.lat()));
        assertEquals("13°44'25.8976\"", degreesMinutesSecondsCmPrecsion(latlon.lon()));
    }

    /**
     * Test that coordinate extraction works for file with broken XMP data which should be ignored, but with valid EXIF coordinates
     */
    @Test
    public void testCoordBrokenXmpValidExif() {
        GpxImageEntry entry = new GpxImageEntry(new File("data_nodist/exif-example_orientation=3.jpg")); // got xmp but it's bad so renders empty xmp directory, valid exif
        entry.extractExif();
        LatLon latlon = entry.getExifCoor();
        assertNotNull(latlon);
        assertEquals("53°19'17.8380\"", degreesMinutesSecondsCmPrecsion(latlon.lat()));
        assertEquals("12°56'49.0440\"", degreesMinutesSecondsCmPrecsion(latlon.lon()));
    }

    /**
     * Test that coordinate extraction works for file with valid XMP &amp; EXIF coordinates, where the XMP data has better coordinates
     */
    @Test
    public void testCoordValidBetterXmpValidExif() {
        GpxImageEntry entry = new GpxImageEntry(new File(TestUtils.getTestDataRoot() + "20190913_005052-betterxmp.jpg")); // valid xmp, valid exif (with better coords in xmp)
        entry.extractExif();
        LatLon latlon = entry.getExifCoor();
        assertNotNull(latlon);
        assertEquals("43°37'38.3370\"", degreesMinutesSecondsCmPrecsion(latlon.lat()));
        assertEquals("10°17'19.0458\"", degreesMinutesSecondsCmPrecsion(latlon.lon()));
    }

    /*
     * Test that coordinate extraction works for file with valid XMP &amp; EXIF coordinates, where the EXIF data has better coordinates
     */
    @Test
    public void testCoordValidValidXmpBetterExif() {
        GpxImageEntry entry = new GpxImageEntry(new File(TestUtils.getTestDataRoot() + "20190913_005052-betterexif.jpg")); // valid xmp, valid exif (with better coords in xmp)
        entry.extractExif();
        LatLon latlon = entry.getExifCoor();
        assertNotNull(latlon);
        assertEquals("43°37'01.1111\"", degreesMinutesSecondsCmPrecsion(latlon.lat()));
        assertEquals("10°17'19.0000\"", degreesMinutesSecondsCmPrecsion(latlon.lon()));
    }
}

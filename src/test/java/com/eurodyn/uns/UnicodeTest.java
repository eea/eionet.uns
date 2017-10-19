package com.eurodyn.uns;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.eurodyn.uns.ApplicationTestContext;
import com.eurodyn.uns.util.TestUtils;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.junit.Before;
import org.junit.Test;

import com.eurodyn.uns.dao.jdbc.BaseJdbcDao;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.sql.DataSource;

import static com.eurodyn.uns.dao.jdbc.BaseJdbcDao.closeAllResources;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Some Unicode related tests.
 *
 * @author jaanus.heinlaid@gmail.com
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ApplicationTestContext.class })
public class UnicodeTest {

    @Autowired
    private DataSource ds;

    @Before
    public void setup() throws Exception {
        TestUtils.setUpDatabase(ds, "seed-unicode.xml");
    }

    /**
     * A round-trip database storage-retrieval test for UTF-8 characters, executed on the NOTIFICATION_TEMPLATE table.
     * The number of letters and spaces of the test string is 23. The number of bytes is 44. A UNICODE-aware
     * database is expected to return 23. Otherwise a LEFT() or SUBSTRING() database function can cut the string in the
     * middle of the letter.
     *
     * @throws Exception when any sort of error happens.
     */
    @Test
    public void testUnicodeRoundtrip() throws Exception {

        // Russian for "environmental protection".
        String testStr = "охрана окружающей среды";
        //int testStrBytesLength = testStr.getBytes("UTF-8").length;
        int testStrBytesLength = 23;

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = BaseJdbcDao.getConnection();

            pstmt = conn.prepareStatement("insert into NOTIFICATION_TEMPLATE (NAME, SUBJECT, TEXT_PLAIN) values ('Name', ?, ?)");
            pstmt.setString(1, testStr);
            pstmt.setString(2, testStr);
            pstmt.executeUpdate();
            closeAllResources(null, pstmt, null);

            pstmt =
                    conn.prepareStatement("select SUBJECT, TEXT_PLAIN, char_length(SUBJECT), char_length(TEXT_PLAIN)"
                            + " from NOTIFICATION_TEMPLATE limit 1");
            rs = pstmt.executeQuery();
            assertTrue("Expected the result set to contain at least one record", rs.next());

            String subject = rs.getString(1);
            String textPlain = rs.getString(2);
            int subjectLength = rs.getInt(3);
            int textPlainLength = rs.getInt(4);

            assertEquals("Expected SUBJECT to be same as was inserted", testStr, subject);
            assertEquals("Expected TEXT_PLAIN to be same as was inserted", testStr, textPlain);
            assertEquals("Expected SUBJECT length to be " + testStrBytesLength, testStrBytesLength, subjectLength);
            assertEquals("Expected TEXT_PLAIN length to be " + testStrBytesLength, testStrBytesLength, textPlainLength);

        } finally {
            closeAllResources(rs, pstmt, conn);
        }
    }

}


/***************************************************************************
 *   Copyright 2006-2013 by Christian Ihle                                 *
 *   kontakt@usikkert.net                                                  *
 *                                                                         *
 *   This file is part of KouChat.                                         *
 *                                                                         *
 *   KouChat is free software; you can redistribute it and/or modify       *
 *   it under the terms of the GNU Lesser General Public License as        *
 *   published by the Free Software Foundation, either version 3 of        *
 *   the License, or (at your option) any later version.                   *
 *                                                                         *
 *   KouChat is distributed in the hope that it will be useful,            *
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU      *
 *   Lesser General Public License for more details.                       *
 *                                                                         *
 *   You should have received a copy of the GNU Lesser General Public      *
 *   License along with KouChat.                                           *
 *   If not, see <http://www.gnu.org/licenses/>.                           *
 ***************************************************************************/

package net.usikkert.kouchat.android.filetransfer;

import static org.junit.Assert.*;

import java.io.File;

import org.fest.util.Lists;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowContentResolver;
import org.robolectric.tester.android.database.SimpleTestCursor;
import org.robolectric.tester.android.database.TestCursor;

import android.content.ContentResolver;
import android.net.Uri;

/**
 * Test of {@link AndroidFileUtils}.
 *
 * @author Christian Ihle
 */
@RunWith(RobolectricTestRunner.class)
public class AndroidFileUtilsTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private AndroidFileUtils androidFileUtils;

    private ContentResolver contentResolver;
    private SimpleTestCursor cursor;
    private ShadowContentResolver shadowContentResolver;

    @Before
    public void setUp() {
        androidFileUtils = new AndroidFileUtils();

        contentResolver = Robolectric.application.getContentResolver();
        shadowContentResolver = Robolectric.shadowOf(contentResolver);

        cursor = new SimpleTestCursor() {
            @Override
            public int getCount() {
                return results.length; // Not implemented in SimpleTestCursor
            }

            @Override
            public boolean moveToFirst() {
                return moveToNext(); // Not implemented in SimpleTestCursor
            }
        };
    }

    @Test
    public void getFileFromUriShouldThrowExceptionIfContentResolverIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("ContentResolver can not be null");

        androidFileUtils.getFileFromContentUri(Uri.EMPTY, null);
    }

    @Test
    public void getFileFromUriShouldNotQueryAndJustReturnNullIfUriIsNull() {
        shadowContentResolver.setCursor(new TestCursor());

        assertNull(androidFileUtils.getFileFromContentUri(null, contentResolver));
    }

    @Test
    public void getFileFromUriShouldNotQueryAndJustReturnNullIfUriIsWrongType() {
        shadowContentResolver.setCursor(new TestCursor());

        assertNull(androidFileUtils.getFileFromContentUri(Uri.parse("file://home/nothing.txt"), contentResolver));
    }

    @Test
    public void getFileFromUriShouldReturnNullIfCursorIsNullAfterQuery() {
        shadowContentResolver.setCursor(null);

        assertNull(androidFileUtils.getFileFromContentUri(Uri.parse("content://contacts/photos/253"), contentResolver));
    }

    @Test
    public void getFileFromUriShouldReturnNullIfNoResults() {
        shadowContentResolver.setCursor(cursor);

        assertNull(androidFileUtils.getFileFromContentUri(Uri.parse("content://contacts/photos/253"), contentResolver));
    }

    @Test
    public void getFileFromUriShouldReturnFirstResult() {
        shadowContentResolver.setCursor(cursor);
        cursor.setColumnNames(Lists.newArrayList("_data"));
        cursor.setResults(new String[][] {{"/home/user/pictures/dsc0001.jpg", "/home/user/pictures/dsc0002.jpg"}});

        final File fileFromUri = androidFileUtils.getFileFromContentUri(Uri.parse("content://contacts/photos/253"), contentResolver);

        assertNotNull(fileFromUri);
        assertEquals("dsc0001.jpg", fileFromUri.getName());
    }
}
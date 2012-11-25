
/***************************************************************************
 *   Copyright 2006-2012 by Christian Ihle                                 *
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

package net.usikkert.kouchat.android;

import net.usikkert.kouchat.android.controller.MainChatController;
import net.usikkert.kouchat.misc.CommandException;
import net.usikkert.kouchat.net.Messages;
import net.usikkert.kouchat.util.TestClient;
import net.usikkert.kouchat.util.TestUtils;

import com.jayway.android.robotium.solo.Solo;

import android.test.ActivityInstrumentationTestCase2;

/**
 * Tests sending and receiving messages in the main chat.
 *
 * @author Christian Ihle
 */
public class MainChatTest extends ActivityInstrumentationTestCase2<MainChatController> {

    private Solo solo;

    public MainChatTest() {
        super(MainChatController.class);
    }

    public void setUp() {
        solo = new Solo(getInstrumentation(), getActivity());
    }

    public void test01OwnMessageIsShownInChat() {
        TestUtils.writeLine(solo, "This is a new message from myself");
        solo.sleep(500);

        assertTrue(TestUtils.searchText(solo, "This is a new message from myself"));
    }

    public void test02OtherClientMessageIsShownInChat() throws CommandException {
        final TestClient client = new TestClient();
        final Messages messages = client.logon();

        messages.sendChatMessage("Hello, this is a message from someone else");
        assertTrue(solo.searchText("Hello, this is a message from someone else"));

        client.logoff();
    }

    public void test03OrientationSwitchShouldKeepText() {
        assertTrue(solo.searchText("Welcome to KouChat"));

        solo.setActivityOrientation(Solo.PORTRAIT);
        solo.sleep(500);

        assertTrue(solo.searchText("Welcome to KouChat"));
    }

    // Must be verified manually. I haven't found an automated way to verify scrolling yet.
    public void test04OrientationSwitchShouldScrollToBottom() {
        for (int i = 1; i <= 30; i++) {
            TestUtils.writeLine(solo,
                    "This is message number " + i + "! " +
                            "This is message number " + i + "! " +
                            "This is message number " + i + "! " +
                            "This is message number " + i + "! " +
                            "This is message number " + i + "! " +
                            "This is message number " + i + "! " +
                            "This is message number " + i + "! " +
                            "This is message number " + i + "! " +
                            "This is message number " + i + "! " +
                            "This is message number " + i + "! " +
                            "This is message number " + i + "! " +
                            "This is message number " + i + "! ");
        }

        solo.setActivityOrientation(Solo.PORTRAIT);
        solo.sleep(3000); // See if message number 30 is visible
    }

    // This test actually fails if scrolling doesn't work,
    // as the link to click is out of sight because of the previous test.
    public void test05OrientationSwitchShouldKeepLinks() {
        TestUtils.writeLine(solo, "http://kouchat.googlecode.com/");

        solo.sleep(500);
        assertTrue(solo.getCurrentActivity().hasWindowFocus()); // KouChat is in focus
        TestUtils.clickOnText(solo, "http://kouchat.googlecode.com/");
        solo.sleep(1000);
        assertFalse(solo.getCurrentActivity().hasWindowFocus()); // Browser is in focus

        solo.sleep(3000); // Close browser manually now!
        solo.setActivityOrientation(Solo.PORTRAIT);

        solo.sleep(500);
        assertTrue(solo.getCurrentActivity().hasWindowFocus()); // KouChat is in focus
        TestUtils.clickOnText(solo, "http://kouchat.googlecode.com/");
        solo.sleep(1000);
        assertFalse(solo.getCurrentActivity().hasWindowFocus()); // Browser is in focus
    }

    public void test99Quit() {
        TestUtils.quit(solo);
    }

    public void tearDown() {
        solo.finishOpenedActivities();
    }
}

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

package net.usikkert.kouchat.misc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.usikkert.kouchat.event.UserListListener;

/**
 * This is a sorted version of the user list.
 *
 * <p>The users in the list are sorted by nick name,
 * as specified in {@link User#compareTo(User)}.</p>
 *
 * @author Christian Ihle
 */
public class SortedUserList implements UserList {

    /** The list of users in the chat. */
    private final List<User> userList;

    /** The list of listeners of changes to the user list. */
    private final List<UserListListener> listeners;

    /**
     * Constructor.
     */
    public SortedUserList() {
        userList = new ArrayList<User>();
        listeners = new ArrayList<UserListListener>();
    }

    /**
     * Adds the user, and then sorts the list.
     *
     * {@inheritDoc}
     */
    @Override
    public boolean add(final User user) {
        final boolean success = userList.add(user);

        if (success) {
            Collections.sort(userList);
            fireUserAdded(userList.indexOf(user));
        }

        return success;
    }

    /** {@inheritDoc} */
    @Override
    public User get(final int pos) {
        if (pos < userList.size()) {
            return userList.get(pos);
        } else {
            return null;
        }
    }

    /** {@inheritDoc} */
    @Override
    public int indexOf(final User user) {
        return userList.indexOf(user);
    }

    /** {@inheritDoc} */
    @Override
    public User remove(final int pos) {
        final User user = userList.remove(pos);
        fireUserRemoved(pos);

        return user;
    }

    /** {@inheritDoc} */
    @Override
    public boolean remove(final User user) {
        final int pos = userList.indexOf(user);
        final boolean success = userList.remove(user);
        fireUserRemoved(pos);

        return success;
    }

    /**
     * Sets the user, and then sorts the list.
     *
     * {@inheritDoc}
     */
    @Override
    public User set(final int pos, final User user) {
        final User oldUser = userList.set(pos, user);
        Collections.sort(userList);
        fireUserChanged(userList.indexOf(user));

        return oldUser;
    }

    /** {@inheritDoc} */
    @Override
    public int size() {
        return userList.size();
    }

    /** {@inheritDoc} */
    @Override
    public void addUserListListener(final UserListListener listener) {
        listeners.add(listener);
    }

    /** {@inheritDoc} */
    @Override
    public void removeUserListListener(final UserListListener listener) {
        listeners.remove(listener);
    }

    /**
     * Notifies the listeners that a user was added.
     *
     * @param pos The position where the user was added.
     */
    private void fireUserAdded(final int pos) {
        for (final UserListListener listener : listeners) {
            listener.userAdded(pos);
        }
    }

    /**
     * Notifies the listeners that a user was changed.
     *
     * @param pos The position of the changed user.
     */
    private void fireUserChanged(final int pos) {
        for (final UserListListener listener : listeners) {
            listener.userChanged(pos);
        }
    }

    /**
     * Notifies the listeners that a user was removed.
     *
     * @param pos The position of the removed user.
     */
    private void fireUserRemoved(final int pos) {
        for (final UserListListener listener : listeners) {
            listener.userRemoved(pos);
        }
    }
}
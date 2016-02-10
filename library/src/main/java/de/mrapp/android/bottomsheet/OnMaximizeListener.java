/*
 * AndroidBottomSheet Copyright 2016 Michael Rapp
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 */
package de.mrapp.android.bottomsheet;

/**
 * Defines the interface, a class, which should be notified, when a bottom sheet has been maximized,
 * must implement.
 *
 * @author Michael Rapp
 * @since 1.0.0
 */
public interface OnMaximizeListener {

    /**
     * The method, which is invoked, when a bottom sheet has been maximized.
     *
     * @param bottomSheet
     *         The bottom sheet, which has been maximized, as an instance of the class {@link
     *         BottomSheet}
     */
    void onMaximize(BottomSheet bottomSheet);

}
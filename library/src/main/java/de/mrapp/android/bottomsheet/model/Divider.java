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
package de.mrapp.android.bottomsheet.model;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

/**
 * Represents a divider, which can be shown in a bottom sheet.
 *
 * @author Michael Rapp
 * @since 1.0.0
 */
public class Divider extends AbstractItem {

    /**
     * The constant serial version UID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Creates a new divider.
     */
    public Divider() {
        super(null);
    }

    /**
     * Sets the divider's title.
     *
     * @param context
     *         The context, which should be used, as an instance of the class {@link Context}. The
     *         context may not be null
     * @param resourceId
     *         The resource id of the title, which should be set, as an {@link Integer} value. The
     *         resource id must correspond to a valid string resource
     */
    public final void setTitle(@NonNull final Context context, @StringRes final int resourceId) {
        setTitle(context.getText(resourceId));
    }

    @Override
    public final Divider clone() {
        Divider clonedDivider = new Divider();
        clonedDivider.setTitle(getTitle());
        return clonedDivider;
    }

    @Override
    public final String toString() {
        return "Divider [title=" + getTitle() + "]";
    }

}
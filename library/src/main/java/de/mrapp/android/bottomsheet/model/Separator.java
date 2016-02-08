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
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.text.TextUtils;

import java.io.Serializable;

/**
 * Represents a separator, which can be shown in a bottom sheet.
 *
 * @author Michael Rapp
 * @since 1.0.0
 */
public class Separator implements Serializable, Cloneable, Parcelable {

    /**
     * A creator, which allows to create instances of the class {@link Separator} from parcels.
     */
    public static final Creator<Separator> CREATOR = new Creator<Separator>() {

        @Override
        public Separator createFromParcel(final Parcel source) {
            return new Separator(source);
        }

        @Override
        public Separator[] newArray(final int size) {
            return new Separator[size];
        }

    };

    /**
     * The constant serial version UID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The separator's title.
     */
    private CharSequence title;

    /**
     * Creates a new separator.
     *
     * @param source
     *         The parcel, the menu item should be created from, as an instance of the class {@link
     *         Parcel}. The parcel may not be null
     */
    private Separator(@NonNull final Parcel source) {
        this.title = TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(source);
    }

    /**
     * Creates a new separator.
     */
    public Separator() {
        this.title = null;
    }

    /**
     * Returns the separator's title.
     *
     * @return The separator's title as an instance of the type {@link CharSequence} or null, if no
     * title has been set
     */
    public final CharSequence getTitle() {
        return title;
    }

    /**
     * Sets the separator's title.
     *
     * @param title
     *         The title, which should be set, as an instance of the type {@link CharSequence} or
     *         null, if no title should be set
     */
    public final void setTitle(@Nullable final CharSequence title) {
        this.title = title;
    }

    /**
     * Sets the separator's title.
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
    public final Separator clone() {
        Separator clonedSeparator = new Separator();
        clonedSeparator.setTitle(title);
        return clonedSeparator;
    }

    @Override
    public final String toString() {
        return "Separator [title=" + title + "]";
    }

    @Override
    public final int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((title == null) ? 0 : title.hashCode());
        return result;
    }

    @Override
    public final boolean equals(final Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Separator other = (Separator) obj;
        if (title == null) {
            if (other.title != null)
                return false;
        } else if (!title.equals(other.title))
            return false;
        return true;
    }

    @Override
    public final int describeContents() {
        return 0;
    }

    @Override
    public final void writeToParcel(final Parcel dest, final int flags) {
        TextUtils.writeToParcel(getTitle(), dest, flags);
    }

}
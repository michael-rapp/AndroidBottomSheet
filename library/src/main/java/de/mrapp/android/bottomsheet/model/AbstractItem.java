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

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;

import java.io.Serializable;

/**
 * An abstract base class for all items, which can be shown in a bottom sheet.
 *
 * @author Michael Rapp
 * @since 1.0.0
 */
public abstract class AbstractItem implements Serializable, Cloneable, Parcelable {

    /**
     * The constant serial version UID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The item's id.
     */
    private final int id;

    /**
     * Creates a new item.
     *
     * @param source
     *         The parcel, the item should be created from, as an instance of the class {@link
     *         Parcel}. The parcel may not be null
     */
    protected AbstractItem(@NonNull final Parcel source) {
        this.id = source.readInt();
    }

    /**
     * Creates a new item.
     *
     * @param id
     *         The item's id as an {@link Integer} value
     */
    public AbstractItem(final int id) {
        this.id = id;
    }

    /**
     * Returns the item's id.
     *
     * @return The item's id as an {@link Integer} value
     */
    public final int getId() {
        return id;
    }

    @CallSuper
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + id;
        return result;
    }

    @CallSuper
    @Override
    public boolean equals(final Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        AbstractItem other = (AbstractItem) obj;
        if (id != other.id)
            return false;
        return true;
    }

    @Override
    public final int describeContents() {
        return 0;
    }

    @CallSuper
    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeInt(getId());
    }

}
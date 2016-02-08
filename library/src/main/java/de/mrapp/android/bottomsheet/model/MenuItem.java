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
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;

import java.io.Serializable;

import static de.mrapp.android.util.Condition.ensureNotEmpty;
import static de.mrapp.android.util.Condition.ensureNotNull;

/**
 * Represents a menu item, which can be shown in a bottom sheet.
 *
 * @author Michael Rapp
 * @since 1.0.0
 */
public class MenuItem implements Serializable, Cloneable, Parcelable {

    /**
     * A creator, which allows to create instances of the class {@link MenuItem} from parcels.
     */
    public static final Creator<MenuItem> CREATOR = new Creator<MenuItem>() {

        @Override
        public MenuItem createFromParcel(final Parcel source) {
            return new MenuItem(source);
        }

        @Override
        public MenuItem[] newArray(final int size) {
            return new MenuItem[size];
        }

    };

    /**
     * The constant serial version UID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The menu item's title.
     */
    private final CharSequence title;

    /**
     * The menu item's icon.
     */
    private Drawable icon;

    /**
     * Creates a new menu item.
     *
     * @param source
     *         The parcel, the menu item should be created from, as an instance of the class {@link
     *         Parcel}. The parcel may not be null
     */
    @SuppressWarnings("deprecation")
    private MenuItem(@NonNull final Parcel source) {
        this.title = TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(source);
        this.icon =
                new BitmapDrawable((Bitmap) source.readParcelable(Bitmap.class.getClassLoader()));
    }

    /**
     * Creates a new menu item.
     *
     * @param title
     *         The menu item's title as an instance of the type {@link CharSequence}. The title may
     *         neither be null, nor empty
     */
    public MenuItem(@NonNull final CharSequence title) {
        ensureNotNull(title, "The title may not be null");
        ensureNotEmpty(title, "The title may not be empty");
        this.title = title;
        this.icon = null;
    }

    /**
     * Creates a new menu item.
     *
     * @param context
     *         The context, which should be used, as an instance of the class {@link Context}. The
     *         context may not be null
     * @param resourceId
     *         The resource id of the menu item's title as an {@link Integer} value. The resource id
     *         must correspond to a valid string resource
     */
    public MenuItem(@NonNull final Context context, @StringRes final int resourceId) {
        this(context.getText(resourceId));
    }

    /**
     * Returns the menu item's title.
     *
     * @return The menu item's title as an instance of the type {@link CharSequence}
     */
    public final CharSequence getTitle() {
        return title;
    }

    /**
     * Returns the menu item's icon.
     *
     * @return The menu item's icon as an instance of the class {@link Drawable}
     */
    public final Drawable getIcon() {
        return icon;
    }

    /**
     * Sets the menu item's icon.
     *
     * @param icon
     *         The icon, which should be set, as an instance of the class {@link Drawable}, or null,
     *         if no icon should be set
     */
    public final void setIcon(@Nullable final Drawable icon) {
        this.icon = icon;
    }

    /**
     * Sets the menu item's icon.
     *
     * @param context
     *         The context, which should be used, as an instance of the class {@link Context}. The
     *         context may not be null
     * @param resourceId
     *         The resource id of the icon, which should be set, as an {@link Integer} value. The
     *         resource id must correspond to a valid drawable resource
     */
    public final void setIcon(@NonNull final Context context, @DrawableRes final int resourceId) {
        setIcon(ContextCompat.getDrawable(context, resourceId));
    }

    @Override
    public final MenuItem clone() {
        MenuItem clonedMenuItem = new MenuItem(title);
        clonedMenuItem.setIcon(getIcon());
        return clonedMenuItem;
    }

    @Override
    public final String toString() {
        return "MenuItem [title=" + title + ", icon=" + icon + "]";
    }

    @Override
    public final int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + title.hashCode();
        result = prime * result + ((icon == null) ? 0 : icon.hashCode());
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
        MenuItem other = (MenuItem) obj;
        if (!title.equals(other.title))
            return false;
        if (icon == null) {
            if (other.icon != null)
                return false;
        } else if (!icon.equals(other.icon))
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
        Bitmap bitmap = (getIcon() != null && getIcon() instanceof BitmapDrawable) ?
                ((BitmapDrawable) getIcon()).getBitmap() : null;
        dest.writeParcelable(bitmap, flags);
    }

}
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
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;

import static de.mrapp.android.util.Condition.ensureNotEmpty;
import static de.mrapp.android.util.Condition.ensureNotNull;

/**
 * Represents a item, which can be shown in a bottom sheet.
 *
 * @author Michael Rapp
 * @since 1.0.0
 */
public class Item extends AbstractItem {

    /**
     * The constant serial version UID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The item's icon.
     */
    private Drawable icon;

    /**
     * True, if the item is enabled, false otherwise.
     */
    private boolean enabled;

    /**
     * Creates a new item.
     *
     * @param title
     *         The item's title as an instance of the type {@link CharSequence}. The title may
     *         neither be null, nor empty
     */
    public Item(@NonNull final CharSequence title) {
        super(title);
        ensureNotNull(title, "The title may not be null");
        ensureNotEmpty(title, "The title may not be empty");
        this.icon = null;
        this.enabled = true;
    }

    /**
     * Creates a new item.
     *
     * @param context
     *         The context, which should be used, as an instance of the class {@link Context}. The
     *         context may not be null
     * @param resourceId
     *         The resource id of the item's title as an {@link Integer} value. The resource id must
     *         correspond to a valid string resource
     */
    public Item(@NonNull final Context context, @StringRes final int resourceId) {
        this(context.getText(resourceId));
    }

    /**
     * Returns the item's icon.
     *
     * @return The item's icon as an instance of the class {@link Drawable}
     */
    public final Drawable getIcon() {
        return icon;
    }

    /**
     * Sets the item's icon.
     *
     * @param icon
     *         The icon, which should be set, as an instance of the class {@link Drawable}, or null,
     *         if no icon should be set
     */
    public final void setIcon(@Nullable final Drawable icon) {
        this.icon = icon;
    }

    /**
     * Sets the item's icon.
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

    /**
     * Returns, whether the item is enabled, or not.
     *
     * @return True, if the item is enabled, false otherwise
     */
    public final boolean isEnabled() {
        return enabled;
    }

    /**
     * Sets, whether the item should be enabled, or not.
     *
     * @param enabled
     *         True, if the item should be enabled, false otherwise
     */
    public final void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Sets the title of the item.
     *
     * @param context
     *         The context, which should be used, as an instance of the class {@link Context}. The
     *         context may not be null
     * @param resourceId
     *         The resource id of the title, which should be set, as an {@link Integer} value. The
     *         resource id must correspond to a valid string resource
     */
    public final void setTitle(@NonNull final Context context, @StringRes final int resourceId) {
        ensureNotNull(context, "The context may not be null");
        setTitle(context.getText(resourceId));
    }

    @Override
    public final void setTitle(@NonNull final CharSequence title) {
        ensureNotNull(title, "The title may not be null");
        ensureNotEmpty(title, "The title may not be empty");
        super.setTitle(title);
    }

    @Override
    public final Item clone() {
        Item clonedItem = new Item(getTitle());
        clonedItem.setIcon(getIcon());
        clonedItem.setEnabled(isEnabled());
        return clonedItem;
    }

    @Override
    public final String toString() {
        return "Item [title=" + getTitle() + ", icon=" + getIcon() +
                ", enabled=" + isEnabled() + "]";
    }

    @Override
    public final int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((icon == null) ? 0 : icon.hashCode());
        result = prime * result + (enabled ? 1231 : 1237);
        return result;
    }

    @Override
    public final boolean equals(final Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        Item other = (Item) obj;
        if (icon == null) {
            if (other.icon != null)
                return false;
        } else if (!icon.equals(other.icon))
            return false;
        if (enabled != other.enabled)
            return false;
        return true;
    }

}
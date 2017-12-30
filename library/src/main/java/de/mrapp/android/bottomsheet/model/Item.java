/*
 * Copyright 2016 - 2017 Michael Rapp
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package de.mrapp.android.bottomsheet.model;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;

import static de.mrapp.android.util.Condition.ensureAtLeast;
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
     * @param id
     *         The item's id as an {@link Integer} value. The id must be at least 0
     * @param title
     *         The item's title as an instance of the type {@link CharSequence}. The title may
     *         neither be null, nor empty
     */
    public Item(final int id, @NonNull final CharSequence title) {
        super(id, title);
        ensureAtLeast(id, 0, "The id must be at least 0");
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
     * @param id
     *         The item's id as an {@link Integer} value
     * @param resourceId
     *         The resource id of the item's title as an {@link Integer} value. The resource id must
     *         correspond to a valid string resource
     */
    public Item(@NonNull final Context context, final int id, @StringRes final int resourceId) {
        this(id, context.getText(resourceId));
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

    @SuppressWarnings("CloneDoesntCallSuperClone")
    @Override
    public final Item clone() {
        Item clonedItem = new Item(getId(), getTitle());
        clonedItem.setIcon(getIcon());
        clonedItem.setEnabled(isEnabled());
        return clonedItem;
    }

    @Override
    public final String toString() {
        return "Item [id=" + getId() + ", title=" + getTitle() + ", icon=" + getIcon() +
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
        return enabled == other.enabled;
    }

}
/*
 * Copyright 2014 - 2016 Michael Rapp
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

import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;

import java.io.Serializable;

/**
 * An abstract base class for all items, which can be shown in a bottom sheet.
 *
 * @author Michael Rapp
 * @since 1.0.0
 */
public abstract class AbstractItem implements Serializable, Cloneable {

    /**
     * The constant serial version UID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The item's title.
     */
    private CharSequence title;

    /**
     * Creates a new item.
     *
     * @param title
     *         The item's title as an instance of the type {@link CharSequence} or null, if no title
     *         should be set
     */
    public AbstractItem(@Nullable final CharSequence title) {
        this.title = title;
    }

    /**
     * Returns the item's title.
     *
     * @return The items's title as an instance of the type {@link CharSequence} or null, if no
     * title has been set
     */
    public final CharSequence getTitle() {
        return title;
    }

    /**
     * Sets the item's title.
     *
     * @param title
     *         The title, which should be set, as an instance of the type {@link CharSequence} or
     *         null, of no title should be set
     */
    @CallSuper
    public void setTitle(@Nullable  final CharSequence title) {
        this.title = title;
    }

    @CallSuper
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((title == null) ? 0 : title.hashCode());
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
        if (title == null) {
            if (other.title != null)
                return false;
        } else if (!title.equals(other.title))
            return false;
        return true;
    }

}
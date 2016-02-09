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
package de.mrapp.android.bottomsheet.adapter;

import android.content.Context;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import de.mrapp.android.bottomsheet.BottomSheet;
import de.mrapp.android.bottomsheet.R;
import de.mrapp.android.bottomsheet.model.MenuItem;
import de.mrapp.android.bottomsheet.model.Separator;

import static de.mrapp.android.util.Condition.ensureNotNull;

/**
 * An adapter, which manages the menu items of a {@link BottomSheet}.
 *
 * @author Michael Rapp
 * @since 1.0.0
 */
public class BottomSheetAdapter extends BaseAdapter {

    /**
     * The view type, which is used to visualize placeholders.
     */
    private static final int PLACEHOLDER_VIEW_TYPE = 0;

    /**
     * The view type, which is used to visualize menu items.
     */
    private static final int MENU_ITEM_VIEW_TYPE = 1;

    /**
     * The view type, which is used to visualize separators.
     */
    private static final int SEPARATOR_VIEW_TYPE = 2;

    /**
     * The view holder, which is used to visualize menu items.
     */
    private static class MenuItemViewHolder {

        /**
         * The text view, which is used to show a menu item's title.
         */
        private TextView titleTextView;

    }

    /**
     * The context, which is used by the adapter.
     */
    private final Context context;

    /**
     * A list, which contains the items of the adapter.
     */
    private List<Parcelable> items;

    /**
     * True, if the <code>notifyDataSetChange</code>-method is called automatically, when the
     * adapter's items are changed, false otherwise.
     */
    private boolean notifyOnChange;

    /**
     * Notifies, that the adapter's items have been changed.
     */
    private void notifyOnDataSetChanged() {
        if (notifyOnChange) {
            notifyDataSetChanged();
        }
    }

    /**
     * Inflates the view, which is used to visualize a menu item.
     *
     * @param parent
     *         The parent of the view, which should be inflated, as an instance of the class {@link
     *         ViewGroup} or null, if no parent is available
     * @return The view, which has been inflated, as an instance of the class {@link View}
     */
    private View inflateMenuItemView(@Nullable final ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.menu_item, parent, false);
        MenuItemViewHolder viewHolder = new MenuItemViewHolder();
        viewHolder.titleTextView = (TextView) view.findViewById(android.R.id.title);
        view.setTag(viewHolder);
        return view;
    }

    /**
     * Visualizes a specific menu item.
     *
     * @param menuItem
     *         The menu item, which should be visualized, as an instance of the class {@link
     *         MenuItem}. The menu item may not be null
     * @param viewHolder
     *         The view holder, which contains the views, which should be used to visualize the menu
     *         item, as an instance of the class {@link MenuItemViewHolder}. The view holder may not
     *         be null
     */
    private void visualizeMenuItem(@NonNull final MenuItem menuItem,
                                   @NonNull final MenuItemViewHolder viewHolder) {
        viewHolder.titleTextView.setText(menuItem.getTitle());
    }

    /**
     * Creates a new adapter, which manages the menu items of a {@link BottomSheet}.
     *
     * @param context
     *         The context, which should be used by the adapter, as an instance of the class {@link
     *         Context}. The context may not be null
     */
    public BottomSheetAdapter(@NonNull final Context context) {
        ensureNotNull(context, "The context may not be null");
        this.context = context;
        this.items = new ArrayList<>();
        this.notifyOnChange = true;
        items.add(new MenuItem("Item 1"));
        items.add(new MenuItem("Item 2"));
        items.add(new MenuItem("Item 3"));
        items.add(new MenuItem("Item 4"));
        items.add(new MenuItem("Item 5"));
        items.add(new MenuItem("Item 6"));
        items.add(new MenuItem("Item 7"));
        items.add(new MenuItem("Item 8"));
        items.add(new MenuItem("Item 9"));
        items.add(new MenuItem("Item 10"));
        items.add(new MenuItem("Item 11"));
        items.add(new MenuItem("Item 12"));
        items.add(new MenuItem("Item 13"));
        items.add(new MenuItem("Item 14"));
        items.add(new MenuItem("Item 15"));
        items.add(new MenuItem("Item 16"));
        items.add(new MenuItem("Item 17"));
        items.add(new MenuItem("Item 18"));
        items.add(new MenuItem("Item 19"));
        items.add(new MenuItem("Item 20"));
        items.add(new MenuItem("Item 21"));
        items.add(new MenuItem("Item 22"));
        items.add(new MenuItem("Item 23"));
        items.add(new MenuItem("Item 24"));
        items.add(new MenuItem("Item 25"));
        items.add(new MenuItem("Item 26"));
        items.add(new MenuItem("Item 27"));
        items.add(new MenuItem("Item 28"));
        items.add(new MenuItem("Item 29"));
        items.add(new MenuItem("Item 30"));
    }

    /**
     * Adds a new menu item to the adapter.
     *
     * @param menuItem
     *         The menu item, which should be added, as an instance of the class {@link MenuItem}.
     *         The menu item may not be null
     */
    public final void add(@NonNull final MenuItem menuItem) {
        ensureNotNull(menuItem, "The menu item may not be null");
        items.add(menuItem);
        notifyOnDataSetChanged();
    }

    /**
     * Adds a new separator to the adapter.
     *
     * @param separator
     *         The separator, which should be added, as an instance of the class {@link Separator}.
     *         The separator may not be null
     */
    public final void add(@NonNull final Separator separator) {
        ensureNotNull(separator, "The separator may not be null");
        items.add(separator);
        notifyOnDataSetChanged();
    }

    /**
     * Removes the item at a specific index.
     *
     * @param index
     *         The index of the item, which should be removed, as an {@link Integer} value
     */
    public final void remove(final int index) {
        items.remove(index);
        notifyOnDataSetChanged();
    }

    /**
     * Removes all items from the adapter.
     */
    public final void clear() {
        items.clear();
        notifyOnDataSetChanged();
    }

    /**
     * Sets, whether the <code>notifyDataSetChanged</code>-method should be called automatically,
     * when the adapter's items have been changed, or not.
     *
     * @param notifyOnChange
     *         True, if the <code>notifyDataSetChanged</code>-method should be called automatically,
     *         when the adapter's items have been changed, false otherwise
     */
    public final void notifyOnChange(final boolean notifyOnChange) {
        this.notifyOnChange = notifyOnChange;
    }

    @Override
    public final int getCount() {
        return items.size();
    }

    @Override
    public final Parcelable getItem(final int position) {
        return items.get(position);
    }

    @Override
    public final long getItemId(final int position) {
        return position;
    }

    @Override
    public final View getView(final int position, final View convertView, final ViewGroup parent) {
        View view = convertView;
        int viewType = getItemViewType(position);
        Parcelable item = getItem(position);

        if (view == null) {
            if (viewType == PLACEHOLDER_VIEW_TYPE) {

            } else if (viewType == MENU_ITEM_VIEW_TYPE) {
                view = inflateMenuItemView(parent);
            } else {

            }
        }

        if (viewType == MENU_ITEM_VIEW_TYPE) {
            MenuItemViewHolder viewHolder = (MenuItemViewHolder) view.getTag();
            visualizeMenuItem((MenuItem) item, viewHolder);
        } else if (viewType == SEPARATOR_VIEW_TYPE) {

        }

        return view;
    }

    @Override
    public final int getViewTypeCount() {
        return 3;
    }

    @Override
    public final int getItemViewType(final int position) {
        Parcelable item = getItem(position);

        if (item == null) {
            return PLACEHOLDER_VIEW_TYPE;
        } else if (item instanceof MenuItem) {
            return MENU_ITEM_VIEW_TYPE;
        } else {
            return SEPARATOR_VIEW_TYPE;
        }
    }

}
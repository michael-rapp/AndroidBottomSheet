/*
 * Copyright 2016 - 2018 Michael Rapp
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
package de.mrapp.android.bottomsheet.adapter;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import de.mrapp.android.bottomsheet.BottomSheet;
import de.mrapp.android.bottomsheet.BottomSheet.Style;
import de.mrapp.android.bottomsheet.R;
import de.mrapp.android.bottomsheet.model.AbstractItem;
import de.mrapp.android.bottomsheet.model.Divider;
import de.mrapp.android.bottomsheet.model.Item;
import de.mrapp.android.util.DisplayUtil.DeviceType;
import de.mrapp.android.util.DisplayUtil.Orientation;
import de.mrapp.util.Condition;

import static de.mrapp.android.util.DisplayUtil.getDeviceType;
import static de.mrapp.android.util.DisplayUtil.getOrientation;

/**
 * An adapter, which manages the items of a {@link BottomSheet}. It allows to show the items in a
 * list or grid and supports to show dividers.
 *
 * @author Michael Rapp
 * @since 1.0.0
 */
public class DividableGridAdapter extends BaseAdapter {

    /**
     * The view holder, which is used to visualize items.
     */
    private static class ItemViewHolder {

        /**
         * The image view, which is used to show an item's icon.
         */
        private ImageView iconImageView;

        /**
         * The text view, which is used to show an item's title.
         */
        private TextView titleTextView;

    }

    /**
     * The view holder, which is used to visualize dividers.
     */
    private static class DividerViewHolder {

        /**
         * The view, which is used to show the left divider.
         */
        private View leftDivider;

        /**
         * The view, which is used to show the right divider.
         */
        private View rightDivider;

        /**
         * The text view, which is used to show a divider's title.
         */
        private TextView titleTextView;

    }

    /**
     * The view type, which is used to visualize placeholders.
     */
    private static final int PLACEHOLDER_VIEW_TYPE = 0;

    /**
     * The view type, which is used to visualize items.
     */
    private static final int ITEM_VIEW_TYPE = 1;

    /**
     * The view type, which is used to visualize dividers.
     */
    private static final int SEPARATOR_VIEW_TYPE = 2;

    /**
     * The context, which is used by the adapter.
     */
    private final Context context;

    /**
     * The style, which is used to display the adapter's items.
     */
    private Style style;

    /**
     * The number of columns, which are displayed by the adapter.
     */
    private int columnCount;

    /**
     * A list, which contains the items of the adapter.
     */
    private final List<AbstractItem> items;

    /**
     * A list, which contains the items of the adapter including placeholders.
     */
    private List<AbstractItem> rawItems;

    /**
     * The number of items, which contain an icon.
     */
    private int iconCount;

    /**
     * The number of dividers, which are contained by the adapter.
     */
    private int dividerCount;

    /**
     * True, if the <code>notifyDataSetChange</code>-method is called automatically, when the
     * adapter's items are changed, false otherwise.
     */
    private boolean notifyOnChange;

    /**
     * The text color of the adapter's items.
     */
    private int itemColor;

    /**
     * The color of the adapter's dividers.
     */
    private int dividerColor;

    /**
     * Returns a list, which contains the items of the adapter including placeholders.
     *
     * @return A list, which contains the items of the adapter including placeholders, as an
     * instance of the type {@link List} or an empty list, if the adapter contains no items
     */
    private List<AbstractItem> getRawItems() {
        if (rawItems == null) {
            rawItems = new ArrayList<>();

            for (int i = 0; i < items.size(); i++) {
                AbstractItem item = items.get(i);

                if (item instanceof Divider && columnCount > 1) {
                    for (int j = 0; j < rawItems.size() % columnCount; j++) {
                        rawItems.add(null);
                    }

                    rawItems.add(item);

                    for (int j = 0; j < columnCount - 1; j++) {
                        rawItems.add(new Divider());
                    }
                } else {
                    rawItems.add(item);
                }
            }
        }

        return rawItems;
    }

    /**
     * Notifies, that the adapter's items have been changed.
     */
    private void notifyOnDataSetChanged() {
        if (notifyOnChange) {
            notifyDataSetChanged();
        }
    }

    /**
     * Inflates the view, which is used to visualize a placeholder.
     *
     * @param parent
     *         The parent of the view, which should be inflated, as an instance of the class {@link
     *         ViewGroup} or null, if no parent is available
     * @return The view, which has been inflated, as an instance of the class {@link View}
     */
    private View inflatePlaceholderView(@Nullable ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        return layoutInflater.inflate(
                style == Style.GRID ? R.layout.grid_placeholder : R.layout.list_placeholder, parent,
                false);
    }

    /**
     * Inflates the view, which is used to visualize an item.
     *
     * @param parent
     *         The parent of the view, which should be inflated, as an instance of the class {@link
     *         ViewGroup} or null, if no parent is available
     * @return The view, which has been inflated, as an instance of the class {@link View}
     */
    private View inflateItemView(@Nullable final ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater
                .inflate(style == Style.GRID ? R.layout.grid_item : R.layout.list_item, parent,
                        false);
        ItemViewHolder viewHolder = new ItemViewHolder();
        viewHolder.iconImageView = view.findViewById(android.R.id.icon);
        viewHolder.titleTextView = view.findViewById(android.R.id.title);
        view.setTag(viewHolder);
        return view;
    }

    /**
     * Visualizes a specific item.
     *
     * @param item
     *         The item, which should be visualized, as an instance of the class {@link Item}. The
     *         item may not be null
     * @param viewHolder
     *         The view holder, which contains the views, which should be used to visualize the
     *         item, as an instance of the class {@link ItemViewHolder}. The view holder may not be
     *         null
     */
    @SuppressWarnings("PrimitiveArrayArgumentToVariableArgMethod")
    private void visualizeItem(@NonNull final Item item, @NonNull final ItemViewHolder viewHolder) {
        viewHolder.iconImageView.setVisibility(iconCount > 0 ? View.VISIBLE : View.GONE);
        viewHolder.iconImageView.setEnabled(item.isEnabled());

        if (item.getIcon() != null && item.getIcon() instanceof StateListDrawable) {
            StateListDrawable stateListDrawable = (StateListDrawable) item.getIcon();

            try {
                int[] currentState = viewHolder.iconImageView.getDrawableState();
                Method getStateDrawableIndex =
                        StateListDrawable.class.getMethod("getStateDrawableIndex", int[].class);
                Method getStateDrawable =
                        StateListDrawable.class.getMethod("getStateDrawable", int.class);
                int index = (int) getStateDrawableIndex.invoke(stateListDrawable, currentState);
                Drawable drawable = (Drawable) getStateDrawable.invoke(stateListDrawable, index);
                viewHolder.iconImageView.setImageDrawable(drawable);
            } catch (Exception e) {
                viewHolder.iconImageView.setImageDrawable(item.getIcon());
            }
        } else {
            viewHolder.iconImageView.setImageDrawable(item.getIcon());
        }

        viewHolder.titleTextView.setText(item.getTitle());
        viewHolder.titleTextView.setEnabled(item.isEnabled());

        if (getItemColor() != -1) {
            viewHolder.titleTextView.setTextColor(getItemColor());
        }
    }

    /**
     * Inflates the view, which is used to visualize a divider.
     *
     * @param parent
     *         The parent of the view, which should be inflated, as an instance of the class {@link
     *         ViewGroup} or null, if no parent is available
     * @param divider
     *         The divider, which should be visualized, as an instance of the class {@link Divider}.
     *         The divider may not be null
     * @param position
     *         The position of the divider, which should be visualized, as an {@link Integer} value
     * @return The view, which has been inflated, as an instance of the class {@link View}
     */
    private View inflateDividerView(@Nullable final ViewGroup parent,
                                    @NonNull final Divider divider, final int position) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.divider, parent, false);
        DividerViewHolder viewHolder = new DividerViewHolder();
        viewHolder.leftDivider = view.findViewById(R.id.left_divider);
        viewHolder.rightDivider = view.findViewById(R.id.right_divider);
        viewHolder.titleTextView = view.findViewById(android.R.id.title);
        view.setTag(viewHolder);

        if (!TextUtils.isEmpty(divider.getTitle()) || (position % columnCount > 0 && !TextUtils
                .isEmpty(getRawItems().get(position - (position % columnCount)).getTitle()))) {
            view.getLayoutParams().height = context.getResources()
                    .getDimensionPixelSize(R.dimen.bottom_sheet_divider_title_height);
        }

        return view;
    }

    /**
     * Visualizes a specific divider.
     *
     * @param divider
     *         The divider, which should be visualized, as an instance of the class {@link Divider}.
     *         The divider may not be null
     * @param viewHolder
     *         The view holder, which contains the views, which should be used to visualize the
     *         divider, as an instance of the class {@link DividerViewHolder}. The view holder may
     *         not be null
     */
    private void visualizeDivider(@NonNull final Divider divider,
                                  @NonNull final DividerViewHolder viewHolder) {
        if (!TextUtils.isEmpty(divider.getTitle())) {
            viewHolder.titleTextView.setText(divider.getTitle());
            viewHolder.titleTextView.setVisibility(View.VISIBLE);
            viewHolder.leftDivider.setVisibility(View.VISIBLE);
        } else {
            viewHolder.titleTextView.setVisibility(View.GONE);
            viewHolder.leftDivider.setVisibility(View.GONE);
        }

        if (dividerColor != -1) {
            viewHolder.titleTextView.setTextColor(dividerColor);
            viewHolder.leftDivider.setBackgroundColor(dividerColor);
            viewHolder.rightDivider.setBackgroundColor(dividerColor);
        }
    }

    /**
     * Creates a new adapter, which manages the items of a {@link BottomSheet}.
     *
     * @param context
     *         The context, which should be used by the adapter, as an instance of the class {@link
     *         Context}. The context may not be null
     * @param style
     *         The style, which should be used to display the adapter's items, as a value of the
     *         enum {@link Style}. The style may either be <code>LIST</code>,
     *         <code>LIST_COLUMNS</code> or <code>GRID</code>
     * @param width
     *         The width of the bottom sheet, the items, which are displayed by the adapter, belong
     *         to, as an {@link Integer} value
     */
    public DividableGridAdapter(@NonNull final Context context, final Style style,
                                final int width) {
        Condition.INSTANCE.ensureNotNull(context, "The context may not be null");
        Condition.INSTANCE.ensureNotNull(style, "The style may not be null");
        this.context = context;
        this.style = style;
        this.items = new ArrayList<>();
        this.rawItems = null;
        this.iconCount = 0;
        this.dividerCount = 0;
        this.notifyOnChange = true;
        this.itemColor = -1;
        this.dividerColor = -1;
        setWidth(width);
    }

    /**
     * Returns the style, which is used to display the adapter's items.
     *
     * @return The style, which is used to display the adapter's items, as a value of the enum
     * {@link Style}. The style may either be <code>LIST</code>, <code>LIST_COLUMNS</code> or
     * <code>GRID</code>
     */
    public final Style getStyle() {
        return style;
    }

    /**
     * Sets the style, which should be used to display the adapter's items.
     *
     * @param style
     *         The style, which should be set, as a value of the enum {@link Style}. The style may
     *         either be <code>LIST</code>, <code>LIST_COLUMNS</code> or <code>GRID</code>
     */
    public final void setStyle(@NonNull final Style style) {
        Condition.INSTANCE.ensureNotNull(style, "The style may not be null");
        this.style = style;
        this.rawItems = null;
        notifyDataSetChanged();
    }

    /**
     * Sets the width of the bottom sheet, the items, which are displayed by the adapter, belong
     * to.
     *
     * @param width
     *         The width, which should be set, as an {@link Integer} value
     */
    public final void setWidth(final int width) {
        if (style == Style.LIST_COLUMNS && (getDeviceType(context) == DeviceType.TABLET ||
                getOrientation(context) == Orientation.LANDSCAPE)) {
            columnCount = 2;
        } else if (style == Style.GRID) {
            int padding = context.getResources()
                    .getDimensionPixelSize(R.dimen.bottom_sheet_grid_item_horizontal_padding);
            int itemSize = context.getResources()
                    .getDimensionPixelSize(R.dimen.bottom_sheet_grid_item_size);
            columnCount = ((getDeviceType(context) != DeviceType.TABLET &&
                    context.getResources().getConfiguration().orientation ==
                            Configuration.ORIENTATION_PORTRAIT ?
                    context.getResources().getDisplayMetrics().widthPixels : width) - 2 * padding) /
                    itemSize;
        } else {
            columnCount = 1;
        }

        rawItems = null;
        notifyDataSetChanged();
    }

    /**
     * Returns, whether the adapter contains dividers, or not.
     *
     * @return True, if the adapter contains dividers, false otherwise
     */
    public final boolean containsDividers() {
        return dividerCount > 0;
    }

    /**
     * Returns the number of columns, which are displayed by the adapter.
     *
     * @return The number of columns, which are displayed by the adapter
     */
    public final int getColumnCount() {
        return columnCount;
    }

    /**
     * Returns the text color of the adapter's items.
     *
     * @return The text color of the adapter's items as an {@link Integer} value or -1, if no custom
     * color has been set
     */
    public final int getItemColor() {
        return itemColor;
    }

    /**
     * Sets the text color of the adapter's items.
     *
     * @param color
     *         The text color, which should be set, as an {@link Integer} value or -1, if no custom
     *         color should be set
     */
    public final void setItemColor(@ColorInt final int color) {
        this.itemColor = color;
    }

    /**
     * Returns the color of the adapter's dividers.
     *
     * @return The color of the adapter's dividers as an {@link Integer} value or -1, if no custom
     * color has been set
     */
    public final int getDividerColor() {
        return dividerColor;
    }

    /**
     * Sets the color of the adapter's dividers.
     *
     * @param color
     *         The color, which should be set, as an {@link Integer} value or -1, if no custom color
     *         should be set
     */
    public final void setDividerColor(@ColorInt final int color) {
        this.dividerColor = color;
    }

    /**
     * Adds a new item to the adapter.
     *
     * @param item
     *         The item, which should be added, as an instance of the class {@link AbstractItem}.
     *         The item may not be null
     */
    public final void add(@NonNull final AbstractItem item) {
        Condition.INSTANCE.ensureNotNull(item, "The item may not be null");
        items.add(item);

        if (item instanceof Item && ((Item) item).getIcon() != null) {
            iconCount++;
        } else if (item instanceof Divider) {
            dividerCount++;
        }

        rawItems = null;
        notifyOnDataSetChanged();
    }

    /**
     * Replaces the item with at a specific index
     *
     * @param index
     *         The index of the item, which should be replaced, as an {@link Integer} value
     * @param item
     *         The item, which should be set, as an instance of the class {@link AbstractItem}. The
     *         item may not be null
     */
    public final void set(final int index, @NonNull final AbstractItem item) {
        Condition.INSTANCE.ensureNotNull(item, "The item may not be null");
        AbstractItem replacedItem = items.set(index, item);

        if (replacedItem instanceof Item && ((Item) replacedItem).getIcon() != null) {
            iconCount--;
        } else if (replacedItem instanceof Divider) {
            dividerCount--;
        }

        if (item instanceof Item && ((Item) item).getIcon() != null) {
            iconCount++;
        } else if (item instanceof Divider) {
            dividerCount++;
        }

        rawItems = null;
        notifyOnDataSetChanged();
    }

    /**
     * Removes the item at a specific index.
     *
     * @param index
     *         The index of the item, which should be removed, as an {@link Integer} value
     */
    public final void remove(final int index) {
        AbstractItem removedItem = items.remove(index);

        if (removedItem instanceof Item && ((Item) removedItem).getIcon() != null) {
            iconCount--;
        } else if (removedItem instanceof Divider) {
            dividerCount--;
        }

        rawItems = null;
        notifyOnDataSetChanged();
    }

    /**
     * Removes all items from the adapter.
     */
    public final void clear() {
        items.clear();
        iconCount = 0;
        dividerCount = 0;

        if (rawItems != null) {
            rawItems.clear();
        }

        notifyOnDataSetChanged();
    }

    /**
     * Returns the number of items, which are contained by the adapter.
     */
    public final int getItemCount() {
        return items.size();
    }

    /**
     * Returns, whether the item at a specific index is enabled, or not.
     *
     * @param index
     *         The index of the item, which should be checked, as an {@link Integer} value
     * @return True, if the item is enabled, false otherwise
     */
    public final boolean isItemEnabled(final int index) {
        AbstractItem item = items.get(index);
        return item instanceof Item && ((Item) item).isEnabled();
    }

    /**
     * Sets, whether the item at a specific index should be enabled, or not.
     *
     * @param index
     *         The index of the item as an {@link Integer} value
     * @param enabled
     *         True, if the item should be enabled, false otherwise
     */
    public final void setItemEnabled(final int index, final boolean enabled) {
        AbstractItem item = items.get(index);

        if (item instanceof Item) {
            ((Item) item).setEnabled(enabled);
            rawItems = null;
            notifyOnDataSetChanged();
        }
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
    public final boolean isEnabled(final int position) {
        AbstractItem item = getItem(position);
        return item instanceof Item && ((Item) item).isEnabled();
    }

    @Override
    public final int getCount() {
        return getRawItems().size();
    }

    @Override
    public final AbstractItem getItem(final int position) {
        return getRawItems().get(position);
    }

    @Override
    public final long getItemId(final int position) {
        return position;
    }

    @Override
    public final View getView(final int position, final View convertView, final ViewGroup parent) {
        View view = convertView;
        int viewType = getItemViewType(position);
        AbstractItem item = getItem(position);

        if (view == null) {
            if (viewType == PLACEHOLDER_VIEW_TYPE) {
                view = inflatePlaceholderView(parent);
            } else if (viewType == ITEM_VIEW_TYPE) {
                view = inflateItemView(parent);
            } else {
                view = inflateDividerView(parent, (Divider) item, position);
            }
        }

        if (viewType == ITEM_VIEW_TYPE) {
            ItemViewHolder viewHolder = (ItemViewHolder) view.getTag();
            visualizeItem((Item) item, viewHolder);
        } else if (viewType == SEPARATOR_VIEW_TYPE) {
            DividerViewHolder viewHolder = (DividerViewHolder) view.getTag();
            visualizeDivider((Divider) item, viewHolder);
        }

        return view;
    }

    @Override
    public final int getViewTypeCount() {
        return 3;
    }

    @Override
    public final int getItemViewType(final int position) {
        AbstractItem item = getItem(position);

        if (item == null) {
            return PLACEHOLDER_VIEW_TYPE;
        } else if (item instanceof Item) {
            return ITEM_VIEW_TYPE;
        } else {
            return SEPARATOR_VIEW_TYPE;
        }
    }

}
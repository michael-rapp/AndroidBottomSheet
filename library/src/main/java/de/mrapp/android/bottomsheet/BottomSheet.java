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

import android.app.Activity;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.TypedArray;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.AttrRes;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.annotation.StyleRes;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import de.mrapp.android.bottomsheet.adapter.DividableGridAdapter;
import de.mrapp.android.bottomsheet.model.Divider;
import de.mrapp.android.bottomsheet.model.Item;
import de.mrapp.android.bottomsheet.view.DraggableView;

import static de.mrapp.android.util.Condition.ensureAtLeast;
import static de.mrapp.android.util.Condition.ensureAtMaximum;
import static de.mrapp.android.util.Condition.ensureNotNull;

/**
 * A bottom sheet, which is designed according to the Android 5's Material Design guidelines even on
 * pre-Lollipop devices. Such a bottom sheet appears at the bottom of the window and consists of a
 * title and multiple items. It is possible to customize the appearance of the bottom sheet or to
 * replace its title and items with custom views.
 *
 * For creating or showing such bottom sheets, the methods {@link Builder#create()} or {@link
 * Builder#show()} of the builder {@link de.mrapp.android.bottomsheet.BottomSheet.Builder} can be
 * used.
 *
 * @author Michael Rapp
 * @since 1.0.0
 */
public class BottomSheet extends Dialog implements DialogInterface, DraggableView.Callback {

    /**
     * A builder, which allows to create and show bottom sheets, which are designed according to
     * Android 5's Material Design guidelines even on pre-Lollipop devices. Such a bottom sheet
     * appears at the bottom of the window and consists of a title and multiple items. It is
     * possible to customize the appearance of the bottom sheet or to replace its title and items
     * with custom views.
     */
    public static class Builder {

        /**
         * The context, which is used by the builder.
         */
        private final Context context;

        /**
         * The resource id of the theme, which should be used by the bottom sheet, which is created
         * by the builder.
         */
        private final int themeResourceId;

        /**
         * True, if the bottom sheet, which is created by the builder, should be cancelable, false
         * otherwise.
         */
        private boolean cancelable = true;

        /**
         * The style of the bottom sheet, which is created by the builder.
         */
        private Style style = Style.LIST;

        /**
         * The listener, which should be notified, when an item of the bottom sheet, which is
         * created by the builder, has been clicked.
         */
        private OnItemClickListener itemClickListener;

        /**
         * The listener, which should be notified, when the bottom sheet, which is created by the
         * builder, has been maximized.
         */
        private OnMaximizeListener maximizeListener;

        /**
         * The listener, which should be notified, when the bottom sheet, which is created by the
         * builder, is canceled.
         */
        private OnCancelListener cancelListener;

        /**
         * The listener, which should be notified, when the bottom sheet, which is created by the
         * builder, is dismissed.
         */
        private OnDismissListener dismissListener;

        /**
         * The listener, which should be notified, if a key is dispatched to the bottom sheet, which
         * is created by the builder.
         */
        private OnKeyListener keyListener;

        /**
         * The title of the bottom sheet, which is created by the builder.
         */
        private CharSequence title;

        /**
         * The icon of the bottom sheet, which is created by the builder.
         */
        private Drawable icon;

        /**
         * The color of the title of the bottom sheet, which is created by the builder.
         */
        private int titleColor = -1;

        /**
         * The color of the items of the bottom sheet, which is created by the builder.
         */
        private int itemColor = -1;

        /**
         * The color of the dividers of the bottom sheet, which is created by the builder.
         */
        private int dividerColor = -1;

        /**
         * The background of the bottom sheet, which is created by the builder.
         */
        private Drawable background;

        /**
         * The custom content view of the bottom sheet, which is created by the builder.
         */
        private View customView;

        /**
         * The resource id of the custom content view of the bottom sheet, which is created by the
         * builder.
         */
        private int customViewId = -1;

        /**
         * The custom title view of the bottom sheet, which is created by the builder.
         */
        private View customTitleView;

        /**
         * The sensitivity, which specifies the distance after which dragging has an effect on the
         * bottom sheet
         */
        private float dragSensitivity = 0.25f;

        /**
         * The dim amount, which is used to darken the area around the bottom sheet, which is
         * created by the builder.
         */
        private float dimAmount = 0.25f;

        /**
         * The width of the bottom sheet, which is created by the builder, in pixels.
         */
        private int width;

        /**
         * The items of the bottom sheet, which is created by the builder.
         */
        private Collection<Parcelable> items = new LinkedList<>();

        /**
         * The activity, which should be used to start the apps, which are added as items to the
         * bottom sheet, which is created by the builder.
         */
        private Activity activity;

        /**
         * The intent, the apps, which are added as items to the bottom sheet, which is created by
         * the builder, should handle.
         */
        private Intent intent;

        /**
         * Inflates the bottom sheet's layout.
         *
         * @return The root view of the layout, which has been inflated, as an instance of the class
         * {@link DraggableView}
         */
        @SuppressWarnings("deprecation")
        private DraggableView inflateLayout() {
            DraggableView root = (DraggableView) View.inflate(context, R.layout.bottom_sheet, null);

            if (background != null) {
                root.setBackgroundDrawable(background);
            }

            if (style == Style.LIST) {
                int paddingTop = getContext().getResources()
                        .getDimensionPixelSize(R.dimen.bottom_sheet_list_padding_top);
                root.setPadding(0, paddingTop, 0, 0);
            } else {
                int paddingTop = getContext().getResources()
                        .getDimensionPixelSize(R.dimen.bottom_sheet_grid_padding_top);
                root.setPadding(0, paddingTop, 0, 0);
            }

            return root;
        }

        /**
         * Inflates the bottom sheet's title view, which may either be the default view or a custom
         * view, if one has been set before.
         *
         * @param root
         *         The root view of the bottom sheet's layout as an instance of the class {@link
         *         DraggableView}
         */
        private void inflateTitleView(@NonNull final DraggableView root) {
            ViewGroup titleContainer = (ViewGroup) root.findViewById(R.id.title_container);

            if (customTitleView != null) {
                titleContainer.setVisibility(View.VISIBLE);
                titleContainer.addView(customTitleView, ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
            } else {
                View.inflate(context, R.layout.bottom_sheet_title, titleContainer);

                if (style == Style.LIST) {
                    int padding = getContext().getResources().getDimensionPixelSize(
                            R.dimen.bottom_sheet_list_item_horizontal_padding);
                    titleContainer.setPadding(padding, 0, padding, 0);
                } else {
                    int padding = getContext().getResources().getDimensionPixelSize(
                            R.dimen.bottom_sheet_grid_item_horizontal_padding);
                    titleContainer.setPadding(padding, 0, padding, 0);
                }
            }

            initializeTitle(root, titleContainer);
        }

        /**
         * Initializes the bottom sheet's title and icon.
         *
         * @param root
         *         The root view of the bottom sheet's layout as an instance of the class {@link
         *         DraggableView}
         * @param titleContainer
         *         The parent view of the title view as an instance of the class {@link ViewGroup}
         */
        private void initializeTitle(@NonNull final DraggableView root,
                                     @NonNull final ViewGroup titleContainer) {
            View titleView = titleContainer.findViewById(android.R.id.title);

            if (titleView != null && titleView instanceof TextView) {
                TextView titleTextView = (TextView) titleView;

                if (titleColor != -1) {
                    titleTextView.setTextColor(titleColor);
                }

                if (!TextUtils.isEmpty(title) || icon != null) {
                    titleContainer.setVisibility(View.VISIBLE);
                    titleTextView.setText(title);
                    root.setPadding(root.getPaddingLeft(), 0, root.getPaddingRight(),
                            root.getPaddingBottom());

                    if (icon != null) {
                        titleTextView
                                .setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null);
                    }

                }
            }
        }

        /**
         * Inflates the bottom sheet's content view, which may either be the default view or a
         * custom view, if one has been set before.
         *
         * @param root
         *         The root view of the bottom sheet's layout as an instance of the class {@link
         *         DraggableView}
         * @return The grid view, which is contained by the bottom sheet, as an instance of the
         * class {@link GridView} or null, if no grid view is contained by the bottom sheet
         */
        private GridView inflateContentView(@NonNull final DraggableView root) {
            ViewGroup contentContainer = (ViewGroup) root.findViewById(R.id.content_container);

            if (customView != null) {
                contentContainer.setVisibility(View.VISIBLE);
                contentContainer.addView(customView, ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
            } else if (customViewId != -1) {
                contentContainer.setVisibility(View.VISIBLE);
                View.inflate(context, customViewId, contentContainer);
            } else {
                View.inflate(context, R.layout.bottom_sheet_grid_view, contentContainer);
            }

            return initializeContent(contentContainer);
        }

        /**
         * Initializes the bottom sheet's content.
         *
         * @param contentContainer
         *         The parent view of the content view as an instance of the class  {@link
         *         ViewGroup}
         * @return The grid view, which is contained by the bottom sheet, as an instance of the
         * class {@link GridView} or null, if no grid view is contained by the bottom sheet
         */
        private GridView initializeContent(@NonNull final ViewGroup contentContainer) {
            GridView gridView =
                    (GridView) contentContainer.findViewById(R.id.bottom_sheet_grid_view);

            if (gridView != null) {
                contentContainer.setVisibility(View.VISIBLE);
                gridView.setNumColumns(1);

                if (style == Style.LIST) {
                    int paddingBottom = getContext().getResources()
                            .getDimensionPixelSize(R.dimen.bottom_sheet_list_padding_bottom);
                    gridView.setPadding(0, 0, 0, paddingBottom);
                } else {
                    int paddingBottom = getContext().getResources()
                            .getDimensionPixelSize(R.dimen.bottom_sheet_grid_padding_bottom);
                    gridView.setPadding(0, 0, 0, paddingBottom);
                }
            }

            return gridView;
        }

        /**
         * Creates a new builder, which allows to create bottom sheets, which are designed according
         * to Android 5's Material Design guidelines even on pre-Lollipop devices.
         *
         * @param context
         *         The context, which should be used by the builder, as an instance of the class
         *         {@link Context}. The context may not be null
         */
        public Builder(@NonNull final Context context) {
            this(context, -1);
        }

        /**
         * Creates a new builder, which allows to create bottom sheets, which are designed according
         * to Android 5's Material Design guidelines even on pre-Lollipop devices.
         *
         * @param context
         *         The context, which should be used by the builder, as an instance of the class
         *         {@link Context}. The context may not be null
         * @param themeResourceId
         *         The resource id of the theme, which should be used by the bottom sheet, as an
         *         {@link Integer} value. The resource id must correspond to a valid theme
         */
        public Builder(@NonNull final Context context, @StyleRes final int themeResourceId) {
            ensureNotNull(context, "The context may not be null");
            this.context = context;
            this.themeResourceId = themeResourceId;
            this.width = getContext().getResources().getDimensionPixelSize(R.dimen.default_width);
        }

        /**
         * Returns the context, which is used by the builder.
         *
         * @return The context, which is used by the builder, as an instance of the class {@link
         * Context}
         */
        public final Context getContext() {
            return context;
        }

        /**
         * Sets, whether the bottom sheet, which is created by the builder, should be cancelable, or
         * not.
         *
         * @param cancelable
         *         True, if the bottom sheet, which is created by the builder, should be cancelable,
         *         false otherwise
         * @return The builder, the method has been called upon, as an instance of the class {@link
         * Builder}
         */
        public final Builder setCancelable(final boolean cancelable) {
            this.cancelable = cancelable;
            return this;
        }

        /**
         * Sets the style of the bottom sheet, which is created by the builder.
         *
         * @param style
         *         The style, which should be set, as a value of the enum {@link Style}. The value
         *         may either be <code>List</code> or <code>GRID</code>
         * @return The builder, the method has been called upon, as an instance of the class {@link
         * Builder}
         */
        public final Builder setStyle(@NonNull final Style style) {
            ensureNotNull(style, "The style may not be null");
            this.style = style;
            return this;
        }

        /**
         * Sets the listener, which should be notified, when an item of the bottom sheet has been
         * clicked.
         *
         * @param listener
         *         The listener, which should be set, as an instance of the type {@link
         *         OnItemClickListener} or null, if no listener should be notified
         * @return The builder, the method has been called upon, as an instance of the class {@link
         * Builder}
         */
        public final Builder setOnItemClickListener(@Nullable final OnItemClickListener listener) {
            this.itemClickListener = listener;
            return this;
        }

        /**
         * Sets the listener, which should be notified, when the bottom sheet, which is created by
         * the builder, has been maximized.
         *
         * @param listener
         *         The listener, which should be set, as an instance of the type {@link
         *         OnMaximizeListener} or null, if no listener should be notified
         * @return The builder, the method has been called upon, as an instance of the class {@link
         * Builder}
         */
        public final Builder setOnMaximizeListener(@Nullable final OnMaximizeListener listener) {
            this.maximizeListener = listener;
            return this;
        }

        /**
         * Sets the listener, which should be notified, when the bottom sheet, which is created by
         * the builder, is canceled.
         *
         * If you are interested in listening for all cases where the bottom sheet is dismissed and
         * not just when it is canceled, see {@link #setOnDismissListener(android.content.DialogInterface.OnDismissListener)
         * setOnDismissListener}.
         *
         * @param listener
         *         The listener, which should be set, as an instance of the type {@link
         *         OnCancelListener}, or null, if no listener should be set
         * @return The builder, the method has been called upon, as an instance of the class {@link
         * Builder}
         * @see #setOnDismissListener(android.content.DialogInterface.OnDismissListener)
         */
        public Builder setOnCancelListener(@Nullable final OnCancelListener listener) {
            this.cancelListener = listener;
            return this;
        }

        /**
         * Sets the listener, which should be notified, when the bottom sheet, which is created by
         * the builder, is dismissed for any reason.
         *
         * @param listener
         *         The listener, which should be set, as an instance of the type {@link
         *         OnDismissListener}, or null, if no listener should be set
         * @return The builder, the method has been called upon, as an instance of the class {@link
         * Builder}
         */
        public final Builder setOnDismissListener(@Nullable final OnDismissListener listener) {
            this.dismissListener = listener;
            return this;
        }

        /**
         * Sets the listener, which should be notified, if a key is dispatched to the bottom sheet,
         * which is created by the builder.
         *
         * @param listener
         *         The listener, which should be set, as an instance of the type {@link
         *         OnKeyListener}, or null, if no listener should be set
         * @return The builder, the method has been called upon, as an instance of the class {@link
         * Builder}
         */
        public final Builder setOnKeyListener(@Nullable final OnKeyListener listener) {
            this.keyListener = listener;
            return this;
        }

        /**
         * Sets the color of the title of the bottom sheet, which is created by the builder.
         *
         * @param color
         *         The color, which should be set, as an {@link Integer} value or -1, if no custom
         *         color should be set
         * @return The builder, the method has been called upon, as an instance of the class {@link
         * Builder}
         */
        public final Builder setTitleColor(@ColorInt final int color) {
            this.titleColor = color;
            return this;
        }

        /**
         * Sets the color of the items of the bottom sheet, which is created by the builder.
         *
         * @param color
         *         The color, which should be set, as an {@link Integer} value or -1, if no custom
         *         color should be set
         * @return The builder, the method has been called upon, as an instance of the class {@link
         * Builder}
         */
        public final Builder setItemColor(@ColorInt final int color) {
            this.itemColor = color;
            return this;
        }

        /**
         * Sets the color of the dividers of the bottom sheet, which is created by the builder.
         *
         * @param color
         *         The color, which should be set, as an {@link Integer} value or -1, if no custom
         *         color should be set
         * @return The builder, the method has been called upon, as an instance of the class {@link
         * Builder}
         */
        public final Builder setDividerColor(@ColorInt final int color) {
            this.dividerColor = color;
            return this;
        }

        /**
         * Sets the background of the bottom sheet, which is created by the builder.
         *
         * @param background
         *         The background, which should be set, as an instance of the class {@link Drawable}
         *         or null, if no custom background should be set
         * @return The builder, the method has been called upon, as an instance of the class {@link
         * Builder}
         */
        public final Builder setBackground(@Nullable final Drawable background) {
            this.background = background;
            return this;
        }

        /**
         * Sets the background of the bottom sheet, which is created by the builder.
         *
         * @param resourceId
         *         The resource id of the background, which should be set, as an {@link Integer}
         *         value. The resource id must correspond to a valid drawable resource
         * @return The builder, the method has been called upon, as an instance of the class {@link
         * Builder}
         */
        @SuppressWarnings("deprecation")
        public final Builder setBackground(@DrawableRes final int resourceId) {
            this.background = context.getResources().getDrawable(resourceId);
            return this;
        }

        /**
         * Sets the background color of the bottom sheet, which is created by the builder.
         *
         * @param color
         *         The background color, which should be set, as an {@link Integer} value or -1, if
         *         no custom background color should be set
         * @return The builder, the method has been called upon, as an instance of the class {@link
         * Builder}
         */
        public final Builder setBackgroundColor(@ColorInt final int color) {
            this.background = color != -1 ? new ColorDrawable(color) : null;
            return this;
        }

        /**
         * Sets the title of the bottom sheet, which is created by the builder.
         *
         * @param title
         *         The title, which should be set, as an instance of the type {@link CharSequence}
         *         or null, if no title should be shown
         * @return The builder, the method has been called upon, as an instance of the class {@link
         * Builder}
         */
        public final Builder setTitle(@Nullable final CharSequence title) {
            this.title = title;
            return this;
        }

        /**
         * Sets the title of the bottom sheet, which is created by the builder.
         *
         * @param resourceId
         *         The resource id of the title, which should be set, as an {@link Integer} value.
         *         The resource id must correspond to a valid string resource
         * @return The builder, the method has been called upon, as an instance of the class {@link
         * Builder}
         */
        public final Builder setTitle(@StringRes final int resourceId) {
            return setTitle(context.getText(resourceId));
        }

        /**
         * Sets the icon of the bottom sheet, which is created by the builder.
         *
         * @param icon
         *         The icon, which should be set, as an instance of the class {@link Drawable} or
         *         null, if no icon should be shown
         * @return The builder, the method has been called upon, as an instance of the class {@link
         * Builder}
         */
        public final Builder setIcon(@Nullable final Drawable icon) {
            this.icon = icon;
            return this;
        }

        /**
         * Sets the icon of the bottom sheet, which is created by the builder.
         *
         * @param resourceId
         *         The resource id of the icon, which should be set, as an {@link Integer} value.
         *         The resource id must correspond to a valid drawable resource
         * @return The builder, the method has been called upon, as an instance of the class {@link
         * Builder}
         */
        @SuppressWarnings("deprecation")
        public final Builder setIcon(@DrawableRes final int resourceId) {
            return setIcon(context.getResources().getDrawable(resourceId));
        }

        /**
         * Set the icon of the bottom sheet, which is created by the builder.
         *
         * @param attributeId
         *         The id of the theme attribute, which supplies the icon, which should be set, as
         *         an {@link Integer} value. The id must point to a valid drawable resource
         * @return The builder, the method has been called upon, as an instance of the class {@link
         * Builder}
         */
        public final Builder setIconAttribute(@AttrRes final int attributeId) {
            TypedArray typedArray =
                    context.getTheme().obtainStyledAttributes(new int[]{attributeId});
            return setIcon(typedArray.getDrawable(0));
        }

        /**
         * Sets the custom view, which should be shown by the bottom sheet, which is created by the
         * builder.
         *
         * @param view
         *         The view, which should be set, as an instance of the class {@link View} or null,
         *         if no custom view should be shown
         * @return The builder, the method has been called upon, as an instance of the class {@link
         * Builder}
         */
        public final Builder setView(@Nullable final View view) {
            customView = view;
            customViewId = 0;
            return this;
        }

        /**
         * Sets the custom view, which should be shown by the bottom sheet, which is created by the
         * builder.
         *
         * @param resourceId
         *         The resource id of the view, which should be set, as an {@link Integer} value.
         *         The resource id must correspond to a valid layout resource
         * @return The builder, the method has been called upon, as an instance of the class {@link
         * Builder}
         */
        public final Builder setView(@LayoutRes final int resourceId) {
            customViewId = resourceId;
            customView = null;
            return this;
        }

        /**
         * Sets the custom view, which should be used to show the title of the bottom sheet, which
         * is created by the builder.
         *
         * @param view
         *         The view, which should be set, as an instance of the class {@link View} or null,
         *         if no custom view should be used to show the title
         * @return The builder, the method has been called upon, as an instance of the class {@link
         * Builder}
         */
        public final Builder setCustomTitle(@Nullable final View view) {
            customTitleView = view;
            return this;
        }

        /**
         * Sets the sensitivity, which specifies the distance after which dragging has an effect on
         * the bottom sheet, in relation to an internal value range.
         *
         * @param dragSensitivity
         *         The drag sensitivity, which should be set, as a {@link Float} value. The drag
         *         sensitivity must be at lest 0 and at maximum 1
         * @return The builder, the method has been called upon, as an instance of the class {@link
         * Builder}
         */
        public final Builder setDragSensitivity(final float dragSensitivity) {
            ensureAtLeast(dragSensitivity, 0, "The drag sensitivity must be at least 0");
            ensureAtMaximum(dragSensitivity, 1, "The drag sensitivity must be at maximum 1");
            this.dragSensitivity = dragSensitivity;
            return this;
        }

        /**
         * Sets the dim amount, which should be used to darken the area outside the bottom sheet,
         * which is created by the builder.
         *
         * @param dimAmount
         *         The dim amount, which should be set, as a {@link Float} value. The dim amount
         *         must be at least 0 (fully transparent) and at maximum 1 (fully opaque)
         * @return The builder, the method has been called upon, as an instance of the class {@link
         * Builder}
         */
        public final Builder setDimAmount(final float dimAmount) {
            ensureAtLeast(dimAmount, 0, "The dim amount must be at least 0");
            ensureAtMaximum(dimAmount, 1, "The dim amount must be at least 1");
            this.dimAmount = dimAmount;
            return this;
        }

        /**
         * Sets the width of the bottom sheet, which is created by the builder. The width is only
         * used on tablet devices or in landscape mode.
         *
         * @param width
         *         The width, which should be set, in pixels as an {@link Integer} value. The width
         *         must be at least 1
         * @return The builder, the method has been called upon, as an instance of the class {@link
         * Builder}
         */
        public final Builder setWidth(final int width) {
            ensureAtLeast(width, 1, "The width must be at least 1");
            this.width = width;
            return this;
        }

        /**
         * Adds a new item to the bottom sheet, which is created by the builder.
         *
         * @param id
         *         The id of the item, which should be added, as an {@link Integer} value
         * @param title
         *         The title of the item, which should be added, as an instance of the type {@link
         *         CharSequence}. The title may neither be null, nor empty
         * @return The builder, the method has been called upon, as an instance of the class {@link
         * Builder}
         */
        public final Builder addItem(final int id, @NonNull final CharSequence title) {
            Item item = new Item(id, title);
            items.add(item);
            activity = null;
            intent = null;
            return this;
        }

        /**
         * Adds a new item to the bottom sheet, which is created by the builder.
         *
         * @param id
         *         The id of the item, which should be added, as an {@link Integer} value
         * @param title
         *         The title of the item, which should be added, as an instance of the type {@link
         *         CharSequence}. The title may neither be null, nor empty
         * @param icon
         *         The icon of the item, which should be added, as an instance of the class {@link
         *         Drawable}, or null, if no item should be used
         * @return The builder, the method has been called upon, as an instance of the class {@link
         * Builder}
         */
        public final Builder addItem(final int id, @NonNull final CharSequence title,
                                     @Nullable final Drawable icon) {
            Item item = new Item(id, title);
            item.setIcon(icon);
            items.add(item);
            activity = null;
            intent = null;
            return this;
        }

        /**
         * Adds a new item to the bottom sheet, which is created by the builder.
         *
         * @param id
         *         The id of the item, which should be added, as an {@link Integer} value
         * @param titleId
         *         The resource id of the title of the item, which should be added, as an {@link
         *         Integer} value. The resource id must correspond to a valid string resource
         * @return The builder, the method has been called upon, as an instance of the class {@link
         * Builder}
         */
        public final Builder addItem(final int id, @StringRes final int titleId) {
            Item item = new Item(id, getContext(), titleId);
            items.add(item);
            activity = null;
            intent = null;
            return this;
        }

        /**
         * Adds a new item to the bottom sheet, which is created by the builder.
         *
         * @param id
         *         The id of the item, which should be added, as an {@link Integer} value
         * @param titleId
         *         The resource id of the title of the item, which should be added, as an {@link
         *         Integer} value. The resource id must correspond to a valid string resource
         * @param iconId
         *         The resource id of the icon of the item, which should be added, as an {@link
         *         Integer} value. The resource id must correspond to a valid drawable resource
         * @return The builder, the method has been called upon, as an instance of the class {@link
         * Builder}
         */
        public final Builder addItem(final int id, @StringRes final int titleId,
                                     @DrawableRes final int iconId) {
            Item item = new Item(id, getContext(), titleId);
            item.setIcon(getContext(), iconId);
            items.add(item);
            activity = null;
            intent = null;
            return this;
        }

        /**
         * Adds a new divider to the bottom sheet, which is created by the builder.
         *
         * @param id
         *         The id of the divider, which should be added, as an {@link Integer} value
         * @return The builder, the method has been called upon, as an instance of the class {@link
         * Builder}
         */
        public final Builder addDivider(final int id) {
            items.add(new Divider(id));
            activity = null;
            intent = null;
            return this;
        }

        /**
         * Adds a new divider to the bottom sheet, which is created by the builder.
         *
         * @param id
         *         The id of the divider, which should be added, as an {@link Integer} value
         * @param title
         *         The title of the divider, which should be added, as an instance of the type
         *         {@link CharSequence}, or null, if no title should be used
         */
        public final Builder addDivider(final int id, @Nullable final CharSequence title) {
            Divider divider = new Divider(id);
            divider.setTitle(title);
            items.add(divider);
            activity = null;
            intent = null;
            return this;
        }

        /**
         * Adds a new divider to the bottom sheet, which is created by the builder.
         *
         * @param id
         *         The id of the divider, which should be added, as an {@link Integer} value
         * @param titleId
         *         The resource id of the title, which should be added, as an {@link Integer} value.
         *         The resource id must correspond to a valid string resource
         * @return The builder, the method has been called upon, as an instance of the class {@link
         * Builder}
         */
        public final Builder addDivider(final int id, @StringRes final int titleId) {
            Divider divider = new Divider(id);
            divider.setTitle(getContext(), titleId);
            items.add(divider);
            activity = null;
            intent = null;
            return this;
        }

        /**
         * Adds the apps, which are able to handle a specific intent, as items to the bottom sheet,
         * which is created by the builder. This causes all previously added items to be removed.
         * When an item is clicked, the corresponding app is started.
         *
         * @param activity
         *         The activity, the bottom sheet, which is created by the builder, belongs to, as
         *         an instance of the class {@link Activity}. The activity may not be null
         * @param intent
         *         The intent as an instance of the class {@link Intent}. The intent may not be
         *         null
         * @return The builder, the method has been called upon, as an instance of the class {@link
         * Builder}
         */
        public final Builder setIntent(@NonNull final Activity activity,
                                       @NonNull final Intent intent) {
            ensureNotNull(activity, "The activity may not be null");
            ensureNotNull(intent, "The intent may not be null");
            this.items.clear();
            this.activity = activity;
            this.intent = intent;
            return this;
        }

        /**
         * Creates a bottom sheet with the arguments, which have been supplied to the builder.
         * Calling this method does not display the bottom sheet.
         *
         * @return The bottom sheet, which has been create as an instance of the class {@link
         * BottomSheet}
         */
        public final BottomSheet create() {
            DraggableView root = inflateLayout();
            inflateTitleView(root);
            GridView gridView = inflateContentView(root);
            int themeResourceId =
                    this.themeResourceId != -1 ? this.themeResourceId : R.style.BottomSheet;
            BottomSheet bottomSheet =
                    new BottomSheet(context, themeResourceId, gridView != null ? items : null);
            bottomSheet.requestWindowFeature(Window.FEATURE_NO_TITLE);
            FrameLayout.LayoutParams layoutParams =
                    new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                            FrameLayout.LayoutParams.MATCH_PARENT);
            layoutParams.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
            bottomSheet.setContentView(root, layoutParams);
            bottomSheet.setOnItemClickListener(itemClickListener);
            bottomSheet.setOnMaximizeListener(maximizeListener);
            bottomSheet.setOnCancelListener(cancelListener);
            bottomSheet.setOnDismissListener(dismissListener);
            bottomSheet.setOnKeyListener(keyListener);
            bottomSheet.setCancelable(cancelable);
            bottomSheet.setCanceledOnTouchOutside(true);
            bottomSheet.setDragSensitivity(dragSensitivity);
            bottomSheet.setDimAmount(dimAmount);
            bottomSheet.setItemColor(itemColor);
            bottomSheet.setDividerColor(dividerColor);
            bottomSheet.setWidth(width);

            if (activity != null && intent != null) {
                bottomSheet.setIntent(activity, intent);
            }

            return bottomSheet;
        }

        /**
         * Creates a bottom sheet with the arguments, which have been supplied to the builder and
         * immediately displays it.
         *
         * @return The bottom sheet, which has been shown, as an instance of the class {@link
         * BottomSheet}
         */
        public final BottomSheet show() {
            BottomSheet bottomSheet = create();
            bottomSheet.show();
            return bottomSheet;
        }

    }

    /**
     * Contains all possible styles of a {@link BottomSheet}.
     */
    public enum Style {

        /**
         * If the bottom sheet's items should be shown in a list.
         */
        LIST,

        /**
         * If the bottom sheet's items should be shown in a grid.
         */
        GRID

    }

    /**
     * The minimum value of the internal value range, which specifies after which distance dragging
     * has an effect on the bottom sheet.
     */
    private static final int MIN_DRAG_SENSITIVITY = 10;

    /**
     * The maximum value of the internal value range, which specifies after which distance dragging
     * has an effect on the bottom sheet.
     */
    private static final int MAX_DRAG_SENSITIVITY = 260;

    /**
     * The listener, which is notified, when an item of the bottom sheet has been clicked.
     */
    private OnItemClickListener itemClickListener;

    /**
     * The listener, which is notified, when the bottom sheet is maximized.
     */
    private OnMaximizeListener maximizeListener;

    /**
     * The root view of the bottom sheet.
     */
    private DraggableView rootView;

    /**
     * The parent view of the text view, which is used to show the title of the bottom sheet.
     */
    private ViewGroup titleContainer;

    /**
     * The text view, which is used to show the title of the bottom sheet.
     */
    private TextView titleTextView;

    /**
     * The parent view of the custom view, which is shown by the bottom sheet.
     */
    private ViewGroup contentContainer;

    /**
     * The grid view, which is used to show the bottom sheet's items.
     */
    private GridView gridView;

    /**
     * The adapter, which is used to manage the bottom sheet's items.
     */
    private DividableGridAdapter adapter;

    /**
     * True, if the bottom sheet is cancelable, false otherwise.
     */
    private boolean cancelable;

    /**
     * True, if the bottom sheet is canceled, when the decor view is touched, false otherwise.
     */
    private boolean canceledOnTouchOutside;

    /**
     * The sensitivity, which specifies the distance after which dragging has an effect on the
     * bottom sheet, in relation to an internal value range.
     */
    private float dragSensitivity;

    /**
     * The dim amount, which is used to darken the area outside the bottom sheet.
     */
    private float dimAmount;

    /**
     * The width of the bottom sheet in pixels.
     */
    private int width;

    /**
     * Initializes the bottom sheet.
     *
     * @param items
     *         A collection, which contains the items, which should be added to the bottom sheet, as
     *         an instance of the type {@link Collection} or null, if a custom view should be shown
     *         instead
     */
    private void initialize(@Nullable final Collection<Parcelable> items) {
        if (items != null) {
            adapter = new DividableGridAdapter(getContext());

            for (Parcelable item : items) {
                if (item instanceof Item) {
                    adapter.add((Item) item);
                } else if (item instanceof Divider) {
                    adapter.add((Divider) item);
                }
            }
        }
    }

    /**
     * Creates and returns the layout params, which should be used to show the bottom sheet.
     *
     * @return The layout params, which have been created, as an instance of the class {@link
     * android.view.WindowManager.LayoutParams}
     */
    private WindowManager.LayoutParams createLayoutParams() {
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
        return layoutParams;
    }

    /**
     * Creates and returns a listener, which allows to cancel the bottom sheet, when the decor view
     * is touched.
     *
     * @return The listener, which has been created, as an instance of the type {@link
     * View.OnTouchListener}
     */
    private View.OnTouchListener createCancelOnTouchListener() {
        return new View.OnTouchListener() {

            @Override
            public boolean onTouch(final View v, final MotionEvent event) {
                if (cancelable && canceledOnTouchOutside) {
                    cancel();
                    return true;
                }

                return false;
            }

        };
    }

    /**
     * Calculates and returns the distance after which dragging has an effect on the bottom sheet in
     * pixels. The distance depends on the current set drag sensitivity, which corresponds to an
     * internal value range.
     *
     * @return The distance after which dragging has an effect on the bottom sheet in pixels as an
     * {@link Integer} value
     */
    private int calculateDragSensitivity() {
        int range = MAX_DRAG_SENSITIVITY - MIN_DRAG_SENSITIVITY;
        return Math.round((1 - getDragSensitivity()) * range + MIN_DRAG_SENSITIVITY);
    }

    /**
     * Creates and returns a listener, which allows to observe when the items of a bottom sheet have
     * been clicked.
     *
     * @return The listener, which has been created, as an instance of the type {@link
     * OnItemClickListener}
     */
    private OnItemClickListener createItemClickListener() {
        return new OnItemClickListener() {

            @Override
            public void onItemClick(final AdapterView<?> parent, final View view,
                                    final int position, final long id) {
                if (!rootView.isDragging() && !rootView.isAnimationRunning()) {
                    if (itemClickListener != null) {
                        itemClickListener.onItemClick(parent, view, position, id);
                    }

                    dismiss();
                }
            }

        };
    }

    /**
     * Creates and returns a listener, which allows to start an app, when an item of the bottom
     * sheet has been clicked.
     *
     * @param activity
     *         The activity, the bottom sheet belongs to, as an instance of the class {@link
     *         Activity}. The activity may not be null
     * @param intent
     *         The intent, which should be passed to the started app, as an instance of the class
     *         {@link Intent}. The intent may not be null
     * @param resolveInfos
     *         A list, which contains the resolve infos, which correspond to the apps, which are
     *         able to handle the intent, as an instance of the type {@link List} or an empty list,
     *         if no apps are able to handle the intent
     * @return The listener, which has been created, as an instance of the type {@link
     * OnItemClickListener}
     */
    private OnItemClickListener createIntentClickListener(@NonNull final Activity activity,
                                                          @NonNull final Intent intent,
                                                          @NonNull final List<ResolveInfo> resolveInfos) {
        return new OnItemClickListener() {

            @Override
            public void onItemClick(final AdapterView<?> parent, final View view,
                                    final int position, final long id) {
                ActivityInfo activityInfo = resolveInfos.get(position).activityInfo;
                ComponentName componentName =
                        new ComponentName(activityInfo.applicationInfo.packageName,
                                activityInfo.name);
                intent.setFlags(
                        Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                intent.setComponent(componentName);
                activity.startActivity(intent);
                dismiss();
            }

        };
    }

    /**
     * Notifies, the listener, which has been registered to be notified, when the bottom sheet has
     * been maximized, about the bottom sheet being maximized.
     */
    private void notifyOnMaximize() {
        if (maximizeListener != null) {
            maximizeListener.onMaximize(this);
        }
    }

    /**
     * Creates a bottom sheet, which is designed according to Android 5's Material Design guidelines
     * even on pre-Lollipop devices.
     *
     * @param context
     *         The context, which should be used by the bottom sheet, as an instance of the class
     *         {@link Context}. The context may not be null
     * @param items
     *         A collection, which contains the items, which should be added to the bottom sheet, as
     *         an instance of the type {@link Collection} or null, if a custom view should be shown
     *         instead
     */
    protected BottomSheet(@NonNull final Context context,
                          @Nullable final Collection<Parcelable> items) {
        super(context);
        initialize(items);
    }

    /**
     * Creates a bottom sheet, which is designed according to Android 5's Material Design guidelines
     * even on pre-Lollipop devices.
     *
     * @param context
     *         The context, which should be used by the bottom sheet, as an instance of the class
     *         {@link Context}. The context may not be null
     * @param themeResourceId
     *         The resource id of the theme, which should be used by the bottom sheet, as an {@link
     *         Integer} value. The resource id must correspond to a valid theme
     * @param items
     *         A collection, which contains the items, which should be added to the bottom sheet, as
     *         an instance of the type {@link Collection} or null, if a custom view should be shown
     *         instead
     */
    protected BottomSheet(@NonNull final Context context, @StyleRes final int themeResourceId,
                          @Nullable final Collection<Parcelable> items) {
        super(context, themeResourceId);
        initialize(items);
    }

    /**
     * Sets the listener, which should be notified, when an item of the bottom sheet has been
     * clicked.
     *
     * @param listener
     *         The listener, which should be set, as an instance of the type {@link
     *         OnItemClickListener} or null, if no listener should be notified
     */
    public final void setOnItemClickListener(@Nullable final OnItemClickListener listener) {
        this.itemClickListener = listener;
    }

    /**
     * Sets the listener, which should be notified, when the bottom sheet has been maximized.
     *
     * @param listener
     *         The listener, which should be set, as an instance of the type {@link
     *         OnMaximizeListener} or null, if no listener should be notified
     */
    public final void setOnMaximizeListener(@Nullable final OnMaximizeListener listener) {
        this.maximizeListener = listener;
    }

    /**
     * Returns the grid view, which is contained by the bottom sheet.
     *
     * @return The grid view, which is contained by the bottom sheet, as an instance of the class
     * {@link GridView} or null, if the bottom sheet does not show any items or has not been shown
     * yet
     */
    public final GridView getGridView() {
        return (gridView != null && gridView.getVisibility() == View.VISIBLE) ? gridView : null;
    }

    /**
     * Returns the adapter of the grid view, which is contained by the bottom sheet.
     *
     * @return The adapter of the grid view, which is contained by the bottom sheet, as an instance
     * of the type {@link ListAdapter} or null, if the bottom sheet does not show any items or has
     * not been shown yet
     */
    public final ListAdapter getListAdapter() {
        return getGridView() != null ? getGridView().getAdapter() : null;
    }

    /**
     * Returns the icon of the bottom sheet.
     *
     * @return The icon of the bottom sheet, as an instance of the class {@link Drawable} or null,
     * if no icon is shown or if the bottom sheet has not been shown yet
     */
    public final Drawable getIcon() {
        return titleTextView != null ? titleTextView.getCompoundDrawables()[0] : null;
    }

    /**
     * Sets the icon of the bottom sheet.
     *
     * @param icon
     *         The icon, which should be set, as an instance of the class {@link Drawable} or null,
     *         if no icon should be shown
     */
    public final void setIcon(final Drawable icon) {
        if (titleTextView != null) {
            titleTextView.setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null);
        }

        if (titleContainer != null) {
            titleContainer.setVisibility(
                    (!TextUtils.isEmpty(getTitle()) || getIcon() != null) ? View.VISIBLE :
                            View.GONE);
        }
    }

    /**
     * Sets the icon of the bottom sheet.
     *
     * @param resourceId
     *         The resource id of the icon, which should be set, as an {@link Integer} value. The
     *         resource id must correspond to a valid drawable resource
     */
    @SuppressWarnings("deprecation")
    public final void setIcon(@DrawableRes final int resourceId) {
        setIcon(getContext().getResources().getDrawable(resourceId));
    }

    /**
     * Set the icon of the bottom sheet.
     *
     * @param attributeId
     *         The id of the theme attribute, which supplies the icon, which should be set, as an
     *         {@link Integer} value. The id must point to a valid drawable resource
     */
    public final void setIconAttribute(@AttrRes final int attributeId) {
        TypedArray typedArray =
                getContext().getTheme().obtainStyledAttributes(new int[]{attributeId});
        setIcon(typedArray.getDrawable(0));
    }

    /**
     * Returns the color of the title of the bottom sheet.
     *
     * @return The color of the title of the bottom sheet as an {@link Integer} value or -1, if no
     * title is shown or if the bottom sheet has not been shown yet
     */
    public final int getTitleColor() {
        return !TextUtils.isEmpty(getTitle()) ? titleTextView.getCurrentTextColor() : -1;
    }

    /**
     * Sets the color of the title of the bottom sheet.
     *
     * @param color
     *         The color, which should be set, as an {@link Integer} value
     */
    public final void setTitleColor(@ColorInt final int color) {
        if (titleTextView != null) {
            titleTextView.setTextColor(color);
        }
    }

    /**
     * Returns the color of the items of the bottom sheet.
     *
     * @return The color of the items of the bottom sheet as an {@link Integer} value or -1, if the
     * bottom sheet does not show any items or has not been shown yet or if no custom color has been
     * set
     */
    public final int getItemColor() {
        if (adapter != null) {
            return adapter.getItemColor();
        }

        return -1;
    }

    /**
     * Sets the color of the items of the bottom sheet.
     *
     * @param color
     *         The color, which should be set, as an {@link Integer} value or -1, if no custom color
     *         should be set
     */
    public final void setItemColor(@ColorInt final int color) {
        if (adapter != null) {
            adapter.setItemColor(color);
        }
    }

    /**
     * Returns the color of the dividers of the bottom sheet.
     *
     * @return The color of the dividers of the bottom sheet as an {@link Integer} value or -1, if
     * the bottom sheet does not show any items or has not been shown yet or if no custom color has
     * been set
     */
    public final int getDividerColor() {
        if (adapter != null) {
            return adapter.getDividerColor();
        }

        return -1;
    }

    /**
     * Sets the color of the dividers of the bottom sheet.
     *
     * @param color
     *         The color, which should be set, as an {@link Integer} value or -1, if no custom color
     *         should be set
     */
    public final void setDividerColor(@ColorInt final int color) {
        if (adapter != null) {
            adapter.setDividerColor(color);
        }
    }

    /**
     * Returns the background of the bottom sheet.
     *
     * @return The background of the bottom sheet as an instance of the class {@link Drawable} or
     * null, if no custom background is set or if the bottom sheet has not been shown yet
     */
    public final Drawable getBackground() {
        return rootView != null ? rootView.getBackground() : null;
    }

    /**
     * Sets the background of the bottom sheet.
     *
     * @param background
     *         The background, which should be set, as an instance of the class {@link Drawable} or
     *         null, if no custom background should be set
     */
    @SuppressWarnings("deprecation")
    public final void setBackground(@Nullable final Drawable background) {
        if (rootView != null) {
            rootView.setBackgroundDrawable(background);
        }
    }

    /**
     * Sets the background of the bottom sheet.
     *
     * @param resourceId
     *         The resource id of the background, which should be set, as an {@link Integer} value.
     *         The resource id must correspond to a valid drawable resource
     */
    @SuppressWarnings("deprecation")
    public final void setBackground(@DrawableRes final int resourceId) {
        setBackground(getContext().getResources().getDrawable(resourceId));
    }

    /**
     * Sets the background color of the bottom sheet.
     *
     * @param color
     *         The background color, which should be set, as an {@link Integer} value or -1, if no
     *         custom background color should be set
     */
    public final void setBackgroundColor(@ColorInt final int color) {
        setBackground(color != -1 ? new ColorDrawable(color) : null);
    }

    /**
     * Sets the custom view, which should be used to show the title of the bottom sheet.
     *
     * @param view
     *         The view, which should be set, as an instance of the class {@link View} or null, if
     *         no custom view should be used to show the title
     */
    public final void setCustomTitle(@Nullable final View view) {
        if (titleContainer != null) {
            CharSequence title = getTitle();
            Drawable icon = getIcon();
            int titleColor = getTitleColor();
            titleContainer.removeAllViews();

            if (view != null) {
                titleContainer.setVisibility(View.VISIBLE);
                titleContainer.addView(view, ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
            } else {
                View.inflate(getContext(), R.layout.bottom_sheet_title, titleContainer);
            }

            View titleView = titleContainer.findViewById(android.R.id.title);

            if (titleView != null && titleView instanceof TextView) {
                titleTextView = (TextView) titleView;
                titleTextView.setTextColor(titleColor);

                if (!TextUtils.isEmpty(title) || icon != null) {
                    titleContainer.setVisibility(View.VISIBLE);
                    titleTextView.setText(title);

                    if (icon != null) {
                        titleTextView
                                .setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null);
                    }

                }
            }
        }
    }

    /**
     * Sets the custom view, which should be shown by the bottom sheet.
     *
     * @param view
     *         The view, which should be set, as an instance of the class {@link View} or null, if
     *         no custom view should be shown
     */
    public final void setView(@Nullable final View view) {
        if (contentContainer != null) {
            contentContainer.removeAllViews();
            LinearLayout.LayoutParams titleLayoutParams =
                    (LinearLayout.LayoutParams) titleContainer.getLayoutParams();

            if (view != null) {
                contentContainer.setVisibility(View.VISIBLE);
                contentContainer.addView(view, ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
            } else {
                contentContainer.setVisibility(View.GONE);
            }

            titleContainer.setLayoutParams(titleLayoutParams);
        }
    }

    /**
     * Sets the custom view, which should be shown by the bottom sheet.
     *
     * @param resourceId
     *         The resource id of the view, which should be set, as an {@link Integer} value. The
     *         resource id must correspond to a valid layout resource
     */
    public final void setView(@LayoutRes final int resourceId) {
        setView(View.inflate(getContext(), resourceId, null));
    }

    /**
     * Returns the sensitivity, which specifies the distance after which dragging has an effect on
     * the bottom sheet, in relation to an internal value range.
     *
     * @return The drag sensitivity as a {@link Float} value. The drag sensitivity must be at lest 0
     * and at maximum 1
     */
    public final float getDragSensitivity() {
        return dragSensitivity;
    }

    /**
     * Sets the sensitivity, which specifies the distance after which dragging has an effect on the
     * bottom sheet, in relation to an internal value range.
     *
     * @param dragSensitivity
     *         The drag sensitivity, which should be set, as a {@link Float} value. The drag
     *         sensitivity must be at lest 0 and at maximum 1
     */
    public final void setDragSensitivity(final float dragSensitivity) {
        ensureAtLeast(dragSensitivity, 0, "The drag sensitivity must be at least 0");
        ensureAtMaximum(dragSensitivity, 1, "The drag sensitivity must be at maximum 1");
        this.dragSensitivity = dragSensitivity;

        if (rootView != null) {
            rootView.setDragSensitivity(calculateDragSensitivity());
        }
    }

    /**
     * Returns the dim amount, which is used to darken the area outside the bottom sheet.
     *
     * @return The dim amount, which is used to darken the area outside the bottom sheet, as a
     * {@link Float} value
     */
    public final float getDimAmount() {
        return dimAmount;
    }

    /**
     * Sets the dim amount, which should be used to darken the area outside the bottom sheet.
     *
     * @param dimAmount
     *         The dim amount, which should be set, as a {@link Float} value. The dim amount must be
     *         at least 0 (fully transparent) and at maximum 1 (fully opaque)
     */
    public final void setDimAmount(final float dimAmount) {
        ensureAtLeast(dimAmount, 0, "The dim amount must be at least 0");
        ensureAtMaximum(dimAmount, 1, "The dim amount must be at maximum 1");
        this.dimAmount = dimAmount;
        getWindow().getAttributes().dimAmount = dimAmount;
    }

    /**
     * Returns the width of the bottom sheet. The width is only used on tablet devices or in
     * landscape mode.
     *
     * @return The width of the bottom sheet in pixels as an {@link Integer} value
     */
    public final int getWidth() {
        return width;
    }

    /**
     * Sets the width of the bottom sheet. The width is only used on tablet devices or in landscape
     * mode.
     *
     * @param width
     *         The width, which should be set, in pixels as an {@link Integer} value. The width must
     *         be at least 1
     */
    public final void setWidth(final int width) {
        ensureAtLeast(width, 1, "The width must be at least 1");
        this.width = width;

        if (rootView != null) {
            rootView.setWidth(width);
        }
    }

    /**
     * Adds a new item to the bottom sheet.
     *
     * @param id
     *         The id of the item, which should be added, as an {@link Integer} value
     * @param title
     *         The title of the item, which should be added, as an instance of the type {@link
     *         CharSequence}. The title may neither be null, nor empty
     */
    public final void addItem(final int id, @NonNull final CharSequence title) {
        if (adapter != null) {
            Item item = new Item(id, title);
            adapter.add(item);
        }
    }

    /**
     * Adds a new item to the bottom sheet.
     *
     * @param id
     *         The id of the item, which should be added, as an {@link Integer} value
     * @param title
     *         The title of the item, which should be added, as an instance of the type {@link
     *         CharSequence}. The title may neither be null, nor empty
     * @param icon
     *         The icon of the item, which should be added, as an instance of the class {@link
     *         Drawable}, or null, if no item should be used
     */
    public final void addItem(final int id, @NonNull final CharSequence title,
                              @Nullable final Drawable icon) {
        if (adapter != null) {
            Item item = new Item(id, title);
            item.setIcon(icon);
            adapter.add(item);
        }
    }

    /**
     * Adds a new item to the bottom sheet.
     *
     * @param id
     *         The id of the item, which should be added, as an {@link Integer} value
     * @param titleId
     *         The resource id of the title of the item, which should be added, as an {@link
     *         Integer} value. The resource id must correspond to a valid string resource
     */
    public final void addItem(final int id, @StringRes final int titleId) {
        if (adapter != null) {
            Item item = new Item(id, getContext(), titleId);
            adapter.add(item);
        }
    }

    /**
     * Adds a new item to the bottom sheet.
     *
     * @param id
     *         The id of the item, which should be added, as an {@link Integer} value
     * @param titleId
     *         The resource id of the title of the item, which should be added, as an {@link
     *         Integer} value. The resource id must correspond to a valid string resource
     * @param iconId
     *         The resource id of the icon of the item, which should be added, as an {@link Integer}
     *         value. The resource id must correspond to a valid drawable resource
     */
    public final void addItem(final int id, @StringRes final int titleId,
                              @DrawableRes final int iconId) {
        if (adapter != null) {
            Item item = new Item(id, getContext(), titleId);
            item.setIcon(getContext(), iconId);
            adapter.add(item);
        }
    }

    /**
     * Replaces the item with a specific id with another item.
     *
     * @param id
     *         The id of the item, which should be replaced, as an {@link Integer} value
     * @param title
     *         The title of the item, which should be added, as an instance of the type {@link
     *         CharSequence}. The title may neither be null, nor empty
     */
    public final void setItem(final int id, @NonNull final CharSequence title) {
        if (adapter != null) {
            Item item = new Item(id, title);
            adapter.set(id, item);
        }
    }

    /**
     * Replaces the item with a specific id with another item.
     *
     * @param id
     *         The id of the item, which should be replaced, as an {@link Integer} value
     * @param title
     *         The title of the item, which should be added, as an instance of the type {@link
     *         CharSequence}. The title may neither be null, nor empty
     * @param icon
     *         The icon of the item, which should be added, as an instance of the class {@link
     *         Drawable}, or null, if no item should be used
     */
    public final void setItem(final int id, @NonNull final CharSequence title,
                              @Nullable final Drawable icon) {
        if (adapter != null) {
            Item item = new Item(id, title);
            item.setIcon(icon);
            adapter.set(id, item);
        }
    }

    /**
     * Replaces the item with a specific id with another item.
     *
     * @param id
     *         The id of the item, which should be replaced, as an {@link Integer} value
     * @param titleId
     *         The resource id of the title of the item, which should be added, as an {@link
     *         Integer} value. The resource id must correspond to a valid string resource
     */
    public final void setItem(final int id, @StringRes final int titleId) {
        if (adapter != null) {
            Item item = new Item(id, getContext(), titleId);
            adapter.set(id, item);
        }
    }

    /**
     * Replaces the item with a specific id with another item.
     *
     * @param id
     *         The id of the item, which should be replaced, as an {@link Integer} value
     * @param titleId
     *         The resource id of the title of the item, which should be added, as an {@link
     *         Integer} value. The resource id must correspond to a valid string resource
     * @param iconId
     *         The resource id of the icon of the item, which should be added, as an {@link Integer}
     *         value. The resource id must correspond to a valid drawable resource
     */
    public final void setItem(final int id, @StringRes final int titleId,
                              @DrawableRes final int iconId) {
        if (adapter != null) {
            Item item = new Item(id, getContext(), titleId);
            item.setIcon(getContext(), iconId);
            adapter.set(id, item);
        }
    }

    /**
     * Adds a new divider to the bottom sheet.
     *
     * @param id
     *         The id of the divider, which should be added, as an {@link Integer} value
     */
    public final void addDivider(final int id) {
        if (adapter != null) {
            adapter.add(new Divider(id));
        }
    }

    /**
     * Adds a new divider to the bottom sheet.
     *
     * @param id
     *         The id of the divider, which should be added, as an {@link Integer} value
     * @param title
     *         The title of the divider, which should be added, as an instance of the type {@link
     *         CharSequence}, or null, if no title should be used
     */
    public final void addDivider(final int id, @Nullable final CharSequence title) {
        if (adapter != null) {
            Divider divider = new Divider(id);
            divider.setTitle(title);
            adapter.add(divider);
        }
    }

    /**
     * Adds a new divider to the bottom sheet.
     *
     * @param id
     *         The id of the divider, which should be added, as an {@link Integer} value
     * @param titleId
     *         The resource id of the title of the divider, which should be added, as an {@link
     *         Integer} value. The resource id must correspond to a valid string resource
     */
    public final void addDivider(final int id, @StringRes final int titleId) {
        if (adapter != null) {
            Divider divider = new Divider(id);
            divider.setTitle(getContext(), titleId);
            adapter.add(divider);
        }
    }

    /**
     * Replaces the item with a specific id with a divider.
     *
     * @param id
     *         The id of the item, which should be replaced, as an {@link Integer} value
     */
    public final void setDivider(final int id) {
        if (adapter != null) {
            Divider divider = new Divider(id);
            adapter.set(id, divider);
        }
    }

    /**
     * Replaces the item with a specific id with a divider.
     *
     * @param id
     *         The id of the item, which should be replaced, as an {@link Integer} value
     * @param title
     *         The title of the divider, which should be added, as an instance of the type {@link
     *         CharSequence}, or null, if no title should be used
     */
    public final void setDivider(final int id, @Nullable final CharSequence title) {
        if (adapter != null) {
            Divider divider = new Divider(id);
            divider.setTitle(title);
            adapter.set(id, divider);
        }
    }

    /**
     * Replaces the item with a specific id with a divider.
     *
     * @param id
     *         The id of the item, which should be replaced, as an {@link Integer} value
     * @param titleId
     *         The resource id of the title of the divider, which should be added, as an {@link
     *         Integer} value. The resource id must correspond to a valid string resource
     */
    public final void setDivider(final int id, @StringRes final int titleId) {
        if (adapter != null) {
            Divider divider = new Divider(id);
            divider.setTitle(getContext(), titleId);
            adapter.set(id, divider);
        }
    }

    /**
     * Removes the item with a specific id from the bottom sheet.
     *
     * @param id
     *         The id of the item, which should be removed, as an {@link Integer} value
     */
    public final void removeItem(final int id) {
        if (adapter != null) {
            adapter.remove(id);
        }
    }

    /**
     * Removes all items from the bottom sheet.
     */
    public final void removeAllItems() {
        if (adapter != null) {
            adapter.clear();
        }
    }

    /**
     * Returns the number of items, which are currently contained by the bottom sheet.
     *
     * @return The number of items, which are currently contained by the bottom sheet, as an {@link
     * Integer} value or -1, if the bottom sheet does not show any items or has not been shown yet
     */
    public final int getItemCount() {
        if (adapter != null) {
            return adapter.getCount();
        }

        return -1;
    }

    /**
     * Adds the apps, which are able to handle a specific intent, as items to the bottom sheet. This
     * causes all previously added items to be removed. When an item is clicked, the corresponding
     * app is started.
     *
     * @param activity
     *         The activity, the bottom sheet belongs to, as an instance of the class {@link
     *         Activity}. The activity may not be null
     * @param intent
     *         The intent as an instance of the class {@link Intent}. The intent may not be null
     */
    public final void setIntent(@NonNull final Activity activity, @NonNull final Intent intent) {
        ensureNotNull(activity, "The activity may not be null");
        ensureNotNull(intent, "The intent may not be null");

        if (adapter != null) {
            removeAllItems();
            PackageManager packageManager = activity.getPackageManager();
            List<ResolveInfo> resolveInfos = packageManager.queryIntentActivities(intent, 0);

            for (int i = 0; i < resolveInfos.size(); i++) {
                ResolveInfo resolveInfo = resolveInfos.get(i);
                addItem(i, resolveInfo.loadLabel(packageManager),
                        resolveInfo.loadIcon(packageManager));
            }

            setOnItemClickListener(
                    createIntentClickListener(activity, (Intent) intent.clone(), resolveInfos));
        }
    }

    /**
     * Invalidates the bottom sheet. This method must be called in order to update the appearance of
     * the bottom sheet, when its items have been changed.
     */
    public final void invalidate() {
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * Sets, whether the bottom sheet should automatically be invalidated, when its items have been
     * changed, or not.
     *
     * @param invalidateOnChange
     *         True, if the bottom sheet should automatically be invalidated, when its items have
     *         been changed, false otherwise
     */
    public final void invalidateOnChange(final boolean invalidateOnChange) {
        if (adapter != null) {
            adapter.notifyOnChange(invalidateOnChange);
        }
    }

    /**
     * Returns, whether the bottom sheet is currently maximized, or not.
     *
     * @return True, if the bottom sheet is currently maximized, false otherwise
     */
    public final boolean isMaximized() {
        return rootView != null && rootView.isMaximized();
    }

    /**
     * Returns the title of the bottom sheet.
     *
     * @return The title of the bottom sheet as an instance of the type {@link CharSequence} or
     * null, if no title is shown or if the bottom sheet has not been shown yet
     */
    public final CharSequence getTitle() {
        return titleTextView != null ? titleTextView.getText() : null;
    }

    @Override
    public final void setTitle(@Nullable final CharSequence title) {
        super.setTitle(title);

        if (titleTextView != null) {
            titleTextView.setText(title);
        }

        if (titleContainer != null) {
            titleContainer.setVisibility(
                    (!TextUtils.isEmpty(getTitle()) || getIcon() != null) ? View.VISIBLE :
                            View.GONE);
        }
    }

    @Override
    public final void dismiss() {
        if (isShowing()) {
            rootView.hideView(false);
        }
    }

    @Override
    public final void cancel() {
        if (isShowing()) {
            rootView.hideView(true);
        }
    }

    @Override
    public final void setCancelable(final boolean cancelable) {
        super.setCancelable(cancelable);
        this.cancelable = cancelable;
    }

    @Override
    public final void setCanceledOnTouchOutside(final boolean canceledOnTouchOutside) {
        super.setCanceledOnTouchOutside(canceledOnTouchOutside);
        this.canceledOnTouchOutside = canceledOnTouchOutside;
    }

    @Override
    public final void onMaximized() {
        notifyOnMaximize();
    }

    @Override
    public final void onHidden(final boolean canceled) {
        if (canceled) {
            super.cancel();
        } else {
            super.dismiss();
        }
    }

    @Override
    public final void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setAttributes(createLayoutParams());
        getWindow().getDecorView().setOnTouchListener(createCancelOnTouchListener());
        rootView = (DraggableView) findViewById(R.id.root);
        rootView.setDragSensitivity(calculateDragSensitivity());
        rootView.setWidth(width);
        rootView.setCallback(this);
        titleContainer = (ViewGroup) findViewById(R.id.title_container);
        View titleView = findViewById(android.R.id.title);
        contentContainer = (ViewGroup) findViewById(R.id.content_container);
        titleTextView =
                (titleView != null && titleView instanceof TextView) ? (TextView) titleView : null;
        gridView = (GridView) findViewById(R.id.bottom_sheet_grid_view);

        if (gridView != null) {
            gridView.setOnItemClickListener(createItemClickListener());
            gridView.setAdapter(adapter);
        }
    }

}
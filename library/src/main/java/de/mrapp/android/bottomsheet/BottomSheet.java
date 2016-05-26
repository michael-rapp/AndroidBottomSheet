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
package de.mrapp.android.bottomsheet;

import android.annotation.TargetApi;
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
import android.os.Build;
import android.support.annotation.AttrRes;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.annotation.StyleRes;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.List;

import de.mrapp.android.bottomsheet.adapter.DividableGridAdapter;
import de.mrapp.android.bottomsheet.model.AbstractItem;
import de.mrapp.android.bottomsheet.model.Divider;
import de.mrapp.android.bottomsheet.model.Item;
import de.mrapp.android.bottomsheet.view.DividableGridView;
import de.mrapp.android.bottomsheet.view.DraggableView;
import de.mrapp.android.util.DisplayUtil;
import de.mrapp.android.util.ViewUtil;

import static de.mrapp.android.util.Condition.ensureAtLeast;
import static de.mrapp.android.util.Condition.ensureAtMaximum;
import static de.mrapp.android.util.Condition.ensureNotNull;
import static de.mrapp.android.util.DisplayUtil.getDeviceType;
import static de.mrapp.android.util.DisplayUtil.getOrientation;

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
         * The bottom sheet, which is created by the builder.
         */
        private BottomSheet bottomSheet;

        /**
         * Initializes the builder.
         *
         * @param context
         *         The context, which should be used by the builder, as an instance of the class
         *         {@link Context}. The context may not be null
         * @param themeResourceId
         *         The resource id of the theme, which should be used by the dialog, as an {@link
         *         Integer} value, or -1, if the default theme should be used
         */
        private void initialize(@NonNull final Context context,
                                @StyleRes final int themeResourceId) {
            TypedValue typedValue = new TypedValue();
            context.getTheme().resolveAttribute(R.attr.bottomSheetTheme, typedValue, true);
            int themeId = typedValue.resourceId;
            themeId = themeId != 0 ? themeId : R.style.BottomSheet_Light;
            bottomSheet = new BottomSheet(context, themeId);
            bottomSheet.requestWindowFeature(Window.FEATURE_NO_TITLE);
            bottomSheet.setCanceledOnTouchOutside(true);
            bottomSheet.setCancelable(true);
            bottomSheet.setContentView(createContentView(),
                    new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT));
            obtainStyledAttributes(themeId);
        }

        /**
         * Creates and returns the bottom sheetview stub, which allows to inflate the bottom sheet's
         * layout when shown.
         *
         * @return The view stub, which has been created
         */
        private View createContentView() {
            FrameLayout contentView = new FrameLayout(getContext());
            contentView.setId(android.R.id.content);
            return contentView;
        }

        /**
         * Obtains all relevant attributes from the current theme.
         */
        private void obtainStyledAttributes(@StyleRes final int themeResourceId) {
            obtainBackground(themeResourceId);
            obtainTitleColor(themeResourceId);
            obtainItemColor(themeResourceId);
            obtainDividerColor(themeResourceId);
            obtainDimAmount(themeResourceId);
            obtainDragSensitivity(themeResourceId);
        }

        /**
         * Obtains the background from the current theme.
         *
         * @param themeResourceId
         *         The resource id of the theme, the background should be obtained from, as an
         *         {@link Integer} value
         */
        private void obtainBackground(@StyleRes final int themeResourceId) {
            TypedArray typedArray = getContext().getTheme().obtainStyledAttributes(themeResourceId,
                    new int[]{R.attr.bottomSheetBackground});
            int color = typedArray.getColor(0, -1);

            if (color != -1) {
                setBackgroundColor(color);
            } else {
                int resourceId = typedArray.getResourceId(0, 0);

                if (resourceId != 0) {
                    setBackground(resourceId);
                }
            }
        }

        /**
         * Obtains the title color from the current theme.
         *
         * @param themeResourceId
         *         The resource id of the theme, the title color should be obtained from, as an
         *         {@link Integer} value
         */
        private void obtainTitleColor(@StyleRes final int themeResourceId) {
            TypedArray typedArray = getContext().getTheme().obtainStyledAttributes(themeResourceId,
                    new int[]{R.attr.bottomSheetTitleColor});
            int color = typedArray.getColor(0, -1);

            if (color != -1) {
                setTitleColor(color);
            }
        }

        /**
         * Obtains the divider color from the current theme.
         *
         * @param themeResourceId
         *         The resource id of the theme, the divider color should be obtained from, as an
         *         {@link Integer} value
         */
        private void obtainDividerColor(@StyleRes final int themeResourceId) {
            TypedArray typedArray = getContext().getTheme().obtainStyledAttributes(themeResourceId,
                    new int[]{R.attr.bottomSheetDividerColor});
            int color = typedArray.getColor(0, -1);

            if (color != -1) {
                setDividerColor(color);
            }
        }

        /**
         * Obtains the item color from the current theme.
         *
         * @param themeResourceId
         *         The resource id of the theme, the item color should be obtained from, as an
         *         {@link Integer} value
         */
        private void obtainItemColor(@StyleRes final int themeResourceId) {
            TypedArray typedArray = getContext().getTheme().obtainStyledAttributes(themeResourceId,
                    new int[]{R.attr.bottomSheetItemColor});
            int color = typedArray.getColor(0, -1);

            if (color != -1) {
                setItemColor(color);
            }
        }

        /**
         * Obtains the dim amount from the current theme.
         *
         * @param themeResourceId
         *         The resource id of the theme, the dim amount should be obtained from, as an
         *         {@link Integer} value
         */
        private void obtainDimAmount(@StyleRes final int themeResourceId) {
            TypedArray typedArray = getContext().getTheme().obtainStyledAttributes(themeResourceId,
                    new int[]{R.attr.bottomSheetDimAmount});
            float dimAmount = typedArray.getFraction(0, 1, 1, -1);

            if (dimAmount != -1) {
                setDimAmount(dimAmount);
            }
        }

        /**
         * Obtains the drag sensitivity from the current theme.
         *
         * @param themeResourceId
         *         The resource id of the theme, the drag sensitivity should be obtained from, as an
         *         {@link Integer} value
         */
        private void obtainDragSensitivity(@StyleRes final int themeResourceId) {
            TypedArray typedArray = getContext().getTheme().obtainStyledAttributes(themeResourceId,
                    new int[]{R.attr.bottomSheetDragSensitivity});
            float dragSensitivity = typedArray.getFraction(0, 1, 1, -1);

            if (dragSensitivity != -1) {
                setDragSensitivity(dragSensitivity);
            }
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
            initialize(context, themeResourceId);
        }

        /**
         * Returns the context, which is used by the builder.
         *
         * @return The context, which is used by the builder, as an instance of the class {@link
         * Context}
         */
        public final Context getContext() {
            return bottomSheet.getContext();
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
            bottomSheet.setCancelable(cancelable);
            return this;
        }

        /**
         * Sets the style of the bottom sheet, which is created by the builder.
         *
         * @param style
         *         The style, which should be set, as a value of the enum {@link Style}. The style
         *         may either be <code>LIST</code>, <code>LIST_COLUMNS</code> or <code>GRID</code>
         * @return The builder, the method has been called upon, as an instance of the class {@link
         * Builder}
         */
        public final Builder setStyle(@NonNull final Style style) {
            bottomSheet.setStyle(style);
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
            bottomSheet.setOnItemClickListener(listener);
            return this;
        }

        /**
         * Sets the listener, which should be notified, when an item of the bottom sheet has been
         * long-clicked.
         *
         * @param listener
         *         The listener, which should be set, as an instance of the type {@link
         *         OnItemLongClickListener} or null, if no listener should be notified
         * @return The builder, the method has been called upon, as an instance of the class {@link
         * Builder}
         */
        public final Builder setOnItemLongClickListener(
                @Nullable final OnItemLongClickListener listener) {
            bottomSheet.setOnItemLongClickListener(listener);
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
            bottomSheet.setOnMaximizeListener(listener);
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
            bottomSheet.setOnCancelListener(listener);
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
            bottomSheet.setOnDismissListener(listener);
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
            bottomSheet.setOnKeyListener(listener);
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
            bottomSheet.setTitleColor(color);
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
            bottomSheet.setItemColor(color);
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
            bottomSheet.setDividerColor(color);
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
            bottomSheet.setBackground(background);
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
        public final Builder setBackground(@DrawableRes final int resourceId) {
            bottomSheet.setBackground(resourceId);
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
            bottomSheet.setBackgroundColor(color);
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
            bottomSheet.setTitle(title);
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
            bottomSheet.setTitle(resourceId);
            return this;
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
            bottomSheet.setIcon(icon);
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
        public final Builder setIcon(@DrawableRes final int resourceId) {
            bottomSheet.setIcon(resourceId);
            return this;
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
            bottomSheet.setIconAttribute(attributeId);
            return this;
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
            bottomSheet.setView(view);
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
            bottomSheet.setView(resourceId);
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
            bottomSheet.setCustomTitle(view);
            return this;
        }

        /**
         * Sets the custom view, which should be used to show the title of the bottom sheet, which
         * is created by the builder.
         *
         * @param resourceId
         *         The resource id of the view, which should be set, as an {@link Integer} value.
         *         The resource id must correspond to a valid layout resource
         * @return The builder, the method has been called upon, as an instance of the class {@link
         * Builder}
         */
        public final Builder setCustomTitle(@LayoutRes final int resourceId) {
            bottomSheet.setCustomTitle(resourceId);
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
            bottomSheet.setDragSensitivity(dragSensitivity);
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
            bottomSheet.setDimAmount(dimAmount);
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
            bottomSheet.setWidth(width);
            return this;
        }

        /**
         * Adds a new item to the bottom sheet, which is created by the builder.
         *
         * @param id
         *         The id of the item, which should be added, as an {@link Integer} value. The id
         *         must be at least 0
         * @param title
         *         The title of the item, which should be added, as an instance of the type {@link
         *         CharSequence}. The title may neither be null, nor empty
         * @return The builder, the method has been called upon, as an instance of the class {@link
         * Builder}
         */
        public final Builder addItem(final int id, @NonNull final CharSequence title) {
            bottomSheet.addItem(id, title);
            return this;
        }

        /**
         * Adds a new item to the bottom sheet, which is created by the builder.
         *
         * @param id
         *         The id of the item, which should be added, as an {@link Integer} value. The id
         *         must be at least 0
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
            bottomSheet.addItem(id, title, icon);
            return this;
        }

        /**
         * Adds a new item to the bottom sheet, which is created by the builder.
         *
         * @param id
         *         The id of the item, which should be added, as an {@link Integer} value. The id
         *         must be at least 0
         * @param titleId
         *         The resource id of the title of the item, which should be added, as an {@link
         *         Integer} value. The resource id must correspond to a valid string resource
         * @return The builder, the method has been called upon, as an instance of the class {@link
         * Builder}
         */
        public final Builder addItem(final int id, @StringRes final int titleId) {
            bottomSheet.addItem(id, titleId);
            return this;
        }

        /**
         * Adds a new item to the bottom sheet, which is created by the builder.
         *
         * @param id
         *         The id of the item, which should be added, as an {@link Integer} value. The id
         *         must be at least 0
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
            bottomSheet.addItem(id, titleId, iconId);
            return this;
        }

        /**
         * Adds a new divider to the bottom sheet, which is created by the builder.
         *
         * @return The builder, the method has been called upon, as an instance of the class {@link
         * Builder}
         */
        public final Builder addDivider() {
            bottomSheet.addDivider();
            return this;
        }

        /**
         * Adds a new divider to the bottom sheet, which is created by the builder.
         *
         * @param title
         *         The title of the divider, which should be added, as an instance of the type
         *         {@link CharSequence}, or null, if no title should be used
         */
        public final Builder addDivider(@Nullable final CharSequence title) {
            bottomSheet.addDivider(title);
            return this;
        }

        /**
         * Adds a new divider to the bottom sheet, which is created by the builder.
         *
         * @param titleId
         *         The resource id of the title, which should be added, as an {@link Integer} value.
         *         The resource id must correspond to a valid string resource
         * @return The builder, the method has been called upon, as an instance of the class {@link
         * Builder}
         */
        public final Builder addDivider(@StringRes final int titleId) {
            bottomSheet.addDivider(titleId);
            return this;
        }

        /**
         * Sets, whether the item at a specific index should be enabled, or not.
         *
         * @param index
         *         The index of the item as an {@link Integer} value
         * @param enabled
         *         True, if the item should be enabled, false otherwise
         * @return The builder, the method has been called upon, as an instance of the class {@link
         * Builder}
         */
        public final Builder setItemEnabled(final int index, final boolean enabled) {
            bottomSheet.setItemEnabled(index, enabled);
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
            bottomSheet.setIntent(activity, intent);
            return this;
        }

        /**
         * Creates a bottom sheet with the arguments, which have been supplied to the builder.
         * Calling this method does not display the bottom sheet.
         *
         * @return The bottom sheet, which has been created as an instance of the class {@link
         * BottomSheet}
         */
        public final BottomSheet create() {
            return bottomSheet;
        }

        /**
         * Creates a bottom sheet with the arguments, which have been supplied to the builder and
         * immediately displays it.
         *
         * @return The bottom sheet, which has been created, as an instance of the class {@link
         * BottomSheet}
         */
        public final BottomSheet show() {
            bottomSheet.show();
            return bottomSheet;
        }

        /**
         * Creates a bottom sheet with the arguments, which have been supplied to the builder and
         * immediately maximizes it.
         *
         * @return The bottom sheet, which has been created, as an instance of the class {@link
         * BottomSheet}
         */
        @TargetApi(Build.VERSION_CODES.FROYO)
        public final BottomSheet maximize() {
            bottomSheet.maximize();
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
         * If the bottom sheet's items should be shown as a two-columned list on tablet devices and
         * in landscape mode.
         */
        LIST_COLUMNS,

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
     * The root view of the bottom sheet.
     */
    private DraggableView rootView;

    /**
     * The layout, which is used to show the bottom sheet's title.
     */
    private ViewGroup titleContainer;

    /**
     * The text view, which is used to show the bottom sheet's title.
     */
    private TextView titleTextView;

    /**
     * The layout, which is used to show the bottom sheet's content.
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
     * The title of the bottom sheet.
     */
    private CharSequence title;

    /**
     * The icon of the bottom sheet.
     */
    private Drawable icon;

    /**
     * The color of the title of the bottom sheet.
     */
    private int titleColor = -1;

    /**
     * The background of the bottom sheet.
     */
    private Drawable background;

    /**
     * The custom content view of the bottom sheet.
     */
    private View customView;

    /**
     * The resource id of the custom content view of the bottom sheet.
     */
    private int customViewId = -1;

    /**
     * The custom title view of the bottom sheet.
     */
    private View customTitleView;

    /**
     * The resource id of the custom title view of the bottom sheet.
     */
    private int customTitleViewId = -1;

    /**
     * True, if the bottom sheet should be maximized immediately after it has been shown, false
     * otherwise.
     */
    private boolean maximize;

    /**
     * The listener, which is notified, when the bottom sheet has been shown.
     */
    private OnShowListener onShowListener;

    /**
     * The listener, which is notified, when an item of the bottom sheet has been clicked.
     */
    private OnItemClickListener itemClickListener;

    /**
     * The listener, which is notified, when an item of the bottom sheet has been long-clicked.
     */
    private OnItemLongClickListener itemLongClickListener;

    /**
     * The listener, which is notified, when the bottom sheet is maximized.
     */
    private OnMaximizeListener maximizeListener;

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
     */
    private void initialize() {
        width = getContext().getResources().getDimensionPixelSize(R.dimen.default_width);
        maximize = false;
        adapter = new DividableGridAdapter(getContext(), Style.LIST, width);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            super.setOnShowListener(createOnShowListener());
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
     * Creates and returns the layout params, which should be used to show the bottom sheet's root
     * view.
     *
     * @return The layout params, which have been created, as an instance of the class {@link
     * android.widget.FrameLayout.LayoutParams }
     */
    private FrameLayout.LayoutParams createRootViewLayoutParams() {
        FrameLayout.LayoutParams layoutParams =
                new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT);
        layoutParams.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        return layoutParams;
    }

    /**
     * Initializes the bottom sheet's root view.
     */
    private void inflateRootView() {
        ViewGroup contentView = (ViewGroup) findViewById(android.R.id.content);
        contentView.removeAllViews();
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        rootView =
                (DraggableView) layoutInflater.inflate(R.layout.bottom_sheet, contentView, false);
        rootView.setCallback(this);
        contentView.addView(rootView, createRootViewLayoutParams());
    }

    /**
     * Inflates the layout, which is used to show the bottom sheet's title. The layout may either be
     * the default one or a custom view, if one has been set before.
     */
    private void inflateTitleView() {
        titleContainer = (ViewGroup) rootView.findViewById(R.id.title_container);
        titleContainer.removeAllViews();

        if (customTitleView != null) {
            titleContainer.addView(customTitleView);
        } else if (customTitleViewId != -1) {
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            View view = layoutInflater.inflate(customTitleViewId, titleContainer, false);
            titleContainer.addView(view);
        } else {
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            View view = layoutInflater.inflate(R.layout.bottom_sheet_title, titleContainer, false);
            titleContainer.addView(view);
        }

        if (getStyle() == Style.LIST) {
            int padding = getContext().getResources()
                    .getDimensionPixelSize(R.dimen.bottom_sheet_list_item_horizontal_padding);
            titleContainer.setPadding(padding, 0, padding, 0);
        } else {
            int padding = getContext().getResources()
                    .getDimensionPixelSize(R.dimen.bottom_sheet_grid_item_horizontal_padding);
            titleContainer.setPadding(padding, 0, padding, 0);
        }

        View titleView = titleContainer.findViewById(android.R.id.title);
        titleTextView = titleView instanceof TextView ? (TextView) titleView : null;
    }

    /**
     * Inflates the layout, which is used to show the bottom sheet's content. The layout may either
     * be the default one or a custom view, if one has been set before.
     */
    private void inflateContentView() {
        contentContainer = (ViewGroup) rootView.findViewById(R.id.content_container);
        contentContainer.removeAllViews();

        if (customView != null) {
            contentContainer.setVisibility(View.VISIBLE);
            contentContainer.addView(customView);
        } else if (customViewId != -1) {
            contentContainer.setVisibility(View.VISIBLE);
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            View view = layoutInflater.inflate(customViewId, contentContainer, false);
            contentContainer.addView(view);
        } else {
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            View view = layoutInflater
                    .inflate(R.layout.bottom_sheet_grid_view, contentContainer, false);
            contentContainer.addView(view);
        }

        showGridView();
    }

    /**
     * Shows the grid view, which is used to show the bottom sheet's items.
     */
    private void showGridView() {
        gridView = (GridView) contentContainer.findViewById(R.id.bottom_sheet_grid_view);

        if (gridView != null) {
            contentContainer.setVisibility(View.VISIBLE);

            if (getStyle() == Style.GRID) {
                int horizontalPadding = getContext().getResources()
                        .getDimensionPixelSize(R.dimen.bottom_sheet_grid_item_horizontal_padding);
                int paddingBottom = getContext().getResources()
                        .getDimensionPixelSize(R.dimen.bottom_sheet_grid_padding_bottom);
                gridView.setPadding(horizontalPadding, 0, horizontalPadding, paddingBottom);
                gridView.setNumColumns(GridView.AUTO_FIT);
                gridView.setColumnWidth(getContext().getResources()
                        .getDimensionPixelSize(R.dimen.bottom_sheet_grid_item_size));
            } else {
                int paddingBottom = getContext().getResources()
                        .getDimensionPixelSize(R.dimen.bottom_sheet_list_padding_bottom);
                gridView.setPadding(0, 0, 0, paddingBottom);
                gridView.setNumColumns(getStyle() == Style.LIST_COLUMNS &&
                        (getDeviceType(getContext()) == DisplayUtil.DeviceType.TABLET ||
                                getOrientation(getContext()) == DisplayUtil.Orientation.LANDSCAPE) ?
                        2 : 1);
            }

            gridView.setOnItemClickListener(createItemClickListener());
            gridView.setOnItemLongClickListener(createItemLongClickListener());
            gridView.setAdapter(adapter);
        }
    }

    /**
     * Adapts the view, which is used to show the dialog's content.
     */
    private void adaptContentView() {
        if (contentContainer != null) {
            inflateContentView();
        }
    }

    /**
     * Adapts the root view.
     */
    private void adaptRootView() {
        if (rootView != null) {
            if (getStyle() == Style.LIST) {
                int paddingTop = getContext().getResources()
                        .getDimensionPixelSize(R.dimen.bottom_sheet_list_padding_top);
                rootView.setPadding(0, paddingTop, 0, 0);
            } else {
                int paddingTop = getContext().getResources()
                        .getDimensionPixelSize(R.dimen.bottom_sheet_grid_padding_top);
                rootView.setPadding(0, paddingTop, 0, 0);
            }
        }
    }

    /**
     * Adapts the view, which is used to show the dialog's title.
     */
    private void adaptTitleView() {
        if (titleContainer != null) {
            inflateTitleView();
            adaptTitle();
            adaptTitleColor();
            adaptIcon();
        }
    }

    /**
     * Adapts the color of the bottom sheet's title.
     */
    private void adaptTitleColor() {
        if (titleTextView != null && titleColor != -1) {
            titleTextView.setTextColor(titleColor);
        }
    }

    /**
     * Adapts the bottom sheet's title.
     */
    private void adaptTitle() {
        if (titleTextView != null) {
            titleTextView.setText(title);
        }

        adaptTitleContainerVisibility();
    }

    /**
     * Adapts the bottom sheet's icon.
     */
    private void adaptIcon() {
        if (titleTextView != null) {
            titleTextView.setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null);
        }

        adaptTitleContainerVisibility();
    }

    /**
     * Adapts the visibility of the layout, which is used to show the bottom sheet's title.
     */
    private void adaptTitleContainerVisibility() {
        if (titleContainer != null) {
            if (customTitleView == null && customTitleViewId == -1) {
                titleContainer.setVisibility(
                        !TextUtils.isEmpty(title) || icon != null ? View.VISIBLE : View.GONE);
            } else {
                titleContainer.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * Adapts the bottom sheet's background.
     */
    private void adaptBackground() {
        if (rootView != null && background != null) {
            ViewUtil.setBackground(rootView, background);
        }
    }

    /**
     * Adapts the bottom sheet's drag sensitivity.
     */
    private void adaptDragSensitivity() {
        if (rootView != null) {
            rootView.setDragSensitivity(calculateDragSensitivity());
        }
    }

    /**
     * Adapts the width of the bottom sheet.
     */
    private void adaptWidth() {
        adapter.setWidth(width);

        if (rootView != null) {
            rootView.setWidth(width);
            rootView.requestLayout();
        }
    }

    /**
     * Adapts the height of the grid view, which is used to show the bottom sheet's items.
     */
    private void adaptGridViewHeight() {
        if (gridView instanceof DividableGridView) {
            ((DividableGridView) gridView).adaptHeightToChildren();
        }
    }

    /**
     * Creates and returns a listener, which allows to immediately maximize the bottom sheet after
     * it has been shown.
     *
     * @return The listener, which has been created, as an instance of the type {@link
     * OnShowListener}
     */
    @TargetApi(Build.VERSION_CODES.FROYO)
    private OnShowListener createOnShowListener() {
        return new OnShowListener() {

            @Override
            public void onShow(final DialogInterface dialog) {
                if (onShowListener != null) {
                    onShowListener.onShow(dialog);
                }

                if (maximize) {
                    maximize = false;
                    rootView.maximize(new DecelerateInterpolator());
                }
            }

        };
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
                if (itemClickListener != null && !rootView.isDragging() &&
                        !rootView.isAnimationRunning()) {
                    int index = position;

                    if (adapter.containsDividers()) {
                        for (int i = position; i >= 0; i--) {
                            if (adapter.getItem(i) == null ||
                                    (adapter.getItem(i) instanceof Divider &&
                                            i % adapter.getColumnCount() > 0)) {
                                index--;
                            }
                        }
                    }

                    itemClickListener.onItemClick(parent, view, index, getId(position));
                }

                dismiss();
            }

        };
    }

    /**
     * Creates and returns a listener, which allows to observe when the items of a bottom sheet have
     * been long-clicked.
     *
     * @return The listener, which has been created, as an instance of the type {qlink
     * OnItemLongClickListener}
     */
    private OnItemLongClickListener createItemLongClickListener() {
        return new OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(final AdapterView<?> parent, final View view,
                                           final int position, final long id) {

                if (!rootView.isDragging() && !rootView.isAnimationRunning() &&
                        itemLongClickListener != null) {
                    int index = position;

                    if (adapter.containsDividers()) {
                        for (int i = position; i >= 0; i--) {
                            if (adapter.getItem(i) == null ||
                                    (adapter.getItem(i) instanceof Divider &&
                                            i % adapter.getColumnCount() > 0)) {
                                index--;
                            }
                        }
                    }

                    return itemLongClickListener
                            .onItemLongClick(parent, view, index, getId(position));
                }

                return false;
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
     * @param themeResourceId
     *         The resource id of the theme, which should be used by the bottom sheet, as an {@link
     *         Integer} value. The resource id must correspond to a valid theme
     */
    protected BottomSheet(@NonNull final Context context, @StyleRes final int themeResourceId) {
        super(context, themeResourceId);
        initialize();
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
     * Sets the listener, which should be notified, when an item of the bottom sheet has been
     * long-clicked.
     *
     * @param listener
     *         The listener, which should be set, as an instance of the type {@link
     *         OnItemLongClickListener} or null, if no listener should be notified
     */
    public final void setOnItemLongClickListener(@Nullable final OnItemLongClickListener listener) {
        this.itemLongClickListener = listener;
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
     * of the type {@link ListAdapter}
     */
    public final ListAdapter getListAdapter() {
        return adapter;
    }

    /**
     * Returns the icon of the bottom sheet.
     *
     * @return The icon of the bottom sheet, as an instance of the class {@link Drawable} or null,
     * if no icon has been set
     */
    public final Drawable getIcon() {
        return icon;
    }

    /**
     * Sets the icon of the bottom sheet.
     *
     * @param icon
     *         The icon, which should be set, as an instance of the class {@link Drawable} or null,
     *         if no icon should be shown
     */
    public final void setIcon(@Nullable final Drawable icon) {
        this.icon = icon;
        adaptIcon();
    }

    /**
     * Sets the icon of the bottom sheet.
     *
     * @param resourceId
     *         The resource id of the icon, which should be set, as an {@link Integer} value. The
     *         resource id must correspond to a valid drawable resource
     */
    public final void setIcon(@DrawableRes final int resourceId) {
        setIcon(ContextCompat.getDrawable(getContext(), resourceId));
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
     * custom color has been set
     */
    public final int getTitleColor() {
        return titleColor;
    }

    /**
     * Sets the color of the title of the bottom sheet.
     *
     * @param color
     *         The color, which should be set, as an {@link Integer} value
     */
    public final void setTitleColor(@ColorInt final int color) {
        titleColor = color;
        adaptTitleColor();
    }

    /**
     * Returns the color of the items of the bottom sheet.
     *
     * @return The color of the items of the bottom sheet as an {@link Integer} value or -1, if no
     * custom color has been set
     */
    public final int getItemColor() {
        return adapter.getItemColor();
    }

    /**
     * Sets the color of the items of the bottom sheet.
     *
     * @param color
     *         The color, which should be set, as an {@link Integer} value or -1, if no custom color
     *         should be set
     */
    public final void setItemColor(@ColorInt final int color) {
        adapter.setItemColor(color);
        adapter.notifyDataSetChanged();
    }

    /**
     * Returns the color of the dividers of the bottom sheet.
     *
     * @return The color of the dividers of the bottom sheet as an {@link Integer} value or -1, if
     * no custom color has been set
     */
    public final int getDividerColor() {
        return adapter.getDividerColor();
    }

    /**
     * Sets the color of the dividers of the bottom sheet.
     *
     * @param color
     *         The color, which should be set, as an {@link Integer} value or -1, if no custom color
     *         should be set
     */
    public final void setDividerColor(@ColorInt final int color) {
        adapter.setDividerColor(color);
        adapter.notifyDataSetChanged();
    }

    /**
     * Returns the background of the bottom sheet.
     *
     * @return The background of the bottom sheet as an instance of the class {@link Drawable} or
     * null, if no custom background has been set
     */
    public final Drawable getBackground() {
        return background;
    }

    /**
     * Sets the background of the bottom sheet.
     *
     * @param background
     *         The background, which should be set, as an instance of the class {@link Drawable} or
     *         null, if no custom background should be set
     */
    public final void setBackground(@Nullable final Drawable background) {
        this.background = background;
        adaptBackground();
    }

    /**
     * Sets the background of the bottom sheet.
     *
     * @param resourceId
     *         The resource id of the background, which should be set, as an {@link Integer} value.
     *         The resource id must correspond to a valid drawable resource
     */
    public final void setBackground(@DrawableRes final int resourceId) {
        setBackground(ContextCompat.getDrawable(getContext(), resourceId));
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
        customTitleView = view;
        customTitleViewId = -1;
        adaptTitleView();
    }

    /**
     * Sets the custom view, which should be used to show the title of the bottom sheet.
     *
     * @param resourceId
     *         The resource id of the view, which should be set, as an {@link Integer} value. The
     *         resource id must correspond to a valid layout resource
     */
    public final void setCustomTitle(@LayoutRes final int resourceId) {
        customTitleView = null;
        customTitleViewId = resourceId;
        adaptTitleView();
    }

    /**
     * Sets the custom view, which should be shown by the bottom sheet.
     *
     * @param view
     *         The view, which should be set, as an instance of the class {@link View} or null, if
     *         no custom view should be shown
     */
    public final void setView(@Nullable final View view) {
        customView = view;
        customViewId = -1;
        adaptContentView();
    }

    /**
     * Sets the custom view, which should be shown by the bottom sheet.
     *
     * @param resourceId
     *         The resource id of the view, which should be set, as an {@link Integer} value. The
     *         resource id must correspond to a valid layout resource
     */
    public final void setView(@LayoutRes final int resourceId) {
        customView = null;
        customViewId = resourceId;
        adaptContentView();
        adaptGridViewHeight();
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
        adaptDragSensitivity();
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
        adaptWidth();
    }

    /**
     * Adds a new item to the bottom sheet.
     *
     * @param id
     *         The id of the item, which should be added, as an {@link Integer} value. The id must
     *         be at least 0
     * @param title
     *         The title of the item, which should be added, as an instance of the type {@link
     *         CharSequence}. The title may neither be null, nor empty
     */
    public final void addItem(final int id, @NonNull final CharSequence title) {
        Item item = new Item(id, title);
        adapter.add(item);
        adaptGridViewHeight();
    }

    /**
     * Adds a new item to the bottom sheet.
     *
     * @param id
     *         The id of the item, which should be added, as an {@link Integer} value. The id must
     *         be at least 0
     * @param title
     *         The title of the item, which should be added, as an instance of the type {@link
     *         CharSequence}. The title may neither be null, nor empty
     * @param icon
     *         The icon of the item, which should be added, as an instance of the class {@link
     *         Drawable}, or null, if no item should be used
     */
    public final void addItem(final int id, @NonNull final CharSequence title,
                              @Nullable final Drawable icon) {
        Item item = new Item(id, title);
        item.setIcon(icon);
        adapter.add(item);
        adaptGridViewHeight();
    }

    /**
     * Adds a new item to the bottom sheet.
     *
     * @param id
     *         The id of the item, which should be added, as an {@link Integer} value. The id must
     *         be at least 0
     * @param titleId
     *         The resource id of the title of the item, which should be added, as an {@link
     *         Integer} value. The resource id must correspond to a valid string resource
     */
    public final void addItem(final int id, @StringRes final int titleId) {
        Item item = new Item(getContext(), id, titleId);
        adapter.add(item);
        adaptGridViewHeight();
    }

    /**
     * Adds a new item to the bottom sheet.
     *
     * @param id
     *         The id of the item, which should be added, as an {@link Integer} value. The id must
     *         be at least 0
     * @param titleId
     *         The resource id of the title of the item, which should be added, as an {@link
     *         Integer} value. The resource id must correspond to a valid string resource
     * @param iconId
     *         The resource id of the icon of the item, which should be added, as an {@link Integer}
     *         value. The resource id must correspond to a valid drawable resource
     */
    public final void addItem(final int id, @StringRes final int titleId,
                              @DrawableRes final int iconId) {
        Item item = new Item(getContext(), id, titleId);
        item.setIcon(getContext(), iconId);
        adapter.add(item);
        adaptGridViewHeight();
    }

    /**
     * Replaces the item at a specific index with another item.
     *
     * @param index
     *         The index of the item, which should be replaced, as an {@link Integer} value
     * @param id
     *         The id of the item, which should be added, as an {@link Integer} value. The id must
     *         be at least 0
     * @param title
     *         The title of the item, which should be added, as an instance of the type {@link
     *         CharSequence}. The title may neither be null, nor empty
     */
    public final void setItem(final int index, final int id, @NonNull final CharSequence title) {
        Item item = new Item(id, title);
        adapter.set(index, item);
        adaptGridViewHeight();
    }

    /**
     * Replaces the item at a specific index with another item.
     *
     * @param index
     *         The index of the item, which should be replaced, as an {@link Integer} value
     * @param id
     *         The id of the item, which should be added, as an {@link Integer} value. The id must
     *         be at least 0
     * @param title
     *         The title of the item, which should be added, as an instance of the type {@link
     *         CharSequence}. The title may neither be null, nor empty
     * @param icon
     *         The icon of the item, which should be added, as an instance of the class {@link
     *         Drawable}, or null, if no item should be used
     */
    public final void setItem(final int index, final int id, @NonNull final CharSequence title,
                              @Nullable final Drawable icon) {
        Item item = new Item(id, title);
        item.setIcon(icon);
        adapter.set(index, item);
        adaptGridViewHeight();
    }

    /**
     * Replaces the item at a specific index with another item.
     *
     * @param index
     *         The index of the item, which should be replaced, as an {@link Integer} value
     * @param id
     *         The id of the item, which should be added, as an {@link Integer} value. The id must
     *         be at least 0
     * @param titleId
     *         The resource id of the title of the item, which should be added, as an {@link
     *         Integer} value. The resource id must correspond to a valid string resource
     */
    public final void setItem(final int index, final int id, @StringRes final int titleId) {
        Item item = new Item(getContext(), id, titleId);
        adapter.set(index, item);
        adaptGridViewHeight();
    }

    /**
     * Replaces the item at a specific index with another item.
     *
     * @param index
     *         The index of the item, which should be replaced, as an {@link Integer} value
     * @param id
     *         The id of the item, which should be added, as an {@link Integer} value. The id must
     *         be at least 0
     * @param titleId
     *         The resource id of the title of the item, which should be added, as an {@link
     *         Integer} value. The resource id must correspond to a valid string resource
     * @param iconId
     *         The resource id of the icon of the item, which should be added, as an {@link Integer}
     *         value. The resource id must correspond to a valid drawable resource
     */
    public final void setItem(final int index, final int id, @StringRes final int titleId,
                              @DrawableRes final int iconId) {
        Item item = new Item(getContext(), id, titleId);
        item.setIcon(getContext(), iconId);
        adapter.set(index, item);
        adaptGridViewHeight();
    }

    /**
     * Adds a new divider to the bottom sheet.
     */
    public final void addDivider() {
        adapter.add(new Divider());
        adaptGridViewHeight();
    }

    /**
     * Adds a new divider to the bottom sheet.
     *
     * @param title
     *         The title of the divider, which should be added, as an instance of the type {@link
     *         CharSequence}, or null, if no title should be used
     */
    public final void addDivider(@Nullable final CharSequence title) {
        Divider divider = new Divider();
        divider.setTitle(title);
        adapter.add(divider);
        adaptGridViewHeight();
    }

    /**
     * Adds a new divider to the bottom sheet.
     *
     * @param titleId
     *         The resource id of the title of the divider, which should be added, as an {@link
     *         Integer} value. The resource id must correspond to a valid string resource
     */
    public final void addDivider(@StringRes final int titleId) {
        Divider divider = new Divider();
        divider.setTitle(getContext(), titleId);
        adapter.add(divider);
        adaptGridViewHeight();
    }

    /**
     * Replaces the item at a specific index with a divider.
     *
     * @param index
     *         The index of the item, which should be replaced, as an {@link Integer} value
     */
    public final void setDivider(final int index) {
        Divider divider = new Divider();
        adapter.set(index, divider);
        adaptGridViewHeight();
    }

    /**
     * Replaces the item at a specific index with a divider.
     *
     * @param index
     *         The index of the item, which should be replaced, as an {@link Integer} value
     * @param title
     *         The title of the divider, which should be added, as an instance of the type {@link
     *         CharSequence}, or null, if no title should be used
     */
    public final void setDivider(final int index, @Nullable final CharSequence title) {
        Divider divider = new Divider();
        divider.setTitle(title);
        adapter.set(index, divider);
        adaptGridViewHeight();
    }

    /**
     * Replaces the item at a specific index with a divider.
     *
     * @param index
     *         The index of the item, which should be replaced, as an {@link Integer} value
     * @param titleId
     *         The resource id of the title of the divider, which should be added, as an {@link
     *         Integer} value. The resource id must correspond to a valid string resource
     */
    public final void setDivider(final int index, @StringRes final int titleId) {
        Divider divider = new Divider();
        divider.setTitle(getContext(), titleId);
        adapter.set(index, divider);
        adaptGridViewHeight();
    }

    /**
     * Removes the item with at a specific index from the bottom sheet.
     *
     * @param index
     *         The index of the item, which should be removed, as an {@link Integer} value
     */
    public final void removeItem(final int index) {
        adapter.remove(index);
        adaptGridViewHeight();
    }

    /**
     * Removes all items from the bottom sheet.
     */
    public final void removeAllItems() {
        adapter.clear();
        adaptGridViewHeight();
    }

    /**
     * Returns, whether the bottom sheet contains any items, or not.
     *
     * @return True, if the bottom sheet contains any items, false otherwise
     */
    public final boolean isEmpty() {
        return adapter.isEmpty();
    }

    /**
     * Returns the number of items, which are currently contained by the bottom sheet.
     *
     * @return The number of items, which are currently contained by the bottom sheet, as an {@link
     * Integer} value or -1, if the bottom sheet does not show any items or has not been shown yet
     */
    public final int getItemCount() {
        return adapter.getItemCount();
    }

    /**
     * Returns the index of the item, which corresponds to a specific id.
     *
     * @param id
     *         The id of the item, whose index should be returned, as an {@link Integer} value. The
     *         id must be at least 0
     * @return The index of the item, which corresponds to the given id, or -1, if no item, which
     * corresponds to the given id, is contained by the bottom sheet
     */
    public final int indexOf(final int id) {
        ensureAtLeast(id, 0, "The id must be at least 0");

        for (int i = 0; i < getItemCount(); i++) {
            AbstractItem item = adapter.getItem(i);

            if (item.getId() == id) {
                return i;
            }
        }

        return -1;
    }

    /**
     * Returns the id of the item, which corresponds to a specific index.
     *
     * @param index
     *         The index of the item, whose id should be returned, as an {@link Integer} value
     * @return The id of the item, which corresponds to the given position, or
     * <code>Divider#DIVIDER_ID</code>, if the item is a divider
     */
    public final int getId(final int index) {
        return adapter.getItem(index).getId();
    }

    /**
     * Returns, whether the item at a specific index is enabled, or not.
     *
     * @param index
     *         The index of the item, which should be checked, as an {@link Integer} value
     * @return True, if the item is enabled, false otherwise
     */
    public final boolean isItemEnabled(final int index) {
        return adapter.isItemEnabled(index);
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
        adapter.setItemEnabled(index, enabled);
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
        removeAllItems();
        PackageManager packageManager = activity.getPackageManager();
        List<ResolveInfo> resolveInfos = packageManager.queryIntentActivities(intent, 0);

        for (int i = 0; i < resolveInfos.size(); i++) {
            ResolveInfo resolveInfo = resolveInfos.get(i);
            addItem(i, resolveInfo.loadLabel(packageManager), resolveInfo.loadIcon(packageManager));
        }

        setOnItemClickListener(
                createIntentClickListener(activity, (Intent) intent.clone(), resolveInfos));
    }

    /**
     * Invalidates the bottom sheet. This method must be called in order to update the appearance of
     * the bottom sheet, when its items have been changed.
     */
    public final void invalidate() {
        adapter.notifyDataSetChanged();
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
        adapter.notifyOnChange(invalidateOnChange);
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
     * Maximizes the bottom sheet.
     */
    @TargetApi(Build.VERSION_CODES.FROYO)
    public final void maximize() {
        if (!isMaximized()) {
            if (!isShowing()) {
                maximize = true;
                show();
            } else {
                rootView.maximize(new AccelerateDecelerateInterpolator());
            }
        }
    }

    /**
     * Returns the style, which is used to display the bottom sheet's items.
     *
     * @return style The style, which is used to display the bottom sheet's items, as a value of the
     * enum {@link Style}
     */
    public final Style getStyle() {
        return adapter.getStyle();
    }

    /**
     * Sets the style, which should be used to display the bottom sheet's items.
     *
     * @param style
     *         The style, which should be set, as a value of the enum {@link Style}. The style may
     *         either be <code>LIST</code>, <code>LIST_COLUMNS</code> or <code>GRID</code>
     */
    public final void setStyle(@NonNull final Style style) {
        ensureNotNull(style, "The style may not be null");
        adapter.setStyle(style);
        adaptRootView();
        adaptTitleView();
        adaptContentView();
        adaptGridViewHeight();
    }

    /**
     * Returns the title of the bottom sheet.
     *
     * @return The title of the bottom sheet as an instance of the type {@link CharSequence} or
     * null, if no title has been set
     */
    public final CharSequence getTitle() {
        return title;
    }

    @Override
    public final void setTitle(@Nullable final CharSequence title) {
        super.setTitle(title);
        this.title = title;
        adaptTitle();
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

    @TargetApi(Build.VERSION_CODES.FROYO)
    @Override
    public final void setOnShowListener(@Nullable final OnShowListener listener) {
        this.onShowListener = listener;
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
    public final void onStart() {
        super.onStart();
        getWindow().setAttributes(createLayoutParams());
        getWindow().getDecorView().setOnTouchListener(createCancelOnTouchListener());
        inflateRootView();
        adaptRootView();
        inflateTitleView();
        inflateContentView();
        adaptTitle();
        adaptTitleColor();
        adaptIcon();
        adaptBackground();
        adaptDragSensitivity();
        adaptWidth();
        adaptGridViewHeight();
    }

    @Override
    public final void onStop() {
        super.onStop();
        rootView = null;
        titleContainer = null;
        titleTextView = null;
        contentContainer = null;
        gridView = null;
    }

}
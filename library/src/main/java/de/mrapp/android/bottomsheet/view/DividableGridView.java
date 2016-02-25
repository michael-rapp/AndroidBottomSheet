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
package de.mrapp.android.bottomsheet.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.GridView;

import de.mrapp.android.bottomsheet.BottomSheet;
import de.mrapp.android.bottomsheet.R;
import de.mrapp.android.bottomsheet.adapter.DividableGridAdapter;
import de.mrapp.android.bottomsheet.model.AbstractItem;
import de.mrapp.android.bottomsheet.model.Divider;

/**
 * A grid view, which allows to display the items of a {@link BottomSheet}. Its height can be
 * adapted to the heights of its children, even if they have different heights.
 *
 * @author Michael Rapp
 * @since 1.0.0
 */
public class DividableGridView extends GridView {

    /**
     * Creates a new grid view, which allows to display the items of a {@link BottomSheet}.
     *
     * @param context
     *         The context, which should be used by the view, as an instance of the class {@link
     *         Context}. The context may not be null
     */
    public DividableGridView(@NonNull final Context context) {
        super(context);
    }

    /**
     * Creates a new grid view, which allows to display the items of a {@link BottomSheet}.
     *
     * @param context
     *         The context, which should be used by the view, as an instance of the class {@link
     *         Context}. The context may not be null
     * @param attributeSet
     *         The attribute set, the view's attributes should be obtained from, as an instance of
     *         the type {@link AttributeSet} or null, if no attributes should be obtained
     */
    public DividableGridView(@NonNull final Context context,
                             @Nullable final AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    /**
     * Creates a new grid view, which allows to display the items of a {@link BottomSheet}.
     *
     * @param context
     *         The context, which should be used by the view, as an instance of the class {@link
     *         Context}. The context may not be null
     * @param attributeSet
     *         The attribute set, the view's attributes should be obtained from, as an instance of
     *         the type {@link AttributeSet} or null, if no attributes should be obtained
     * @param defaultStyle
     *         The default style to apply to this view. If 0, no style will be applied (beyond what
     *         is included in the theme). This may either be an attribute resource, whose value will
     *         be retrieved from the current theme, or an explicit style resource
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public DividableGridView(@NonNull final Context context,
                             @Nullable final AttributeSet attributeSet,
                             @StyleRes final int defaultStyle) {
        super(context, attributeSet, defaultStyle);
    }

    /**
     * Creates a new grid view, which allows to display the items of a {@link BottomSheet}.
     *
     * @param context
     *         The context, which should be used by the view, as an instance of the class {@link
     *         Context}. The context may not be null
     * @param attributeSet
     *         The attribute set, the view's attributes should be obtained from, as an instance of
     *         the type {@link AttributeSet} or null, if no attributes should be obtained
     * @param defaultStyle
     *         The default style to apply to this view. If 0, no style will be applied (beyond what
     *         is included in the theme). This may either be an attribute resource, whose value will
     *         be retrieved from the current theme, or an explicit style resource
     * @param defaultStyleResource
     *         A resource identifier of a style resource that supplies default values for the view,
     *         used only if the default style is 0 or can not be found in the theme. Can be 0 to not
     *         look for defaults
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public DividableGridView(@NonNull final Context context,
                             @Nullable final AttributeSet attributeSet,
                             @StyleRes final int defaultStyle,
                             @StyleRes final int defaultStyleResource) {
        super(context, attributeSet, defaultStyle, defaultStyleResource);
    }

    /**
     * Adapts the height of the grid view to the height of its children.
     */
    public void adaptHeightToChildren() {
        DividableGridAdapter adapter = (DividableGridAdapter) getAdapter();

        if (adapter != null) {
            int height = getPaddingTop() + getPaddingBottom();

            for (int i = 0; i < adapter.getCount(); i += adapter.getColumnCount()) {
                AbstractItem item = adapter.getItem(i);

                if (item instanceof Divider) {
                    height += getResources().getDimensionPixelSize(
                            TextUtils.isEmpty(item.getTitle()) ?
                                    R.dimen.bottom_sheet_divider_height :
                                    R.dimen.bottom_sheet_divider_title_height);
                } else {
                    height += getResources().getDimensionPixelSize(
                            adapter.getStyle() == BottomSheet.Style.GRID ?
                                    R.dimen.bottom_sheet_grid_item_size :
                                    R.dimen.bottom_sheet_list_item_height);
                }
            }

            ViewGroup.LayoutParams params = getLayoutParams();
            params.height = height;
            setLayoutParams(params);
            requestLayout();
        }
    }

}
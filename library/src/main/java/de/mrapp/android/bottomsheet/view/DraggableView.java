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
package de.mrapp.android.bottomsheet.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

import de.mrapp.android.bottomsheet.BottomSheet;
import de.mrapp.android.util.gesture.DragHelper;

/**
 * The root view of a {@link BottomSheet}, which can be dragged by the user.
 *
 * @author Michael Rapp
 * @since 1.0.0
 */
public class DraggableView extends LinearLayout {

    /**
     * The ratio between the view's height and the display's height, which is used to calculate the
     * initial height.
     */
    private static final float INITIAL_HEIGHT_RATIO = 9f / 16f;

    /**
     * An instance of the class {@link DragHelper}, which is used to recognize drag gestures.
     */
    private DragHelper dragHelper;

    /**
     * The view's maximum initial height in pixels.
     */
    private int initialHeight;

    /**
     * Initializes the view.
     */
    private void initialize() {
        dragHelper = new DragHelper(0);
        initialHeight =
                Math.round(getResources().getDisplayMetrics().heightPixels * INITIAL_HEIGHT_RATIO);
    }

    /**
     * Handles when a drag gesture is performed by the user.
     *
     * @param dragPosition
     *         The current vertical position of the drag gesture as a {@link Float} value
     * @return True, if the view has been moved by the drag gesture, false otherwise
     */
    private boolean handleDrag(final float dragPosition) {
        if (getAnimation() == null) {
            dragHelper.update(dragPosition);

            if (dragHelper.hasThresholdBeenReached()) {
                int left = getLeft();
                int right = getRight();
                int bottom = getBottom();
                int top = bottom - initialHeight + dragHelper.getDistance();
                top = Math.max(top, 0);
                top = Math.min(top, bottom);
                layout(left, top, right, bottom);
            }

            return true;
        }

        return false;
    }

    /**
     * Handles when a drag gesture has been ended by the user.
     */
    private void handleRelease() {
        dragHelper.reset();
    }

    /**
     * Handles when the view is clicked by the user.
     *
     * @param clickPosition
     *         The vertical position of the click as a {@link Float} value
     */
    private void handleClick(final float clickPosition) {
        dragHelper.reset();
    }

    /**
     * Creates a new root view of a {@link BottomSheet}, which can be dragged by the user.
     *
     * @param context
     *         The context, which should be used by the view, as an instance of the class {@link
     *         Context}. The context may not be null
     */
    public DraggableView(@NonNull final Context context) {
        super(context);
        initialize();
    }

    /**
     * Creates a new root view of a {@link BottomSheet}, which can be dragged by the user.
     *
     * @param context
     *         The context, which should be used by the view, as an instance of the class {@link
     *         Context}. The context may not be null
     * @param attributeSet
     *         The attribute set, the view's attributes should be obtained from, as an instance of
     *         the type {@link AttributeSet} or null, if no attributes should be obtained
     */
    public DraggableView(@NonNull final Context context,
                         @Nullable final AttributeSet attributeSet) {
        super(context, attributeSet);
        initialize();
    }

    /**
     * Creates a new root view of a {@link BottomSheet}, which can be dragged by the user.
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
    public DraggableView(@NonNull final Context context, @Nullable final AttributeSet attributeSet,
                         final int defaultStyle) {
        super(context, attributeSet, defaultStyle);
        initialize();
    }

    /**
     * Creates a new root view of a {@link BottomSheet}, which can be dragged by the user.
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
    public DraggableView(@NonNull final Context context, @Nullable final AttributeSet attributeSet,
                         final int defaultStyle, final int defaultStyleResource) {
        super(context, attributeSet, defaultStyle, defaultStyleResource);
        initialize();
    }

    /**
     * Sets the distance in pixels, a drag gesture must last until it is recognized.
     *
     * @param dragSensitivity
     *         The distance, which should be set, in pixels as an {@link Integer} value. The value
     *         must be at least 0
     */
    public final void setDragSensitivity(final int dragSensitivity) {
        this.dragHelper = new DragHelper(dragSensitivity);
    }

    @Override
    public final boolean dispatchTouchEvent(final MotionEvent event) {
        boolean handled = false;

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                handled = handleDrag(event.getRawY());
                break;
            case MotionEvent.ACTION_UP:
                if (dragHelper.hasThresholdBeenReached()) {
                    handleRelease();
                } else {
                    handleClick(event.getRawY());
                }

                break;
            default:
                break;
        }

        return handled || super.dispatchTouchEvent(event);
    }

    @Override
    public final boolean onTouchEvent(final MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                return true;
            case MotionEvent.ACTION_MOVE:
                handleDrag(event.getRawY());
                return true;
            case MotionEvent.ACTION_UP:

                if (dragHelper.hasThresholdBeenReached()) {
                    handleRelease();
                } else {
                    handleClick(event.getRawY());
                }

                performClick();
                return true;
            default:
                break;
        }

        return super.onTouchEvent(event);
    }

    @Override
    public final boolean performClick() {
        super.performClick();
        return true;
    }

    @Override
    protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
        int measuredHeight = heightMeasureSpec;

        if (initialHeight < MeasureSpec.getSize(measuredHeight)) {
            int measureMode = MeasureSpec.getMode(measuredHeight);
            measuredHeight = MeasureSpec.makeMeasureSpec(initialHeight, measureMode);
        }

        super.onMeasure(widthMeasureSpec, measuredHeight);
    }

}
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
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import de.mrapp.android.bottomsheet.BottomSheet;
import de.mrapp.android.bottomsheet.R;
import de.mrapp.android.bottomsheet.animation.DraggableViewAnimation;
import de.mrapp.android.util.gesture.DragHelper;

/**
 * The root view of a {@link BottomSheet}, which can be dragged by the user.
 *
 * @author Michael Rapp
 * @since 1.0.0
 */
public class DraggableView extends LinearLayout implements ViewTreeObserver.OnGlobalLayoutListener {

    /**
     * Defines the interface, a class, which should be notified about the view's state, must
     * implement.
     */
    public interface Callback {

        /**
         * The method, which is invoked, when the view has been maximized.
         */
        void onMaximized();

        /**
         * The method, which is invoked, when the view has been hidden.
         */
        void onHidden();

    }

    /**
     * The ratio between the view's height and the display's height, which is used to calculate the
     * initial height.
     */
    private static final float INITIAL_HEIGHT_RATIO = 9f / 16f;

    /**
     * The view group, which contains the view's content.
     */
    private ViewGroup contentContainer;

    /**
     * The callback, which should be notified about the view's state.
     */
    private Callback callback;

    /**
     * An instance of the class {@link DragHelper}, which is used to recognize drag gestures.
     */
    private DragHelper dragHelper;

    /**
     * True, if the view is currently maximized, false otherwise.
     */
    private boolean maximized;

    /**
     * The view's initial top margin in pixels.
     */
    private int initialMargin = -1;

    /**
     * The speed of the animation, which is used to show or hide the sidebar, in pixels per
     * millisecond.
     */
    private float animationSpeed = -1;

    /**
     * Initializes the view.
     */
    private void initialize() {
        getViewTreeObserver().addOnGlobalLayoutListener(this);
        dragHelper = new DragHelper(0);
        maximized = false;
    }

    /**
     * Returns, whether a touch event at a specific position targets a view, which can be scrolled
     * up.
     *
     * @param x
     *         The horizontal position of the touch event in pixels as a {@link Float} value
     * @param y
     *         The vertical position of the touch event in pixels as a {@link Float} value
     * @return True, if the touch event targets a view, which can be scrolled up, false otherwise
     */
    private boolean isScrollUpEvent(final float x, final float y) {
        return isScrollUpEvent(x, y, contentContainer);
    }

    /**
     * Returns, whether a touch event at a specific position targets a view, which can be scrolled
     * up.
     *
     * @param x
     *         The horizontal position of the touch event in pixels as a {@link Float} value
     * @param y
     *         The vertical position of the touch event in pixels as a {@link Float} value
     * @param viewGroup
     *         The view group, which should be used to search for scrollable child views, as an
     *         instance of the class {@link ViewGroup}. The view group may not be null
     * @return True, if the touch event targets a view, which can be scrolled up, false otherwise
     */
    private boolean isScrollUpEvent(final float x, final float y,
                                    @NonNull final ViewGroup viewGroup) {
        int location[] = new int[2];
        viewGroup.getLocationOnScreen(location);

        if (x >= location[0] && x <= viewGroup.getWidth() && y >= location[1] &&
                y <= viewGroup.getHeight()) {
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                View view = viewGroup.getChildAt(i);

                if (ViewCompat.canScrollVertically(view, -1)) {
                    return true;
                } else if (view instanceof ViewGroup) {
                    return isScrollUpEvent(x, y, (ViewGroup) view);
                }
            }
        }

        return false;
    }

    /**
     * Handles when a drag gesture is performed by the user.
     *
     * @return True, if the view has been moved by the drag gesture, false otherwise
     */
    private boolean handleDrag() {
        if (!isAnimationRunning()) {
            if (dragHelper.hasThresholdBeenReached()) {
                int margin = Math.round(isMaximized() ? dragHelper.getDistance() :
                        initialMargin + dragHelper.getDistance());
                margin = Math.max(margin, 0);
                adjustMargin(margin);
            }

            return true;
        }

        return false;
    }

    /**
     * Handles when a drag gesture has been ended by the user.
     */
    private void handleRelease() {
        float speed = Math.max(dragHelper.getDragSpeed(), animationSpeed);

        if (getTop() > initialMargin || dragHelper.getDragSpeed() > animationSpeed) {
            animateHideView(getHeight(), speed, new DecelerateInterpolator());
        } else {
            animateShowView(-getTop(), speed, new DecelerateInterpolator());
        }
    }

    /**
     * Adjusts the top margin of the view.
     *
     * @param margin
     *         The top margin, which should be set, in pixels as an {@link Integer} value
     */
    private void adjustMargin(final int margin) {
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) getLayoutParams();
        layoutParams.topMargin = margin;
        setLayoutParams(layoutParams);
    }

    /**
     * Animates the view to become show.
     *
     * @param diff
     *         The distance the view has to be vertically resized by, as a {@link Float} value
     * @param animationSpeed
     *         The speed of the animation in pixels per milliseconds as a {@link Float} value
     * @param interpolator
     *         The interpolator, which should be used by the animation, as an instance of the type
     *         {@link Interpolator}. The interpolator may not be null
     */
    private void animateShowView(final float diff, final float animationSpeed,
                                 @NonNull final Interpolator interpolator) {
        animateView(diff, animationSpeed, createAnimationListener(true), interpolator);
    }

    /**
     * Animates the view to become hidden.
     *
     * @param diff
     *         The distance the view has to be vertically resized by, as a {@link Float} value
     * @param animationSpeed
     *         The speed of the animation in pixels per milliseconds as a {@link Float} value
     * @param interpolator
     *         The interpolator, which should be used by the animation, as an instance of the type
     *         {@link Interpolator}. The interpolator may not be null
     */
    private void animateHideView(final float diff, final float animationSpeed,
                                 @NonNull final Interpolator interpolator) {
        animateView(diff, animationSpeed, createAnimationListener(false), interpolator);
    }

    /**
     * Animates the view to become shown or hidden.
     *
     * @param diff
     *         The distance the view has to be vertically resized by, as a {@link Float} value
     * @param animationSpeed
     *         The speed of the animation in pixels per millisecond as a {@link Float} value
     * @param animationListener
     *         The listener, which should be notified about the animation's progress, as an instance
     *         of the type {@link AnimationListener}. The listener may not be null
     * @param interpolator
     *         The interpolator, which should be used by the animation, as an instance of the type
     *         {@link Interpolator}. The interpolator may not be null
     */
    private void animateView(final float diff, final float animationSpeed,
                             @NonNull final AnimationListener animationListener,
                             @NonNull final Interpolator interpolator) {
        if (!isDragging() && !isAnimationRunning()) {
            long duration = calculateAnimationDuration(diff, animationSpeed);
            Animation animation =
                    new DraggableViewAnimation(this, diff, duration, animationListener);
            animation.setInterpolator(interpolator);
            startAnimation(animation);
        }
    }

    /**
     * Calculates the duration of the animation, which is used to hide or show the view, depending
     * on a specific distance and speed.
     *
     * @param diff
     *         The distance, the view has to be vertically resized by, as a {@link Float} value
     * @param animationSpeed
     *         The speed of the animation in pixels per millisecond as a {@link Float} value
     * @return The duration of the animation in milliseconds as an {@link Integer} value
     */
    private int calculateAnimationDuration(final float diff, final float animationSpeed) {
        return Math.round(Math.abs(diff) / animationSpeed);
    }

    /**
     * Creates and returns a listener, which allows to handle the end of an animation, which has
     * been used to show or hide the view.
     *
     * @param show
     *         True, if the view should be shown at the end of the animation, false otherwise
     * @return The listener, which has been created, as an instance of the type {@link
     * AnimationListener}
     */
    private AnimationListener createAnimationListener(final boolean show) {
        return new AnimationListener() {

            @Override
            public void onAnimationStart(final Animation animation) {

            }

            @Override
            public void onAnimationEnd(final Animation animation) {
                clearAnimation();
                maximized = show;

                if (maximized) {
                    notifyOnMaximized();
                } else {
                    notifyOnHidden();
                }
            }

            @Override
            public void onAnimationRepeat(final Animation animation) {

            }

        };
    }

    /**
     * Notifies the callback, which should be notified about the view's state, that the view has
     * been maximized.
     */
    private void notifyOnMaximized() {
        if (callback != null) {
            callback.onMaximized();
        }
    }

    /**
     * Notifies the callback, which should be notified about the view's state, that the view has
     * been hidden.
     */
    private void notifyOnHidden() {
        if (callback != null) {
            callback.onHidden();
        }
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
     * Sets the callback, which should be notified about the view's state.
     *
     * @param callback
     *         The callback, which should be set, as an instance of the type {@link Callback}, or
     *         null, if no callback should be notified
     */
    public final void setCallback(@Nullable final Callback callback) {
        this.callback = callback;
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

    /**
     * Returns, whether a drag gesture, which moves the view, is currently performed, or not.
     *
     * @return True, if a drag gesture, which moves the view, is currently performed, false
     * otherwise
     */
    public final boolean isDragging() {
        return !dragHelper.isReseted() && dragHelper.hasThresholdBeenReached();
    }

    /**
     * Returns, whether an animation, which moves the view, is currently running, or not.
     *
     * @return True, if an animation, which moves the view, is currently running, false otherwise
     */
    public final boolean isAnimationRunning() {
        return getAnimation() != null;
    }

    /**
     * Returns, whether the view is currently maximized, or not.
     *
     * @return True, if the view is currently maximized, false otherwise
     */
    public final boolean isMaximized() {
        return maximized;
    }

    @Override
    public final boolean dispatchTouchEvent(final MotionEvent event) {
        boolean handled = false;

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                dragHelper.update(event.getRawY());

                if (isMaximized() && (event.getRawY() - dragHelper.getStartPosition() < 0 ||
                        isScrollUpEvent(event.getRawX(), event.getRawY()))) {
                    dragHelper.reset();
                    break;
                }

                handled = handleDrag();
                break;
            case MotionEvent.ACTION_UP:
                dragHelper.reset();

                if (dragHelper.hasThresholdBeenReached()) {
                    handleRelease();
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
                dragHelper.update(event.getRawY());
                handleDrag();
                return true;
            case MotionEvent.ACTION_UP:
                dragHelper.reset();

                if (dragHelper.hasThresholdBeenReached()) {
                    handleRelease();
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
    protected final void onAttachedToWindow() {
        super.onAttachedToWindow();
        contentContainer = (ViewGroup) findViewById(R.id.content_container);
    }

    @Override
    public final void onGlobalLayout() {
        if (initialMargin == -1 || animationSpeed == -1) {
            int displayHeight = getResources().getDisplayMetrics().heightPixels;
            float initialHeight = displayHeight * INITIAL_HEIGHT_RATIO;
            initialMargin = Math.round(displayHeight - initialHeight);
            int animationDuration = getResources().getInteger(R.integer.animation_duration);
            animationSpeed = initialHeight / (float) animationDuration;
            adjustMargin(initialMargin);
        }
    }

}
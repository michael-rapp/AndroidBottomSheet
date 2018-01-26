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
package de.mrapp.android.bottomsheet.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import de.mrapp.android.bottomsheet.BottomSheet;
import de.mrapp.android.bottomsheet.R;
import de.mrapp.android.bottomsheet.animation.DraggableViewAnimation;
import de.mrapp.android.util.DisplayUtil.DeviceType;
import de.mrapp.android.util.gesture.DragHelper;

import static de.mrapp.android.util.DisplayUtil.getDeviceType;

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
         *
         * @param canceled
         *         True, if the view has been canceled, false otherwise
         */
        void onHidden(boolean canceled);

    }

    /**
     * The ratio between the view's height and the display's height, which is used to calculate the
     * initial height.
     */
    private static final float INITIAL_HEIGHT_RATIO = 9f / 16f;

    /**
     * The view group, which contains the view's title.
     */
    private ViewGroup titleContainer;

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
     * The view's minimum top margin in pixels.
     */
    private int minMargin = -1;

    /**
     * The height of the view's parent in pixels.
     */
    private int parentHeight = -1;

    /**
     * The speed of the animation, which is used to show or hide the sidebar, in pixels per
     * millisecond.
     */
    private float animationSpeed;

    /**
     * The width of the view in pixels.
     */
    private int width;

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

        if (x >= location[0] && x <= location[0] + viewGroup.getWidth() && y >= location[1] &&
                y <= location[1] + viewGroup.getHeight()) {
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                View view = viewGroup.getChildAt(i);

                if (view.canScrollVertically(-1)) {
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
                int margin = Math.round(isMaximized() ? dragHelper.getDragDistance() :
                        initialMargin + dragHelper.getDragDistance());
                margin = Math.max(Math.max(margin, minMargin), 0);
                setTopMargin(margin);
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

        if (getTopMargin() > initialMargin ||
                (dragHelper.getDragSpeed() > animationSpeed && dragHelper.getDragDistance() > 0) ||
                (getDeviceType(getContext()) == DeviceType.TABLET && isMaximized() &&
                        getTopMargin() > minMargin)) {
            animateHideView(parentHeight - getTopMargin(), speed, new DecelerateInterpolator(),
                    true);
        } else {
            animateShowView(-(getTopMargin() - minMargin), speed, new DecelerateInterpolator());
        }
    }

    /**
     * Returns the top margin of the view.
     *
     * @return The top margin of the view in pixels as an {@link Integer} value
     */
    public final int getTopMargin() {
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) getLayoutParams();
        return layoutParams.topMargin;
    }

    /**
     * Set the top margin of the view.
     *
     * @param margin
     *         The top margin, which should be set, in pixels as an {@link Integer} value
     */
    private void setTopMargin(final int margin) {
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) getLayoutParams();
        layoutParams.topMargin = margin;
        setLayoutParams(layoutParams);
    }

    /**
     * Animates the view to become show.
     *
     * @param diff
     *         The distance the view has to be vertically moved by, as an {@link Integer} value
     * @param animationSpeed
     *         The speed of the animation in pixels per milliseconds as a {@link Float} value
     * @param interpolator
     *         The interpolator, which should be used by the animation, as an instance of the type
     *         {@link Interpolator}. The interpolator may not be null
     */
    private void animateShowView(final int diff, final float animationSpeed,
                                 @NonNull final Interpolator interpolator) {
        animateView(diff, animationSpeed, createAnimationListener(true, false), interpolator);
    }

    /**
     * Animates the view to become hidden.
     *
     * @param diff
     *         The distance the view has to be vertically moved by, as an {@link Integer} value
     * @param animationSpeed
     *         The speed of the animation in pixels per milliseconds as a {@link Float} value
     * @param interpolator
     *         The interpolator, which should be used by the animation, as an instance of the type
     *         {@link Interpolator}. The interpolator may not be null
     * @param cancel
     *         True, if the view should be canceled, false otherwise
     */
    private void animateHideView(final int diff, final float animationSpeed,
                                 @NonNull final Interpolator interpolator, final boolean cancel) {
        animateView(diff, animationSpeed, createAnimationListener(false, cancel), interpolator);
    }

    /**
     * Animates the view to become shown or hidden.
     *
     * @param diff
     *         The distance the view has to be vertically moved by, as an {@link Integer} value
     * @param animationSpeed
     *         The speed of the animation in pixels per millisecond as a {@link Float} value
     * @param animationListener
     *         The listener, which should be notified about the animation's progress, as an instance
     *         of the type {@link AnimationListener}. The listener may not be null
     * @param interpolator
     *         The interpolator, which should be used by the animation, as an instance of the type
     *         {@link Interpolator}. The interpolator may not be null
     */
    private void animateView(final int diff, final float animationSpeed,
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
     *         The distance, the view has to be vertically moved by, as an {@link Integer} value
     * @param animationSpeed
     *         The speed of the animation in pixels per millisecond as a {@link Float} value
     * @return The duration of the animation in milliseconds as an {@link Integer} value
     */
    private int calculateAnimationDuration(final int diff, final float animationSpeed) {
        return Math.round(Math.abs(diff) / animationSpeed);
    }

    /**
     * Creates and returns a listener, which allows to handle the end of an animation, which has
     * been used to show or hide the view.
     *
     * @param show
     *         True, if the view should be shown at the end of the animation, false otherwise
     * @param cancel
     *         True, if the view should be canceled, false otherwise
     * @return The listener, which has been created, as an instance of the type {@link
     * AnimationListener}
     */
    private AnimationListener createAnimationListener(final boolean show, final boolean cancel) {
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
                    notifyOnHidden(cancel);
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
     *
     * @param canceled
     *         True, if the view has been canceled, false otherwise
     */
    private void notifyOnHidden(final boolean canceled) {
        if (callback != null) {
            callback.onHidden(canceled);
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
                         @StyleRes final int defaultStyle) {
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
                         @StyleRes final int defaultStyle,
                         @StyleRes final int defaultStyleResource) {
        super(context, attributeSet, defaultStyle, defaultStyleResource);
        initialize();
    }

    /**
     * Hides the view in an animated manner.
     *
     * @param cancel
     *         True, if the view should be canceled, false otherwise
     */
    public final void hideView(final boolean cancel) {
        animateHideView(parentHeight - getTopMargin(), animationSpeed,
                new AccelerateDecelerateInterpolator(), cancel);
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
     * Sets the width of the view. The width is only used on tablet devices or in landscape mode.
     *
     * @param width
     *         The width, which should be set, in pixels as an {@link Integer} value. The width must
     *         be at least 1
     */
    public final void setWidth(final int width) {
        this.width = width;
    }

    /**
     * Returns, whether a drag gesture, which moves the view, is currently performed, or not.
     *
     * @return True, if a drag gesture, which moves the view, is currently performed, false
     * otherwise
     */
    public final boolean isDragging() {
        return !dragHelper.isReset() && dragHelper.hasThresholdBeenReached();
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

    /**
     * Maximizes the view.
     */
    public final void maximize(final Interpolator interpolator) {
        if (!isMaximized()) {
            animateShowView(-(getTopMargin() - minMargin), animationSpeed, interpolator);
        }
    }

    @Override
    public final boolean dispatchTouchEvent(final MotionEvent event) {
        boolean handled = false;

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                dragHelper.update(event.getRawY());

                if (isMaximized() && (event.getRawY() - dragHelper.getDragStartPosition() < 0 ||
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
    public final void onGlobalLayout() {
        if (parentHeight == -1) {
            parentHeight = ((View) getParent()).getHeight();
            float initialHeight = parentHeight * INITIAL_HEIGHT_RATIO;
            int titleContainerHeight =
                    titleContainer.getVisibility() == View.VISIBLE ? titleContainer.getHeight() : 0;
            int contentContainerHeight = contentContainer.getVisibility() == View.VISIBLE ?
                    contentContainer.getHeight() : 0;
            int padding = getPaddingTop() + getPaddingBottom();
            minMargin = parentHeight - titleContainerHeight - contentContainerHeight - padding;
            initialMargin = Math.max(Math.round(parentHeight - initialHeight), minMargin);
            int animationDuration = getResources().getInteger(R.integer.animation_duration);
            animationSpeed = initialHeight / (float) animationDuration;
            setTopMargin(initialMargin);
        }
    }

    @Override
    protected final void onAttachedToWindow() {
        super.onAttachedToWindow();
        titleContainer = findViewById(R.id.title_container);
        contentContainer = findViewById(R.id.content_container);
    }

    @Override
    protected final void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
        if (getDeviceType(getContext()) == DeviceType.TABLET ||
                getResources().getConfiguration().orientation ==
                        Configuration.ORIENTATION_LANDSCAPE) {
            int measureMode = MeasureSpec.getMode(widthMeasureSpec);
            super.onMeasure(MeasureSpec.makeMeasureSpec(width, measureMode), heightMeasureSpec);
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

}
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
package de.mrapp.android.bottomsheet.animation;

import android.support.annotation.NonNull;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.FrameLayout;

import de.mrapp.android.bottomsheet.view.DraggableView;

import static de.mrapp.android.util.Condition.ensureNotNull;

/**
 * An animation, which allows to show or hide a {@link DraggableView}.
 *
 * @author Michael Rapp
 * @since 1.0.0
 */
public class DraggableViewAnimation extends Animation {

    /**
     * The view, which is animated.
     */
    private final DraggableView view;

    /**
     * The initial position of the animated view's top edge.
     */
    private final int startTop;

    /**
     * The distance, the view should be vertically resized by in pixels.
     */
    private final float diff;

    /**
     * Creates a new animation, which allows to show or hide a {@link DraggableView}.
     *
     * @param view
     *         The view, which should be animated, as an instance of the class {@link
     *         DraggableView}. The view may not be null
     * @param diff
     *         The distance, the view should be vertically resized by, as a {@link Float} value. If
     *         the value is negative, the view's height will be increased, if it is positive, the
     *         view's height will be increased
     * @param duration
     *         The duration of the animation in milliseconds, as a {@link Long} value. The duration
     *         may not be less than 0
     * @param listener
     *         The listener, which should be notified about the animation's progress, as an instance
     *         of the type {@link AnimationListener}. The listener may not be null
     */
    public DraggableViewAnimation(@NonNull final DraggableView view, final float diff,
                                  final long duration, @NonNull final AnimationListener listener) {
        ensureNotNull(view, "The view may not be null");
        ensureNotNull(listener, "The animation listener may not be null");
        this.view = view;
        this.startTop = view.getTop();
        this.diff = diff;
        setDuration(duration);
        setAnimationListener(listener);
    }

    @Override
    protected final void applyTransformation(final float interpolatedTime,
                                             final Transformation transformation) {
        super.applyTransformation(interpolatedTime, transformation);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) view.getLayoutParams();
        layoutParams.topMargin = Math.round(startTop + interpolatedTime * diff);
        view.setLayoutParams(layoutParams);
    }

}
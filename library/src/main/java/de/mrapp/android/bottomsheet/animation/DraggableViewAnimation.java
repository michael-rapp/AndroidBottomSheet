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
package de.mrapp.android.bottomsheet.animation;

import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import de.mrapp.android.bottomsheet.view.DraggableView;
import de.mrapp.util.Condition;

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
     * The initial top margin of the animated view.
     */
    private final int initialMargin;

    /**
     * The distance, the view should be vertically moved by in pixels.
     */
    private final int diff;

    /**
     * Creates a new animation, which allows to show or hide a {@link DraggableView}.
     *
     * @param view
     *         The view, which should be animated, as an instance of the class {@link
     *         DraggableView}. The view may not be null
     * @param diff
     *         The distance, the view should be vertically moved by, as an {@link Integer} value. If
     *         the value is negative, the view's height will be increased, if it is positive, the
     *         view's height will be increased
     * @param duration
     *         The duration of the animation in milliseconds, as a {@link Long} value. The duration
     *         may not be less than 0
     * @param listener
     *         The listener, which should be notified about the animation's progress, as an instance
     *         of the type {@link AnimationListener}. The listener may not be null
     */
    public DraggableViewAnimation(@NonNull final DraggableView view, final int diff,
                                  final long duration, @NonNull final AnimationListener listener) {
        Condition.INSTANCE.ensureNotNull(view, "The view may not be null");
        Condition.INSTANCE.ensureNotNull(listener, "The animation listener may not be null");
        this.view = view;
        this.initialMargin = ((FrameLayout.LayoutParams) view.getLayoutParams()).topMargin;
        this.diff = diff;
        setDuration(duration);
        setAnimationListener(listener);
    }

    @Override
    protected final void applyTransformation(final float interpolatedTime,
                                             final Transformation transformation) {
        super.applyTransformation(interpolatedTime, transformation);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) view.getLayoutParams();
        layoutParams.topMargin = Math.round(initialMargin + interpolatedTime * diff);
        view.setLayoutParams(layoutParams);
    }

}
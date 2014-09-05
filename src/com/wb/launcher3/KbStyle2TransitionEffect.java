/*
 * Copyright (C) 2013 wuqizhi@tydtech.com
 *
 * Thebald_wu on 2013/01
 * 
 * The transition effect of launcher pages scrolling
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.wb.launcher3;

import com.wb.launcher3.Workspace.ZInterpolator;

import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;

public class KbStyle2TransitionEffect {
    // the transition effect of workspace pages scrolling
    public static final int WKS_TRANSITION_RAND = 0;
    public static final int WKS_TRANSITION_TRANSLATE = 1;
    public static final int WKS_TRANSITION_WAVE = 2;
    public static final int WKS_TRANSITION_FADE = 3;
    public static final int WKS_TRANSITION_FLIP = 4;
    public static final int WKS_TRANSITION_WINDMILL = 5;
    public static final int WKS_TRANSITION_CUBE = 6;
    public static final int WKS_TRANSITION_PHOTOWALL = 7;
    public static final int WKS_TRANSITION_PUSH = 8;
    public static final int WKS_TRANSITION_UPDOWN = 9;
    public static final int WKS_TRANSITION_TYPE_MAX = 10;
    public static final int WKS_TRANSITION_DEFAULT = WKS_TRANSITION_TRANSLATE;
    public static final String KEY_WKS_TRANSITION_EFFECT = "workspace_transition_effect";

    // the transition effect of application pages scrolling
    public static final int APP_TRANSITION_RAND = 0;
    public static final int APP_TRANSITION_TRANSLATE = 1;
    public static final int APP_TRANSITION_FADE = 2;
    public static final int APP_TRANSITION_FLIP = 3;
    public static final int APP_TRANSITION_WINDMILL = 4;
    public static final int APP_TRANSITION_CUBE = 5;
    public static final int APP_TRANSITION_PHOTOWALL = 6;
    public static final int APP_TRANSITION_BLIND = 7;
    public static final int APP_TRANSITION_ROLL = 8;
    public static final int APP_TRANSITION_PUSH = 9;
    public static final int APP_TRANSITION_UPDOWN = 10;
    public static final int APP_TRANSITION_TYPE_MAX = 11;
    public static final int APP_TRANSITION_DEFAULT = APP_TRANSITION_TRANSLATE;
    public static final String KEY_APP_TRANSITION_EFFECT = "apps_transition_effect";

    private static final int CAMERA_DISTANCE = 5000;
    private static final boolean IMPROVE_PERFORMANCE = true;

    public static void wave(View currV, View nextV, float scrollProgress, float density) {
        float rotation;
        float scale;
        final float OVERSCROLL_ROTATION = 24f;
        final float SCALE_RATIO = 0.4f;
        final float ALPHA_RATIO = 0.8f;

        if (currV != null) {
            rotation = OVERSCROLL_ROTATION * scrollProgress;
            currV.setCameraDistance(density * CAMERA_DISTANCE);
            currV.setPivotX(currV.getMeasuredWidth() * 0.5f);
            currV.setPivotY(currV.getMeasuredHeight() * 0.5f);
            currV.setRotationY(rotation);
            scale = (1 - Math.abs(scrollProgress)) * SCALE_RATIO + (1 - SCALE_RATIO);
            currV.setScaleX(scale);
            currV.setScaleY(scale);
            if (!IMPROVE_PERFORMANCE) {
                currV.setAlpha((1 - Math.abs(scrollProgress)) * ALPHA_RATIO + (1 - ALPHA_RATIO));
            }

            if (nextV != null) {
                if (scrollProgress >= 0) {
                    scrollProgress -= 1;
                } else {
                    scrollProgress += 1;
                }

                rotation = OVERSCROLL_ROTATION * scrollProgress;
                nextV.setCameraDistance(density * CAMERA_DISTANCE);
                nextV.setPivotX(nextV.getMeasuredWidth() * 0.5f);
                nextV.setPivotY(nextV.getMeasuredHeight() * 0.5f);
                nextV.setRotationY(rotation);
                scale = (1 - Math.abs(scrollProgress)) * SCALE_RATIO + (1 - SCALE_RATIO);
                nextV.setScaleX(scale);
                nextV.setScaleY(scale);
                if (!IMPROVE_PERFORMANCE) {
                    nextV.setAlpha((1 - Math.abs(scrollProgress)) * ALPHA_RATIO + (1 - ALPHA_RATIO));
                }
            }
        }
    }

    public static void fade(View leftV, View rightV, float scrollProgress, float density) {
        final float TRANSITION_SCALE_FACTOR = 0.74f;

        if (rightV != null) {
            ZInterpolator zInterpolator = new ZInterpolator(0.5f);
            float interpolatedProgress = zInterpolator.getInterpolation(Math.abs(scrollProgress));
            float scale = (1 - interpolatedProgress) + interpolatedProgress * TRANSITION_SCALE_FACTOR;
            float translationX = scrollProgress * rightV.getMeasuredWidth();
            AccelerateInterpolator alphaInterpolator = new AccelerateInterpolator(0.9f);
            float alpha = alphaInterpolator.getInterpolation(1 - Math.abs(scrollProgress));

            rightV.setCameraDistance(density * CAMERA_DISTANCE);
            int pageWidth = rightV.getMeasuredWidth();
            int pageHeight = rightV.getMeasuredHeight();

            rightV.setPivotY(pageHeight / 2.0f);
            rightV.setPivotX(pageWidth / 2.0f);
            rightV.setTranslationX(translationX);
            rightV.setScaleX(scale);
            rightV.setScaleY(scale);
            rightV.setAlpha(alpha);
        }

        if (leftV != null) {
            leftV.setTranslationX(0);
            leftV.setScaleX(1);
            leftV.setScaleY(1);
            leftV.setAlpha(1);
        }
    }

    public static void flip(View currV, View nextV, float scrollProgress, float density) {
        float rotation;
        final float ALPHA_RATIO = 0.8f;

        if (Math.abs(scrollProgress) < 0.5f) {
            float progress = 2 * scrollProgress;

            nextV.setAlpha(0);
            rotation = -90 * progress;
            currV.setCameraDistance(density * CAMERA_DISTANCE);
            currV.setPivotX(currV.getMeasuredWidth() * 0.5f);
            currV.setPivotY(currV.getMeasuredHeight() * 0.5f);
            currV.setRotationY(rotation);
            if (currV instanceof CellLayout) {
                ((CellLayout) currV).setShortcutAndWidgetAlpha((1 - Math.abs(progress)) * ALPHA_RATIO
                        + (1 - ALPHA_RATIO));
            } else {
                currV.setAlpha((1 - Math.abs(progress)) * ALPHA_RATIO + (1 - ALPHA_RATIO));
            }

            currV.setTranslationX(scrollProgress * currV.getMeasuredWidth());
        } else {
            float progress;
            float transProgress;

            if (scrollProgress > 0) {
                progress = (scrollProgress - 0.5f) * 2 - 1;
                transProgress = (scrollProgress - 1) * nextV.getMeasuredWidth();
            } else {
                progress = (scrollProgress + 0.5f) * 2 + 1;
                transProgress = (scrollProgress + 1) * nextV.getMeasuredWidth();
            }

            currV.setAlpha(0);
            rotation = -90 * progress;
            nextV.setCameraDistance(density * CAMERA_DISTANCE);
            nextV.setPivotX(nextV.getMeasuredWidth() * 0.5f);
            nextV.setPivotY(nextV.getMeasuredHeight() * 0.5f);
            nextV.setRotationY(rotation);
            if (nextV instanceof CellLayout) {
                ((CellLayout) nextV).setShortcutAndWidgetAlpha((1 - Math.abs(progress)) * ALPHA_RATIO
                        + (1 - ALPHA_RATIO));
            } else {
                nextV.setAlpha((1 - Math.abs(progress)) * ALPHA_RATIO + (1 - ALPHA_RATIO));
            }
            nextV.setTranslationX(transProgress);
        }
    }

    public static void windmill(View currV, View nextV, float scrollProgress, float density) {
        float rotation;
        final float ALPHA_RATIO = 0.8f;

        rotation = -45 * scrollProgress;
        currV.setCameraDistance(density * CAMERA_DISTANCE);
        currV.setPivotX(currV.getMeasuredWidth() * 0.5f);
        currV.setPivotY(currV.getMeasuredHeight() * 2.0f);
        currV.setRotation(rotation);
        if (!IMPROVE_PERFORMANCE) {
            currV.setAlpha((1 - Math.abs(scrollProgress)) * ALPHA_RATIO + (1 - ALPHA_RATIO));
        }

        currV.setTranslationX(scrollProgress * currV.getMeasuredWidth());

        if (scrollProgress >= 0) {
            scrollProgress -= 1;
        } else {
            scrollProgress += 1;
        }

        rotation = -45 * scrollProgress;
        nextV.setCameraDistance(density * CAMERA_DISTANCE);
        nextV.setPivotX(nextV.getMeasuredWidth() * 0.5f);
        nextV.setPivotY(nextV.getMeasuredHeight() * 2.0f);
        nextV.setRotation(rotation);
        if (!IMPROVE_PERFORMANCE) {
            nextV.setAlpha((1 - Math.abs(scrollProgress)) * ALPHA_RATIO + (1 - ALPHA_RATIO));
        }
        nextV.setTranslationX(scrollProgress * nextV.getMeasuredWidth());
    }

    public static void cube(View leftV, View rightV, float scrollProgress, float density) {
        float rotation;
        final float ALPHA_RATIO = 0.8f;

        if (leftV != null) {
            if (scrollProgress < 0) {
                scrollProgress += 1;
            }

            rotation = -90 * scrollProgress;
            leftV.setCameraDistance(density * CAMERA_DISTANCE);
            leftV.setPivotX(leftV.getMeasuredWidth());
            leftV.setPivotY(leftV.getMeasuredHeight() * 0.5f);
            leftV.setRotationY(rotation);
            if (!IMPROVE_PERFORMANCE) {
                leftV.setAlpha((1 - Math.abs(scrollProgress)) * ALPHA_RATIO + (1 - ALPHA_RATIO));
            }

            if (rightV != null) {
                rotation += 90;
                rightV.setCameraDistance(density * CAMERA_DISTANCE);
                rightV.setPivotX(0);
                rightV.setPivotY(rightV.getMeasuredHeight() * 0.5f);
                rightV.setRotationY(rotation);
                if (!IMPROVE_PERFORMANCE) {
                    rightV.setAlpha(Math.abs(scrollProgress) * ALPHA_RATIO + (1 - ALPHA_RATIO));
                }
            }
        }
    }

    public static void photowall(View currV, View nextV, float scrollProgress, float density) {
        float rotation;
        final float ALPHA_RATIO = 0.8f;
        float privotX1, privotX2;

        if (Math.abs(scrollProgress) > 0.5f) {
            rotation = 60 - 60 * (Math.abs(scrollProgress) - 0.5f) * 2;
        } else {
            rotation = 60;
        }
        privotX1 = currV.getMeasuredWidth() * 0.5f + scrollProgress * nextV.getMeasuredWidth();

        if (scrollProgress > 0) {
            privotX2 = privotX1 - nextV.getMeasuredWidth();
        } else {
            privotX2 = privotX1 + nextV.getMeasuredWidth();
        }

        currV.setCameraDistance(density * CAMERA_DISTANCE);
        currV.setPivotX(privotX1);
        currV.setPivotY(currV.getMeasuredHeight() * 0.5f);
        currV.setRotationY(rotation);
        if (currV instanceof CellLayout) {
            ((CellLayout) currV).setShortcutAndWidgetAlpha((1 - Math.abs(scrollProgress)) * ALPHA_RATIO
                    + (1 - ALPHA_RATIO));
        } else {
            currV.setAlpha((1 - Math.abs(scrollProgress)) * ALPHA_RATIO + (1 - ALPHA_RATIO));
        }

        nextV.setCameraDistance(density * CAMERA_DISTANCE);
        nextV.setPivotX(privotX2);
        nextV.setPivotY(nextV.getMeasuredHeight() * 0.5f);
        nextV.setRotationY(rotation);
        if (nextV instanceof CellLayout) {
            ((CellLayout) nextV).setShortcutAndWidgetAlpha(Math.abs(scrollProgress) * ALPHA_RATIO + (1 - ALPHA_RATIO));
        } else {
            nextV.setAlpha(Math.abs(scrollProgress) * ALPHA_RATIO + (1 - ALPHA_RATIO));
        }

    }

    public static void push(View currV, View nextV, float scrollProgress) {
        float privotX1, privotX2;
        if (scrollProgress > 0) {
            privotX1 = currV.getMeasuredWidth();
            privotX2 = 0;
        } else {
            privotX1 = 0;
            privotX2 = nextV.getMeasuredWidth();
        }

        currV.setPivotX(privotX1);
        currV.setPivotY(currV.getMeasuredHeight() * 0.5f);
        currV.setScaleX(1 - Math.abs(scrollProgress));

        nextV.setPivotX(privotX2);
        nextV.setPivotY(nextV.getMeasuredHeight() * 0.5f);
        nextV.setScaleX(Math.abs(scrollProgress));
    }

    public static void updown(View currV, View nextV, float scrollProgress) {
        currV.setTranslationY(currV.getMeasuredHeight() * 0.5f * Math.abs(scrollProgress));

        nextV.setTranslationY(nextV.getMeasuredHeight() * 0.5f * (1 - Math.abs(scrollProgress)));
    }

    public static void blind(View currV, View nextV, float scrollProgress, float density) {
        float rotation;

        if (Math.abs(scrollProgress) < 0.5f) {
            float progress = 2 * scrollProgress;

            currV.setAlpha(1);
            currV.setTranslationX(scrollProgress * currV.getMeasuredWidth());
            nextV.setAlpha(0);
            rotation = -90 * progress;
            ViewGroup parent = (ViewGroup) currV;
            if (parent instanceof PagedViewCellLayout) {
                parent = ((PagedViewCellLayout) parent).mChildren;
            }

            if (parent != null) {
                for (int i = 0; i < parent.getChildCount(); i++) {
                    View v = parent.getChildAt(i);
                    if (v != null) {
                        v.setCameraDistance(density * CAMERA_DISTANCE);
                        v.setPivotX(v.getMeasuredWidth() * 0.5f);
                        v.setPivotY(v.getMeasuredHeight() * 0.5f);
                        v.setRotationY(rotation);
                    }
                }
            }
            parent.invalidate();
        } else {
            float progress;
            float transProgress;

            if (scrollProgress > 0) {
                progress = (scrollProgress - 0.5f) * 2 - 1;
                transProgress = (scrollProgress - 1) * nextV.getMeasuredWidth();
            } else {
                progress = (scrollProgress + 0.5f) * 2 + 1;
                transProgress = (scrollProgress + 1) * nextV.getMeasuredWidth();
            }

            currV.setAlpha(0);
            nextV.setAlpha(1);
            nextV.setTranslationX(transProgress);
            rotation = -90 * progress;
            ViewGroup parent = (ViewGroup) nextV;
            if (parent instanceof PagedViewCellLayout) {
                parent = ((PagedViewCellLayout) parent).mChildren;
            }

            if (parent != null) {
                for (int i = 0; i < parent.getChildCount(); i++) {
                    View v = parent.getChildAt(i);
                    if (v != null) {
                        v.setCameraDistance(density * CAMERA_DISTANCE);
                        v.setPivotX(v.getMeasuredWidth() * 0.5f);
                        v.setPivotY(v.getMeasuredHeight() * 0.5f);
                        v.setRotationY(rotation);
                    }
                }
            }
            parent.invalidate();
        }
    }

    public static void roll(View currV, View nextV, float scrollProgress) {
        float rotation;
        final int RADIUS = 180;
        final int CENTER_H_ADP = -55;
        final int CENTER_V_ADP = -80;

        ViewGroup parent = (ViewGroup) currV;
        if (parent instanceof PagedViewCellLayout) {
            parent = ((PagedViewCellLayout) parent).mChildren;
        }

        float progress = Math.min(1, 2 * Math.abs(scrollProgress));

        if (parent != null) {
            int total = parent.getChildCount();
            for (int i = 0; i < total; i++) {
                View v = parent.getChildAt(i);
                if (v != null) {
                    rotation = -360 * i / total * progress;

                    float centerX = currV.getMeasuredWidth() * 0.5f + CENTER_H_ADP;
                    float centerY = currV.getMeasuredHeight() * 0.5f + CENTER_V_ADP;
                    float x = (float) (centerX + RADIUS * Math.cos(rotation * Math.PI / 180));
                    float y = (float) (centerY + RADIUS * Math.sin(rotation * Math.PI / 180));
                    float dx = (x - v.getX()) * progress;
                    float dy = (y - v.getY()) * progress;

                    v.setPivotX(v.getMeasuredWidth() * 0.5f);
                    v.setPivotY(v.getMeasuredHeight() * 0.5f);
                    v.setRotation(rotation);
                    v.setTranslationX(dx);
                    v.setTranslationY(dy);
                }
            }
        }

        parent.invalidate();

        if (scrollProgress >= 0) {
            progress = Math.abs(scrollProgress - 1);
        } else {
            progress = scrollProgress + 1;
        }
        progress = Math.min(1, 3 * progress);

        parent = (ViewGroup) nextV;
        if (parent instanceof PagedViewCellLayout) {
            parent = ((PagedViewCellLayout) parent).mChildren;
        }

        if (parent != null) {
            int total = parent.getChildCount();
            for (int i = 0; i < total; i++) {
                View v = parent.getChildAt(i);
                if (v != null) {
                    rotation = -360 * i / total * progress;

                    float centerX = nextV.getMeasuredWidth() * 0.5f + CENTER_H_ADP;
                    float centerY = nextV.getMeasuredHeight() * 0.5f + CENTER_V_ADP;
                    float x = (float) (centerX + RADIUS * Math.cos(rotation * Math.PI / 180));
                    float y = (float) (centerY + RADIUS * Math.sin(rotation * Math.PI / 180));
                    float dx = (x - v.getX()) * progress;
                    float dy = (y - v.getY()) * progress;

                    v.setPivotX(v.getMeasuredWidth() * 0.5f);
                    v.setPivotY(v.getMeasuredHeight() * 0.5f);
                    v.setRotation(rotation);
                    v.setTranslationX(dx);
                    v.setTranslationY(dy);
                }
            }
        }

        parent.invalidate();
    }
}

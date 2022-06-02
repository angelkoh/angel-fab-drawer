package angel.androidapps.fabdrawer;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.ViewCompat;
import androidx.core.widget.ImageViewCompat;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class FabDrawer extends FrameLayout implements
        CoordinatorLayout.AttachedBehavior {

    private boolean isExtended = false;
    private boolean isShowHorizontal = true;
    private boolean isShowVertical = true;
    private int iconTint = Color.DKGRAY;
    private FloatingActionButton fab;
    private MaterialCardView mcvHorizontal;
    private MaterialCardView mcvVertical;

    private ClickCallback horizontalCallback;
    private ClickCallback verticalCallback;

    public interface ClickCallback {
        void onClick(int position, int drawableId);
    }

    public FabDrawer(@NonNull Context context) {
        super(context);
    }

    public FabDrawer(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initUi(attrs);
    }

    public FabDrawer(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initUi(attrs);
    }

    public FabDrawer(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initUi(attrs);
    }

    private void initUi(AttributeSet attrs) {

        View.inflate(getContext(), R.layout.fab_drawer, this);
        mcvHorizontal = this.findViewById(R.id.mcv_horizontal);
        mcvVertical = this.findViewById(R.id.mcv_vertical);
        fab = this.findViewById(R.id.efab);
        fab.setOnClickListener(v -> toggle());

        updateAttr(attrs);
        tintIcons();

        setExtended(isExtended);
    }

    public void setHorizontalCallback(ClickCallback horizontalCallback) {
        this.horizontalCallback = horizontalCallback;
    }

    public void setVerticalCallback(ClickCallback verticalCallback) {
        this.verticalCallback = verticalCallback;
    }

    public void setHorizontalIconsVisibility(boolean[] isVisible) {
        setIconVisibility(isVisible, findViewById(R.id.ll_horizontal));
    }

    public void setVerticalIconsVisibility(boolean[] isVisible) {
        setIconVisibility(isVisible, findViewById(R.id.ll_vertical));
    }

    private void setIconVisibility(boolean[] isVisible, LinearLayout ll) {
        int count = ll.getChildCount();
        for (int i = 0; i < count && i < isVisible.length; i++) {
            ll.getChildAt(i).setVisibility(isVisible[i] ? View.VISIBLE : View.GONE);
        }
    }


    private void tintIcons() {
        tintChild(findViewById(R.id.ll_vertical));
        tintChild(findViewById(R.id.ll_horizontal));
    }

    private void tintChild(LinearLayout ll) {
        int count = ll.getChildCount();
        for (int i = 0; i < count; i++) {
            ImageView iv = (ImageView) ll.getChildAt(i);
            ImageViewCompat.setImageTintList(iv,
                    ColorStateList.valueOf(iconTint));
        }
    }

    private void updateAttr(AttributeSet attrs) {
        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.FabDrawer);
        try {
            isExtended = ta.getBoolean(R.styleable.FabDrawer_isExtended, false);
            isShowHorizontal = ta.getBoolean(R.styleable.FabDrawer_showHorizontal, true);
            isShowVertical = ta.getBoolean(R.styleable.FabDrawer_showVertical, true);

            iconTint = ta.getColor(R.styleable.FabDrawer_iconTint, Color.DKGRAY);

            int ids = ta.getResourceId(R.styleable.FabDrawer_horizontalIcons, 0);
            if (populateIcons(ids, findViewById(R.id.ll_horizontal), this::handleHorizontalCLicks) == 0) {
                isShowHorizontal = false;
            }
            ids = ta.getResourceId(R.styleable.FabDrawer_verticalIcons, 0);
            if (populateIcons(ids, findViewById(R.id.ll_vertical), this::handleVerticalClicks) == 0) {
                isShowVertical = false;
            }
        } finally {
            ta.recycle();
        }
    }

    private void handleVerticalClicks(int position, int drawableId) {
        if (verticalCallback != null) {
            verticalCallback.onClick(position, drawableId);
        }
    }

    private void handleHorizontalCLicks(int position, int drawableId) {
        if (horizontalCallback != null) {
            horizontalCallback.onClick(position, drawableId);
        }
    }

    private int populateIcons(int rootId, LinearLayout ll, ClickCallback callbacks) {

        int drawableCount = 0;
        if (rootId != 0) {
            TypedArray drawables = getResources().obtainTypedArray(rootId);
            try {
                drawableCount = drawables.length();
                for (int i = drawables.length() - 1; i >= 0; i--) {
                    int drawable = drawables.getResourceId(i, 0);
                    if (drawable != 0) {
                        ImageView iv = new ImageView(getContext());
                        iv.setPadding(16, 16, 16, 16);
                        iv.setImageResource(drawable);
                        ll.addView(iv, 0);

                        if (callbacks != null) {
                            int index = i;
                            iv.setOnClickListener(v -> callbacks.onClick(index, drawable));
                        }
                    }
                }
            } finally {
                drawables.recycle();
            }
        }
        return drawableCount;
    }

    //EXTENDED
    public void toggle() {
        setExtended(!isExtended);
        if (isExtended) {
            rotate2(fab);
        } else {
            rotate1(fab);
        }
    }

    private void rotate1(View view) {
        ObjectAnimator rotate = ObjectAnimator.ofFloat(view, "rotation", 360f, 0f);
        rotate.setDuration(1000);
        rotate.setInterpolator(new BounceInterpolator());
        rotate.start();
    }

    private void rotate2(View view) {
        ObjectAnimator rotate = ObjectAnimator.ofFloat(view, "rotation", 0f, 360f);
        rotate.setDuration(1000);
        rotate.setInterpolator(new BounceInterpolator());
        rotate.start();
    }

    public boolean isExtended() {
        return isExtended;
    }

    public void setExtended(boolean extended) {
        if (isExtended == extended) {
            if (isExtended) {
                if (isShowHorizontal) showHorizontal();
                else hideHorizontal();
                if (isShowVertical) showVertical();
                else hideVertical();
            } else {
                hideHorizontal();
                hideVertical();
            }
        } else {
            isExtended = extended;
            animateHorizontal();
            animateVertical();
        }
    }

    public boolean isShowHorizontal() {
        return isShowHorizontal;
    }

    public void setShowHorizontal(boolean showHorizontal) {
        isShowHorizontal = showHorizontal;
    }

    public boolean isShowVertical() {
        return isShowVertical;
    }

    public void setShowVertical(boolean showVertical) {
        isShowVertical = showVertical;
    }

    private void showHorizontal() {
        mcvHorizontal.setVisibility(View.VISIBLE);
    }

    private void showVertical() {
        mcvVertical.setVisibility(View.VISIBLE);
    }

    private void hideHorizontal() {
        mcvHorizontal.setVisibility(View.INVISIBLE);
    }

    private void hideVertical() {
        mcvVertical.setVisibility(View.INVISIBLE);
    }

    private void animateHorizontal() {
        MaterialCardView mcv = mcvHorizontal;
        if (isShowHorizontal) {

            if (isExtended) {
                //show View
                mcv.setVisibility(View.VISIBLE);
                mcv.setTranslationX(mcv.getWidth());
                mcv.setAlpha(0.3f);

                ObjectAnimator move = ObjectAnimator.ofFloat(mcv, "translationX", 0f);
                move.setDuration(800);
                ObjectAnimator alpha = ObjectAnimator.ofFloat(mcv, "alpha", 1.0f);
                alpha.setDuration(1000);

                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.play(alpha).with(move);
                animatorSet.setInterpolator(new OvershootInterpolator());
                animatorSet.start();

            } else {
                //hide View
                ObjectAnimator move = ObjectAnimator.ofFloat(mcv, "translationX", -10f);
                move.setDuration(100);
                ObjectAnimator move2 = ObjectAnimator.ofFloat(mcv, "translationX", mcv.getWidth());
                move2.setDuration(800);
                ObjectAnimator alpha = ObjectAnimator.ofFloat(mcv, "alpha", 0.1f);
                alpha.setDuration(1000);

                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.play(move).before(move2).before(alpha);
                animatorSet.start();

                mcv.postDelayed(() -> mcv.setVisibility(View.INVISIBLE), 900);

            }
        } else {
            mcv.setVisibility(View.INVISIBLE);
        }
    }

    private void animateVertical() {
        MaterialCardView mcv = mcvVertical;
        if (isShowVertical) {
            if (isExtended) {
                mcv.setVisibility(View.VISIBLE);
                mcv.setTranslationY(100);
                mcv.setAlpha(0.3f);

                //show view
                ObjectAnimator move = ObjectAnimator.ofFloat(mcv, "translationY", 0f);
                move.setDuration(800);
                ObjectAnimator alpha = ObjectAnimator.ofFloat(mcv, "alpha", 1.0f);
                alpha.setDuration(1000);

                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.play(alpha).with(move);
                animatorSet.setInterpolator(new OvershootInterpolator());
                animatorSet.start();
            } else {
                ObjectAnimator move = ObjectAnimator.ofFloat(mcv, "translationY", -10f);
                move.setDuration(100);
                ObjectAnimator move2 = ObjectAnimator.ofFloat(mcv, "translationY", mcv.getHeight());
                move2.setDuration(800);
                ObjectAnimator alpha = ObjectAnimator.ofFloat(mcv, "alpha", 0.1f);
                alpha.setDuration(1000);

                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.play(move).before(move2).before(alpha);
                animatorSet.start();

                mcv.postDelayed(() -> mcv.setVisibility(View.INVISIBLE), 900);
            }
        } else {
            mcv.setVisibility(View.INVISIBLE);
        }
    }

    //COORDINATOR BEHAVIOR


    @NonNull
    @Override
    public CoordinatorLayout.Behavior getBehavior() {
        return new Behavior();
    }

    public static class Behavior extends BaseBehavior<FabDrawer> {
        public Behavior() {
            super();
        }

        public Behavior(Context context, AttributeSet attrs) {
            super(context, attrs);
        }
    }

    protected static class BaseBehavior<T extends FabDrawer> extends
            CoordinatorLayout.Behavior<T> {

        private Rect tmpRect;

        public BaseBehavior() {
            super();
        }

        public BaseBehavior(Context context, AttributeSet attrs) {
            super(context, attrs);

        }


        @Override
        public void onAttachedToLayoutParams(@NonNull CoordinatorLayout.LayoutParams lp) {
            if (lp.dodgeInsetEdges == Gravity.NO_GRAVITY) {
                // If the developer hasn't set dodgeInsetEdges, lets set it to BOTTOM so that
                // we dodge any Snack bars
                lp.dodgeInsetEdges = Gravity.BOTTOM;
            }
        }


        private static boolean isBottomSheet(@NonNull View view) {
            final ViewGroup.LayoutParams lp = view.getLayoutParams();
            if (lp instanceof CoordinatorLayout.LayoutParams) {
                return ((CoordinatorLayout.LayoutParams) lp).getBehavior() instanceof BottomSheetBehavior;
            }
            return false;
        }


        @Override
        public boolean onLayoutChild(
                @NonNull CoordinatorLayout parent,
                @NonNull FabDrawer child,
                int layoutDirection) {

            // Now let the CoordinatorLayout lay out the FAB
            parent.onLayoutChild(child, layoutDirection);
            // Now offset it if needed
            offsetIfNeeded(parent, child);
            return true;
        }


        /**
         * Pre-Lollipop we use padding so that the shadow has enough space to be drawn. This method
         * offsets our layout position so that we're positioned correctly if we're on one of our
         * parent's edges.
         */
        // dereference of possibly-null reference lp
        @SuppressWarnings("nullness:dereference.of.nullable")
        private void offsetIfNeeded(
                @NonNull CoordinatorLayout parent, @NonNull FabDrawer fab) {
            final Rect padding = fab.getClipBounds();

            if (padding != null && padding.centerX() > 0 && padding.centerY() > 0) {
                final CoordinatorLayout.LayoutParams lp =
                        (CoordinatorLayout.LayoutParams) fab.getLayoutParams();

                int offsetTB = 0;
                int offsetLR = 0;

                if (fab.getRight() >= parent.getWidth() - lp.rightMargin) {
                    // If we're on the right edge, shift it the right
                    offsetLR = padding.right;
                } else if (fab.getLeft() <= lp.leftMargin) {
                    // If we're on the left edge, shift it the left
                    offsetLR = -padding.left;
                }
                if (fab.getBottom() >= parent.getHeight() - lp.bottomMargin) {
                    // If we're on the bottom edge, shift it down
                    offsetTB = padding.bottom;
                } else if (fab.getTop() <= lp.topMargin) {
                    // If we're on the top edge, shift it up
                    offsetTB = -padding.top;
                }

                if (offsetTB != 0) {
                    ViewCompat.offsetTopAndBottom(fab, offsetTB);
                }
                if (offsetLR != 0) {
                    ViewCompat.offsetLeftAndRight(fab, offsetLR);
                }
            }
        }

    }
}

package com.sugarya;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sugarya.footer.interfaces.FooterMode;
import com.sugarya.footer.interfaces.IFooterMode;
import com.sugarya.spinnerlibrary.R;
import java.util.LinkedList;
import java.util.List;


/**
 * Created by Ethan on 2017/8/9.
 * 筛选条，支持代码和布局混合控制，
 * 支持五层筛选条件（可轻松拓展筛选数量）
 * 支持两种张开方式，支持高度wrap_content配置
 * 父容器必须是FrameLayout及其子控件
 * 支持下拉列表动画两级协同控制（FooterView控制优先）
 * <p>
 * 名次解释：
 * Spinner Bar 代表内部筛选条
 * Spinner Unit 代表筛选条里的单元
 * Spinner Footer 代表下拉列表
 * unit Icon 代表筛选单元文字旁的图标
 */
public class SpinnerLayout extends RelativeLayout {

    private static final String TAG = SpinnerLayout.class.getSimpleName();

    private static final float DEFAULT_LINE_SCALE = 0.3f;
    private static final int OPEN_FOOTER_ANIMATION_DURATION = 260;
    private static final int CLOSE_FOOTER_ANIMATION_DURATION = 260;

    private static final int DEFAULT_FILTER_BAR_UNIT_HEIGHT = 135;
    private static final int DEFAULT_FILTER_COVER_COLOR = Color.parseColor("#55000000");
    private static final int DEFAULT_FILTER_TITLE_COLOR = Color.parseColor("#333333");
    private static final int DEFAULT_INDICATOR_BACKGROUND = Color.WHITE;
    private static final int DEFAULT_FILTER_TITLE_SELECTED_COLOR = Color.parseColor("#00a7f8");
    private static final int DEFAULT_FILTER_TITLE_SIZE_DP = 14;
    private static final int DEFAULT_LINE_COLOR = Color.parseColor("#e0e0e0");
    private static final int ORIGIN_HEIGHT = 1;

    private int mSpinnerBarHeight = DEFAULT_FILTER_BAR_UNIT_HEIGHT;
    /**
     * 筛选条打开时，背景覆盖物颜色
     */
    private int mSpinnerCoverColor = DEFAULT_FILTER_COVER_COLOR;
    private int mSpinnerBarBackground = DEFAULT_INDICATOR_BACKGROUND;

    private float mSpinnerTitleSize = DEFAULT_FILTER_TITLE_SIZE_DP;
    private int mSpinnerTitleColor = DEFAULT_FILTER_TITLE_COLOR;
    private int mSpinnerTitleSelectedColor = DEFAULT_FILTER_TITLE_SELECTED_COLOR;

    private String mFirstTitle;
    private String mSecondTitle;
    private String mThirdTitle;
    private String mFourthTitle;
    private String mFifthTitle;

    /**
     * 筛选条单元文字旁的图标
     */
    private Drawable mUnitIcon;
    private Drawable mUnitSelectedIcon;


    /**
     * 筛选条单元集
     */
    private List<SpinnerUnitEntity> mSpinnerUnitEntityList = new LinkedList<>();

    /**
     * 筛选条点击监听
     */
    private OnSpinnerLayoutClickListener mOnSpinnerLayoutClickListener;
    /**
     * 筛选条主体布局
     */
    private LinearLayout mSpinnerBarLayout;

    /**
     * FooterView的根视图
     */
    private FrameLayout mFooterViewRoot;
    /**
     * 包裹筛选条主体的布局
     */
    private LinearLayout mSpinnerContainerLayout;
    /**
     * 原始 根布局参数
     */
    private LayoutParams mOriginRootLayoutParams;
    /**
     * mSpinnerContainerLayout 对应的原始布局参数
     */
    private LayoutParams mOriginSpinnerContainerLayoutParams;

    /**
     * 是否正在展示下拉视图
     */
    private boolean isShowing = false;
    /**
     * 当前选中的
     */
    private int mSelectedIndex = -1;

    /**
     * 下拉列表的全局动画模式
     */
    private FooterMode mGlobalFooterMode;

    /**
     * 触摸外部时，所有下拉列表能否被关闭
     */
    private boolean mGlobalIsTouchOutsideCanceled = false;

    /**
     * Filter Unit的位置
     */
    private int mGravityMode = Gravity.CENTER;

    private float mLineScale = DEFAULT_LINE_SCALE;

    /**
     * 存储FilterLayout Padding参数
     */
    private final Rect mSpinnerLayoutPaddingRect = new Rect();

    private static SparseArray<FooterMode> mFooterModeSparse = new SparseArray<>();

    private static SparseIntArray mSpinnerGravitySparse = new SparseIntArray();

    static {
        mFooterModeSparse.put(0, FooterMode.MODE_TRANSLATE);
        mFooterModeSparse.put(1, FooterMode.MODE_EXPAND);

        mSpinnerGravitySparse.put(0, Gravity.CENTER_HORIZONTAL);
        mSpinnerGravitySparse.put(1, Gravity.START);
        mSpinnerGravitySparse.put(2, Gravity.END);
    }




    public SpinnerLayout(Context context) {
        super(context);
        init(context);
        Log.d(TAG, "FilterLayout 1");
    }

    public SpinnerLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initTypedArray(context, attrs);
        init(context);
        Log.d(TAG, "FilterLayout 2");
    }

    public SpinnerLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initTypedArray(context, attrs);
        init(context);
        Log.d(TAG, "FilterLayout 3");
    }

    private void initTypedArray(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SpinnerLayout);
        mFirstTitle = typedArray.getString(R.styleable.SpinnerLayout_firstText);
        mSecondTitle = typedArray.getString(R.styleable.SpinnerLayout_secondText);
        mThirdTitle = typedArray.getString(R.styleable.SpinnerLayout_thirdText);
        mFourthTitle = typedArray.getString(R.styleable.SpinnerLayout_fourthText);
        mFifthTitle = typedArray.getString(R.styleable.SpinnerLayout_fifthText);

        mSpinnerBarHeight = (int) typedArray.getDimension(R.styleable.SpinnerLayout_spinnerHeight, DEFAULT_FILTER_BAR_UNIT_HEIGHT);
        mSpinnerTitleSize = typedArray.getDimension(R.styleable.SpinnerLayout_textSize, dip2px(DEFAULT_FILTER_TITLE_SIZE_DP));
        mSpinnerTitleColor = typedArray.getColor(R.styleable.SpinnerLayout_textColor, DEFAULT_FILTER_TITLE_COLOR);
        mSpinnerTitleSelectedColor = typedArray.getColor(R.styleable.SpinnerLayout_textColorSelected, DEFAULT_FILTER_TITLE_SELECTED_COLOR);
        mSpinnerCoverColor = typedArray.getColor(R.styleable.SpinnerLayout_coverColor, DEFAULT_FILTER_COVER_COLOR);

        mUnitIcon = typedArray.getDrawable(R.styleable.SpinnerLayout_icon) != null?
                typedArray.getDrawable(R.styleable.SpinnerLayout_icon) : getResources().getDrawable(R.drawable.footer_triangle_down_black);
        mUnitSelectedIcon = typedArray.getDrawable(R.styleable.SpinnerLayout_iconSelected) != null?
                typedArray.getDrawable(R.styleable.SpinnerLayout_iconSelected) : getResources().getDrawable(R.drawable.footer_triangle_up_blue);
        mGlobalIsTouchOutsideCanceled = typedArray.getBoolean(R.styleable.SpinnerLayout_touchOutsideCanceled, true);

        mLineScale =typedArray.getFloat(R.styleable.SpinnerLayout_lineScale, DEFAULT_LINE_SCALE);
        mSpinnerBarBackground = typedArray.getColor(R.styleable.SpinnerLayout_spinnerBackground, DEFAULT_INDICATOR_BACKGROUND);
        mGlobalFooterMode = mFooterModeSparse.get(typedArray.getInt(R.styleable.SpinnerLayout_footerMode, 1));
        mGravityMode = mSpinnerGravitySparse.get(typedArray.getInt(R.styleable.SpinnerLayout_spinnerGravity, 0));

        typedArray.recycle();

        initializeFromTypedArray();
    }

    private void initializeFromTypedArray() {
        if (!TextUtils.isEmpty(mFirstTitle)) {
            SpinnerUnitEntity spinnerUnitEntity = new SpinnerUnitEntity(mFirstTitle, true);
            mSpinnerUnitEntityList.add(spinnerUnitEntity);
        }
        if (!TextUtils.isEmpty(mSecondTitle)) {
            SpinnerUnitEntity spinnerUnitEntity = new SpinnerUnitEntity(mSecondTitle, true);
            mSpinnerUnitEntityList.add(spinnerUnitEntity);
        }
        if (!TextUtils.isEmpty(mThirdTitle)) {
            SpinnerUnitEntity spinnerUnitEntity = new SpinnerUnitEntity(mThirdTitle, true);
            mSpinnerUnitEntityList.add(spinnerUnitEntity);
        }
        if (!TextUtils.isEmpty(mFourthTitle)) {
            SpinnerUnitEntity spinnerUnitEntity = new SpinnerUnitEntity(mFourthTitle, true);
            mSpinnerUnitEntityList.add(spinnerUnitEntity);
        }
        if (!TextUtils.isEmpty(mFifthTitle)) {
            SpinnerUnitEntity spinnerUnitEntity = new SpinnerUnitEntity(mFifthTitle, true);
            mSpinnerUnitEntityList.add(spinnerUnitEntity);
        }
    }

    private void init(Context context) {
        switchPaddingToFilterBarLayout();

        mSpinnerContainerLayout = new LinearLayout(getContext());
        mSpinnerContainerLayout.setOrientation(LinearLayout.VERTICAL);
        mOriginSpinnerContainerLayoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

        mSpinnerBarLayout = new LinearLayout(context);
        LinearLayout.LayoutParams SpinnerBarLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, mSpinnerBarHeight);
        mSpinnerBarLayout.setLayoutParams(SpinnerBarLayoutParams);
        mSpinnerBarLayout.setPadding(mSpinnerLayoutPaddingRect.left, mSpinnerLayoutPaddingRect.top, mSpinnerLayoutPaddingRect.right, mSpinnerLayoutPaddingRect.bottom);
        mSpinnerBarLayout.setBackgroundColor(mSpinnerBarBackground);

        mSpinnerBarLayout.setGravity(Gravity.CENTER_VERTICAL);
        mSpinnerBarLayout.setOrientation(LinearLayout.HORIZONTAL);

        //FooterView的根布局
        mFooterViewRoot = new FrameLayout(getContext());
        mFooterViewRoot.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        mSpinnerContainerLayout.addView(mSpinnerBarLayout);
        mSpinnerContainerLayout.addView(mFooterViewRoot);
        addView(mSpinnerContainerLayout);

        int size = mSpinnerUnitEntityList.size();
        for (int i = 0; i < size; i++) {
            addSpinnerUnit(context, i);
            if (i < size - 1) {
                addFilterBarLine(context);
            }
        }
    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        exchangeChildView();
        checkParentLayoutType();
        Log.d(TAG, "onFinishInflate");
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //固定wrap_content
        int size = MeasureSpec.getSize(heightMeasureSpec);
        int needHeightSpec = MeasureSpec.makeMeasureSpec(size, MeasureSpec.EXACTLY);
        setMeasuredDimension(widthMeasureSpec, needHeightSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

    }

    /**
     * 检测父容器是否是FrameLayout
     */
    private void checkParentLayoutType() {
        ViewParent parent = getParent();
        if (parent != null) {
            // todo
        }
    }

    /**
     * 把当前的子控件引用复制一份到筛选条容器内
     */
    private void exchangeChildView() {
        int childCount = getChildCount();
        if (childCount > 1 ) {
            List<View> childViewList = new LinkedList<>();
            for (int i = 1; i < childCount; i++) {
                childViewList.add(getChildAt(i));
            }
//            removeAllViews(); // 如果使用该方法，移除了子控件，xml的注册控件会出现NPE异常

            int size = childViewList.size();
            for (int j = 0; j < size; j++) {
                View childView = childViewList.get(j);
                removeView(childView);
                FooterMode footerMode;
                if (childView instanceof IFooterMode) {
                    IFooterMode mode = (IFooterMode) childView;
                    FooterMode filterMode = mode.getMFooterMode();
                    if (filterMode != null) {
                        footerMode = filterMode;
                    } else {
                        footerMode = mGlobalFooterMode;
                    }
                } else {
                    footerMode = mGlobalFooterMode;
                }
                addFooterView(j, childView, footerMode);
            }
            childViewList.clear();
        }
    }


    /**
     * 把FilterLayout控件的Padding转到FilterBarLayout的Padding上
     */
    private void switchPaddingToFilterBarLayout(){
        mSpinnerLayoutPaddingRect.left = getPaddingLeft();
        mSpinnerLayoutPaddingRect.top = getPaddingTop();
        mSpinnerLayoutPaddingRect.right = getPaddingRight();
        mSpinnerLayoutPaddingRect.bottom = getPaddingBottom();

        setPadding(0, 0, 0, 0);
    }

    /**
     * 添加筛选单元分割线
     *
     * @param context
     */
    private void addFilterBarLine(Context context) {
        View lineView = new View(context);
        lineView.setBackgroundColor(DEFAULT_LINE_COLOR);

        if(mLineScale <= 0){
            mLineScale = 0.1f;
        }else if(mLineScale > 1){
            mLineScale = 1f;
        }
        LinearLayout.LayoutParams lineLayoutParams = new LinearLayout.LayoutParams(2, (int)(mSpinnerBarHeight * mLineScale));

        lineLayoutParams.gravity = Gravity.CENTER_VERTICAL;
        lineView.setLayoutParams(lineLayoutParams);
        mSpinnerBarLayout.addView(lineView);
    }

    /**
     * 添加 筛选条单元
     *
     * @param context
     * @param i
     */
    private void addSpinnerUnit(Context context, int i) {
        LinearLayout spinnerUnitLayout = new LinearLayout(context);
        spinnerUnitLayout.setOrientation(LinearLayout.HORIZONTAL);
        spinnerUnitLayout.setGravity(mGravityMode);
        LinearLayout.LayoutParams unitLayoutParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1);
        spinnerUnitLayout.setLayoutParams(unitLayoutParams);

        final TextView titleView = new TextView(context);
        LinearLayout.LayoutParams titleViewLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        titleViewLayoutParams.gravity = Gravity.CENTER_VERTICAL;
        titleView.setLayoutParams(titleViewLayoutParams);
        titleView.setTextColor(mSpinnerTitleColor);
        titleView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mSpinnerTitleSize);
        titleView.setSingleLine(true);
        titleView.setEllipsize(TextUtils.TruncateAt.END);

        final SpinnerUnitEntity spinnerUnitEntity = mSpinnerUnitEntityList.get(i);
        if (spinnerUnitEntity == null) {
            return;
        }

        String filterTitle = spinnerUnitEntity.getUnitTitle();
        titleView.setText(filterTitle);
        spinnerUnitEntity.setTvUnit(titleView);

        final ImageView unitIconIv = new ImageView(context);
        LinearLayout.LayoutParams unitIconLayoutParams = new LinearLayout.LayoutParams(dip2px(8), dip2px(5));
        unitIconLayoutParams.gravity = Gravity.CENTER_VERTICAL;
        unitIconLayoutParams.leftMargin = dip2px(5);
        unitIconIv.setLayoutParams(unitIconLayoutParams);
        unitIconIv.setImageDrawable(mUnitIcon);
        spinnerUnitEntity.setImgUnitIcon(unitIconIv);

        spinnerUnitLayout.addView(titleView);
        spinnerUnitLayout.addView(unitIconIv);
        mSpinnerBarLayout.addView(spinnerUnitLayout);
        spinnerUnitEntity.setFilterUnitLayout(spinnerUnitLayout);

        final int index = i;
        spinnerUnitLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!spinnerUnitEntity.isExpanded()) {
                    mSelectedIndex = index;
                    if (mOnSpinnerLayoutClickListener != null) {
                        mOnSpinnerLayoutClickListener.onClick(SpinnerLayout.this, index, false);
                    }
                } else {
                    mSelectedIndex = -1;
                    if (mOnSpinnerLayoutClickListener != null) {
                        mOnSpinnerLayoutClickListener.onClick(SpinnerLayout.this, index, true);
                    }
                }

                if (!spinnerUnitEntity.isExpanded()) {
                    isShowing = true;
                    reactSpinnerUnitUIWhenOpen(spinnerUnitEntity);
                    setupSpinnerUnitClickableWhenOpen(index);
                    backgroundReactionWhenOpen(spinnerUnitEntity);
                } else {
                    isShowing = false;
                    reactSpinnerUnitUIWhenClose(spinnerUnitEntity);
                    restoreAllClickableWhenClose();
                    backgroundReactionWhenClose(spinnerUnitEntity);
                }

                if (!spinnerUnitEntity.isExpanded()) {
                    expandFooter(spinnerUnitEntity);
                } else {
                    collapseFooter(spinnerUnitEntity);
                }

                spinnerUnitEntity.setExpanded(!spinnerUnitEntity.isExpanded());
            }
        });
    }

    /**
     * 下拉时，配置筛选条单元
     *
     * @param spinnerUnitEntity
     */
    private void reactSpinnerUnitUIWhenOpen(SpinnerUnitEntity spinnerUnitEntity) {
        if (spinnerUnitEntity.getTvUnit() == null || spinnerUnitEntity.getImgUnitIcon() == null) {
            return;
        }

        spinnerUnitEntity.getTvUnit().setTextColor(mSpinnerTitleSelectedColor);
        spinnerUnitEntity.getImgUnitIcon().setImageDrawable(mUnitSelectedIcon);

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                backWhenTouchOutside();
            }
        });
    }

    private void backWhenTouchOutside() {
        if (mSelectedIndex >= 0 && mSelectedIndex < mSpinnerUnitEntityList.size()) {
            SpinnerUnitEntity spinnerUnitEntity = mSpinnerUnitEntityList.get(mSelectedIndex);
            if (spinnerUnitEntity != null) {
                if (spinnerUnitEntity.isCanceledOnTouchOutside()) {
                    back();
                }
            }
        }
    }

    /**
     * 关闭时，配置筛选条单元
     *
     * @param spinnerUnitEntity
     */
    private void reactSpinnerUnitUIWhenClose(SpinnerUnitEntity spinnerUnitEntity) {
        if (spinnerUnitEntity.getTvUnit() == null || spinnerUnitEntity.getImgUnitIcon() == null) {
            return;
        }

        spinnerUnitEntity.getTvUnit().setTextColor(mSpinnerTitleColor);
        spinnerUnitEntity.getImgUnitIcon().setImageDrawable(mUnitIcon);

        setClickable(false);
    }

    /**
     * 伸展下拉视图
     * 根据数值开启下拉动画，数值的计算逻辑不能在此方法里处理
     *
     * @param spinnerUnitEntity
     */
    private void expandFooter(final SpinnerUnitEntity spinnerUnitEntity) {
        final ViewGroup footerViewContainer = getFooterViewContainer(spinnerUnitEntity);
        if (footerViewContainer == null) {
            return;
        }

        int childCount = footerViewContainer.getChildCount();
        Log.d(TAG, "expandFooter: footerViewContainer childCount = " + childCount);
        if(childCount <= 0){
            return;
        }
        View footerView = footerViewContainer.getChildAt(0);
        ValueAnimator valueAnimator;
        if (spinnerUnitEntity.getFooterMode() == FooterMode.MODE_EXPAND) {
            int height;
            if (footerView.getTag(R.id.footer_view_height) instanceof Integer) {
                height = (int) footerView.getTag(R.id.footer_view_height);
            } else {
                height = ViewGroup.LayoutParams.WRAP_CONTENT;
            }
            Log.d(TAG, "MODE_EXPAND expandFooter: ORIGIN_HEIGHT = " + ORIGIN_HEIGHT + " height = " + height);
            valueAnimator = ValueAnimator.ofInt(ORIGIN_HEIGHT, height);
        } else {
            int height = footerView.getHeight();
            Log.d(TAG, "MODE_TRANSLATE expandFooter: height = " + height + " mSpinnerBarHeight = " + mSpinnerBarHeight);
            valueAnimator = ValueAnimator.ofInt(-height, ORIGIN_HEIGHT);
        }

        valueAnimator.setDuration(OPEN_FOOTER_ANIMATION_DURATION);
        valueAnimator.setInterpolator(new AccelerateInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                ViewGroup.LayoutParams lp = footerViewContainer.getLayoutParams();
                if (lp != null && lp instanceof FrameLayout.LayoutParams) {
                    FrameLayout.LayoutParams footerViewContainerLayoutParams = (FrameLayout.LayoutParams) lp;
                    int value = (int) valueAnimator.getAnimatedValue();
                    if (spinnerUnitEntity.getFooterMode() == FooterMode.MODE_EXPAND) {
                        footerViewContainerLayoutParams.height = value;
                        footerViewContainerLayoutParams.topMargin = ORIGIN_HEIGHT;
                    } else {
                        footerViewContainerLayoutParams.topMargin = value;
                    }
                    footerViewContainer.setLayoutParams(footerViewContainerLayoutParams);
                }
            }
        });

        AlphaAnimation alphaAnimation = new AlphaAnimation(0.3f, 1);
        alphaAnimation.setDuration(OPEN_FOOTER_ANIMATION_DURATION);

        valueAnimator.start();
        footerViewContainer.startAnimation(alphaAnimation);
    }

    /**
     * 折叠下拉视图
     *
     * @param spinnerUnitEntity
     */
    private void collapseFooter(final SpinnerUnitEntity spinnerUnitEntity) {
        final ViewGroup footerViewContainer = getFooterViewContainer(spinnerUnitEntity);
        if (footerViewContainer == null) {
            return;
        }
        int childCount = footerViewContainer.getChildCount();
        Log.d(TAG, "expandFooter: footerViewContainer childCount = " + childCount);
        if(childCount <= 0){
            return;
        }

        View footerView = footerViewContainer.getChildAt(0);
        ValueAnimator valueAnimator;
        if (spinnerUnitEntity.getFooterMode() == FooterMode.MODE_EXPAND) {
            int height;
            if (footerView.getTag(R.id.footer_view_height) instanceof Integer) {
                height = (int) footerView.getTag(R.id.footer_view_height);
            } else {
                height = ViewGroup.LayoutParams.WRAP_CONTENT;
            }
            valueAnimator = ValueAnimator.ofInt(height, ORIGIN_HEIGHT);
        } else {
            int height = footerViewContainer.getHeight();
            valueAnimator = ValueAnimator.ofInt(ORIGIN_HEIGHT, -height);
        }

        valueAnimator.setDuration(CLOSE_FOOTER_ANIMATION_DURATION);
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                ViewGroup.LayoutParams lp = footerViewContainer.getLayoutParams();
                if (lp != null && lp instanceof FrameLayout.LayoutParams) {
                    FrameLayout.LayoutParams footerViewContainerLayoutParams = (FrameLayout.LayoutParams) lp;
                    int value = (int) valueAnimator.getAnimatedValue();
                    if (spinnerUnitEntity.getFooterMode() == FooterMode.MODE_EXPAND) {
                        footerViewContainerLayoutParams.height = value;
                    } else {
                        footerViewContainerLayoutParams.topMargin = value;
                    }
                    footerViewContainer.setLayoutParams(footerViewContainerLayoutParams);
                }
            }
        });

        AlphaAnimation alphaAnimation = new AlphaAnimation(1, 0.3f);
        alphaAnimation.setDuration(CLOSE_FOOTER_ANIMATION_DURATION);

        valueAnimator.start();
        footerViewContainer.startAnimation(alphaAnimation);
    }

    /**
     * 获取下拉视图
     *
     * @param spinnerUnitEntity
     * @return
     */
    private ViewGroup getFooterViewContainer(SpinnerUnitEntity spinnerUnitEntity) {
        return spinnerUnitEntity.getFooterViewContainer();
    }

    /**
     * 设置一个筛选条单元可点击
     *
     * @param selectedIndex 可以点击的筛选序号
     */
    private void setupSpinnerUnitClickableWhenOpen(int selectedIndex) {
        int size = mSpinnerUnitEntityList.size();
        for (int i = 0; i < size; i++) {
            SpinnerUnitEntity spinnerUnitEntity = mSpinnerUnitEntityList.get(i);
            if (spinnerUnitEntity != null) {
                if (i == selectedIndex) {
                    spinnerUnitEntity.getFilterUnitLayout().setClickable(true);
                } else {
                    spinnerUnitEntity.getFilterUnitLayout().setClickable(false);
                }
            }
        }
    }

    /**
     * 打开时背景暗色
     */
    private void backgroundReactionWhenOpen(SpinnerUnitEntity spinnerUnitEntity) {
        if (spinnerUnitEntity.isScreenDimAvailable()) {
            setBackgroundColor(mSpinnerCoverColor);
            ViewGroup.LayoutParams lp = getLayoutParams();
            if (lp instanceof LayoutParams) {
                mOriginRootLayoutParams = (LayoutParams) lp;
                LayoutParams containerLayoutParams = new LayoutParams(mOriginRootLayoutParams.width, mOriginSpinnerContainerLayoutParams.height);
                containerLayoutParams.leftMargin = mOriginRootLayoutParams.leftMargin;
                containerLayoutParams.topMargin = mOriginRootLayoutParams.topMargin;
                containerLayoutParams.rightMargin = mOriginRootLayoutParams.rightMargin;
                containerLayoutParams.bottomMargin = mOriginRootLayoutParams.bottomMargin;
                mSpinnerContainerLayout.setLayoutParams(containerLayoutParams);

                LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
                layoutParams.leftMargin = 0;
                layoutParams.topMargin = 0;
                layoutParams.rightMargin = 0;
                layoutParams.bottomMargin = 0;
                setLayoutParams(layoutParams);
            } else {
                Log.e(TAG, "The parent layout of FilterBarLayout should be RelativeLayout");
            }
        }
    }

    /**
     * 恢复所有的筛选条可点击
     */
    private void restoreAllClickableWhenClose() {
        int size = mSpinnerUnitEntityList.size();
        for (int i = 0; i < size; i++) {
            SpinnerUnitEntity spinnerUnitEntity = mSpinnerUnitEntityList.get(i);
            if (spinnerUnitEntity != null && spinnerUnitEntity.getFilterUnitLayout() != null) {
                spinnerUnitEntity.getFilterUnitLayout().setClickable(true);
            }
        }
    }

    /**
     * 关闭时，恢复背景
     */
    private void backgroundReactionWhenClose(SpinnerUnitEntity spinnerUnitEntity) {
        if (spinnerUnitEntity.isScreenDimAvailable() && mOriginRootLayoutParams != null) {
            setLayoutParams(mOriginRootLayoutParams);
            mSpinnerContainerLayout.setLayoutParams(mOriginSpinnerContainerLayoutParams);
            setBackgroundColor(getContext().getResources().getColor(android.R.color.transparent));
        }
    }

    private int dip2px(float dipValue) {
        float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * 关闭是，筛选条单元响应动作
     *
     * @param spinnerUnitEntity
     */
    private void filterUnitReactionWhenClose(SpinnerUnitEntity spinnerUnitEntity) {
        if (spinnerUnitEntity == null) {
            return;
        }
        restoreAllClickableWhenClose();
        reactSpinnerUnitUIWhenClose(spinnerUnitEntity);
        spinnerUnitEntity.setExpanded(!spinnerUnitEntity.isExpanded());
    }

    /**
     * 添加筛选条元素标题
     *
     * @param unitTitle
     */
    public void addSpinnerTitle(String unitTitle) {
        if (unitTitle == null) {
            return;
        }

        int size = mSpinnerUnitEntityList.size();
        if (size > 0) {
            addFilterBarLine(getContext());
        }

        SpinnerUnitEntity spinnerUnitEntity = new SpinnerUnitEntity(unitTitle, false);
        mSpinnerUnitEntityList.add(spinnerUnitEntity);
        addSpinnerUnit(getContext(), size);
    }

    /**
     * 监听筛选项的点击事件
     *
     * @param onSpinnerLayoutClickListener
     */
    public void setOnSpinnerLayoutClickListener(OnSpinnerLayoutClickListener onSpinnerLayoutClickListener) {
        if (onSpinnerLayoutClickListener == null) {
            return;
        }
        mOnSpinnerLayoutClickListener = onSpinnerLayoutClickListener;
    }

    /**
     * 添加下拉视图
     *
     * @param index 序号
     * @param view  下拉视图
     */
    public void addFooterView(int index, View view) {
        addFooterView(index, view, mGlobalFooterMode);
    }

    /**
     * 添加下拉视图
     *
     * @param index      序号
     * @param childView 下拉视图
     * @param footerMode 视图展开方式
     */
    public void addFooterView(int index, View childView, FooterMode footerMode) {
        if (childView == null) {
            return;
        }

        int size = mSpinnerUnitEntityList.size();
        if (index >= size) {
            Log.d(TAG, "The index value should be less than the count of indicator unit");
            return;
        }
        SpinnerUnitEntity spinnerUnitEntity = mSpinnerUnitEntityList.get(index);
        if (spinnerUnitEntity == null) {
            return;
        }

        ViewGroup cacheFooterViewContainer = spinnerUnitEntity.getFooterViewContainer();
        if(cacheFooterViewContainer != null && childView == cacheFooterViewContainer){
            return;
        }

        ViewGroup.LayoutParams childViewLayoutParams = childView.getLayoutParams();
        if (childViewLayoutParams == null) {
            throw new RuntimeException("The LayoutParams of this footer view is null, You need to create a layout params");
        }

        spinnerUnitEntity.setCanceledOnTouchOutside(mGlobalIsTouchOutsideCanceled);

        FrameLayout footerViewContainer = new FrameLayout(getContext());
        footerViewContainer.addView(childView);

        FrameLayout.LayoutParams footerViewContainerLayoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        Object tag = childView.getTag(R.id.footer_view_height);
        if(tag instanceof Integer){
            spinnerUnitEntity.setFooterViewOriginHeight((Integer) tag);
        }else{
            spinnerUnitEntity.setFooterViewOriginHeight(childViewLayoutParams.height);
        }

        Integer originHeight = spinnerUnitEntity.getFooterViewOriginHeight();
        if (footerMode == FooterMode.MODE_EXPAND) {
            Log.d(TAG, "MODE_EXPAND addFooterView: originHeight = " + originHeight);
            childView.setTag(R.id.footer_view_height, originHeight);

            footerViewContainerLayoutParams.height = ORIGIN_HEIGHT;
            footerViewContainerLayoutParams.topMargin = 0;
        } else {
            //当FilterView高度是wrap_content或match_parent，同时下拉动画模式选择MODE_TRANSLATE时，强制把下拉模式改成MODE_EXPAND
            if(originHeight <= 0){
                footerMode = FooterMode.MODE_EXPAND;
                childView.setTag(R.id.footer_view_height, originHeight);

                footerViewContainerLayoutParams.height = ORIGIN_HEIGHT;
                footerViewContainerLayoutParams.topMargin = 0;
            }else{
                footerViewContainerLayoutParams.height = originHeight;
                footerViewContainerLayoutParams.topMargin = -footerViewContainerLayoutParams.height;
            }
        }
        footerViewContainer.setLayoutParams(footerViewContainerLayoutParams);
        spinnerUnitEntity.setFooterMode(footerMode);

        spinnerUnitEntity.setFooterViewContainer(footerViewContainer);
        mFooterViewRoot.addView(footerViewContainer);
    }

    /**
     * 是否已添加过footerView到筛选条
     *
     * @param footerView
     * @return
     */
    private boolean isFooterViewAddition(View footerView) {
        int childCount = mSpinnerContainerLayout.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = mSpinnerContainerLayout.getChildAt(i);
            if (childAt == footerView) {
                return true;
            }
        }
        return false;
    }

    /**
     * 检查该下拉视图是否在控件填充布局的时候添加过
     *
     * @param footerView
     * @return
     */
    private void checkAndRemoveDuplicateFooterView(View footerView) {
        int size = mSpinnerUnitEntityList.size();
        for (int i = 0; i < size; i++) {
            SpinnerUnitEntity unit = mSpinnerUnitEntityList.get(i);
            if (footerView == unit.getFooterViewContainer()) {
                unit.setFooterViewContainer(null);
                break;
            }
        }
    }

    /**
     * 设置点击筛选条，是否屏幕变暗
     *
     * @param index
     * @param isAvailable
     */
    public void setScreenDimAvailable(int index, boolean isAvailable) {
        SpinnerUnitEntity spinnerUnitEntity = mSpinnerUnitEntityList.get(index);
        if (spinnerUnitEntity == null) {
            return;
        }
        spinnerUnitEntity.setScreenDimAvailable(isAvailable);
    }

    public void back() {
        if (isShowing() && mSelectedIndex >= 0) {
            back(mSelectedIndex, null);
        }
    }

    /**
     * 还原筛选条
     *
     * @param index 筛选序号
     */
    public void back(int index) {
        back(index, null);
    }

    /**
     * 还原筛选条
     *
     * @param index 序号
     * @param title 筛选条选中的内容
     */
    public void back(int index, String title) {
        SpinnerUnitEntity spinnerUnitEntity = mSpinnerUnitEntityList.get(index);
        if (spinnerUnitEntity == null) {
            return;
        }
        collapseFooter(spinnerUnitEntity);
        filterUnitReactionWhenClose(spinnerUnitEntity);
        backgroundReactionWhenClose(spinnerUnitEntity);
        isShowing = false;

        TextView tvFilterTitle = spinnerUnitEntity.getTvUnit();
        if (tvFilterTitle != null && title != null) {
            tvFilterTitle.setText(title);
        }
    }

    public boolean isShowing() {
        return isShowing;
    }


    /**
     * 设置所有的下拉列表
     * @param enable
     */
    public void setCanceledOnTouchOutside(boolean enable) {
        mGlobalIsTouchOutsideCanceled = enable;
        for (SpinnerUnitEntity unit : mSpinnerUnitEntityList) {
            unit.setCanceledOnTouchOutside(enable);
        }
    }

    /**
     * 设置某个序号的下拉列表
     * @param index
     * @param enable
     */
    public void setCanceledOnTouchOutside(int index, boolean enable) {
        if (index >= 0 && index < mSpinnerUnitEntityList.size()) {
            SpinnerUnitEntity spinnerUnitEntity = mSpinnerUnitEntityList.get(index);
            spinnerUnitEntity.setCanceledOnTouchOutside(enable);
        }
    }

    public void setSpinnerIconResource(@DrawableRes int resId) {
        mUnitIcon = getResources().getDrawable(resId);
    }

    public void setSpinnerIconSelectedResource(@DrawableRes int resId) {
        mUnitSelectedIcon = getResources().getDrawable(resId);
    }

    public void setSpinnerIconDrawable(Drawable filterIcon) {
        mUnitIcon = filterIcon;
    }

    public void setFilterIconSelectedDrawable(Drawable filterSelectedIcon) {
        mUnitSelectedIcon = filterSelectedIcon;
    }

    public int getSpinnerBarHeight() {
        return mSpinnerBarHeight;
    }

    public List<SpinnerUnitEntity> getSpinnerUnitEntityList() {
        return mSpinnerUnitEntityList;
    }

    public FooterMode getGlobalFilterMode() {
        return mGlobalFooterMode;
    }

    /**
     * 筛选条单元实体类
     */
    public static class SpinnerUnitEntity {
        /**
         * 各个筛选单元的标题
         */
        private String unitTitle;
        /**
         * 打开下拉视图时，是否需要暗色屏幕显示
         */
        private boolean screenDimAvailable;
        /**
         * 下拉视图动画模式 1.平移  2.折叠
         */
        private FooterMode footerMode;

        /**
         * 筛选条单元视图
         */
        private View FilterUnitLayout;
        /**
         * 筛选标题控件
         */
        private TextView tvUnit;
        /**
         * 筛选单元 图片
         */
        private ImageView imgUnitIcon;
        /**
         * 是否处在下拉状态
         */
        private boolean isExpanded = false;

        /**
         * 触摸筛选条外部是，是否可以关闭筛选条的标示
         */
        private boolean isCanceledOnTouchOutside = false;

        private ViewGroup footerViewContainer;

        /**
         * footerView最初的高度
         */
        private Integer footerViewOriginHeight;

        public Integer getFooterViewOriginHeight() {
            return footerViewOriginHeight;
        }

        public void setFooterViewOriginHeight(int footerViewOriginHeight) {
            if (this.footerViewOriginHeight == null) {
                this.footerViewOriginHeight = footerViewOriginHeight;
            }
        }

        SpinnerUnitEntity(String unitTitle, boolean screenDimAvailable) {
            this.unitTitle = unitTitle;
            this.screenDimAvailable = screenDimAvailable;
        }

        public SpinnerUnitEntity(String unitTitle, boolean screenDimAvailable, FooterMode footerMode, View FilterUnitLayout, TextView tvUnit, ImageView imgUnitIcon, boolean isExpanded, boolean isCanceledOnTouchOutside, ViewGroup footerViewContainer) {
            this.unitTitle = unitTitle;
            this.screenDimAvailable = screenDimAvailable;
            this.footerMode = footerMode;
            this.FilterUnitLayout = FilterUnitLayout;
            this.tvUnit = tvUnit;
            this.imgUnitIcon = imgUnitIcon;
            this.isExpanded = isExpanded;
            this.isCanceledOnTouchOutside = isCanceledOnTouchOutside;
            this.footerViewContainer = footerViewContainer;
        }

        String getUnitTitle() {
            return unitTitle;
        }

        public void setUnitTitle(String unitTitle) {
            this.unitTitle = unitTitle;
        }

        boolean isScreenDimAvailable() {
            return screenDimAvailable;
        }

        void setScreenDimAvailable(boolean screenDimAvailable) {
            this.screenDimAvailable = screenDimAvailable;
        }

        FooterMode getFooterMode() {
            return footerMode;
        }

        void setFooterMode(FooterMode footerMode) {
            this.footerMode = footerMode;
        }

        View getFilterUnitLayout() {
            return FilterUnitLayout;
        }

        void setFilterUnitLayout(View filterUnitLayout) {
            this.FilterUnitLayout = filterUnitLayout;
        }

        TextView getTvUnit() {
            return tvUnit;
        }

        void setTvUnit(TextView tvUnit) {
            this.tvUnit = tvUnit;
        }

        ImageView getImgUnitIcon() {
            return imgUnitIcon;
        }

        void setImgUnitIcon(ImageView imgUnitIcon) {
            this.imgUnitIcon = imgUnitIcon;
        }

        boolean isExpanded() {
            return isExpanded;
        }

        void setExpanded(boolean expanded) {
            isExpanded = expanded;
        }

        public ViewGroup getFooterViewContainer() {
            return footerViewContainer;
        }

        public void setFooterViewContainer(ViewGroup footerViewContainer) {
            this.footerViewContainer = footerViewContainer;
        }

        boolean isCanceledOnTouchOutside() {
            return isCanceledOnTouchOutside;
        }

        void setCanceledOnTouchOutside(boolean canceledOnTouchOutside) {
            isCanceledOnTouchOutside = canceledOnTouchOutside;
        }
    }


    public interface OnSpinnerLayoutClickListener {

        void onClick(SpinnerLayout spinnerLayout, int index, boolean isFooterViewShowing);
    }
}

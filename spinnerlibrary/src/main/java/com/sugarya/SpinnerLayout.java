package com.sugarya;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.sugarya.animateoperator.AnimateOperatorManager;
import com.sugarya.animateoperator.operator.FlexibleOperator;
import com.sugarya.animateoperator.operator.TransitionOperator;
import com.sugarya.footer.base.BaseSpinnerFooter;
import com.sugarya.footer.interfaces.FooterMode;
import com.sugarya.footer.model.BaseFooterProperty;
import com.sugarya.footer.model.SpinnerLayoutProperty;
import com.sugarya.footer.model.SpinnerUnitEntity;
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
    private static final int FOOTER_ANIMATION_DURATION = 260;


    private static SparseArray<FooterMode> mFooterModeSparse = new SparseArray<>();
    private static SparseIntArray mSpinnerGravitySparse = new SparseIntArray();

    static {
        mFooterModeSparse.put(0, FooterMode.MODE_TRANSLATE);
        mFooterModeSparse.put(1, FooterMode.MODE_EXPAND);

        mSpinnerGravitySparse.put(0, Gravity.CENTER_HORIZONTAL);
        mSpinnerGravitySparse.put(1, Gravity.START);
        mSpinnerGravitySparse.put(2, Gravity.END);
    }

    /**
     * 筛选条单元集
     */
    private List<SpinnerUnitEntity> mSpinnerUnitEntityList = new LinkedList<>();

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
     * SpinnerLayout属性 容器
     */
    private SpinnerLayoutProperty mSpinnerLayoutProperty = new SpinnerLayoutProperty();

    /**
     * 是否正在展示下拉视图
     */
    private boolean mIsShowing = false;
    /**
     * 当前选中的
     */
    private SpinnerUnitEntity mSelectedSpinnerUnitEntity;

    /**
     * 下拉动画执行类
     */
    private FlexibleOperator mFlexibleOperator;
    private TransitionOperator mTransitionOperator;

    private long mLastSpinnerUnitLayoutClickTime = -1;


    public SpinnerLayout(Context context) {
        super(context);
        init(context);
        Log.d(TAG, "FilterLayout 1");
    }

    public SpinnerLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttributeSet(context, attrs);
        init(context);
        Log.d(TAG, "FilterLayout 2");
    }

    public SpinnerLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttributeSet(context, attrs);
        init(context);
        Log.d(TAG, "FilterLayout 3");
    }

    private void initAttributeSet(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SpinnerLayout);

        int barHeight = (int) typedArray.getDimension(R.styleable.SpinnerLayout_spinnerHeight, SpinnerConfig.DEFAULT_SPINNER_BAR_HEIGHT);
        mSpinnerLayoutProperty.setBarHeight(barHeight);

        int barBackground = typedArray.getColor(R.styleable.SpinnerLayout_spinnerBackground, SpinnerConfig.DEFAULT_SPINNER_BACKGROUND_COLOR);
        mSpinnerLayoutProperty.setBarBackground(barBackground);

        float textSize = typedArray.getDimension(R.styleable.SpinnerLayout_textSize, dip2px(SpinnerConfig.DEFAULT_SPINNER_TITLE_SIZE_DP));
        mSpinnerLayoutProperty.setTextSize(textSize);

        int textColor = typedArray.getColor(R.styleable.SpinnerLayout_textColor, SpinnerConfig.DEFAULT_SPINNER_BACK_SURFACE_COLOR);
        mSpinnerLayoutProperty.setTextColor(textColor);

        int textSelectedColor = typedArray.getColor(R.styleable.SpinnerLayout_textColorSelected, SpinnerConfig.DEFAULT_SPINNER_UNIT_TITLE_COLOR_SELECTED);
        mSpinnerLayoutProperty.setTextSelectedColor(textSelectedColor);

        int backSurfaceColor = typedArray.getColor(R.styleable.SpinnerLayout_backSurfaceColor, SpinnerConfig.DEFAULT_SPINNER_BACK_SURFACE_COLOR);
        mSpinnerLayoutProperty.setBackSurfaceColor(backSurfaceColor);

        Drawable unitIcon = typedArray.getDrawable(R.styleable.SpinnerLayout_icon) != null ?
                typedArray.getDrawable(R.styleable.SpinnerLayout_icon) : getResources().getDrawable(R.drawable.footer_triangle_down_black);
        mSpinnerLayoutProperty.setUnitIcon(unitIcon);

        Drawable unitIconSelected = typedArray.getDrawable(R.styleable.SpinnerLayout_iconSelected) != null ?
                typedArray.getDrawable(R.styleable.SpinnerLayout_iconSelected) : getResources().getDrawable(R.drawable.footer_triangle_up_blue);
        mSpinnerLayoutProperty.setUnitIconSelected(unitIconSelected);

        boolean isTouchOutsideCanceled = typedArray.getBoolean(R.styleable.SpinnerLayout_touchOutsideCanceled, true);
        mSpinnerLayoutProperty.setTouchOutsideCanceled(isTouchOutsideCanceled);

        float lineScale = typedArray.getFloat(R.styleable.SpinnerLayout_lineScale, DEFAULT_LINE_SCALE);
        mSpinnerLayoutProperty.setLineScale(lineScale);

        FooterMode footerMode = mFooterModeSparse.get(typedArray.getInt(R.styleable.SpinnerLayout_footerMode, 1));
        mSpinnerLayoutProperty.setFooterMode(footerMode);

        int spinnerGravity = mSpinnerGravitySparse.get(typedArray.getInt(R.styleable.SpinnerLayout_spinnerGravity, 0));
        mSpinnerLayoutProperty.setSpinnerGravity(spinnerGravity);

        typedArray.recycle();
    }

    private void init(Context context) {
        Rect switchPaddingRect = getAndSwitchPaddingToSpinnerBarLayout();
        mSpinnerContainerLayout = generateSpinnerContainerLayout(context, switchPaddingRect);
        addView(mSpinnerContainerLayout);
    }

    /**
     * 把SpinnerLayout控件的Padding转到SpinnerBarLayout的Padding上
     */
    private Rect getAndSwitchPaddingToSpinnerBarLayout() {
        Rect rect = new Rect();
        rect.left = getPaddingLeft();
        rect.top = getPaddingTop();
        rect.right = getPaddingRight();
        rect.bottom = getPaddingBottom();
        setPadding(0, 0, 0, 0);
        return rect;
    }

    /**
     * 设置下SpinnerContainerLayout的构成
     **/
    private LinearLayout generateSpinnerContainerLayout(Context context, Rect paddingRect) {
        mSpinnerBarLayout = new LinearLayout(context);
        LinearLayout.LayoutParams SpinnerBarLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, mSpinnerLayoutProperty.getBarHeight());
        mSpinnerBarLayout.setLayoutParams(SpinnerBarLayoutParams);
        mSpinnerBarLayout.setPadding(paddingRect.left, paddingRect.top, paddingRect.right, paddingRect.bottom);
        mSpinnerBarLayout.setBackgroundColor(mSpinnerLayoutProperty.getBarBackground());
        mSpinnerBarLayout.setGravity(Gravity.CENTER_VERTICAL);
        mSpinnerBarLayout.setOrientation(LinearLayout.HORIZONTAL);

        //FooterView的根布局
        mFooterViewRoot = new FrameLayout(getContext());
        mFooterViewRoot.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        mOriginSpinnerContainerLayoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        LinearLayout spinnerContainerLayout = new LinearLayout(getContext());
        spinnerContainerLayout.setOrientation(LinearLayout.VERTICAL);
        spinnerContainerLayout.addView(mSpinnerBarLayout);
        spinnerContainerLayout.addView(mFooterViewRoot);

        return spinnerContainerLayout;
    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        childrenViewChangeIntoFooterView();

        checkParentLayoutType();
        Log.d(TAG, "onFinishInflate");
    }

    private void childrenViewChangeIntoFooterView() {
        int childCount = getChildCount();
        if (childCount > 1) {
            List<View> childViewList = new LinkedList<>();
            for (int i = 1; i < childCount; i++) {
                childViewList.add(getChildAt(i));
            }

            int size = childViewList.size();
            for (int j = 0; j < size; j++) {
                View childView = childViewList.get(j);
                removeView(childView);
                if (childView instanceof BaseSpinnerFooter) {
                    BaseSpinnerFooter<BaseFooterProperty> spinnerFooter = (BaseSpinnerFooter<BaseFooterProperty>) childView;
                    addSpinnerFooter(spinnerFooter);
                    if (j < size - 1) {
                        View lineView = generateSpinnerUnitLine(getContext());
                        mSpinnerBarLayout.addView(lineView);
                    }
                }
            }
            childViewList.clear();
        }
    }

    private void addSpinnerFooter(BaseSpinnerFooter<BaseFooterProperty> baseSpinnerFooter) {
        SpinnerUnitEntity spinnerUnitEntity = new SpinnerUnitEntity(baseSpinnerFooter);
        mSpinnerUnitEntityList.add(spinnerUnitEntity);

        FrameLayout footerViewContainerLayout = generateFooterViewContainerLayout(baseSpinnerFooter);
        spinnerUnitEntity.setFooterViewContainer(footerViewContainerLayout);

        BaseFooterProperty baseFooterViewProperty = baseSpinnerFooter.getMFooterViewProperty();
        updateSpinnerLayoutPropertyFromFooterViewProperty(baseFooterViewProperty);
        spinnerUnitEntity.setMBaseFooterProperty(baseFooterViewProperty);

        String title = baseFooterViewProperty.getText();
        Log.d(TAG, "addSpinnerFooter: title = " + title);
        LinearLayout spinnerUnitLayout = generateSpinnerUnitLayout(getContext(), spinnerUnitEntity, title);
        initSpinnerUnitLayoutListener(spinnerUnitLayout, spinnerUnitEntity);

    }

    private void updateSpinnerLayoutPropertyFromFooterViewProperty(BaseFooterProperty baseFooterViewProperty) {
        SpinnerLayoutProperty property = mSpinnerLayoutProperty;

        float textSize = baseFooterViewProperty.getTextSize();
        if (textSize > 0) {
            property.setTextSize(textSize);
        }

        int textColor = baseFooterViewProperty.getTextColor();
        if (textColor > 0) {
            property.setTextColor(textColor);
        }

        int textSelectedColor = baseFooterViewProperty.getTextSelectedColor();
        if (textSelectedColor > 0) {
            property.setTextSelectedColor(textSelectedColor);
        }

        boolean backSurfaceAvailable = baseFooterViewProperty.getBackSurfaceAvailable();
        property.setBackSurfaceAvailable(backSurfaceAvailable);

        Drawable unitIconDrawable = baseFooterViewProperty.getUnitIcon();
        if (unitIconDrawable != null) {
            property.setUnitIcon(unitIconDrawable);
        }

        Drawable unitIconSelectedDrawable = baseFooterViewProperty.getUnitIconSelected();
        if (unitIconSelectedDrawable != null) {
            property.setUnitIconSelected(unitIconSelectedDrawable);
        }

        boolean touchOutsideCanceled = baseFooterViewProperty.isTouchOutsideCanceled();
        property.setTouchOutsideCanceled(touchOutsideCanceled);

        FooterMode footerMode = baseFooterViewProperty.getFooterMode();
        property.setFooterMode(footerMode);

    }

    private LinearLayout generateSpinnerUnitLayout(Context context, SpinnerUnitEntity spinnerUnitEntity, String spinnerUnitTitle) {

        final TextView titleView = new TextView(context);
        LinearLayout.LayoutParams titleViewLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        titleViewLayoutParams.gravity = Gravity.CENTER_VERTICAL;
        titleView.setLayoutParams(titleViewLayoutParams);
        int textColor = mSpinnerLayoutProperty.getTextColor();
        titleView.setTextColor(textColor);
        float textSize = mSpinnerLayoutProperty.getTextSize();
        titleView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        titleView.setSingleLine(true);
        titleView.setEllipsize(TextUtils.TruncateAt.END);
        titleView.setText(spinnerUnitTitle);

        final ImageView unitIconIv = new ImageView(context);
        LinearLayout.LayoutParams unitIconLayoutParams = new LinearLayout.LayoutParams(dip2px(8), dip2px(5));
        unitIconLayoutParams.gravity = Gravity.CENTER_VERTICAL;
        unitIconLayoutParams.leftMargin = dip2px(5);
        unitIconIv.setLayoutParams(unitIconLayoutParams);
        Drawable unitIcon = mSpinnerLayoutProperty.getUnitIcon();
        unitIconIv.setImageDrawable(unitIcon);

        LinearLayout spinnerUnitLayout = new LinearLayout(context);
        spinnerUnitLayout.setOrientation(LinearLayout.HORIZONTAL);
        int gravity = mSpinnerLayoutProperty.getSpinnerGravity();
        spinnerUnitLayout.setGravity(gravity);
        LinearLayout.LayoutParams unitLayoutParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1);
        spinnerUnitLayout.setLayoutParams(unitLayoutParams);
        spinnerUnitLayout.addView(titleView);
        spinnerUnitLayout.addView(unitIconIv);

        mSpinnerBarLayout.addView(spinnerUnitLayout);
        spinnerUnitEntity.setTvUnit(titleView);
        spinnerUnitEntity.setImgUnitIcon(unitIconIv);
        spinnerUnitEntity.setSpinnerUnitLayout(spinnerUnitLayout);

        return spinnerUnitLayout;
    }

    private void initSpinnerUnitLayoutListener(LinearLayout spinnerUnitLayout, final SpinnerUnitEntity currentSpinnerUnitEntity) {
        spinnerUnitLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                long currentTimeMillis = System.currentTimeMillis();
                if (mLastSpinnerUnitLayoutClickTime > 0 && currentTimeMillis - mLastSpinnerUnitLayoutClickTime <= FOOTER_ANIMATION_DURATION) {
                    return;
                }

                mSelectedSpinnerUnitEntity = currentSpinnerUnitEntity;

                if (!currentSpinnerUnitEntity.isExpanded()) {
                    expandFooter(currentSpinnerUnitEntity);
                } else {
                    collapseFooter(currentSpinnerUnitEntity);
                }

                if (!currentSpinnerUnitEntity.isExpanded()) {
                    mIsShowing = true;
                    reactionOfSpinnerUnitUIWhenToOpen(currentSpinnerUnitEntity);
                    setupSpinnerUnitClickableWhenToOpen(mSelectedSpinnerUnitEntity);
                    reactionOfBackgroundWhenToOpen();
                    setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (mSpinnerLayoutProperty.isTouchOutsideCanceled()) {
                                back();
                            }
                        }
                    });
                } else {
                    mIsShowing = false;
                    reactionOfSpinnerUnitWhenToClose(currentSpinnerUnitEntity);
                }
                currentSpinnerUnitEntity.setExpanded(!currentSpinnerUnitEntity.isExpanded());

                mLastSpinnerUnitLayoutClickTime = currentTimeMillis;
            }
        });
    }

    /**
     * 添加筛选单元分割线
     *
     * @param context
     */
    private View generateSpinnerUnitLine(Context context) {
        View lineView = new View(context);
        lineView.setBackgroundColor(SpinnerConfig.DEFAULT_LINE_COLOR);
        float lineScale = mSpinnerLayoutProperty.getLineScale();
        if (lineScale <= 0) {
            mSpinnerLayoutProperty.setLineScale(0.1f);
        } else if (lineScale > 1) {
            mSpinnerLayoutProperty.setLineScale(1f);
        }
        int lineHeight = (int) (mSpinnerLayoutProperty.getBarHeight() * lineScale);
        LinearLayout.LayoutParams lineLayoutParams = new LinearLayout.LayoutParams(2, lineHeight);
        lineLayoutParams.gravity = Gravity.CENTER_VERTICAL;
        lineView.setLayoutParams(lineLayoutParams);
        return lineView;
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
     * 下拉时，配置筛选条单元
     *
     * @param spinnerUnitEntity
     */
    private void reactionOfSpinnerUnitUIWhenToOpen(SpinnerUnitEntity spinnerUnitEntity) {
        if (spinnerUnitEntity.getTvUnit() == null || spinnerUnitEntity.getImgUnitIcon() == null) {
            return;
        }

        spinnerUnitEntity.getTvUnit().setTextColor(mSpinnerLayoutProperty.getTextSelectedColor());
        spinnerUnitEntity.getImgUnitIcon().setImageDrawable(mSpinnerLayoutProperty.getUnitIconSelected());
    }


    /**
     * 关闭时，配置筛选条单元
     *
     * @param spinnerUnitEntity
     */
    private void reactionOfSpinnerUnitUIWhenToClose(SpinnerUnitEntity spinnerUnitEntity) {
        if (spinnerUnitEntity.getTvUnit() == null || spinnerUnitEntity.getImgUnitIcon() == null) {
            return;
        }

        spinnerUnitEntity.getTvUnit().setTextColor(mSpinnerLayoutProperty.getTextColor());
        spinnerUnitEntity.getImgUnitIcon().setImageDrawable(mSpinnerLayoutProperty.getUnitIcon());

        setClickable(false);
    }


    private FrameLayout generateFooterViewContainerLayout(BaseSpinnerFooter spinnerFooter) {
        if (spinnerFooter == null) {
            throw new IllegalArgumentException("BaseSpinnerFooter is null");
        }
        ViewGroup.LayoutParams childViewLayoutParams = spinnerFooter.getLayoutParams();
        if (childViewLayoutParams == null) {
            throw new RuntimeException("The LayoutParams of this footer view is null, You need to create a layout params");
        }

        FrameLayout footerViewContainerLayout = new FrameLayout(getContext());
        footerViewContainerLayout.addView(spinnerFooter);
        FrameLayout.LayoutParams layoutParamsForStartingValue = generateFooterViewContainerLayoutParamsForStartingValue(spinnerFooter.getHeight());
        footerViewContainerLayout.setLayoutParams(layoutParamsForStartingValue);

        mFooterViewRoot.addView(footerViewContainerLayout);

        return footerViewContainerLayout;
    }

    private FrameLayout.LayoutParams generateFooterViewContainerLayoutParamsForStartingValue(int originHeight) {
        FrameLayout.LayoutParams footerViewContainerLayoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        FooterMode footerMode = mSpinnerLayoutProperty.getFooterMode();
        if (footerMode == FooterMode.MODE_EXPAND) {

            footerViewContainerLayoutParams.height = SpinnerConfig.ORIGIN_HEIGHT;
            footerViewContainerLayoutParams.topMargin = 0;
        } else if (footerMode == FooterMode.MODE_TRANSLATE) {
            /*
                当FilterView高度是wrap_content或match_parent，与MODE_TRANSLATE从图，
                因为无法确定MODE_TRANSLATE下FooterView的初始位置，故强制把下拉模式改成MODE_EXPAND
             */
            if (originHeight <= 0) {
                mSpinnerLayoutProperty.setFooterMode(FooterMode.MODE_EXPAND);
                footerViewContainerLayoutParams.height = SpinnerConfig.ORIGIN_HEIGHT;
                footerViewContainerLayoutParams.topMargin = 0;
            } else {
                footerViewContainerLayoutParams.height = originHeight;
                footerViewContainerLayoutParams.topMargin = -footerViewContainerLayoutParams.height;
            }
        }

        return footerViewContainerLayoutParams;

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
        if (childCount <= 0) {
            return;
        }

        View footerView = footerViewContainer.getChildAt(0);

        //重置height和topMargin属性的值
        ViewGroup.LayoutParams lp = footerViewContainer.getLayoutParams();
        if (lp != null && lp instanceof FrameLayout.LayoutParams) {
            FrameLayout.LayoutParams footerViewContainerLayoutParams = (FrameLayout.LayoutParams) lp;
            if(mSpinnerLayoutProperty.getFooterMode() == FooterMode.MODE_EXPAND){
                footerViewContainerLayoutParams.topMargin = 0;
            }else{
                footerViewContainerLayoutParams.height = footerView.getHeight();
            }
            footerViewContainer.setLayoutParams(footerViewContainerLayoutParams);
        }

        //比大小，选择高的值作为动画属性值
        float height;
        int computedEndingHeight = spinnerUnitEntity.getBaseSpinnerFooter().getComputedEndingHeight();
        float currentViewHeight = footerView.getHeight();
        if(currentViewHeight > computedEndingHeight){
            height = currentViewHeight;
        }else{
            height = computedEndingHeight;
        }


        if (mSpinnerLayoutProperty.getFooterMode() == FooterMode.MODE_EXPAND) {
            Log.d(TAG, "expandFooter: MODE_EXPAND height = " + height);
            mFlexibleOperator = AnimateOperatorManager.getInstance()
                    .flexibleBuilder(footerViewContainer)
                    .setDuration(FOOTER_ANIMATION_DURATION)
                    .setHeight(TypedValue.COMPLEX_UNIT_PX, height)
                    .create();
            mFlexibleOperator.expand();
        } else {
            Log.d(TAG, "expandFooter: TRANSLATION height = " + height);
            mTransitionOperator = AnimateOperatorManager.getInstance()
                    .transitionBuild(footerViewContainer)
                    .setDuration(FOOTER_ANIMATION_DURATION)
                    .setStartMarginTop(TypedValue.COMPLEX_UNIT_PX, -height)
                    .setEndMarginTop(SpinnerConfig.ORIGIN_HEIGHT)
                    .create();
            mTransitionOperator.expand();
        }

//        AlphaAnimation alphaAnimation = new AlphaAnimation(0.3f, 1);
//        alphaAnimation.setDuration(FOOTER_ANIMATION_DURATION);
//        footerViewContainer.startAnimation(alphaAnimation);
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
        if (mSpinnerLayoutProperty.getFooterMode() == FooterMode.MODE_EXPAND) {
            mFlexibleOperator.collapse();
        } else {
            mTransitionOperator.collapse();
        }
//
//        AlphaAnimation alphaAnimation = new AlphaAnimation(1, 0.3f);
//        alphaAnimation.setDuration(FOOTER_ANIMATION_DURATION);
//        footerViewContainer.startAnimation(alphaAnimation);
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
     */
    private void setupSpinnerUnitClickableWhenToOpen(SpinnerUnitEntity selectedSpinnerUnitEntity) {
        int size = mSpinnerUnitEntityList.size();
        for (int i = 0; i < size; i++) {
            SpinnerUnitEntity spinnerUnitEntity = mSpinnerUnitEntityList.get(i);
            if (spinnerUnitEntity != null) {
                if (spinnerUnitEntity == selectedSpinnerUnitEntity) {
                    spinnerUnitEntity.getSpinnerUnitLayout().setClickable(true);
                } else {
                    spinnerUnitEntity.getSpinnerUnitLayout().setClickable(false);
                }
            }
        }
    }

    /**
     * 打开时背景暗色
     */
    private void reactionOfBackgroundWhenToOpen() {
        if (mSpinnerLayoutProperty.getBackSurfaceAvailable()) {
            setBackgroundColor(mSpinnerLayoutProperty.getBackSurfaceColor());
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
    private void restoreAllClickableWhenToClose() {
        int size = mSpinnerUnitEntityList.size();
        for (int i = 0; i < size; i++) {
            SpinnerUnitEntity spinnerUnitEntity = mSpinnerUnitEntityList.get(i);
            if (spinnerUnitEntity != null && spinnerUnitEntity.getSpinnerUnitLayout() != null) {
                spinnerUnitEntity.getSpinnerUnitLayout().setClickable(true);
            }
        }
    }

    /**
     * 关闭时，恢复背景
     */
    private void reactionOfBackgroundWhenToClose() {
        if (mSpinnerLayoutProperty.getBackSurfaceAvailable() && mOriginRootLayoutParams != null) {
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
    private void reactionOfSpinnerUnitWhenToClose(SpinnerUnitEntity spinnerUnitEntity) {
        if (spinnerUnitEntity == null) {
            return;
        }

        reactionOfSpinnerUnitUIWhenToClose(spinnerUnitEntity);
        restoreAllClickableWhenToClose();
        reactionOfBackgroundWhenToClose();
    }


    /**
     * 代码添加FooterView
     */
    public void addFooterView() {

    }


//    /**
//     * 监听筛选项的点击事件
//     *
//     * @param onSpinnerLayoutClickListener
//     */
//    public void setOnSpinnerLayoutClickListener(OnSpinnerLayoutClickListener onSpinnerLayoutClickListener) {
//        if (onSpinnerLayoutClickListener == null) {
//            return;
//        }
//        mOnSpinnerLayoutClickListener = onSpinnerLayoutClickListener;
//    }


    /**
     * 还原筛选条
     *
     * @param selectedSpinnerUnitEntity
     * @param title
     */
    private void close(SpinnerUnitEntity selectedSpinnerUnitEntity, String title) {
        if (selectedSpinnerUnitEntity == null) {
            return;
        }
        mIsShowing = false;
        collapseFooter(selectedSpinnerUnitEntity);
        reactionOfSpinnerUnitWhenToClose(selectedSpinnerUnitEntity);
        selectedSpinnerUnitEntity.setExpanded(!selectedSpinnerUnitEntity.isExpanded());

        TextView tvFilterTitle = selectedSpinnerUnitEntity.getTvUnit();
        if (tvFilterTitle != null && title != null) {
            tvFilterTitle.setText(title);
        }
    }

    public void back() {
        if (isShowing() && mSelectedSpinnerUnitEntity != null) {
            close(mSelectedSpinnerUnitEntity, null);
        }
    }

    /**
     * 还原筛选条
     *
     * @param currentIndex 筛选序号
     */
    public void back(int currentIndex) {
        if (currentIndex >= 0 && currentIndex < mSpinnerUnitEntityList.size()) {
            SpinnerUnitEntity spinnerUnitEntity = mSpinnerUnitEntityList.get(currentIndex);
            close(spinnerUnitEntity, null);
        }
    }


    public void back(int currentIndex, String title) {
        if (currentIndex >= 0 && currentIndex < mSpinnerUnitEntityList.size()) {
            SpinnerUnitEntity spinnerUnitEntity = mSpinnerUnitEntityList.get(currentIndex);
            close(spinnerUnitEntity, title);
        }
    }

//    /**
//     * 设置点击筛选条，是否屏幕变暗
//     *
//     * @param index
//     * @param isAvailable
//     */
//    public void setScreenDimAvailable(int index, boolean isAvailable) {
//        SpinnerUnitEntity spinnerUnitEntity = mSpinnerUnitEntityList.get(index);
//        if (spinnerUnitEntity == null) {
//            return;
//        }
//        spinnerUnitEntity.setScreenDimAvailable(isAvailable);
//    }


    public boolean isShowing() {
        return mIsShowing;
    }


//    /**
//     * 设置所有的下拉列表
//     *
//     * @param enable
//     */
//    public void setCanceledOnTouchOutside(boolean enable) {
//        mGlobalIsTouchOutsideCanceled = enable;
//        for (SpinnerUnitEntity unit : mSpinnerUnitEntityList) {
//            unit.setCanceledOnTouchOutside(enable);
//        }
//    }

//    /**
//     * 设置某个序号的下拉列表
//     *
//     * @param index
//     * @param enable
//     */
//    public void setCanceledOnTouchOutside(int index, boolean enable) {
//        if (index >= 0 && index < mSpinnerUnitEntityList.size()) {
//            SpinnerUnitEntity spinnerUnitEntity = mSpinnerUnitEntityList.get(index);
//            spinnerUnitEntity.setCanceledOnTouchOutside(enable);
//        }
//    }


}

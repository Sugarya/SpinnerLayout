package com.sugarya;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
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
import com.sugarya.footer.model.BaseFooterPropertyWrapper;
import com.sugarya.footer.model.SpinnerLayoutProperty;
import com.sugarya.footer.model.SpinnerLayoutPropertyWrapper;
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

    private static final int FOOTER_ANIMATION_DURATION = 300;


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
    private RelativeLayout.LayoutParams mOriginRootLayoutParams;
    /**
     * mSpinnerContainerLayout 对应的原始布局参数
     */
    private LayoutParams mOriginSpinnerContainerLayoutParams;

    /**
     * SpinnerLayout属性 容器
     */
    private SpinnerLayoutProperty mSpinnerLayoutProperty;
    private SpinnerLayoutPropertyWrapper mSpinnerLayoutPropertyWrapper;

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
        // todo
        initSpinnerLayoutProperty();
        init(context);
        Log.d(TAG, "FilterLayout 1");
    }

    public SpinnerLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mSpinnerLayoutProperty = parseAttributeSet(context, attrs);
        mSpinnerLayoutPropertyWrapper = new SpinnerLayoutPropertyWrapper(getContext(), mSpinnerLayoutProperty);
        init(context);
        Log.d(TAG, "FilterLayout 2");
    }

    public SpinnerLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mSpinnerLayoutProperty = parseAttributeSet(context, attrs);
        mSpinnerLayoutPropertyWrapper = new SpinnerLayoutPropertyWrapper(getContext(), mSpinnerLayoutProperty);
        init(context);
        Log.d(TAG, "FilterLayout 3");
    }

    private void initSpinnerLayoutProperty() {

    }

    private SpinnerLayoutProperty parseAttributeSet(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SpinnerLayout);

        Float barHeight;
        float barHeightValue = typedArray.getDimension(R.styleable.SpinnerLayout_spinnerHeight, -1f);
        if(barHeightValue == -1){
            throw new IllegalArgumentException("SpinnerLayout need a bar height");
        }else{
            barHeight = barHeightValue;
        }

        Integer barBackground;
        int barBackgroundValue = typedArray.getColor(R.styleable.SpinnerLayout_spinnerBackground, -1);
        if(barBackgroundValue == -1){
            barBackground = null;
        }else{
            barBackground = barBackgroundValue;
        }

        Float textSize;
        float textSizeValue = typedArray.getDimension(R.styleable.SpinnerLayout_textSize, -1f);
        if(textSizeValue == -1){
            textSize = null;
        }else{
            textSize = textSizeValue;
        }

        Integer textColor;
        int textColorValue = typedArray.getColor(R.styleable.SpinnerLayout_textColor, -1);
        if(textColorValue == -1){
            textColor = null;
        }else{
            textColor = textColorValue;
        }

        Integer textSelectedColor;
        int textSelectedColorValue = typedArray.getColor(R.styleable.SpinnerLayout_textColorSelected, -1);
        if(textSelectedColorValue == -1){
            textSelectedColor = null;
        }else{
            textSelectedColor = textSelectedColorValue;
        }

        Integer backSurfaceColor;
        int backSurfaceColorValue = typedArray.getColor(R.styleable.SpinnerLayout_backSurfaceColor, -1);
        if(backSurfaceColorValue == -1){
            backSurfaceColor = null;
        }else{
            backSurfaceColor = backSurfaceColorValue;
        }

        Drawable unitIcon = typedArray.getDrawable(R.styleable.SpinnerLayout_icon);

        Drawable unitIconSelected = typedArray.getDrawable(R.styleable.SpinnerLayout_iconSelected);

        Boolean backSurfaceAvailable;
        boolean testAvailableValue1 = typedArray.getBoolean(R.styleable.SpinnerLayout_backSurfaceAvailable, false);
        boolean testAvailableValue2 = typedArray.getBoolean(R.styleable.SpinnerLayout_backSurfaceAvailable, true);
        if(testAvailableValue1 == testAvailableValue2){
            backSurfaceAvailable = testAvailableValue1;
        }else{
            backSurfaceAvailable = null;
        }

        Boolean isTouchOutsideCanceled;
        boolean testTouchOutsideCanceledValue1 = typedArray.getBoolean(R.styleable.SpinnerLayout_touchOutsideCanceled, false);
        boolean testTouchOutsideCanceledValue2 = typedArray.getBoolean(R.styleable.SpinnerLayout_touchOutsideCanceled, true);
        if(testTouchOutsideCanceledValue1 == testTouchOutsideCanceledValue2){
            isTouchOutsideCanceled = testTouchOutsideCanceledValue1;
        }else{
            isTouchOutsideCanceled = null;
        }

        Float lineScale;
        float lineScaleValue = typedArray.getFloat(R.styleable.SpinnerLayout_lineScale, -1);
        if(lineScaleValue == -1){
             lineScale = null;
        }else{
            if (lineScaleValue <= 0) {
                lineScale = 0.1f;
            } else if (lineScaleValue > 1) {
                lineScale = 1f;
            }else {
                lineScale = lineScaleValue;
            }
        }

        FooterMode footerMode;
        int ordinal = typedArray.getInt(R.styleable.SpinnerLayout_footerMode, -1);
        if(ordinal == -1){
            footerMode = null;
        }else{
            footerMode = mFooterModeSparse.get(ordinal);
        }

        Integer spinnerGravity;
        int ordinalGravity = typedArray.getInt(R.styleable.SpinnerLayout_spinnerGravity, -1);
        if(ordinalGravity == -1){
            spinnerGravity = null;
        }else{
            spinnerGravity = mSpinnerGravitySparse.get(ordinalGravity);
        }

        typedArray.recycle();


        return new SpinnerLayoutProperty(
                barHeight,
                textSize,
                textColor,
                textSelectedColor,
                backSurfaceColor,
                unitIcon,
                unitIconSelected,
                backSurfaceAvailable,
                isTouchOutsideCanceled,
                lineScale,
                barBackground,
                footerMode,
                spinnerGravity
        );
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
        LinearLayout.LayoutParams SpinnerBarLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) mSpinnerLayoutPropertyWrapper.getBarHeight());
        mSpinnerBarLayout.setLayoutParams(SpinnerBarLayoutParams);
        mSpinnerBarLayout.setPadding(paddingRect.left, paddingRect.top, paddingRect.right, paddingRect.bottom);
        mSpinnerBarLayout.setBackgroundColor(mSpinnerLayoutPropertyWrapper.getBarBackground());
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
                    if (j >= 1) {
                        View lineView = generateSpinnerUnitLine(getContext());
                        mSpinnerBarLayout.addView(lineView);
                    }
                    @SuppressWarnings("unchecked")
                    BaseSpinnerFooter<BaseFooterProperty> spinnerFooter = (BaseSpinnerFooter<BaseFooterProperty>) childView;
                    addSpinnerFooter(spinnerFooter);
                }
            }
            childViewList.clear();
        }
    }

    /**
     * xml， 添加FooterView
     *
     * @param baseSpinnerFooter
     */
    private void addSpinnerFooter(BaseSpinnerFooter<BaseFooterProperty> baseSpinnerFooter) {
        SpinnerUnitEntity spinnerUnitEntity = new SpinnerUnitEntity(baseSpinnerFooter);
        mSpinnerUnitEntityList.add(spinnerUnitEntity);

        BaseFooterPropertyWrapper baseFooterPropertyWrapper = new BaseFooterPropertyWrapper(getContext(), mSpinnerLayoutProperty, baseSpinnerFooter.getBaseFooterViewProperty());
        spinnerUnitEntity.setBaseFooterPropertyWrapper(baseFooterPropertyWrapper);


        FrameLayout footerViewContainerLayout = generateFooterViewContainerLayout(baseSpinnerFooter, baseFooterPropertyWrapper);
        spinnerUnitEntity.setFooterViewContainer(footerViewContainerLayout);

        String title = baseFooterPropertyWrapper.getText();
        LinearLayout spinnerUnitLayout = generateSpinnerUnitLayout(getContext(), spinnerUnitEntity, title);
        initSpinnerUnitLayoutListener(spinnerUnitLayout, spinnerUnitEntity);
    }


    /**
     * 生成SpinnerUnitLayout
     *
     * @param context
     * @param spinnerUnitEntity
     * @param spinnerUnitTitle
     * @return
     */
    private LinearLayout generateSpinnerUnitLayout(Context context, SpinnerUnitEntity spinnerUnitEntity, String spinnerUnitTitle) {

        BaseFooterPropertyWrapper baseFooterPropertyWrapper = spinnerUnitEntity.getBaseFooterPropertyWrapper();

        final TextView titleView = new TextView(context);
        LinearLayout.LayoutParams titleViewLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        titleViewLayoutParams.gravity = Gravity.CENTER_VERTICAL;
        titleView.setLayoutParams(titleViewLayoutParams);
        int textColor = baseFooterPropertyWrapper.getTextColor();
        titleView.setTextColor(textColor);
        float textSize = baseFooterPropertyWrapper.getTextSize();
        titleView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        titleView.setSingleLine(true);
        titleView.setEllipsize(TextUtils.TruncateAt.END);
        titleView.setText(spinnerUnitTitle);

        final ImageView unitIconIv = new ImageView(context);
        LinearLayout.LayoutParams unitIconLayoutParams = new LinearLayout.LayoutParams(dip2px(8), dip2px(5));
        unitIconLayoutParams.gravity = Gravity.CENTER_VERTICAL;
        unitIconLayoutParams.leftMargin = dip2px(5);
        unitIconIv.setLayoutParams(unitIconLayoutParams);
        Drawable unitIcon = baseFooterPropertyWrapper.getUnitIcon();
        unitIconIv.setImageDrawable(unitIcon);

        LinearLayout spinnerUnitLayout = new LinearLayout(context);
        spinnerUnitLayout.setOrientation(LinearLayout.HORIZONTAL);
        int gravity = mSpinnerLayoutPropertyWrapper.getSpinnerGravity();
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

    /**
     * 设置SpinnerUnitLayout点击监听
     *
     * @param spinnerUnitLayout
     * @param currentSpinnerUnitEntity
     */
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

                    Boolean backSurfaceAvailable = currentSpinnerUnitEntity.getBaseFooterPropertyWrapper().getBackSurfaceAvailable();
                    reactionOfBackgroundWhenToOpen(backSurfaceAvailable);

                    setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (currentSpinnerUnitEntity.getBaseFooterPropertyWrapper() != null
                                    && currentSpinnerUnitEntity.getBaseFooterPropertyWrapper().isTouchOutsideCanceled()) {
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
        float lineScale = mSpinnerLayoutPropertyWrapper.getLineScale();
        int lineHeight = (int) (mSpinnerLayoutPropertyWrapper.getBarHeight() * lineScale);
        LinearLayout.LayoutParams lineLayoutParams = new LinearLayout.LayoutParams(2, lineHeight);
        lineLayoutParams.gravity = Gravity.CENTER_VERTICAL;
        lineView.setLayoutParams(lineLayoutParams);
        return lineView;
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        Log.d(TAG, "onWindowFocusChanged: ");
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

        BaseFooterPropertyWrapper baseFooterPropertyWrapper = spinnerUnitEntity.getBaseFooterPropertyWrapper();
        spinnerUnitEntity.getTvUnit().setTextColor(baseFooterPropertyWrapper.getTextSelectedColor());
        spinnerUnitEntity.getImgUnitIcon().setImageDrawable(baseFooterPropertyWrapper.getUnitIconSelected());
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

        BaseFooterPropertyWrapper baseFooterPropertyWrapper = spinnerUnitEntity.getBaseFooterPropertyWrapper();

        spinnerUnitEntity.getTvUnit().setTextColor(baseFooterPropertyWrapper.getTextColor());
        spinnerUnitEntity.getImgUnitIcon().setImageDrawable(baseFooterPropertyWrapper.getUnitIcon());

        setClickable(false);
    }

    /**
     * 生成FooterView的包裹视图，用作下拉动画的目标
     *
     * @param spinnerFooter
     * @return
     */
    private FrameLayout generateFooterViewContainerLayout(BaseSpinnerFooter spinnerFooter, BaseFooterPropertyWrapper baseFooterPropertyWrapper) {
        if (spinnerFooter == null) {
            throw new IllegalArgumentException("BaseSpinnerFooter is null");
        }
        ViewGroup.LayoutParams childViewLayoutParams = spinnerFooter.getLayoutParams();
        if (childViewLayoutParams == null) {
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            spinnerFooter.setLayoutParams(layoutParams);
        }

        FrameLayout footerViewContainerLayout = new FrameLayout(getContext());
        footerViewContainerLayout.addView(spinnerFooter);
        FrameLayout.LayoutParams layoutParamsForStartingValue = generateFooterViewContainerLayoutParamsForStartingValue(spinnerFooter.getHeight(), baseFooterPropertyWrapper);
        footerViewContainerLayout.setLayoutParams(layoutParamsForStartingValue);

        mFooterViewRoot.addView(footerViewContainerLayout);

        return footerViewContainerLayout;
    }

    private FrameLayout.LayoutParams generateFooterViewContainerLayoutParamsForStartingValue(int originHeight, BaseFooterPropertyWrapper baseFooterPropertyWrapper) {
        FrameLayout.LayoutParams footerViewContainerLayoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        FooterMode footerMode = baseFooterPropertyWrapper.getFooterMode();
        if (footerMode == FooterMode.MODE_EXPAND) {

            footerViewContainerLayoutParams.height = SpinnerConfig.ORIGIN_HEIGHT;
            footerViewContainerLayoutParams.topMargin = 0;
        } else if (footerMode == FooterMode.MODE_TRANSLATE) {
            /*
                当FilterView高度是wrap_content或match_parent，与MODE_TRANSLATE从图，
                因为无法确定MODE_TRANSLATE下FooterView的初始位置，故强制把下拉模式改成MODE_EXPAND
             */
            if (originHeight <= 0) {
                baseFooterPropertyWrapper.setFooterMode(FooterMode.MODE_EXPAND);
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
        BaseFooterPropertyWrapper baseFooterPropertyWrapper = spinnerUnitEntity.getBaseFooterPropertyWrapper();
        TextView tvUnit = spinnerUnitEntity.getTvUnit();
        if(tvUnit != null && baseFooterPropertyWrapper != null){
            tvUnit.setText(baseFooterPropertyWrapper.getText());
        }

        //重置height和topMargin属性的值
        ViewGroup.LayoutParams lp = footerViewContainer.getLayoutParams();
        if (lp != null && lp instanceof FrameLayout.LayoutParams) {
            FrameLayout.LayoutParams footerViewContainerLayoutParams = (FrameLayout.LayoutParams) lp;
            if (baseFooterPropertyWrapper.getFooterMode() == FooterMode.MODE_EXPAND) {
                footerViewContainerLayoutParams.topMargin = 0;
            } else {
                footerViewContainerLayoutParams.height = footerView.getHeight();
            }
            footerViewContainer.setLayoutParams(footerViewContainerLayoutParams);
        }

        //比大小，选择大的值作为动画属性值
        float height;
        int computedEndingHeight = spinnerUnitEntity.getBaseSpinnerFooter().getComputedEndingHeight();
        float currentViewHeight = footerView.getHeight();
        if (currentViewHeight > computedEndingHeight) {
            height = currentViewHeight;
        } else {
            height = computedEndingHeight;
        }


        if (baseFooterPropertyWrapper.getFooterMode() == FooterMode.MODE_EXPAND) {
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
        TextView tvUnit = spinnerUnitEntity.getTvUnit();
        if(tvUnit != null){
            BaseFooterProperty baseFooterViewProperty = spinnerUnitEntity.getBaseSpinnerFooter().getBaseFooterViewProperty();
            String selectedOptionText = baseFooterViewProperty.getSelectedOptionText();
            if(!TextUtils.isEmpty(selectedOptionText)){
                tvUnit.setText(selectedOptionText);
            }
        }

        BaseFooterPropertyWrapper baseFooterPropertyWrapper = spinnerUnitEntity.getBaseFooterPropertyWrapper();
        if (baseFooterPropertyWrapper.getFooterMode() == FooterMode.MODE_EXPAND) {
            mFlexibleOperator.collapse();
        } else {
            mTransitionOperator.collapse();
        }
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
    private void reactionOfBackgroundWhenToOpen(boolean backSurfaceAvailable) {
        if (backSurfaceAvailable) {
            if (getLayoutParams() instanceof RelativeLayout.LayoutParams) {
                mOriginRootLayoutParams = (RelativeLayout.LayoutParams) getLayoutParams();

                Rect rect = new Rect();
                getWindowVisibleDisplayFrame(rect);
                Log.d(TAG, "reactionOfBackgroundWhenToOpen: rect.top = " + rect.top);

                int[] locations = new int[]{0, 0};
                getLocationInWindow(locations);
                int x = locations[0];
                int y = locations[1];
                Log.d(TAG, "reactionOfBackgroundWhenToOpen: x = " + x + " y = " + y);
                LayoutParams containerLayoutParams = new LayoutParams(mOriginRootLayoutParams.width, mOriginSpinnerContainerLayoutParams.height);
                containerLayoutParams.leftMargin = x;
                //通过手动赋值，确定topMargin的值
//                containerLayoutParams.topMargin = (int) (y - rect.top - TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, SpinnerConfig.getInstance().getWindowPaddingTop(), getResources().getDisplayMetrics()));
                containerLayoutParams.topMargin = y - rect.top;
                if(mOriginRootLayoutParams.width <= 0){
                    containerLayoutParams.rightMargin = 0;
                }else{
                    int widthPixels = getResources().getDisplayMetrics().widthPixels;
                    containerLayoutParams.rightMargin = widthPixels - (x + getWidth());
                }
                containerLayoutParams.bottomMargin = mOriginRootLayoutParams.bottomMargin;

                mSpinnerContainerLayout.setLayoutParams(containerLayoutParams);
                Log.d(TAG, "reactionOfBackgroundWhenToOpen: topMargin = " + containerLayoutParams.topMargin + " leftMargin = " + x + " rightMargin = " + containerLayoutParams.rightMargin);

                LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
                layoutParams.leftMargin = 0;
                layoutParams.topMargin = 0;
                layoutParams.rightMargin = 0;
                layoutParams.bottomMargin = 0;
                setLayoutParams(layoutParams);

                ObjectAnimator colorAnimator = ObjectAnimator.ofInt(this, "BackgroundColor", Color.TRANSPARENT, mSpinnerLayoutPropertyWrapper.getBackSurfaceColor());
                colorAnimator.setEvaluator(new ArgbEvaluator());
                colorAnimator.setDuration(FOOTER_ANIMATION_DURATION);
                colorAnimator.start();
//                setBackgroundColor(mSpinnerLayoutPropertyWrapper.getBackSurfaceColor());
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
    private void reactionOfBackgroundWhenToClose(boolean backSurfaceAvailable) {
        if (backSurfaceAvailable && mOriginRootLayoutParams != null) {
            //            setBackgroundColor(getContext().getResources().getColor(android.R.color.transparent));

            ObjectAnimator colorAnimator = ObjectAnimator.ofInt(this, "BackgroundColor", mSpinnerLayoutPropertyWrapper.getBackSurfaceColor(), Color.TRANSPARENT);
            colorAnimator.setEvaluator(new ArgbEvaluator());
            colorAnimator.setDuration(FOOTER_ANIMATION_DURATION);
            colorAnimator.start();

            setLayoutParams(mOriginRootLayoutParams);
            mSpinnerContainerLayout.setLayoutParams(mOriginSpinnerContainerLayoutParams);
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

        BaseFooterPropertyWrapper baseFooterPropertyWrapper = spinnerUnitEntity.getBaseFooterPropertyWrapper();
        Boolean backSurfaceAvailable = baseFooterPropertyWrapper.getBackSurfaceAvailable();
        reactionOfBackgroundWhenToClose(backSurfaceAvailable);
    }


    /**
     * 还原筛选条
     *
     * @param selectedSpinnerUnitEntity
     * @param option
     */
    private void close(SpinnerUnitEntity selectedSpinnerUnitEntity, String option) {
        if (selectedSpinnerUnitEntity == null) {
            return;
        }
        mIsShowing = false;
        if (!TextUtils.isEmpty(option)) {
            BaseFooterPropertyWrapper baseFooterPropertyWrapper = selectedSpinnerUnitEntity.getBaseFooterPropertyWrapper();
            if(baseFooterPropertyWrapper != null){
                baseFooterPropertyWrapper.setSelectedOptionText(option);
            }
        }

        collapseFooter(selectedSpinnerUnitEntity);
        reactionOfSpinnerUnitWhenToClose(selectedSpinnerUnitEntity);
        selectedSpinnerUnitEntity.setExpanded(!selectedSpinnerUnitEntity.isExpanded());
    }

    public void back() {
        if (isShowing() && mSelectedSpinnerUnitEntity != null) {
            close(mSelectedSpinnerUnitEntity, "");
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
            close(spinnerUnitEntity, "");
        }
    }


    public void back(int currentIndex, String title) {
        if (currentIndex >= 0 && currentIndex < mSpinnerUnitEntityList.size()) {
            SpinnerUnitEntity spinnerUnitEntity = mSpinnerUnitEntityList.get(currentIndex);
            close(spinnerUnitEntity, title);
        }
    }

    /**
     * 代码添加FooterView
     */
    public void addFooterView(BaseSpinnerFooter<BaseFooterProperty> spinnerFooter) {
        int size = mSpinnerUnitEntityList.size();
        if (size >= 1) {
            View lineView = generateSpinnerUnitLine(getContext());
            mSpinnerBarLayout.addView(lineView);
        }

        addSpinnerFooter(spinnerFooter);
    }


    public boolean isShowing() {
        return mIsShowing;
    }


}

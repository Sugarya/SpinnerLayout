package com.sugarya.footer.model;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sugarya.footer.interfaces.FooterMode;

/**
 * 筛选条单元实体类
 */
public class SpinnerUnitEntity {
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

    public SpinnerUnitEntity(String unitTitle, boolean screenDimAvailable) {
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

    public String getUnitTitle() {
        return unitTitle;
    }

    public void setUnitTitle(String unitTitle) {
        this.unitTitle = unitTitle;
    }

    public boolean isScreenDimAvailable() {
        return screenDimAvailable;
    }

    public void setScreenDimAvailable(boolean screenDimAvailable) {
        this.screenDimAvailable = screenDimAvailable;
    }

    public FooterMode getFooterMode() {
        return footerMode;
    }

    public void setFooterMode(FooterMode footerMode) {
        this.footerMode = footerMode;
    }

    public View getFilterUnitLayout() {
        return FilterUnitLayout;
    }

    public void setFilterUnitLayout(View filterUnitLayout) {
        this.FilterUnitLayout = filterUnitLayout;
    }

    public TextView getTvUnit() {
        return tvUnit;
    }

    public void setTvUnit(TextView tvUnit) {
        this.tvUnit = tvUnit;
    }

    public ImageView getImgUnitIcon() {
        return imgUnitIcon;
    }

    public void setImgUnitIcon(ImageView imgUnitIcon) {
        this.imgUnitIcon = imgUnitIcon;
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
    }

    public ViewGroup getFooterViewContainer() {
        return footerViewContainer;
    }

    public void setFooterViewContainer(ViewGroup footerViewContainer) {
        this.footerViewContainer = footerViewContainer;
    }

    public boolean isCanceledOnTouchOutside() {
        return isCanceledOnTouchOutside;
    }

    public void setCanceledOnTouchOutside(boolean canceledOnTouchOutside) {
        isCanceledOnTouchOutside = canceledOnTouchOutside;
    }
}

package com.sugarya.footer.interfaces

/**
 * Adapter里item点击响应
 */
interface OnFooterItemContainerClickListener {

    fun onItemClick(list: MutableList<IFooterItem>, position: Int)
}
package com.sugarya.footer.interfaces

/**
 * Adapter里item点击响应
 */
interface OnItemContainerClickListener {

    fun onItemClick(list: MutableList<IFooterItem>, position: Int)
}
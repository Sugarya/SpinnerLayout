package com.sugarya.model

import com.sugarya.footer.interfaces.IFooterItem

data class StatusModel(val statusId: String, val statusName: String): IFooterItem{
    override val titleName: String
        get() = statusName
    override val titleId: String
        get() = statusId

    override var isSelected: Boolean = false
}

data class LabelModel(val labelId: String, val labelName: String): IFooterItem{
    override val titleName: String
        get() = labelName
    override val titleId: String
        get() = labelId

    override var isSelected: Boolean = false
}
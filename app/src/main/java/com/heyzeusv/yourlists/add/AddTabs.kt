package com.heyzeusv.yourlists.add

import androidx.annotation.StringRes
import com.heyzeusv.yourlists.R

enum class AddTabs(@StringRes val nameId: Int) {
    ITEM(R.string.as_item_tab),
    LIST(R.string.as_list_tab),
}
package com.bteamcoding.bubbletranslation.feature_bubble_translation.presentation.ccp

import androidx.compose.ui.graphics.Color

data class PickerCustomization(
    var itemPadding: Int = 10,
    var dividerColor: Color = Color.LightGray,
    var headerTitle: String = "Chọn ngôn ngữ",
    var searchHint: String = "Tìm kiếm ngôn ngữ",
    var showSearchClearIcon: Boolean = true,
    var showCountryCode: Boolean = false,
    var showFlag: Boolean = true,
    var showCountryIso: Boolean = true,

    )

data class ViewCustomization(
    var showFlag: Boolean = true,
    var showCountryIso: Boolean = false,
    var showCountryName: Boolean = true,
    var showCountryCode: Boolean = false,
    var showArrow: Boolean = true,
    var clipToFull: Boolean = false,
)

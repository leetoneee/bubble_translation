package com.bteamcoding.bubbletranslation.core.components

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Arrangement.Absolute.SpaceEvenly
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material.icons.filled.DoubleArrow
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bteamcoding.bubbletranslation.R
import com.bteamcoding.bubbletranslation.core.utils.LanguageManager
import com.bteamcoding.bubbletranslation.feature_bubble_translation.presentation.ccp.Country
import com.bteamcoding.bubbletranslation.feature_bubble_translation.presentation.ccp.CountryPickerBottomSheet
import com.bteamcoding.bubbletranslation.feature_bubble_translation.presentation.ccp.PickerCustomization
import com.bteamcoding.bubbletranslation.feature_home.component.HexagonButton
import com.bteamcoding.bubbletranslation.ui.theme.Inter

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun SelectLang(
    modifier: Modifier = Modifier,
    shapeSize: Dp = 42.dp,
    textSize: TextUnit = 14.sp,
    enabled: Boolean = true,
) {
    var isPickerOpenSourceLanguage by remember { mutableStateOf(false) }
    var isPickerOpenTargetLanguage by remember { mutableStateOf(false) }
    val sourceLanguage by LanguageManager.sourceLang.collectAsStateWithLifecycle()
    val targetLanguage by LanguageManager.targetLang.collectAsStateWithLifecycle()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ){
        FilledIconButton(
            onClick = {isPickerOpenSourceLanguage = true},
            modifier = Modifier
                .size(shapeSize),
            enabled = enabled,
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = colorResource(R.color.blue_dark),
            )
        ) {
            Text(
                sourceLanguage.countryIso,
                fontSize = textSize,
                fontFamily = Inter,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
        }

        if (isPickerOpenSourceLanguage) {
            CountryPickerBottomSheet(
                modifier = Modifier
                    .clip(shape = RoundedCornerShape(10.dp))
                    .fillMaxWidth()
                    .fillMaxHeight(0.9f),
                onDismissRequest = { isPickerOpenSourceLanguage = false },
                onItemClicked = { country ->
                    LanguageManager.updateSourceLanguage(country)
                    isPickerOpenSourceLanguage = false
                },
                listOfCountry = Country.getAllCountries(),
                pickerCustomization = PickerCustomization(),
                itemPadding = 10,
            )
        }

        HexagonButton(
            width = shapeSize*4/5,
            height = shapeSize*4/5,
            icon = ImageVector.vectorResource(R.drawable.two_arrow),
            backgroundColor = colorResource(R.color.blue_medium),
            onClick = {}
        )

        FilledIconButton(
            onClick = {isPickerOpenTargetLanguage = true},
            modifier = Modifier.size(shapeSize),
            enabled = enabled,
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = colorResource(R.color.blue_dark),
            )
        ) {
            Text(
                targetLanguage.countryIso,
                fontSize = textSize,
                fontFamily = Inter,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
        }

        if (isPickerOpenTargetLanguage) {
            CountryPickerBottomSheet(
                modifier = Modifier
                    .clip(shape = RoundedCornerShape(10.dp))
                    .fillMaxWidth()
                    .fillMaxHeight(0.9f),
                onDismissRequest = { isPickerOpenTargetLanguage = false },
                onItemClicked = { country ->
                    LanguageManager.updateTargetLanguage(country)
                    isPickerOpenTargetLanguage = false
                },
                listOfCountry = Country.getAllCountries(),
                pickerCustomization = PickerCustomization(),
                itemPadding = 10,
            )
        }
    }
}


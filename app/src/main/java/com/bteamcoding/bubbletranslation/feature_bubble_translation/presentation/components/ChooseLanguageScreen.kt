package com.bteamcoding.bubbletranslation.feature_bubble_translation.presentation.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bteamcoding.bubbletranslation.feature_bubble_translation.presentation.ccp.Country
import com.bteamcoding.bubbletranslation.feature_bubble_translation.presentation.ccp.CountryCodePicker
import com.bteamcoding.bubbletranslation.feature_bubble_translation.presentation.ccp.PickerCustomization
import com.bteamcoding.bubbletranslation.feature_bubble_translation.presentation.ccp.ViewCustomization
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import com.bteamcoding.bubbletranslation.feature_bubble_translation.presentation.FloatingWidgetState

@Composable
fun ChooseLanguageScreen(
    state: FloatingWidgetState,
    onUpdateSourceLanguage: (Country) -> Unit,
    onUpdateTargetLanguage: (Country) -> Unit,
    onShowLanguageScreenChanged: (Boolean) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .align(Alignment.BottomCenter)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Tiêu đề
                Text(
                    text = "Chọn ngôn ngữ",
                    fontSize = 20.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Ngon ngu
                Row(
                    horizontalArrangement = Arrangement.spacedBy(70.dp)
                ) {
                    Text(
                        text = "Ngôn ngữ gốc",
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    Text(
                        text = "Ngôn ngữ đích",
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Chọn ngôn ngữ nguồn
                    Column {
                        var sourceCountry = state.sourceLanguage

                        CountryCodePicker(
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally),
                            selectedCountry = sourceCountry,
                            onCountrySelected = { country ->
                                sourceCountry = country
                                onUpdateSourceLanguage(country) // Cập nhật ngôn ngữ nguồn
                            },
                            viewCustomization = ViewCustomization(
                                showFlag = true,
                                showCountryIso = true,
                                showCountryName = false,
                                showCountryCode = false,
                                clipToFull = false
                            ),
                            pickerCustomization = PickerCustomization(
                                showFlag = true,
                                showCountryIso = true,
                                showCountryCode = false,
                            ),
                            showSheet = true,
                        )
                    }

                    Icon(imageVector = Icons.Default.ArrowForward, contentDescription = "Arrow")

                    // Chọn ngôn ngữ đích
                    Column {
                        var targetCountry = state.targetLanguage

                        CountryCodePicker(
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                            selectedCountry = targetCountry,
                            onCountrySelected = { country ->
                                targetCountry = country
                                onUpdateTargetLanguage(country) // Cập nhật ngôn ngữ đích
                            },
                            viewCustomization = ViewCustomization(
                                showFlag = true,
                                showCountryIso = true,
                                showCountryName = false,
                                showCountryCode = false,
                                clipToFull = false
                            ),
                            pickerCustomization = PickerCustomization(
                                showFlag = true,
                                showCountryIso = true,
                                showCountryCode = false,
                            ),
                            showSheet = true,
                        )
                    }
                }

                // Nút Xác nhận
                Button(
                    onClick = {
                        // Cập nhật ngôn ngữ khi nhấn nút xác nhận
                        onShowLanguageScreenChanged(false) // Đóng màn hình chọn ngôn ngữ
                    },
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .fillMaxWidth()
                ) {
                    Text(text = "Xác nhận")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewChooseLanguageScreen() {
    // Tạo một state giả để truyền vào ChooseLanguageScreen cho Preview
    val fakeState = FloatingWidgetState(
        sourceLanguage = Country.English,  // Giả sử ngôn ngữ nguồn là tiếng Anh
        targetLanguage = Country.Vietnamese // Giả sử ngôn ngữ đích là tiếng Việt
    )

    ChooseLanguageScreen(
        state = fakeState,
        onUpdateSourceLanguage = {},
        onUpdateTargetLanguage = {},
        onShowLanguageScreenChanged = {}
    )
}
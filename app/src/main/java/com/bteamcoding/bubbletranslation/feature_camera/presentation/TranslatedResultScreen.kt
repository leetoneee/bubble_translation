package com.bteamcoding.bubbletranslation.feature_camera.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.outlined.Compare
import androidx.compose.material.icons.outlined.CopyAll
import androidx.compose.material.icons.outlined.Headset
import androidx.compose.material.icons.outlined.StopCircle
import androidx.compose.material.icons.outlined.TextFields
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.constraintlayout.compose.ConstraintLayout
import com.bteamcoding.bubbletranslation.R
import com.bteamcoding.bubbletranslation.feature_camera.domain.TranslatedVisionText

@Composable
fun TranslatedResultScreen(
    text: TranslatedVisionText,
    isSpeaking: Boolean,
    onBack: () -> Unit,
    onShare: () -> Unit,
    onCopyText: (String) -> Unit,
    onSpeak: (String) -> Unit,
    onStopSpeaking: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(0xFFF8F8F8))
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxHeight()
            .padding(bottom = 8.dp)
    ) {
        item {
            ConstraintLayout(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF8F8F8))
                    .padding(horizontal = 16.dp)
            ) {
                val (topGrp, detectedText, translatedText) = createRefs()

                ConstraintLayout(
                    modifier = Modifier
                        .padding(top = 32.dp)
                        .fillMaxWidth()
                        .constrainAs(topGrp) {
                            top.linkTo(parent.top)
                        }
                ) {
                    val (backBtn, shareBtn, centerText) = createRefs()

                    FilledIconButton(
                        onClick = onBack,
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = Color(0xFFF3F2F7).copy(
                                alpha = 0.7f
                            )
                        ),
                        modifier = Modifier
                            .size(50.dp)
                            .constrainAs(backBtn) {
                                top.linkTo(parent.top)
                                bottom.linkTo(parent.bottom)
                                start.linkTo(parent.start)
                            }
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.ArrowBackIosNew,
                            contentDescription = "Back",
                            modifier = Modifier.size(24.dp),
                            tint = Color.Black
                        )
                    }

                    FilledIconButton(
                        onClick = onShare,
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = Color(0xFFF3F2F7).copy(
                                alpha = 0.7f
                            )
                        ),
                        modifier = Modifier
                            .size(50.dp)
                            .constrainAs(shareBtn) {
                                top.linkTo(parent.top)
                                bottom.linkTo(parent.bottom)
                                end.linkTo(parent.end)
                            }
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Share,
                            contentDescription = "Share",
                            tint = Color.Black
                        )
                    }

                    Text(
                        text = "Translated Results",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Normal,
                        modifier = Modifier.constrainAs(centerText) {
                            top.linkTo(parent.top)
                            bottom.linkTo(parent.bottom)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        }
                    )
                }

                ConstraintLayout(
                    modifier = Modifier
                        .constrainAs(detectedText) {
                            top.linkTo(topGrp.bottom)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        }
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(top = 16.dp)
                        .background(Color.White, RoundedCornerShape(20.dp))
                        .border(1.dp, Color.Gray, RoundedCornerShape(20.dp))
                        .padding(8.dp)
                ) {
                    val (actionGroup, textView) = createRefs()

                    ConstraintLayout(
                        modifier = Modifier
                            .fillMaxWidth()
                            .constrainAs(actionGroup) {
                                top.linkTo(parent.top)
                            }
                    ) {
                        val (sourceLang, copyBtn, voiceBtn) = createRefs()

                        Text(
                            text = "English - Ngôn ngữ được phát hiện",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color.Gray,
                            modifier = Modifier.constrainAs(sourceLang) {
                                top.linkTo(parent.top)
                                bottom.linkTo(parent.bottom)
                                start.linkTo(parent.start)
                            }
                        )

                        IconButton(
                            onClick = {
                                val textCopied =
                                    text.textBlocks.joinToString("\n") { it.originalBlock.text }
                                onCopyText(textCopied)
                            },
                            modifier = Modifier.constrainAs(copyBtn) {
                                top.linkTo(parent.top)
                                end.linkTo(voiceBtn.start, margin = 8.dp)
                            },
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.CopyAll,
                                contentDescription = null,
                                modifier = Modifier.size(24.dp),
                                tint = Color.Gray
                            )
                        }

                        if (isSpeaking) {
                            IconButton(
                                onClick = onStopSpeaking,
                                modifier = Modifier.constrainAs(voiceBtn) {
                                    top.linkTo(parent.top)
                                    end.linkTo(parent.end)
                                },
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.StopCircle,
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp),
                                    tint = Color.Gray
                                )
                            }
                        } else {
                            IconButton(
                                onClick = {
                                    val textCopied =
                                        text.textBlocks.joinToString("\n") { it.originalBlock.text }
                                    onSpeak(textCopied)
                                },
                                modifier = Modifier.constrainAs(voiceBtn) {
                                    top.linkTo(parent.top)
                                    end.linkTo(parent.end)
                                },
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Headset,
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp),
                                    tint = Color.Gray
                                )
                            }
                        }
                    }

                    Box(modifier = Modifier
                        .fillMaxWidth()
                        .constrainAs(textView) {
                            top.linkTo(actionGroup.bottom)
                        }) {
                        SelectionContainer(
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            Text(
                                text = text.textBlocks.joinToString("\n") { it.originalBlock.text },
                                style = TextStyle(
                                    color = Color.Black,
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 16.sp
                                ),
                            )
                        }
                    }
                }

                ConstraintLayout(
                    modifier = Modifier
                        .constrainAs(translatedText) {
                            top.linkTo(detectedText.bottom)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        }
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(top = 16.dp)
                        .background(Color.White, RoundedCornerShape(20.dp))
                        .border(1.dp, Color.Gray, RoundedCornerShape(20.dp))
                        .padding(8.dp)
                ) {
                    val (actionGroup, textView) = createRefs()

                    ConstraintLayout(
                        modifier = Modifier
                            .fillMaxWidth()
                            .constrainAs(actionGroup) {
                                top.linkTo(parent.top)
                            }
                    ) {
                        val (targetLang, copyBtn, voiceBtn) = createRefs()

                        Text(
                            text = "Vietnamese - Ngôn ngữ đã dịch",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color.Gray,
                            modifier = Modifier.constrainAs(targetLang) {
                                top.linkTo(parent.top)
                                bottom.linkTo(parent.bottom)
                                start.linkTo(parent.start)
                            }
                        )

                        if (isSpeaking) {
                            IconButton(
                                onClick = onStopSpeaking,
                                modifier = Modifier.constrainAs(voiceBtn) {
                                    top.linkTo(parent.top)
                                    end.linkTo(parent.end)
                                },
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.StopCircle,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(24.dp)
                                        .constrainAs(copyBtn) {

                                        },
                                    tint = Color.Gray
                                )
                            }
                        } else {
                            IconButton(
                                onClick = {
                                    val textCopied =
                                        text.textBlocks.joinToString("\n") { it.translatedText }
                                    onSpeak(textCopied)
                                },
                                modifier = Modifier.constrainAs(voiceBtn) {
                                    top.linkTo(parent.top)
                                    end.linkTo(parent.end)
                                },
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Headset,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(24.dp)
                                        .constrainAs(copyBtn) {

                                        },
                                    tint = Color.Gray
                                )
                            }
                        }

                        IconButton(
                            onClick = {
                                val textCopied =
                                    text.textBlocks.joinToString("\n") { it.translatedText }
                                onCopyText(textCopied)
                            },
                            modifier = Modifier.constrainAs(copyBtn) {
                                top.linkTo(parent.top)
                                end.linkTo(voiceBtn.start, margin = 8.dp)
                            },
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.CopyAll,
                                contentDescription = null,
                                modifier = Modifier.size(24.dp),
                                tint = Color.Gray
                            )
                        }


                    }

                    Box(modifier = Modifier
                        .fillMaxWidth()
                        .constrainAs(textView) {
                            top.linkTo(actionGroup.bottom)
                        }) {
                        SelectionContainer(
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            Text(
                                text = text.textBlocks.joinToString("\n") { it.translatedText },
                                style = TextStyle(
                                    color = Color.Black,
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 16.sp
                                ),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
@Preview
fun TranslatedResultScreenPreview() {
    TranslatedResultScreen(
        text = TranslatedVisionText(
            text = "",
            textBlocks = listOf()
        ),
        isSpeaking = false,
        onBack = {},
        onShare = {},
        onCopyText = {},
        onSpeak = {},
        onStopSpeaking = {}
    )
}
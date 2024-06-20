package com.practice.main.main.dialog

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hsk.ktx.date.Date
import com.practice.designsystem.LightAndDarkPreview
import com.practice.designsystem.a11y.isLargeFont
import com.practice.designsystem.components.DialogBodyLarge
import com.practice.designsystem.components.DialogBodyMedium
import com.practice.designsystem.components.DialogBodySmall
import com.practice.designsystem.components.DialogTitleLarge
import com.practice.designsystem.theme.BlindarTheme
import com.practice.main.R
import com.practice.main.main.previewUiMenus
import com.practice.main.state.UiMeal
import com.practice.main.state.UiNutrient
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun NutrientDialogContents(
    uiMeal: UiMeal,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val chipTargetNames = listOf("열량", "탄수화물", "단백질", "지방")
    val (importantNutrients, otherNutrients) = uiMeal.nutrients.partition { nutrient ->
        chipTargetNames.contains(nutrient.name)
    }

    val month = uiMeal.month
    val day = uiMeal.day
    val mealTime = uiMeal.mealTime
    val dialogTitle =
        stringResource(id = R.string.nutrient_dialog_title, "${month}월 ${day}일 $mealTime")

    val shape = RoundedCornerShape(16.dp)
    LazyColumn(
        modifier = modifier
            .shadow(4.dp, shape = shape)
            .clip(shape)
            .background(MaterialTheme.colorScheme.surface)
            .heightIn(max = 550.dp), // TODO: 높이 어떻게?
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(16.dp),
    ) {
        item {
            DialogTitleLarge(
                text = dialogTitle,
                color = contentColorFor(backgroundColor = MaterialTheme.colorScheme.surface),
            )
        }
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                ImportantNutrients(importantNutrients.toImmutableList())
                NutrientList(
                    nutrients = otherNutrients.toImmutableList(),
                    modifier = Modifier.padding(top = 8.dp),
                )
            }
        }
        item {
            NutrientDialogCloseButton(
                onClick = onClose,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun ImportantNutrients(
    importantNutrients: ImmutableList<UiNutrient>,
    modifier: Modifier = Modifier,
) {
    if (LocalDensity.current.isLargeFont) {
        NutrientList(
            nutrients = importantNutrients,
            modifier = modifier,
        )
    } else {
        NutrientChipGrid(
            nutrients = importantNutrients,
            modifier = modifier,
        )
    }
}

@Composable
private fun NutrientChipGrid(
    nutrients: ImmutableList<UiNutrient>,
    modifier: Modifier = Modifier
) {
    val chipColors =
        listOf(Color(0xFFFFE2E5), Color(0xFFFFF4DE), Color(0xFFDCFCE7), Color(0xFFF3E8FF))
    val columns = 2
    val rows = nutrients.chunked(columns)

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        rows.forEachIndexed { rowIndex, row ->
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                row.forEachIndexed { colIndex, item ->
                    val index = rowIndex * columns + colIndex
                    NutrientChip(
                        uiNutrient = item,
                        backgroundColor = chipColors[index],
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

@Composable
private fun NutrientList(
    nutrients: ImmutableList<UiNutrient>,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        nutrients.forEach { nutrient ->
            NutrientListItem(nutrient)
        }
    }
}

@Composable
private fun NutrientListItem(
    uiNutrient: UiNutrient,
    modifier: Modifier = Modifier,
) {
    val itemModifier = modifier.clearAndSetSemantics {
        contentDescription = uiNutrient.description
    }
    if (LocalDensity.current.isLargeFont) {
        NutrientListItemLarge(
            uiNutrient = uiNutrient,
            modifier = itemModifier,
        )
    } else {
        NutrientListItemNormal(
            uiNutrient = uiNutrient,
            modifier = itemModifier,
        )
    }
}

@Composable
private fun NutrientListItemNormal(
    uiNutrient: UiNutrient,
    modifier: Modifier = Modifier,
    textColor: Color = contentColorFor(MaterialTheme.colorScheme.surface),
) {
    Row(modifier = modifier.padding(16.dp)) {
        DialogBodySmall(
            text = uiNutrient.name,
            color = textColor
        )
        Spacer(modifier = Modifier.weight(1f))
        DialogBodySmall(
            text = "${uiNutrient.amount} ${uiNutrient.unit}",
            color = textColor,
        )
    }
}

@Composable
private fun NutrientListItemLarge(
    uiNutrient: UiNutrient,
    modifier: Modifier = Modifier,
    textColor: Color = contentColorFor(MaterialTheme.colorScheme.surface),
) {
    Column(modifier = modifier) {
        DialogBodyLarge(
            text = uiNutrient.name,
            color = textColor
        )
        DialogBodyLarge(
            text = "${uiNutrient.amount} ${uiNutrient.unit}",
            color = textColor,
        )
    }
}

@Composable
private fun NutrientChip(
    uiNutrient: UiNutrient,
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color.Unspecified,
) {
    val textColor = Color.Black
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .clearAndSetSemantics {
                contentDescription = uiNutrient.description
            }
    ) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            DialogTitleLarge(
                text = "${uiNutrient.amount}${uiNutrient.unit}",
                color = textColor,
            )
            DialogBodyMedium(
                text = uiNutrient.name,
                color = textColor,
            )
        }
    }
}

@Composable
private fun NutrientDialogCloseButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.primaryContainer)
            .clickable { onClick() }
            .padding(12.dp)
    ) {
        DialogBodySmall(
            text = stringResource(id = R.string.nutrient_dialog_close),
            modifier = Modifier.align(Alignment.Center),
            color = contentColorFor(backgroundColor = MaterialTheme.colorScheme.primaryContainer)
        )
    }
}

private val previewUiNutrients = persistentListOf(
    UiNutrient("열량", 765.8, "kcal"),
    UiNutrient("탄수화물", 110.2, "g"),
    UiNutrient("단백질", 34.9, "g"),
    UiNutrient("지방", 32.0, "g"),
    UiNutrient("비타민A", 595.2, "R.E"),
    UiNutrient("티아민", 0.5, "mg"),
    UiNutrient("리보플라민", 0.8, "mg"),
    UiNutrient("비타민C", 11.3, "mg"),
    UiNutrient("칼슘", 322.3, "mg"),
    UiNutrient("철분", 5.2, "mg"),
)

@LightAndDarkPreview
@Composable
private fun NutrientChipPreview() {
    BlindarTheme {
        NutrientChip(
            uiNutrient = previewUiNutrients[0],
            backgroundColor = Color(0xFFFFE2E5),
            modifier = Modifier.size(150.dp),
        )
    }
}

@LightAndDarkPreview
@Composable
private fun NutrientListPreview() {
    BlindarTheme {
        NutrientList(
            nutrients = previewUiNutrients.subList(0, 4),
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface),
        )
    }
}

@LightAndDarkPreview
@Composable
private fun NutrientDialogCloseButtonPreview() {
    val coroutineScope = rememberCoroutineScope()
    var isVisible by remember { mutableStateOf(false) }
    BlindarTheme {
        NutrientDialogCloseButton(
            onClick = {
                coroutineScope.launch {
                    isVisible = true
                    delay(200L)
                    isVisible = false
                }
            },
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
        )
        if (isVisible) {
            Text(text = "클릭 실행됨")
        }
    }
}

@LightAndDarkPreview
@Composable
private fun NutrientDialogContentsPreview() {
    BlindarTheme {
        val now = Date.now()
        Box(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.primary)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            NutrientDialogContents(
                uiMeal = UiMeal(
                    now.year,
                    now.month,
                    now.dayOfMonth,
                    "중식",
                    previewUiMenus,
                    previewUiNutrients
                ),
                onClose = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center),
            )
        }
    }
}

@Preview(fontScale = 2f)
@Preview(fontScale = 2f, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun NutrientListItemLargePreview() {
    BlindarTheme {
        NutrientListItemLarge(
            uiNutrient = previewUiNutrients[0],
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp),
        )
    }
}
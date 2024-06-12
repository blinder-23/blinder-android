package com.practice.main.calendar

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.hsk.ktx.date.Date
import com.practice.designsystem.calendar.core.rememberCalendarState
import com.practice.main.MainScreenViewModel
import com.practice.main.R
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CalendarMainScreen(
    windowSize: WindowSizeClass,
    viewModel: MainScreenViewModel,
    mealPagerState: PagerState,
    onNavigateToSettingsScreen: () -> Unit,
    onNavigateToSelectSchoolScreen: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState

    val calendarState = rememberCalendarState(uiState.year, uiState.month, uiState.selectedDate)
    val calendarPageCount = 13

    val coroutineScope = rememberCoroutineScope()
    val onMealTimeClick: (Int) -> Unit = { index ->
        viewModel.onMealTimeClick(index)
        coroutineScope.launch { mealPagerState.animateScrollToPage(index) }
    }

    when (windowSize.widthSizeClass) {
        WindowWidthSizeClass.Expanded -> {
            HorizontalCalendarMainScreen(
                calendarPageCount = calendarPageCount,
                uiState = uiState,
                calendarState = calendarState,
                mealPagerState = mealPagerState,
                onRefreshIconClick = { viewModel.onRefreshIconClick(context) },
                onSettingsIconClick = onNavigateToSettingsScreen,
                onCalendarHeaderClick = { viewModel.onDateClick(Date.now()) },
                calendarHeaderClickLabel = stringResource(id = R.string.calendar_header_click),
                onDateClick = viewModel::onDateClick,
                onSwiped = viewModel::onSwiped,
                getContentDescription = viewModel::getContentDescription,
                getClickLabel = viewModel::getClickLabel,
                drawUnderlineToScheduleDate = { },
                onNavigateToSelectSchoolScreen = onNavigateToSelectSchoolScreen,
                onMealTimeClick = onMealTimeClick,
                onNutrientDialogOpen = viewModel::openNutrientDialog,
                onMemoDialogOpen = viewModel::openMemoDialog,
                modifier = modifier,
            ) { date -> viewModel.getCustomActions(date) }
        }

        else -> {
            VerticalCalendarMainScreen(
                calendarPageCount = calendarPageCount,
                uiState = uiState,
                calendarState = calendarState,
                mealPagerState = mealPagerState,
                onRefreshIconClick = { viewModel.onRefreshIconClick(context) },
                onSettingsIconClick = onNavigateToSettingsScreen,
                onCalendarHeaderClick = { viewModel.onDateClick(Date.now()) },
                calendarHeaderClickLabel = stringResource(id = R.string.calendar_header_click),
                onDateClick = viewModel::onDateClick,
                onSwiped = viewModel::onSwiped,
                getContentDescription = viewModel::getContentDescription,
                getClickLabel = viewModel::getClickLabel,
                drawUnderlineToScheduleDate = {},
                onNavigateToSelectSchoolScreen = onNavigateToSelectSchoolScreen,
                onMealTimeClick = onMealTimeClick,
                onNutrientDialogOpen = viewModel::openNutrientDialog,
                onMemoDialogOpen = viewModel::openMemoDialog,
                modifier = modifier,
            ) { date -> viewModel.getCustomActions(date) }
        }
    }
}
package org.mifos.mobilewallet.mifospay.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.mifos.mobilewallet.mifospay.ui.utility.TabContent

/**
 * @author pratyush
 * @since 23/3/24
 */

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MifosScrollableTabRow(
    tabContents: List<TabContent>,
    pagerState: PagerState,
    modifier: Modifier = Modifier,
    containerColor: Color = Color.White,
    selectedContentColor: Color = Color.Black,
    unselectedContentColor: Color = Color.LightGray,
    edgePadding: Dp = 8.dp
) {
    val scope = rememberCoroutineScope()

    ScrollableTabRow(
        containerColor = containerColor,
        selectedTabIndex = pagerState.currentPage,
        edgePadding = edgePadding,
        indicator = {},
        divider = {},
    ) {
        tabContents.forEachIndexed { index, currentTab ->
            Tab(
                text = { Text(text = currentTab.tabName) },
                selected = pagerState.currentPage == index,
                selectedContentColor = selectedContentColor,
                unselectedContentColor = unselectedContentColor,
                onClick = {
                    scope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                }
            )
        }
    }

    HorizontalPager(
        state = pagerState,
        modifier = modifier
    ) { page ->
        tabContents.getOrNull(page)?.content?.invoke() ?: Text("Page $page")
    }
}

package org.mifospay.core.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import kotlinx.coroutines.launch
import org.mifospay.core.ui.utility.TabContent

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
    containerColor: Color = MaterialTheme.colorScheme.surface,
    selectedContentColor: Color = MaterialTheme.colorScheme.onSurface,
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
                text = { Text(text = currentTab.tabName,
                    color = MaterialTheme.colorScheme.onSurface) },
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
        count = tabContents.size,
        state = pagerState,
        modifier = modifier
    ) { page ->
        tabContents.getOrNull(page)?.content?.invoke() ?: Text("Page $page")
    }
}

package org.mifos.mobilewallet.mifospay.home.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FinanceScreen() {

    val pagerState = rememberPagerState(
        pageCount = { FinanceScreenContents.entries.size }
    )
    val scope = rememberCoroutineScope()
    val selectedTabIndex by remember { derivedStateOf { pagerState.currentPage }}

    BackHandler {
        // TODO: Implement back handler with merchant screen
    }

    Column(modifier = Modifier.fillMaxSize()) {
        ScrollableTabRow(
            containerColor = Color.White,
            selectedTabIndex = pagerState.currentPage,
            edgePadding = 8.dp,
            indicator = {},
            divider = {},
        ) {
            FinanceScreenContents.entries.forEachIndexed { index, currentTab ->
                Tab(
                    text = { Text(currentTab.name) },
                    selected = selectedTabIndex == index,
                    selectedContentColor = Color.Black,
                    unselectedContentColor = Color.LightGray,
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(currentTab.ordinal)
                        }
                    }
                )
            }
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { page ->
            when (page) {
                0 -> Text("Accounts page") // TODO: Replace with Accounts Screen
                1 -> Text("Cards page") // TODO: Replace with Cards Screen
                2 -> Text("Merchants page") // TODO: Replace with Merchants Screen
                3 -> Text("KYC page") // TODO: Replace with KYC Screen
            }
        }
    }
}

enum class FinanceScreenContents {
    ACCOUNTS,
    CARDS,
    MERCHANTS,
    KYC
}

@Preview(showBackground = true)
@Composable
fun FinanceScreenPreview() {
    FinanceScreen()
}
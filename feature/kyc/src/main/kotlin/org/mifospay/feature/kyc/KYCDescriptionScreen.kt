package org.mifospay.feature.kyc

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mifospay.core.model.entity.kyc.KYCLevel1Details
import org.mifospay.core.designsystem.component.MifosOverlayLoadingWheel
import org.mifospay.kyc.R

@Composable
fun KYCScreen(
    viewModel: KYCDescriptionViewModel = hiltViewModel(),
    onLevel1Clicked: () -> Unit,
    onLevel2Clicked: () -> Unit,
    onLevel3Clicked: () -> Unit
    ) {
    val kUiState by viewModel.kycdescriptionState.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()

    KYCDescriptionScreen(
        kUiState = kUiState,
        onLevel1Clicked = {
            // Todo : Implement onLevel1Clicked flow
            onLevel1Clicked.invoke()
        },
        onLevel2Clicked = {
            // Todo : Implement onLevel2Clicked flow
            onLevel2Clicked.invoke()
        },
        onLevel3Clicked = {
            // Todo : Implement onLevel3Clicked flow
            onLevel3Clicked.invoke()
        },
        isRefreshing = isRefreshing,
        onRefresh = { viewModel.refresh() }
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun KYCDescriptionScreen(
    kUiState: KYCDescriptionUiState,
    onLevel1Clicked: () -> Unit,
    onLevel2Clicked: () -> Unit,
    onLevel3Clicked: () -> Unit,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
) {
    val pullRefreshState = rememberPullRefreshState(isRefreshing, onRefresh)
    Box(Modifier.pullRefresh(pullRefreshState)) {
        when (kUiState) {
            KYCDescriptionUiState.Loading -> {
                MifosOverlayLoadingWheel(contentDesc = stringResource(R.string.feature_kyc_loading))
            }

            is KYCDescriptionUiState.Error -> {
                PlaceholderScreen()
            }

            is KYCDescriptionUiState.KYCDescription -> {
                val kyc = kUiState.kycLevel1Details
                if (kyc != null) {
                    KYCDescriptionScreen(
                        kyc,
                        onLevel1Clicked,
                        onLevel2Clicked,
                        onLevel3Clicked
                    )
                }
            }
        }
        PullRefreshIndicator(
            refreshing = isRefreshing,
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }
}

@Composable
fun KYCDescriptionScreen(
    kyc: KYCLevel1Details,
    onLevel1Clicked: () -> Unit,
    onLevel2Clicked: () -> Unit,
    onLevel3Clicked: () -> Unit
) {
    val currentLevel = kyc.currentLevel

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = stringResource(R.string.feature_kyc_complete_kyc),
            modifier = Modifier.padding(top = 40.dp),
            fontSize = 19.sp,
            textAlign = TextAlign.Center
        )

        KYCLevelButton(
            level = 1,
            enabled = currentLevel >= 0.toString(),
            completed = currentLevel >= 1.toString(),
            modifier = Modifier.padding(top = 90.dp),
            onLevel1Clicked
        )

        KYCLevelButton(
            level = 2,
            enabled = currentLevel >= 1.toString(),
            completed = currentLevel >= 2.toString(),
            modifier = Modifier.padding(top = 80.dp),
            onLevel2Clicked
        )

        KYCLevelButton(
            level = 3,
            enabled = currentLevel >= 2.toString(),
            completed = currentLevel >= 3.toString(),
            modifier = Modifier.padding(top = 80.dp),
            onLevel3Clicked
        )
    }
}

@Composable
fun KYCLevelButton(
    level: Int,
    enabled: Boolean,
    completed: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        ButtonComponent(
            stringResource(R.string.feature_kyc_level) + "$level",
            enabled,
            completed,
            onClick
        )
        Spacer(modifier = Modifier.weight(0.1f))
        IconComponent(
            completed,
            modifier = Modifier.weight(0.9f)
        )
    }
}

@Composable
fun ButtonComponent(
    value: String,
    enabled: Boolean,
    completed: Boolean,
    onButtonClicked: () -> Unit
) {
    Button(
        modifier = Modifier
            .width(130.dp)
            .heightIn(38.dp)
            .shadow(43.dp),
        onClick = {
            if (enabled) {
                onButtonClicked.invoke()
            }
        },
        contentPadding = PaddingValues(),
        colors = ButtonDefaults.buttonColors(
            containerColor = when {
                completed -> Color.White
                enabled -> Color.Blue
                else -> Color.Gray
            },
            contentColor = when {
                completed -> Color.Blue
                enabled -> Color.White
                else -> Color.Gray
            }
        ),
        shape = RoundedCornerShape(10.dp),
        enabled = enabled
    ) {
        Text(
            text = value,
            fontSize = 18.sp,
            fontWeight = FontWeight.Normal
        )
    }
}

@Composable
fun IconComponent(completed: Boolean, modifier: Modifier = Modifier) {
    if (completed) {
        Row(
            modifier = modifier
                .height(23.dp)
        ) {
            Icon(
                Icons.Filled.Check,
                contentDescription = stringResource(R.string.feature_kyc_check),
                modifier = Modifier
                    .size(20.dp)
            )
            Spacer(modifier = Modifier.width(26.dp))
            Text(
                text = stringResource(R.string.feature_kyc_completion)
            )
        }
    }
}

@Composable
fun PlaceholderScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_error_state),
                contentDescription = null,
                modifier = Modifier
                    .size(48.dp)
                    .scale(1.5f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(id = R.string.feature_kyc_error_oops),
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(id = R.string.feature_kyc_error_kyc_details),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun KYCDescriptionPreview() {
    val onLevel1Clicked: () -> Unit = { }
    val onLevel2Clicked: () -> Unit = { }
    val onLevel3Clicked: () -> Unit = { }
    KYCDescriptionScreen(
        kyc = KYCLevel1Details(),
        onLevel1Clicked,
        onLevel2Clicked,
        onLevel3Clicked
    )
}
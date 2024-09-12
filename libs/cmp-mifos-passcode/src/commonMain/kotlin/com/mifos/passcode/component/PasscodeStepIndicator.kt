package com.mifos.passcode.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.mifos.passcode.theme.blueTint
import com.mifos.passcode.utility.Constants.STEPS_COUNT
import com.mifos.passcode.utility.Step

@Composable
fun PasscodeStepIndicator(
    modifier: Modifier = Modifier,
    activeStep: Step
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(
            space = 6.dp,
            alignment = Alignment.CenterHorizontally
        )
    ) {
        repeat(STEPS_COUNT) { step ->
            val isActiveStep = step <= activeStep.index
            val stepColor =
                animateColorAsState(if (isActiveStep) blueTint else Color.Gray, label = "")

            Box(
                modifier = Modifier
                    .size(
                        width = 72.dp,
                        height = 4.dp
                    )
                    .background(
                        color = stepColor.value,
                        shape = MaterialTheme.shapes.medium
                    )
            )
        }
    }
}
package org.mifospay.core.ui.utility

data class DialogState(
    val type: DialogType = DialogType.NONE,
    val onConfirm: () -> Unit = {}
)

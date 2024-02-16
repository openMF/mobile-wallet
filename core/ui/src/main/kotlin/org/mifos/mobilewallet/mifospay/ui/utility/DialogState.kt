package org.mifos.mobilewallet.mifospay.ui.utility

data class DialogState(
    val type: DialogType = DialogType.NONE,
    val onConfirm: () -> Unit = {}
)

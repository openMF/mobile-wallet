package org.mifos.mobilewallet.mifospay.common.compose

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

data class ChildLayout(
    val contentType: String = "",
    val content: @Composable (item: Any?) -> Unit = {},
    val items: List<Any> = emptyList()
)

@Composable
fun VerticalScrollLayout(
    modifier: Modifier = Modifier,
    vararg childLayouts: ChildLayout
) {
    LazyColumn(modifier = modifier.fillMaxSize()) {
        childLayouts.forEach { child ->
            if (child.items.isEmpty()) {
                loadItem(child)
            } else {
                loadItems(child)
            }
        }
    }
}

/**
 * Use single item compose if no scroll or only horizontal scroll needed
 */
private fun LazyListScope.loadItem(childLayout: ChildLayout) {
    item(contentType = childLayout.contentType) {
        childLayout.content(null)
    }
}

/**
 * Use load multiple items to the lazy column when nested vertical scroll is needed
 */
private fun LazyListScope.loadItems(childLayout: ChildLayout) {
    items(items = childLayout.items) { item ->
        childLayout.content(item)
    }
}

/**
 * Compose items only if general item is successfully casted to defined class
 */
@Suppress("UNCHECKED_CAST")
@Composable
fun <T : Any> LoadItemAfterSafeCast(
    generalItem: Any?,
    composeWithSafeItem: @Composable (item: T) -> Unit
) {
    (generalItem as? T)?.let { safeItem ->
        composeWithSafeItem(safeItem)
    }
}
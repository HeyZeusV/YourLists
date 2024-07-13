package com.heyzeusv.yourlists.util

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalMinimumInteractiveComponentEnforcement
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.heyzeusv.yourlists.R
import com.heyzeusv.yourlists.database.models.BaseItem
import com.heyzeusv.yourlists.database.models.Item
import com.heyzeusv.yourlists.ui.theme.BlackAlpha60

@Composable
fun EmptyList(
    message: String,
    buttonOnClick: () -> Unit,
    buttonIcon: ImageVector,
    buttonText: String,
) {
    Column(
        modifier = Modifier
            .padding(horizontal = dRes(R.dimen.el_padding_horizontal))
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = message,
            modifier = Modifier.padding(bottom = dRes(R.dimen.el_text_padding_bottom)),
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.headlineMedium
        )
        Button(
            onClick = buttonOnClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = buttonIcon,
                contentDescription = buttonText,
                modifier = Modifier.size(dRes(R.dimen.el_icon_size))
            )
            Text(
                text = buttonText,
                style = MaterialTheme.typography.headlineSmall
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemInfo(
    item: BaseItem,
    surfaceOnClick: () -> Unit = { },
) {
    Surface(modifier = Modifier.clickable { surfaceOnClick() }) {
        Row(
            modifier = Modifier
                .padding(
                    horizontal = dRes(R.dimen.if_padding_horizontal),
                    vertical = dRes(R.dimen.if_padding_vertical)
                )
                .heightIn(min = dRes(R.dimen.if_height_min))
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(dRes(R.dimen.if_spacedBy)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (item is Item) {
                CompositionLocalProvider(LocalMinimumInteractiveComponentEnforcement provides false) {
                    Checkbox(
                        checked = item.isChecked,
                        onCheckedChange = { },
                    )
                }
            }
            Text(
                text = item.name,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.titleMedium
            )
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = item.category,
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "${item.quantity} ${item.unit}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
fun TextFieldWithLimit(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    isError: Boolean,
    maxLength: Int,
    modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
) {
    TextField(
        value = value,
        onValueChange = { if (it.length <= maxLength) onValueChange(it) },
        modifier = modifier,
        label = { Text(text = label) },
        trailingIcon = {
            if (isError) {
                Icon(
                    painter = pRes(R.drawable.icon_error),
                    contentDescription = sRes(R.string.iad_cdesc_error),
                )
            }
        },
        supportingText = {
            Row {
                Text(
                    text = if (isError) sRes(R.string.iad_error) else "",
                    modifier = Modifier.weight(1f),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "${value.length}/$maxLength",
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.End,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        },
        isError = isError,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        singleLine = true,
    )
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun BottomSheet(
    isVisible: Boolean,
    updateIsVisible: (Boolean) -> Unit,
    content: @Composable () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }

    AnimatedVisibility(
        visible = isVisible,
        enter = EnterTransition.None,
        exit = ExitTransition.None,
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = { updateIsVisible(false) },
                )
                .animateEnterExit(
                    enter = fadeIn(),
                    exit = fadeOut(),
                ),
            color = BlackAlpha60,
        ) { }
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomCenter,
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = dRes(R.dimen.bs_bottom_spacer))
                    .animateEnterExit(
                        enter = slideInVertically { it },
                        exit = slideOutVertically { it },
                    ),
            ) {
                content()
            }
        }
    }
}

@Preview
@Composable
private fun EmptyListPreview() {
    PreviewUtil.run {
        Preview {
            EmptyList(
                message = "Blah Blah Blah",
                buttonOnClick = { },
                buttonIcon = Icons.Filled.Add,
                buttonText = "More Blah Blah",
            )
        }
    }
}

@Preview
@Composable
private fun ItemInfoPreview() {
    PreviewUtil.run {
        Preview {
            Column {
                ItemInfo(item = itemChecked)
                ItemInfo(item = itemUnchecked)
            }
        }
    }
}

@Preview
@Composable
private fun DefaultItemInfoPreview() {
    PreviewUtil.run {
        Preview {
            Column {
                ItemInfo(item = defaultItem)
                ItemInfo(item = defaultItem)
            }
        }
    }
}
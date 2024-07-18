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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.LocalMinimumInteractiveComponentEnforcement
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.heyzeusv.yourlists.R
import com.heyzeusv.yourlists.add.FilteredDropDownMenu
import com.heyzeusv.yourlists.database.models.BaseItem
import com.heyzeusv.yourlists.database.models.Category
import com.heyzeusv.yourlists.database.models.Item
import com.heyzeusv.yourlists.database.models.ItemListWithItems
import com.heyzeusv.yourlists.ui.theme.BlackAlpha60
import java.text.DecimalFormat

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
    surfaceOnClick: () -> Unit,
    checkboxOnClick: (Item, (Boolean) -> Unit) -> Unit = { _, _ -> },
) {
    var isCheckboxEnabled by remember { mutableStateOf(true) }
    val decimalFormat = DecimalFormat("#,##0.00")

    Surface(modifier = Modifier.clickable { surfaceOnClick() }) {
        Row(
            modifier = Modifier
                .padding(
                    horizontal = dRes(R.dimen.if_padding_horizontal),
                    vertical = dRes(R.dimen.if_padding_vertical)
                )
                .heightIn(min = dRes(R.dimen.if_height_min))
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(dRes(R.dimen.if_spacedBy_horizontal)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (item is Item) {
                CompositionLocalProvider(LocalMinimumInteractiveComponentEnforcement provides false) {
                    Checkbox(
                        checked = item.isChecked,
                        onCheckedChange = { checkboxOnClick(item) { isCheckboxEnabled = it } },
                        enabled = isCheckboxEnabled,
                        colors = CheckboxDefaults.colors(
                            disabledCheckedColor = MaterialTheme.colorScheme.primary,
                            disabledUncheckedColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        ),
                    )
                }
            }
            CheckboxDefaults.colors()
            Text(
                text = item.name,
                modifier = Modifier.weight(.80f),
                style = MaterialTheme.typography.titleMedium,
            )
            Column(
                modifier = Modifier.weight(.20f),
                verticalArrangement = Arrangement.spacedBy(dRes(R.dimen.if_spacedBy_vertical)),
                horizontalAlignment = Alignment.End,
            ) {
                Text(
                    text = item.category,
                    textAlign = TextAlign.End,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 2,
                    style = MaterialTheme.typography.bodySmall,
                )
                Text(
                    text = "${decimalFormat.format(item.quantity)} ${item.unit}",
                    textAlign = TextAlign.End,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 2,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
    }
}

@Composable
fun ListInfo(
    itemList: ItemListWithItems,
    itemListOnClick: (ItemListWithItems) -> Unit,
    displayOptions: Boolean,
    optionOnClick: (ItemListWithItems) -> Unit = { },
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { itemListOnClick(itemList) },
        shape = RoundedCornerShape(dRes(R.dimen.card_radius)),
    ) {
        Column(modifier = Modifier.padding(all = dRes(R.dimen.osli_padding_all))) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = itemList.itemList.name,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.headlineMedium
                )
                if (displayOptions) {
                    Icon(
                        painter = pRes(R.drawable.icon_options),
                        contentDescription = sRes(R.string.button_cdesc_options),
                        modifier = Modifier
                            .align(Alignment.Top)
                            .padding(top = dRes(R.dimen.osli_options_padding_top))
                            .clickable { optionOnClick(itemList) },
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(dRes(R.dimen.osli_progress_spacedBy)),
                verticalAlignment = Alignment.CenterVertically
            ) {
                LinearProgressIndicator(
                    progress = { itemList.progress.first },
                    modifier = Modifier
                        .height(dRes(R.dimen.osli_progress_height))
                        .weight(1f),
                    trackColor = MaterialTheme.colorScheme.background,
                    strokeCap = StrokeCap.Round
                )
                Text(text = itemList.progress.second)
            }
        }
    }
}

@Composable
fun TextFieldWithLimit(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    label: String,
    isError: Boolean,
    maxLength: Int,
    modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
) {
    TextField(
        value = value,
        onValueChange = { if (it.text.length <= maxLength) onValueChange(it) },
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
                    text = "${value.text.length}/$maxLength",
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

@Composable
fun EditItemBottomSheetContent(
    closeBottomSheet: () -> Unit,
    selectedItem: BaseItem,
    categories: List<Category>,
    primaryLabel: String,
    primaryOnClick: (BaseItem) -> Unit,
    secondaryLabel: String?,
    secondaryOnClick: ((BaseItem) -> Unit)?,
    deleteLabel: String,
    deleteOnClick: (BaseItem) -> Unit,
) {
    val focusManager = LocalFocusManager.current
    val unitList = saRes(R.array.unit_values).toList()
    val decimalFormat = DecimalFormat("#,##0.00")

    var name by remember { mutableStateOf(TextFieldValue(selectedItem.name)) }
    var category by remember { mutableStateOf(TextFieldValue(selectedItem.category)) }
    var quantity by remember {
        mutableStateOf(selectedItem.quantity.toTextFieldValue(decimalFormat))
    }
    var unit by remember { mutableStateOf(TextFieldValue(selectedItem.unit)) }
    var memo by remember { mutableStateOf(TextFieldValue(selectedItem.memo)) }

    var isNameError by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .padding(all = dRes(R.dimen.bs_padding_all))
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(dRes(R.dimen.asbs_vertical_spacedBy))
    ) {
        TextFieldWithLimit(
            value = name,
            onValueChange = { name = it },
            label = sRes(R.string.asbs_name),
            isError = isNameError,
            maxLength = iRes(R.integer.name_max_length),
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            ),
        )
        FilteredDropDownMenu(
            value = category,
            onValueChanged = { category = it },
            label = sRes(R.string.asbs_category),
            options = categories.map { it.name },
            maxLength = iRes(R.integer.category_max_length),
            optionOnClick = {
                category = TextFieldValue(it)
                focusManager.moveFocus(FocusDirection.Down)
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            ),
        )
        Row(horizontalArrangement = Arrangement.spacedBy(dRes(R.dimen.bs_horizontal_spacedBy))) {
            TextFieldWithLimit(
                value = quantity,
                onValueChange = { quantity = it.formatTextAsDouble(decimalFormat) },
                label = sRes(R.string.asbs_quantity),
                isError = false,
                maxLength = iRes(R.integer.quantity_max_length),
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next,
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Right) }
                ),
            )
            FilteredDropDownMenu(
                value = unit,
                onValueChanged = { unit = it },
                label = sRes(R.string.asbs_unit),
                options = unitList,
                optionOnClick = {
                    unit = TextFieldValue(it)
                    focusManager.moveFocus(FocusDirection.Down)
                },
                maxLength = iRes(R.integer.unit_max_length),
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
            )
        }
        TextFieldWithLimit(
            value = memo,
            onValueChange = { memo = it },
            label = sRes(R.string.asbs_memo),
            isError = false,
            maxLength = iRes(R.integer.memo_max_length),
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
        )
        Button(
            onClick = {
                if (name.text.isBlank()) {
                    isNameError = true
                } else {
                    val updatedQuantity = quantity.toDouble()
                    val updatedSelectedDefaultItem = selectedItem.editCopy(
                        name = name.text,
                        category = category.text.ifBlank { categories.first().name },
                        quantity = if (updatedQuantity == 0.0) 1.0 else updatedQuantity,
                        unit = unit.text.ifBlank { unitList.first() },
                        memo = memo.text,
                    )
                    primaryOnClick(updatedSelectedDefaultItem)
                    closeBottomSheet()
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.extraSmall,
        ) {
            Text(text = primaryLabel.uppercase())
        }
        Row(horizontalArrangement = Arrangement.spacedBy(dRes(R.dimen.bs_horizontal_spacedBy))) {
            if (secondaryOnClick != null) {
                OutlinedButton(
                    onClick = {
                        if (name.text.isBlank()) {
                            isNameError = true
                        } else {
                            val updatedQuantity = quantity.toDouble()
                            val updatedSelectedDefaultItem = selectedItem.editCopy(
                                name = name.text,
                                category = category.text.ifBlank { categories.first().name },
                                quantity = if (updatedQuantity == 0.0) 1.0 else updatedQuantity,
                                unit = unit.text.ifBlank { unitList.first() },
                                memo = memo.text,
                            )
                            secondaryOnClick(updatedSelectedDefaultItem)
                            closeBottomSheet()
                        }
                    },
                    modifier = Modifier.weight(1f),
                    shape = MaterialTheme.shapes.extraSmall,
                    contentPadding = PaddingValues(
                        horizontal = dRes(R.dimen.asbs_button_padding_horizontal),
                        vertical = dRes(R.dimen.asbs_button_padding_vertical)
                    ),
                ) {
                    Text(text = secondaryLabel!!.uppercase())
                }
            }
            if (selectedItem.itemId != 0L) {
                Button(
                    onClick = {
                        deleteOnClick(selectedItem)
                        closeBottomSheet()
                    },
                    modifier = Modifier.weight(1f),
                    shape = MaterialTheme.shapes.extraSmall,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer,
                    ),
                    contentPadding = PaddingValues(
                        horizontal = dRes(R.dimen.asbs_button_padding_horizontal),
                        vertical = dRes(R.dimen.asbs_button_padding_vertical)
                    ),
                ) {
                    Text(text = deleteLabel.uppercase())
                }
            }
        }
        Spacer(modifier = Modifier.height(dRes(R.dimen.bs_bottom_spacer)))
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
                ItemInfo(
                    item = itemChecked,
                    surfaceOnClick = { },
                )
                ItemInfo(
                    item = itemUnchecked,
                    surfaceOnClick = { },
                )
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
                ItemInfo(
                    item = defaultItem,
                    surfaceOnClick = { },
                )
                ItemInfo(
                    item = defaultItem,
                    surfaceOnClick = { },
                )
            }
        }
    }
}

@Preview
@Composable
private fun ListInfoPreview() {
    PreviewUtil.run {
        Preview {
            ListInfo(
                itemList = halfCheckedItemList,
                itemListOnClick = { },
                displayOptions = true,
                optionOnClick = { },
            )
        }
    }
}

@Preview
@Composable
private fun ListInfoNoOptionsPreview() {
    PreviewUtil.run {
        Preview {
            ListInfo(
                itemList = emptyItemList,
                itemListOnClick = { },
                displayOptions = false,
                optionOnClick = { },
            )
        }
    }
}

@Preview
@Composable
private fun EditItemBottomSheetContentNewItemPreview() {
    PreviewUtil.run {
        Preview {
            Surface(modifier = Modifier.fillMaxWidth()) {
                EditItemBottomSheetContent(
                    closeBottomSheet = { },
                    selectedItem = defaultItem,
                    categories = emptyList(),
                    primaryLabel = "Primary Button",
                    primaryOnClick = { },
                    secondaryLabel = "Secondary Button",
                    secondaryOnClick = { },
                    deleteLabel = "Delete Button",
                    deleteOnClick = { },
                )
            }
        }
    }
}

@Preview
@Composable
private fun EditItemSheetContentExistingItemPreview() {
    PreviewUtil.run {
        Preview {
            Surface(modifier = Modifier.fillMaxWidth()) {
                EditItemBottomSheetContent(
                    closeBottomSheet = { },
                    selectedItem = defaultItem.editCopy(itemId = 10L),
                    categories = emptyList(),
                    primaryLabel = "Primary Button",
                    primaryOnClick = { },
                    secondaryLabel = "Secondary Button",
                    secondaryOnClick = { },
                    deleteLabel = "Delete Button",
                    deleteOnClick = { },
                )
            }
        }
    }
}

@Preview
@Composable
private fun EditItemSheetContentNullAddPreview() {
    PreviewUtil.run {
        Preview {
            Surface(modifier = Modifier.fillMaxWidth()) {
                EditItemBottomSheetContent(
                    closeBottomSheet = { },
                    selectedItem = defaultItem.editCopy(itemId = 10L),
                    categories = emptyList(),
                    primaryLabel = "Primary Button",
                    primaryOnClick = { },
                    secondaryLabel = null,
                    secondaryOnClick = null,
                    deleteLabel = "Delete Button",
                    deleteOnClick = { },
                )
            }
        }
    }
}
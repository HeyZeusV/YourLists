package com.heyzeusv.yourlists.add

import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.heyzeusv.yourlists.R
import com.heyzeusv.yourlists.database.models.Category
import com.heyzeusv.yourlists.database.models.DefaultItem
import com.heyzeusv.yourlists.database.models.ItemListWithItems
import com.heyzeusv.yourlists.util.AddDestination
import com.heyzeusv.yourlists.util.BottomSheet
import com.heyzeusv.yourlists.util.FabState
import com.heyzeusv.yourlists.util.ItemInfo
import com.heyzeusv.yourlists.util.PreviewUtil
import com.heyzeusv.yourlists.util.TextFieldWithLimit
import com.heyzeusv.yourlists.util.TopAppBarState
import com.heyzeusv.yourlists.util.dRes
import com.heyzeusv.yourlists.util.iRes
import com.heyzeusv.yourlists.util.pRes
import com.heyzeusv.yourlists.util.sRes
import com.heyzeusv.yourlists.util.saRes

@Composable
fun AddScreen(
    addVM: AddViewModel,
    navController: NavHostController,
    topAppBarSetup: (TopAppBarState) -> Unit,
    fabSetup: (FabState) -> Unit,
) {
    val topAppBarTitle = sRes(AddDestination.title)
    val defaultItemQuery by addVM.defaultItemQuery.collectAsStateWithLifecycle()
    val defaultItems by addVM.defaultItems.collectAsStateWithLifecycle(initialValue = emptyList())
    val itemLists by addVM.itemLists.collectAsStateWithLifecycle()
    val categories by addVM.categories.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = Unit) {
        topAppBarSetup(
            TopAppBarState(
                destination = AddDestination,
                title = topAppBarTitle,
                onNavPressed = { navController.navigateUp() }
            )
        )
    }
    LaunchedEffect(key1 = Unit) {
        fabSetup(FabState(isFabDisplayed = false))
    }
    AddScreen(
        defaultItemQuery = defaultItemQuery,
        updateDefaultItemQuery = { addVM.updateDefaultItemQuery(it) },
        defaultItems = defaultItems,
        categories = categories,
        saveAndAddOnClick = addVM::saveDefaultItemAndAddItem,
        addToListOnClick = addVM::addItem,
        deleteDefaultItemOnClick = addVM::deleteDefaultItem,
        itemLists = itemLists,
    )
}

@Composable
fun AddScreen(
    defaultItemQuery: String,
    updateDefaultItemQuery: (String) -> Unit,
    defaultItems: List<DefaultItem>,
    categories: List<Category>,
    saveAndAddOnClick: (DefaultItem) -> Unit,
    addToListOnClick: (DefaultItem) -> Unit,
    deleteDefaultItemOnClick: (DefaultItem) -> Unit,
    itemLists: List<ItemListWithItems>,
) {
    val listState = rememberLazyListState()
    val maxLength = iRes(R.integer.name_max_length)
    var isBottomSheetDisplayed by remember { mutableStateOf(false) }
    var selectedDefaultItem by remember { mutableStateOf(DefaultItem()) }
    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier
            .padding(all = dRes(R.dimen.as_padding_all))
            .fillMaxSize()
    ) {
        TextField(
            value = defaultItemQuery ,
            onValueChange = { if (it.length <= maxLength) updateDefaultItemQuery(it) },
            modifier = Modifier
                .padding(bottom = dRes(R.dimen.as_query_padding_bottom))
                .fillMaxWidth(),
            placeholder = { Text(text = sRes(R.string.as_item_query_placeholder)) },
            trailingIcon = {
                if (defaultItemQuery.isNotBlank()) {
                    IconButton(onClick = { updateDefaultItemQuery("") }) {
                        Icon(
                            painter = pRes(R.drawable.icon_cancel),
                            contentDescription = sRes(R.string.as_query_clear)
                        )
                    }
                }
            },
            supportingText = {
                Text(
                    text = "${defaultItemQuery.length}/$maxLength",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.End,
                    style = MaterialTheme.typography.bodySmall,
                )
            },
            singleLine = true,
        )
        if (defaultItemQuery.isNotBlank()) {
            Surface(
                modifier = Modifier
                    .padding(bottom = dRes(R.dimen.if_spacedBy))
                    .fillMaxWidth()
                    .heightIn(dRes(R.dimen.if_height_min))
                    .align(Alignment.CenterHorizontally)
                    .clickable {
                        focusManager.clearFocus()
                        isBottomSheetDisplayed = true
                        selectedDefaultItem = DefaultItem(name = defaultItemQuery)
                    },
            ) {
                Box(contentAlignment = Alignment.CenterStart) {
                    Text(
                        text = sRes(R.string.as_add_new, defaultItemQuery),
                        modifier = Modifier.padding(
                            horizontal = dRes(R.dimen.if_padding_horizontal),
                            vertical = dRes(R.dimen.if_padding_vertical),
                        ),
                        style = MaterialTheme.typography.titleMedium,
                    )
                }
            }
        }
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = listState,
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            items(defaultItems) {
                ItemInfo(
                    item = it,
                    surfaceOnClick = {
                        focusManager.clearFocus()
                        isBottomSheetDisplayed = true
                        selectedDefaultItem = it
                    },
                )
            }
        }
    }
    BottomSheet(
        isVisible = isBottomSheetDisplayed,
        updateIsVisible = { isBottomSheetDisplayed = it },
    ) {
        AddBottomSheetContent(
            selectedDefaultItem = selectedDefaultItem,
            categories = categories,
            saveAndAddOnClick = saveAndAddOnClick,
            addToListOnClick = addToListOnClick,
            deleteDefaultItemOnClick = deleteDefaultItemOnClick,
        )
    }
}

@Composable
fun AddBottomSheetContent(
    selectedDefaultItem: DefaultItem,
    categories: List<Category>,
    saveAndAddOnClick: (DefaultItem) -> Unit,
    addToListOnClick: (DefaultItem) -> Unit,
    deleteDefaultItemOnClick: (DefaultItem) -> Unit,
) {
    val focusManager = LocalFocusManager.current
    val unitList = saRes(R.array.unit_values).toList()

    var name by remember { mutableStateOf(selectedDefaultItem.name) }
    var category by remember { mutableStateOf(selectedDefaultItem.category) }
    // TODO: DecimalFormat
    var quantity by remember { mutableStateOf(selectedDefaultItem.quantity.toString()) }
    var unit by remember { mutableStateOf(selectedDefaultItem.unit) }
    var memo by remember { mutableStateOf(selectedDefaultItem.memo) }

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
                category = it
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
                onValueChange = { quantity = it },
                label = sRes(R.string.asbs_quantity),
                isError = false,
                maxLength = iRes(R.integer.quantity_max_length),
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
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
                    unit = it
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
                if (name.isBlank()) {
                    isNameError = true
                } else {
                    val updatedSelectedDefaultItem = selectedDefaultItem.copy(
                        name = name,
                        category = category,
                        quantity = quantity.toDouble(),
                        unit = unit,
                        memo = memo,
                    )
                    saveAndAddOnClick(updatedSelectedDefaultItem)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.extraSmall,
        ) {
            Text(text = sRes(R.string.asbs_save_add).uppercase())
        }
        Row(horizontalArrangement = Arrangement.spacedBy(dRes(R.dimen.bs_horizontal_spacedBy))) {
            OutlinedButton(
                onClick = {
                    if (name.isBlank()) {
                        isNameError = true
                    } else {
                        val updatedSelectedDefaultItem = selectedDefaultItem.copy(
                            name = name,
                            category = category,
                            quantity = quantity.toDouble(),
                            unit = unit,
                            memo = memo,
                        )
                        addToListOnClick(updatedSelectedDefaultItem)
                    }
                },
                modifier = Modifier.weight(1f),
                shape = MaterialTheme.shapes.extraSmall,
            ) {
                Text(text = sRes(R.string.asbs_add).uppercase())
            }
            if (selectedDefaultItem.itemId != 0L) {
                Button(
                    onClick = { deleteDefaultItemOnClick(selectedDefaultItem) },
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
                    Text(text = sRes(R.string.asbs_delete).uppercase())
                }
            }
        }
        Spacer(modifier = Modifier.height(dRes(R.dimen.bs_bottom_spacer)))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilteredDropDownMenu(
    value: String,
    onValueChanged: (String) -> Unit,
    label: String,
    options: List<String>,
    optionOnClick: (String) -> Unit,
    maxLength: Int,
    modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier,
    ) {
        TextField(
            value = value,
            onValueChange = { if (it.length <= maxLength) onValueChanged(it) },
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { expanded = it.isFocused }
                .menuAnchor(),
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            supportingText = {
                Row {
                    Text(
                        text = "${value.length}/$maxLength",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.End,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            },
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            singleLine = true,
        )
        val filteredOptions = options.filter { it.contains(value, ignoreCase = true) }
        if (filteredOptions.isNotEmpty()) {
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { }
            ) {
                filteredOptions.forEach { option ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = option,
                                maxLines = 1,
                            )
                        },
                        onClick = {
                            optionOnClick(option)
                            expanded = false
                        },
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun AddScreenPreview() {
    PreviewUtil.run {
        Preview {
            AddScreen(
                defaultItemQuery = "Preview",
                updateDefaultItemQuery = { },
                defaultItems = defaultItemList,
                categories = emptyList(),
                itemLists = emptyList(),
                saveAndAddOnClick = { },
                addToListOnClick = { },
                deleteDefaultItemOnClick = { },
            )
        }
    }
}

@Preview
@Composable
private fun AddScreenBlankQueryPreview() {
    PreviewUtil.run {
        Preview {
            AddScreen(
                defaultItemQuery = "",
                updateDefaultItemQuery = { },
                defaultItems = defaultItemList,
                categories = emptyList(),
                itemLists = emptyList(),
                saveAndAddOnClick = { },
                addToListOnClick = { },
                deleteDefaultItemOnClick = { },
            )
        }
    }
}

@Preview
@Composable
private fun AddBottomSheetContentNewItemPreview() {
    PreviewUtil.run {
        Preview {
            Surface(modifier = Modifier.fillMaxWidth()) {
                AddBottomSheetContent(
                    selectedDefaultItem = defaultItem,
                    categories = emptyList(),
                    saveAndAddOnClick = { },
                    addToListOnClick = { },
                    deleteDefaultItemOnClick = { },
                )
            }
        }
    }
}

@Preview
@Composable
private fun AddBottomSheetContentExistingItemPreview() {
    PreviewUtil.run {
        Preview {
            Surface(modifier = Modifier.fillMaxWidth()) {
                AddBottomSheetContent(
                    selectedDefaultItem = defaultItem.copy(itemId = 10L),
                    categories = emptyList(),
                    saveAndAddOnClick = { },
                    addToListOnClick = { },
                    deleteDefaultItemOnClick = { },
                )
            }
        }
    }
}

@Preview
@Composable
private fun FilteredDropDownMenuPreview() {
    PreviewUtil.run {
        Preview {
            FilteredDropDownMenu(
                value = "Preview",
                onValueChanged = { },
                label = "Preview Label",
                options = listOf("Preview1", "Preview2", "Preview3"),
                optionOnClick = { },
                maxLength = 999,
            )
        }
    }
}
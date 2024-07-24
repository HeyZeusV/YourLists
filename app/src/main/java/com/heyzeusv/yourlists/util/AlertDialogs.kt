package com.heyzeusv.yourlists.util

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import com.heyzeusv.yourlists.R
import com.heyzeusv.yourlists.util.FilterOption.ASC
import com.heyzeusv.yourlists.util.FilterOption.DESC

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputAlertDialog(
    display: Boolean,
    onDismissRequest: () -> Unit,
    title: String,
    maxLength: Int,
    onConfirm: (String) -> Unit,
    onDismiss: (() -> Unit)? = null,
) {
    var input by remember { mutableStateOf(TextFieldValue("")) }
    var isError by remember { mutableStateOf(false) }

    val focusRequester = FocusRequester()

    if (display) {
        LaunchedEffect(key1 = Unit) {
            focusRequester.requestFocus()
        }
        BasicAlertDialog(onDismissRequest = onDismissRequest) {
            Card(shape = MaterialTheme.shapes.medium) {
                Column(modifier = Modifier.padding(all = dRes(R.dimen.ad_padding_all)) ) {
                    Text(
                        text = title,
                        modifier = Modifier.padding(bottom = dRes(R.dimen.ad_title_padding_bottom)),
                        style = MaterialTheme.typography.headlineSmall
                    )
                    TextFieldWithLimit(
                        value = input,
                        onValueChange = { input = it },
                        label = sRes(R.string.iad_label),
                        isError = isError,
                        maxLength = maxLength,
                        modifier = Modifier
                            .padding(bottom = dRes(R.dimen.iad_input_padding_bottom))
                            .focusRequester(focusRequester),
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(
                            space = dRes(R.dimen.ad_buttons_spacedBy),
                            alignment = Alignment.End
                        ),
                    ) {
                        if (onDismiss != null) {
                            TextButton(onClick = onDismiss) {
                                Text(text = sRes(R.string.ad_cancel).uppercase())
                            }
                        }
                        TextButton(
                            onClick = {
                                if (input.text.isNotBlank()) {
                                    onConfirm(input.text)
                                } else {
                                    isError = true
                                }
                            }
                        ) {
                            Text(text = sRes(R.string.iad_confirm).uppercase())
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterAlertDialog(
    display: Boolean,
    title: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    filters: @Composable () -> Unit,
) {

    if (display) {
        BasicAlertDialog(onDismissRequest = onDismiss) {
            Card(shape = MaterialTheme.shapes.medium) {
                Column(modifier = Modifier.padding(all = dRes(R.dimen.ad_padding_all))) {
                    Text(
                        text = title,
                        modifier = Modifier.padding(bottom = dRes(R.dimen.ad_title_padding_bottom)),
                        style = MaterialTheme.typography.headlineSmall
                    )
                    filters()
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(
                            space = dRes(R.dimen.ad_buttons_spacedBy),
                            alignment = Alignment.End
                        ),
                    ) {
                        TextButton(onClick = onDismiss) {
                            Text(text = sRes(R.string.ad_cancel).uppercase())
                        }
                        TextButton(onClick = onConfirm) {
                            Text(text = sRes(R.string.fad_apply).uppercase())
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SingleFilterSelection(
    name: String,
    isSelected: Boolean,
    updateIsSelected: (Boolean) -> Unit,
    filterOption: FilterOption,
    updateFilterOption: (FilterOption) -> Unit,
) {
    Column {
        SelectionWithText(
            name = name,
            isSelected = isSelected,
            updateIsSelected = updateIsSelected,
            role = Role.Checkbox,
        )
        FilterOption.entries.forEach { option ->
            SelectionWithText(
                name = sRes(option.nameId),
                isSelected = filterOption == option,
                updateIsSelected = { if (it) updateFilterOption(option) },
                role = Role.RadioButton,
                modifier = Modifier.padding(start = dRes(R.dimen.fad_option_padding_start)),
                enabled = isSelected,
            )
        }
    }
}

@Composable
fun SelectionWithText(
    name: String,
    isSelected: Boolean,
    updateIsSelected: (Boolean) -> Unit,
    role: Role,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Row(
        modifier = modifier
            .height(dRes(R.dimen.fad_option_height))
            .fillMaxWidth()
            .toggleable(
                value = isSelected,
                enabled = enabled,
                onValueChange = { updateIsSelected(it) },
                role = role
            ),
        horizontalArrangement = Arrangement.spacedBy(dRes(R.dimen.fad_option_spacedBy_horizontal)),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (role == Role.Checkbox) {
            Checkbox(
                checked = isSelected,
                onCheckedChange = null,
            )
        }
        if (role == Role.RadioButton) {
            RadioButton(
                selected = isSelected,
                onClick = null,
                enabled = enabled,
            )
        }
        Text(
            text = name,
            modifier = Modifier.alpha(if (enabled) 1f else 0.38f),
            style = MaterialTheme.typography.titleMedium.copy(
                fontSize = TextUnit(18f, TextUnitType.Sp)
            ),
        )
    }
}

@Preview
@Composable
private fun InputAlertDialogPreview() {
    PreviewUtil.run {
        Preview {
            InputAlertDialog(
                display = true,
                onDismissRequest = { },
                title = "InputAlertDialogPreview",
                maxLength = 32,
                onConfirm = { },
                onDismiss = { }
             )
        }
    }
}

@Preview
@Composable
private fun InputAlertDialogNullDismissPreview() {
    PreviewUtil.run {
        Preview {
            InputAlertDialog(
                display = true,
                onDismissRequest = { },
                title = "InputAlertDialogPreview",
                maxLength = 32,
                onConfirm = { },
                onDismiss = null
            )
        }
    }
}

@Preview
@Composable
private fun FilterAlertDialogPreview() {
    PreviewUtil.run {
        Preview {
            Surface(modifier = Modifier.fillMaxSize()) { }
            FilterAlertDialog(
                display = true,
                title = "FilterAlertDialogPreview",
                onConfirm = { },
                onDismiss = { },
                filters = {
                    Column {
                        SingleFilterSelection(
                            name = "By selected",
                            isSelected = true,
                            updateIsSelected = { },
                            filterOption = ASC,
                            updateFilterOption = { }
                        )
                        SingleFilterSelection(
                            name = "By category",
                            isSelected = true,
                            updateIsSelected = { },
                            filterOption = ASC,
                            updateFilterOption = { }
                        )
                        SingleFilterSelection(
                            name = "By name",
                            isSelected = false,
                            updateIsSelected = { },
                            filterOption = DESC,
                            updateFilterOption = { }
                        )
                    }
                },
            )
        }
    }
}

@Preview
@Composable
private fun SingleFilterSelectionPreview() {
    PreviewUtil.run {
        Preview {
            Surface {
                SingleFilterSelection(
                    name = "SingleFilterSelectionPreview",
                    isSelected = true,
                    updateIsSelected = { },
                    filterOption = ASC,
                    updateFilterOption = { }
                )
            }
        }
    }
}

@Preview
@Composable
private fun SingleFilterSelectionUnselectedPreview() {
    PreviewUtil.run {
        Preview {
            Surface {
                SingleFilterSelection(
                    name = "SingleFilterSelectionPreview",
                    isSelected = false,
                    updateIsSelected = { },
                    filterOption = DESC,
                    updateFilterOption = { }
                )
            }
        }
    }
}

@Preview
@Composable
private fun SelectionWithTextCheckbox() {
    PreviewUtil.run {
        Preview {
            Surface {
                SelectionWithText(
                    name = "SelectionWithTextCheckbox",
                    isSelected = true,
                    updateIsSelected = { },
                    role = Role.Checkbox,
                )
            }
        }
    }
}

@Preview
@Composable
private fun SelectionWithTextRadio() {
    PreviewUtil.run {
        Preview {
            Surface {
                SelectionWithText(
                    name = "SelectionWithTextRadio",
                    isSelected = true,
                    updateIsSelected = { },
                    role = Role.RadioButton,
                )
            }
        }
    }
}
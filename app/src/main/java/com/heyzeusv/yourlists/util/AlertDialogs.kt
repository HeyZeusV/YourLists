package com.heyzeusv.yourlists.util

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.heyzeusv.yourlists.R

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
    var input by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }

    if (display) {
        BasicAlertDialog(onDismissRequest = onDismissRequest) {
            Card(shape = MaterialTheme.shapes.medium) {
                Column(modifier = Modifier.padding(all = dRes(R.dimen.iad_padding_all)) ) {
                    Text(
                        text = title,
                        modifier = Modifier.padding(bottom = dRes(R.dimen.iad_title_padding_bottom)),
                        style = MaterialTheme.typography.headlineSmall
                    )
                    TextFieldWithLimit(
                        value = input,
                        onValueChange = { input = it },
                        label = sRes(R.string.iad_label),
                        isError = isError,
                        maxLength = maxLength,
                        modifier = Modifier.padding(bottom = dRes(R.dimen.iad_input_padding_bottom)),
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(
                            space = dRes(R.dimen.iad_buttons_spacedBy),
                            alignment = Alignment.End
                        ),
                    ) {
                        if (onDismiss != null) {
                            TextButton(onClick = onDismiss) {
                                Text(text = sRes(R.string.iad_cancel).uppercase())
                            }
                        }
                        TextButton(
                            onClick = {
                                if (input.isNotBlank()) {
                                    onConfirm(input)
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
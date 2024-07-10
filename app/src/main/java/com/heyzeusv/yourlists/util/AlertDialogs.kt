package com.heyzeusv.yourlists.util

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
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
                    TextField(
                        value = input,
                        onValueChange = { if (it.length <= maxLength) input = it },
                        modifier = Modifier.padding(bottom = dRes(R.dimen.iad_input_padding_bottom)),
                        label = { Text(text = sRes(R.string.iad_label)) },
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
                                    text = "${input.length}/$maxLength",
                                    modifier = Modifier.weight(1f),
                                    textAlign = TextAlign.End,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        },
                        isError = isError,
                        singleLine = true,
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
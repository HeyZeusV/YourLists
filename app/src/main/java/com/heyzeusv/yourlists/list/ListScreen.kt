package com.heyzeusv.yourlists.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.tooling.preview.Preview
import com.heyzeusv.yourlists.R
import com.heyzeusv.yourlists.util.PreviewUtil
import com.heyzeusv.yourlists.util.dRes
import com.heyzeusv.yourlists.util.pRes
import com.heyzeusv.yourlists.util.sRes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListScreen() {
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    if (showBottomSheet) {
        ModalBottomSheet(onDismissRequest = { showBottomSheet = false },
            modifier = Modifier.fillMaxSize(),
            sheetState = sheetState,
            dragHandle = { },
        ) {
            ListScreenBottomSheetContent()
        }
    }
}

@Composable
fun ListInfo() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(dRes(R.dimen.card_radius)),
    ) {
        Column(modifier = Modifier.padding(all = dRes(R.dimen.lmi_padding_all))) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "List Title",
                    style = MaterialTheme.typography.headlineMedium
                )
                Icon(
                    painter = pRes(R.drawable.icon_options),
                    contentDescription = sRes(R.string.button_cdesc_options),
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(dRes(R.dimen.lmi_progress_spacedBy)),
                verticalAlignment = Alignment.CenterVertically
            ) {
                LinearProgressIndicator(
                    progress = { .75f },
                    modifier = Modifier
                        .height(dRes(R.dimen.lmi_progress_height))
                        .weight(1f),
                    trackColor = MaterialTheme.colorScheme.background,
                    strokeCap = StrokeCap.Round
                )
                Text(text = "8/10")
            }
        }
    }
}

@Composable
fun ListScreenBottomSheetContent(
) {
    Column(
        modifier = Modifier
            .padding(all = dRes(R.dimen.lmbs_padding_all))
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(dRes(R.dimen.lmbs_vertical_spacedBy)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ListScreenBottomSheetActions.entries.forEach {
            ListScreenBottomSheetAction(action = it)
        }
    }
}

@Composable
fun ListScreenBottomSheetAction(action: ListScreenBottomSheetActions) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(dRes(R.dimen.lmbs_horizontal_spacedBy))
    ) {
        Icon(
            painter = pRes(action.iconId),
            contentDescription = sRes(action.iconCdescId),
            modifier = Modifier.fillMaxHeight(),
            tint = action.color
        )
        Text(
            text = sRes(action.nameId),
            color = action.color,
            style = MaterialTheme.typography.titleLarge
        )
    }
}

@Preview
@Composable
private fun ListInfoPreview() {
    PreviewUtil.apply {
        Preview {
            ListInfo()
        }
    }
}

@Preview
@Composable
private fun ListScreenBottomSheetPreview() {
    PreviewUtil.apply {
        Preview {
            Surface(modifier = Modifier.fillMaxWidth()) {
                ListScreenBottomSheetContent()
            }
        }
    }
}

@Preview
@Composable
private fun ListScreenBottomSheetActionPreview() {
    PreviewUtil.apply {
        Preview {
            Surface(modifier = Modifier.fillMaxWidth()) {
                ListScreenBottomSheetAction(ListScreenBottomSheetActions.DELETE)
            }
        }
    }
}


package com.thedearbear.nnov.ui.composables

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.thedearbear.nnov.R

@Composable
fun VendorDialog(onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(15)
        ) {
            Icon(
                imageVector = Icons.Filled.Info,
                contentDescription = null,
                modifier = Modifier
                    .padding(vertical = 32.dp)
                    .align(Alignment.CenterHorizontally)
            )

            Text(
                text = stringResource(R.string.dialog_vendor_text),
                modifier = Modifier.padding(
                    start = 32.dp,
                    end = 32.dp,
                    bottom = 32.dp
                ),
                textAlign = TextAlign.Center
            )
        }
    }
}

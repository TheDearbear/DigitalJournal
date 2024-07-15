package com.thedearbear.nnov.ui.composables

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.thedearbear.nnov.R

@Composable
fun AccountLoadingDialog(
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.button_cancel))
            }
        },
        icon = {
            CircularProgressIndicator()
        },
        text = {
            Text(stringResource(R.string.account_loading))
        }
    )

}
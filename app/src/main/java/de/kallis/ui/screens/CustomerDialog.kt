package de.kallis.ui.screens

import android.app.Activity
import android.content.Intent
import android.provider.ContactsContract
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext   // <-- hier ist der wichtige Import
import androidx.compose.ui.unit.dp

@Composable
fun CustomerDialog(
    onOk: (String) -> Unit,
    onCancel: () -> Unit
) {
    var customerName by remember { mutableStateOf("") }
    val context = LocalContext.current

    val contactPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            val uri = result.data?.data ?: return@rememberLauncherForActivityResult
            context.contentResolver.query(
                uri,
                arrayOf(ContactsContract.Contacts.DISPLAY_NAME),
                null, null, null
            )?.use { cursor ->
                if (cursor.moveToFirst()) {
                    customerName = cursor.getString(0)
                }
            }
        }
    }

    AlertDialog(
        onDismissRequest = onCancel,
        title = { Text("Kunde auswählen") },
        text = {
            Column {
                TextField(
                    value = customerName,
                    onValueChange = { customerName = it },
                    label = { Text("Name") }
                )
                Spacer(Modifier.height(16.dp))
                Button(onClick = {
                    val intent = Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)
                    contactPicker.launch(intent)
                }) {
                    Text("Aus Adressbuch")
                }
            }
        },
        confirmButton = {
            Button(onClick = { onOk(customerName) }) { Text("OK") }
        },
        dismissButton = {
            Button(onClick = onCancel) { Text("Abbrechen") }
        }
    )
}

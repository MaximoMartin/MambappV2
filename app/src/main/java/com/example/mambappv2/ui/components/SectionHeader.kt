package com.example.mambappv2.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    icon: @Composable (() -> Unit)? = {
        Icon(Icons.Default.Info, contentDescription = null)
    }
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (icon != null) icon()

        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium
        )
    }
}

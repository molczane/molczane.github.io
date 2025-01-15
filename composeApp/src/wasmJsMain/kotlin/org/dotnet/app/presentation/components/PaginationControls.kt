package org.dotnet.app.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dotnetwebapp.composeapp.generated.resources.Res
import dotnetwebapp.composeapp.generated.resources.arrow_back
import dotnetwebapp.composeapp.generated.resources.arrow_forward
import org.jetbrains.compose.resources.painterResource

@Composable
fun PaginationControls(
    currentPage: Int,
    totalPages: Int,
    onPageSelected: (Int) -> Unit
) {
    if (totalPages <= 1) return

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Previous page button
        IconButton(
            onClick = { onPageSelected(currentPage - 1) },
            enabled = currentPage > 1,
            modifier = Modifier.padding(horizontal = 4.dp)
        ) {
            Icon(
                painter = painterResource(Res.drawable.arrow_back),
                contentDescription = "Poprzednia strona"
            )
        }

        // Page numbers
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            val visiblePages = calculateVisiblePages(currentPage, totalPages)

            visiblePages.forEach { pageNum ->
                if (pageNum == -1) {
                    // Show ellipsis
                    Text(
                        "...",
                        modifier = Modifier.padding(horizontal = 8.dp),
                        style = MaterialTheme.typography.body1
                    )
                } else {
                    Button(
                        onClick = { onPageSelected(pageNum) },
                        colors = if (pageNum == currentPage) {
                            ButtonDefaults.buttonColors(
                                backgroundColor = MaterialTheme.colors.primary,
                                contentColor = MaterialTheme.colors.onPrimary
                            )
                        } else {
                            ButtonDefaults.buttonColors(
                                backgroundColor = MaterialTheme.colors.surface,
                                contentColor = MaterialTheme.colors.onSurface
                            )
                        },
                        modifier = Modifier.wrapContentWidth() // Adjusts width dynamically
                    ) {
                        Text(pageNum.toString())
                    }
                }
            }
        }

        // Next page button
        IconButton(
            onClick = { onPageSelected(currentPage + 1) },
            enabled = currentPage < totalPages,
            modifier = Modifier.padding(horizontal = 4.dp)
        ) {
            Icon(
                painter = painterResource(Res.drawable.arrow_forward),
                contentDescription = "Następna strona"
            )
        }
    }
}

private fun calculateVisiblePages(currentPage: Int, totalPages: Int): List<Int> {
    if (totalPages <= 7) {
        return (1..totalPages).toList()
    }

    val visiblePages = mutableListOf<Int>()

    // Always show first page
    visiblePages.add(1)

    if (currentPage > 3) {
        visiblePages.add(-1) // Add ellipsis
    }

    // Add pages around current page
    val start = maxOf(2, currentPage - 1)
    val end = minOf(totalPages - 1, currentPage + 1)

    for (i in start..end) {
        visiblePages.add(i)
    }

    if (currentPage < totalPages - 2) {
        visiblePages.add(-1) // Add ellipsis
    }

    // Always show last page
    visiblePages.add(totalPages)

    return visiblePages
}
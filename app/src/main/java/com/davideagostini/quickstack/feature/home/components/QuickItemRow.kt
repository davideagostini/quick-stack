package com.davideagostini.quickstack.feature.home.components

import android.text.format.DateUtils
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.ContentPaste
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.davideagostini.quickstack.R
import com.davideagostini.quickstack.domain.model.QuickItem
import com.davideagostini.quickstack.domain.model.QuickItemType
import java.util.Calendar

@Composable
fun QuickItemRow(
    item: QuickItem,
    index: Int,
    count: Int,
    onClick: () -> Unit,
) {
    val shape = listItemShape(index = index, count = count)
    val verticalPadding = when {
        count == 1 -> PaddingValues(horizontal = 16.dp, vertical = 4.dp)
        index == 0 -> PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 1.dp)
        index == count - 1 -> PaddingValues(start = 16.dp, end = 16.dp, top = 1.dp, bottom = 4.dp)
        else -> PaddingValues(horizontal = 16.dp, vertical = 1.dp)
    }
    val icon = quickItemIcon(item)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(verticalPadding)
            .clip(shape)
            .clickable(onClick = onClick),
        shape = shape,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(contentAlignment = Alignment.TopEnd) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.size(40.dp),
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = icon,
                            contentDescription = stringResource(itemIconDescription(item)),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        )
                    }
                }

                if (item.isPinned) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.size(16.dp),
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Outlined.PushPin,
                                contentDescription = stringResource(R.string.type_pinned),
                                tint = MaterialTheme.colorScheme.onTertiary,
                                modifier = Modifier.size(10.dp),
                            )
                        }
                    }
                }
            }

            androidx.compose.foundation.layout.Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp),
            ) {
                Text(
                    text = quickItemDisplayTitle(item),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface,
                )

                Text(
                    text = item.content,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                )

                Text(
                    text = quickItemMeta(item),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 6.dp),
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.size(20.dp),
            )
        }
    }
}

@Composable
fun QuickItemMetaText(
    item: QuickItem,
    modifier: Modifier = Modifier,
    style: TextStyle = LocalTextStyle.current,
    textAlign: TextAlign? = null,
) {
    Text(
        text = quickItemMeta(item),
        style = style,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = textAlign,
        modifier = modifier,
    )
}

@Composable
fun quickItemDisplayTitle(item: QuickItem): String = when (item.type) {
    QuickItemType.TEXT_NOTE -> stringResource(R.string.type_note)
    QuickItemType.CLIPBOARD -> stringResource(R.string.type_clipboard)
    QuickItemType.REMINDER -> stringResource(R.string.type_reminder)
    QuickItemType.TIMER -> stringResource(R.string.type_timer)
}

@Composable
fun quickItemMeta(item: QuickItem): String {
    val now = System.currentTimeMillis()
    val createdAt = relativeLabel(item.createdAt, now)

    return when {
        item.isCompleted -> stringResource(R.string.item_meta_completed_at, createdAt)
        item.isTriggeredActionable -> stringResource(R.string.item_meta_ready_now)
        item.isPendingSchedule && item.type == QuickItemType.REMINDER && item.scheduledAt != null && isTonightReminder(item.scheduledAt, now) -> {
            stringResource(R.string.item_meta_reminder_tonight)
        }
        item.isPendingSchedule && item.type == QuickItemType.REMINDER && item.scheduledAt != null -> {
            stringResource(R.string.item_meta_reminder_at, relativeLabel(item.scheduledAt, now))
        }
        item.isPendingSchedule && item.type == QuickItemType.TIMER && item.scheduledAt != null -> {
            stringResource(R.string.item_meta_timer_at, relativeLabel(item.scheduledAt, now))
        }
        item.isPinned -> stringResource(R.string.item_meta_pinned_at, createdAt)
        else -> stringResource(R.string.item_meta_saved_at, createdAt)
    }
}

private fun relativeLabel(timestamp: Long, now: Long): String =
    DateUtils.getRelativeTimeSpanString(
        timestamp,
        now,
        DateUtils.MINUTE_IN_MILLIS,
        DateUtils.FORMAT_ABBREV_RELATIVE,
    ).toString()

private fun isTonightReminder(scheduledAt: Long, now: Long): Boolean {
    val scheduledCalendar = Calendar.getInstance().apply { timeInMillis = scheduledAt }
    val nowCalendar = Calendar.getInstance().apply { timeInMillis = now }

    return scheduledCalendar.get(Calendar.YEAR) == nowCalendar.get(Calendar.YEAR) &&
        scheduledCalendar.get(Calendar.DAY_OF_YEAR) == nowCalendar.get(Calendar.DAY_OF_YEAR) &&
        scheduledCalendar.get(Calendar.HOUR_OF_DAY) == 20 &&
        scheduledCalendar.get(Calendar.MINUTE) == 0 &&
        scheduledAt > now
}

private fun listItemShape(index: Int, count: Int): Shape = when {
    count <= 1 -> RoundedCornerShape(28.dp)
    index == 0 -> RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp, bottomStart = 10.dp, bottomEnd = 10.dp)
    index == count - 1 -> RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp, bottomStart = 28.dp, bottomEnd = 28.dp)
    else -> RoundedCornerShape(10.dp)
}

fun quickItemIcon(item: QuickItem): ImageVector = when (item.type) {
    QuickItemType.TEXT_NOTE -> Icons.Outlined.Description
    QuickItemType.CLIPBOARD -> Icons.Outlined.ContentPaste
    QuickItemType.REMINDER -> Icons.Outlined.Notifications
    QuickItemType.TIMER -> Icons.Outlined.Timer
}

private fun itemIconDescription(item: QuickItem): Int = when (item.type) {
    QuickItemType.TEXT_NOTE -> R.string.type_note
    QuickItemType.CLIPBOARD -> R.string.type_clipboard
    QuickItemType.REMINDER -> R.string.type_reminder
    QuickItemType.TIMER -> R.string.type_timer
}

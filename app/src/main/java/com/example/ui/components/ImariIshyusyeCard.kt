package com.example.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.AnnouncementEntity
import com.example.ui.theme.IkibinaDarkGray
import com.example.ui.theme.IkibinaGold
import com.example.ui.theme.IkibinaGoldDeep
import com.example.ui.theme.IkibinaGoldLight
import com.example.ui.theme.IkibinaSurfaceDark
import java.text.NumberFormat
import java.util.Locale

@Composable
fun ImariIshyusyeCard(
    communalAvailableAmount: Double,
    announcement: AnnouncementEntity?,
    modifier: Modifier = Modifier
) {
    // Allows alternating between IMARI ISHYUSYE and ANNOUNCEMENT
    var showAnnouncement by remember { mutableStateOf(false) }
    val formatter = remember { NumberFormat.getNumberInstance(Locale.US) }
    val formattedAmount = formatter.format(communalAvailableAmount.toLong())
    val maxLoanLimit = formatter.format((communalAvailableAmount * 0.80).toLong())

    Card(
        modifier = modifier
            .testTag("imari_ishyusye_card")
            .fillMaxWidth()
            .clickable(enabled = announcement != null) {
                showAnnouncement = !showAnnouncement
            },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = IkibinaSurfaceDark),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            IkibinaSurfaceDark,
                            IkibinaDarkGray
                        )
                    )
                )
                .padding(20.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Header with badge and switch affordance
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(IkibinaGold),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (showAnnouncement) Icons.Default.Campaign else Icons.Default.WorkspacePremium,
                                contentDescription = null,
                                tint = IkibinaDarkGray,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (showAnnouncement) "📢 ITANGAZO RY'ITSINDA" else "💰 ASIGAYE MURI RUSANGE",
                            color = IkibinaGold,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                    }

                    if (announcement != null) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White.copy(alpha = 0.1f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.SwapHoriz,
                                contentDescription = "Hindura amakuru",
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (showAnnouncement) "Reba Imari" else "Itangazo",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                AnimatedContent(
                    targetState = showAnnouncement,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    label = "imari_content"
                ) { isAnnounce ->
                    if (isAnnounce && announcement != null) {
                        Column {
                            Text(
                                text = announcement.title,
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = announcement.content,
                                color = Color.White.copy(alpha = 0.85f),
                                fontSize = 14.sp,
                                lineHeight = 20.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Ryanditswe na: ${announcement.authorName}",
                                color = IkibinaGoldLight,
                                fontSize = 11.sp
                            )
                        }
                    } else {
                        Column {
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(
                                    text = "RWF ",
                                    color = IkibinaGold,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = formattedAmount,
                                    color = Color.White,
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = (-0.5).sp
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color.White.copy(alpha = 0.08f))
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Column {
                                    Text(
                                        text = "AMAFARANGA AHARI KURI BOSE — ushobora gusaba kugeza kuri 80% bitewe n'amategeko y'itsinda.",
                                        color = Color.White.copy(alpha = 0.9f),
                                        fontSize = 12.sp,
                                        lineHeight = 16.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Inguzanyo ntarengwa ishoboka ubu: RWF $maxLoanLimit",
                                        color = IkibinaGold,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

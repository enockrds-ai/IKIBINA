package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.IkibinaGold
import com.example.ui.theme.IkibinaGoldLight
import com.example.ui.theme.IkibinaGreen
import com.example.ui.theme.IkibinaGreenLight
import com.example.ui.theme.IkibinaRed
import com.example.ui.theme.IkibinaRedLight

@Composable
fun StatusBadge(
    status: String,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor, label) = when (status.uppercase()) {
        "APPROVED", "ACTIVE", "PAID" -> Triple(
            IkibinaGreenLight,
            IkibinaGreen,
            when (status.uppercase()) {
                "APPROVED" -> "BYEMEJWE"
                "ACTIVE" -> "ARAKORA"
                "PAID" -> "CYISHYUWE"
                else -> status
            }
        )
        "PENDING", "PENDING_PAYMENT" -> Triple(
            IkibinaGoldLight,
            Color(0xFFB45309),
            if (status == "PENDING_PAYMENT") "KIRARANE" else "BITEGEREJE"
        )
        "REJECTED" -> Triple(
            IkibinaRedLight,
            IkibinaRed,
            "CYANZE"
        )
        "INACTIVE" -> Triple(
            Color(0xFFE2E8F0),
            Color(0xFF64748B),
            "YAHAGARITSWE"
        )
        "WAIVED" -> Triple(
            Color(0xFFE0F2FE),
            Color(0xFF0369A1),
            "CYAHAWE IMBABABAZI"
        )
        else -> Triple(
            Color(0xFFE2E8F0),
            Color(0xFF475569),
            status
        )
    }

    Box(
        modifier = modifier
            .background(color = bgColor, shape = RoundedCornerShape(12.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = label,
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

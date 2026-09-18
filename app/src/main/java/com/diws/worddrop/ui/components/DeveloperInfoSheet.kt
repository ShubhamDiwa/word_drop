package com.diws.worddrop.ui.components

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.automirrored.rounded.Launch
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.diws.worddrop.BuildConfig
import com.diws.worddrop.R
import com.diws.worddrop.ui.theme.DarkBackground
import com.diws.worddrop.ui.theme.DarkSurfaceContainer
import com.diws.worddrop.ui.theme.DarkSurfaceHigh
import com.diws.worddrop.ui.theme.PrimaryPurple
import com.diws.worddrop.ui.theme.SecondaryTeal
import com.diws.worddrop.ui.theme.TextPrimary
import com.diws.worddrop.ui.theme.TextSecondary
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback

// ─────────────────────────────────────────────────────────────────────────────
// Contact social link data
// ─────────────────────────────────────────────────────────────────────────────

private data class ContactLink(
    val label: String,
    val handle: String,
    val url: String,
    val iconVector: ImageVector? = null,
    val iconRes: Int? = null,               // for custom painter (Instagram)
    val containerColor: Color,
    val iconTint: Color,
    val isEmail: Boolean = false
)

private val contactLinks = listOf(
    ContactLink(
        label = "Instagram",
        handle = "@shubhammam_",
        url = "https://www.instagram.com/shubhammam_?stkn=MXd4eDN5a3Izd3NkZA==",
        iconRes = R.drawable.ic_instagram,   // see note below
        containerColor = Color(0xFF3A1A2E),
        iconTint = Color(0xFFE1306C)
    ),
    ContactLink(
        label = "LinkedIn",
        handle = "Shubham Diwakar",
        url = "https://www.linkedin.com/in/shubham-android-developer",
        iconRes = R.drawable.ic_linkedin,
        containerColor = Color(0xFF0A1E30),
        iconTint = Color(0xFF0A66C2)
    ),
    ContactLink(
        label = "Email",
        handle = "shubhamdiwakar2698@gmail.com",
        url = "mailto:shubhamdiwakar2698@gmail.com",
        iconVector = Icons.Rounded.Email,
        containerColor = Color(0xFF1A1E2E),
        iconTint = SecondaryTeal,
        isEmail = true
    )
)

// ─────────────────────────────────────────────────────────────────────────────
// Sheet
// ─────────────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeveloperInfoSheet(
    onDismissRequest: () -> Unit,
    rewardAdUnitId: String = BuildConfig.ADMOB_REWARDED_ID
) {
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var rewardedAd by remember { mutableStateOf<RewardedAd?>(null) }
    var isLoadingAd by remember { mutableStateOf(false) }

    fun loadAd() {
        isLoadingAd = true
        RewardedAd.load(
            context,
            rewardAdUnitId,
            AdRequest.Builder().build(),
            object : RewardedAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedAd) {
                    rewardedAd = ad
                    isLoadingAd = false
                    Log.d("DeveloperInfoSheet", "Rewarded ad loaded.")
                }
                override fun onAdFailedToLoad(error: LoadAdError) {
                    rewardedAd = null
                    isLoadingAd = false
                    Log.e("DeveloperInfoSheet", "Failed to load rewarded ad: ${error.message}")
                }
            }
        )
    }

    LaunchedEffect(Unit) { loadAd() }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        containerColor = DarkSurfaceContainer,
        contentColor = TextPrimary
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp, top = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            // ── Header ──────────────────────────────────────────────────
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "Contact Us",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextPrimary
                )
                Text(
                    text = "Reach out — we'd love to hear from you!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }

            HorizontalDivider(color = DarkSurfaceHigh, thickness = 1.dp)

            // ── Social links ─────────────────────────────────────────────
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                contactLinks.forEach { link ->
                    ContactRow(link = link)
                }
            }

            HorizontalDivider(color = DarkSurfaceHigh, thickness = 1.dp)

            // ── Support button ────────────────────────────────────────────
            Button(
                onClick = {
                    val activity = context as? Activity
                    if (rewardedAd != null && activity != null) {
                        rewardedAd?.show(activity) { _ ->
                            Toast.makeText(
                                context,
                                "Thank you for supporting Word Drop! ❤️",
                                Toast.LENGTH_LONG
                            ).show()
                            loadAd()
                        }
                    } else if (isLoadingAd) {
                        Toast.makeText(context, "Loading video ad, please wait...", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Ad not ready yet. Retrying...", Toast.LENGTH_SHORT).show()
                        loadAd()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryPurple,
                    contentColor = DarkBackground
                )
            ) {
                if (isLoadingAd) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = DarkBackground,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("Loading Ad...", fontWeight = FontWeight.Bold, color = DarkBackground)
                } else {
                    Icon(
                        imageVector = Icons.Rounded.Favorite,
                        contentDescription = null,
                        tint = DarkBackground
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Support Developer (Watch Ad)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = DarkBackground
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Single contact row
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun ContactRow(link: ContactLink) {
    val context = LocalContext.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(DarkSurfaceHigh)
            .clickable {
                try {
                    val intent = if (link.isEmail) {
                        Intent(Intent.ACTION_SENDTO, Uri.parse(link.url)).apply {
                            putExtra(Intent.EXTRA_EMAIL, arrayOf("shubhamdiwakar2698@gmail.com"))
                            putExtra(Intent.EXTRA_SUBJECT, "Word Drop - Feedback")
                        }
                    } else {
                        Intent(Intent.ACTION_VIEW, Uri.parse(link.url))
                    }
                    context.startActivity(intent)
                } catch (_: Exception) {
                    Toast.makeText(context, "Could not open ${link.label}", Toast.LENGTH_SHORT).show()
                }
            }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Icon circle
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(link.containerColor),
            contentAlignment = Alignment.Center
        ) {
            when {
                link.iconVector != null -> Icon(
                    imageVector = link.iconVector,
                    contentDescription = link.label,
                    tint = link.iconTint,
                    modifier = Modifier.size(22.dp)
                )
                link.iconRes != null -> Icon(
                    painter = painterResource(id = link.iconRes),
                    contentDescription = link.label,
                    tint = link.iconTint,
                    modifier = Modifier.size(22.dp)
                )
            }
        }

        // Labels
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = link.label,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                text = link.handle,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
        }

        // Launch arrow
        Icon(
            imageVector = Icons.AutoMirrored.Rounded.Launch,
            contentDescription = "Open",
            tint = TextSecondary,
            modifier = Modifier.size(16.dp)
        )
    }
}

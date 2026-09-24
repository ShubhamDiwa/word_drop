package com.diws.wordzip.ui.onboarding

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccessTime
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.MenuBook
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.diws.wordzip.ui.theme.LightBackground
import com.diws.wordzip.ui.theme.LightSurface
import com.diws.wordzip.ui.theme.LightSurfaceContainer
import com.diws.wordzip.ui.theme.LightSurfaceHigh
import com.diws.wordzip.ui.theme.OutlineColorLight
import com.diws.wordzip.ui.theme.PrimaryTerracotta
import com.diws.wordzip.ui.theme.TextPrimaryLight
import com.diws.wordzip.ui.theme.TextSecondaryLight
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

// ─────────────────────────────────────────────────────────────────────────────
// Particle system
// ─────────────────────────────────────────────────────────────────────────────

private val dropColors = listOf(
    Color(0xFFD86B4D), // Terracotta
    Color(0xFFFFDBCE), // Soft peach
    Color(0xFFD69A55), // Warm gold
    Color(0xFF5BA773), // Natural green
    Color(0xFFFFB74D), // Amber
    Color(0xFFFF8A65), // Soft orange
    Color(0xFF81C784), // Light green
    Color(0xFFFFCC80), // Light amber
)

private data class Drop(
    var x: Float,
    var y: Float,
    var vx: Float,
    var vy: Float,
    val color: Color,
    val radius: Float,
    val isCircle: Boolean,       // true = circle, false = rounded rect (confetti strip)
    val rectW: Float,
    val rectH: Float,
    var rotation: Float,
    val rotationSpeed: Float,
    var alpha: Float = 1f,
    val startY: Float            // remember spawn Y for alpha calc
)

private fun spawnDrops(count: Int, screenW: Float, screenH: Float): List<Drop> =
    List(count) {
        val angle = Random.nextFloat() * 360f
        val speed = 18f + Random.nextFloat() * 28f
        val vx = cos(Math.toRadians(angle.toDouble())).toFloat() * speed
        // Bias initial velocity upward so particles arc nicely
        val vy = -speed * (0.6f + Random.nextFloat() * 0.8f)
        val isCircle = Random.nextBoolean()
        val r = 6f + Random.nextFloat() * 10f
        Drop(
            x = screenW / 2f + (Random.nextFloat() - 0.5f) * screenW * 0.3f,
            y = screenH * 0.88f,   // spawn near the Get Started button
            vx = vx,
            vy = vy,
            color = dropColors.random(),
            radius = r,
            isCircle = isCircle,
            rectW = r * (1.2f + Random.nextFloat() * 0.8f),
            rectH = r * 2.5f,
            rotation = Random.nextFloat() * 360f,
            rotationSpeed = (Random.nextFloat() - 0.5f) * 12f,
            startY = screenH * 0.88f
        )
    }

private const val GRAVITY = 1.4f
private const val ANIM_DURATION_MS = 2000L

// ─────────────────────────────────────────────────────────────────────────────
// Data model for info slides
// ─────────────────────────────────────────────────────────────────────────────

private data class OnboardingPage(
    val icon: ImageVector,
    val iconTint: Color,
    val iconBackground: Color,
    val title: String,
    val subtitle: String,
    val accentColor: Color,
    val gradientStart: Color,
    val gradientEnd: Color
)

private val infoPages = listOf(
    OnboardingPage(
        icon = Icons.Rounded.AutoAwesome,
        iconTint = Color(0xFFD86B4D),           // Terracotta
        iconBackground = Color(0xFFFFEDE7),      // Soft peach tint
        title = "Let Words Find You",
        subtitle = "Wordzip delivers a fresh vocabulary word to your phone every few hours — all day, every day. No effort needed.",
        accentColor = Color(0xFFD86B4D),
        gradientStart = Color(0xFFFFF3EE),
        gradientEnd = LightBackground
    ),
    OnboardingPage(
        icon = Icons.Rounded.MenuBook,
        iconTint = Color(0xFF0D8267),            // Deep teal
        iconBackground = Color(0xFFDDF5F0),      // Soft mint tint
        title = "Daily Wordzips",
        subtitle = "Learn up to 5 new words a day with full definitions, real pronunciations, and example sentences in context.",
        accentColor = Color(0xFF0D8267),
        gradientStart = Color(0xFFF0FAF8),
        gradientEnd = LightBackground
    ),
    OnboardingPage(
        icon = Icons.Rounded.Notifications,
        iconTint = Color(0xFFB45309),            // Rich amber
        iconBackground = Color(0xFFFFF4DE),      // Soft amber tint
        title = "Smart Notifications",
        subtitle = "Wordzip sends you new words throughout the day as notifications — so you learn naturally, right in your flow.",
        accentColor = Color(0xFFB45309),
        gradientStart = Color(0xFFFFF9EC),
        gradientEnd = LightBackground
    ),
    OnboardingPage(
        icon = Icons.Rounded.LocalFireDepartment,
        iconTint = Color(0xFF1B8755),            // Deep forest green
        iconBackground = Color(0xFFDDF4EA),      // Soft green tint
        title = "Build Your Streak",
        subtitle = "Come back daily, build your streak, and watch your vocabulary grow. Your future self will thank you.",
        accentColor = Color(0xFF1B8755),
        gradientStart = Color(0xFFF0FAF5),
        gradientEnd = LightBackground
    )
)

private val TOTAL_PAGES = infoPages.size + 1
private val SCHEDULE_PAGE_INDEX = infoPages.size

// ─────────────────────────────────────────────────────────────────────────────
// Root composable
// ─────────────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(onFinished: (notificationTimes: List<String>) -> Unit) {
    val pagerState = rememberPagerState(pageCount = { TOTAL_PAGES })
    val scope = rememberCoroutineScope()

    val selectedTimes = remember {
        mutableStateListOf("08:00", "12:00", "16:00", "20:00")
    }

    // ── Celebration state ──────────────────────────────────────────────────
    var celebrationActive by remember { mutableStateOf(false) }
    val drops = remember { mutableStateListOf<Drop>() }

    // When celebration fires: spawn drops, animate them, then navigate
    LaunchedEffect(celebrationActive) {
        if (!celebrationActive) return@LaunchedEffect
        // Navigation happens after the drop animation finishes
        delay(ANIM_DURATION_MS)
        onFinished(selectedTimes.toList())
    }

    val isLastPage = pagerState.currentPage == TOTAL_PAGES - 1
    val currentAccent = if (pagerState.currentPage < infoPages.size)
        infoPages[pagerState.currentPage].accentColor else PrimaryTerracotta

    Box(modifier = Modifier.fillMaxSize().background(LightBackground)) {

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
            userScrollEnabled = !celebrationActive  // lock swipe during celebration
        ) { index ->
            if (index < infoPages.size) {
                OnboardingPageContent(page = infoPages[index])
            } else {
                SchedulePageContent(
                    selectedTimes = selectedTimes,
                    onAddTime = { time ->
                        if (!selectedTimes.contains(time) && selectedTimes.size < 10) {
                            selectedTimes.add(time)
                            selectedTimes.sortWith(compareBy { it })
                        }
                    },
                    onRemoveTime = { time ->
                        if (selectedTimes.size > 1) selectedTimes.remove(time)
                    }
                )
            }
        }

        // Skip button
        if (!isLastPage && !celebrationActive) {
            TextButton(
                onClick = { onFinished(selectedTimes.toList()) },
                modifier = Modifier.align(Alignment.TopEnd).padding(top = 48.dp, end = 16.dp)
            ) {
                Text("Skip", color = TextSecondaryLight, fontSize = 15.sp, fontWeight = FontWeight.Medium)
            }
        }

        // Bottom controls
        if (!celebrationActive) {
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(horizontal = 28.dp, vertical = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(28.dp)
            ) {
                PageIndicator(
                    pageCount = TOTAL_PAGES,
                    currentPage = pagerState.currentPage,
                    accentColor = currentAccent
                )

                Button(
                    onClick = {
                        if (isLastPage) {
                            celebrationActive = true
                        } else {
                            scope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .shadow(
                            elevation = 8.dp,
                            shape = RoundedCornerShape(18.dp),
                            ambientColor = currentAccent.copy(alpha = 0.25f),
                            spotColor = currentAccent.copy(alpha = 0.35f)
                        ),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = currentAccent,
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = if (isLastPage) "Get Started" else "Next",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    )
                }
            }
        }

        // ── Celebration overlay ────────────────────────────────────────────
        if (celebrationActive) {
            CelebrationOverlay(
                modifier = Modifier.fillMaxSize(),
                drops = drops,
                onInit = { screenW, screenH ->
                    // Spawn particles on first frame
                    if (drops.isEmpty()) {
                        drops.addAll(spawnDrops(90, screenW, screenH))
                    }
                },
                onFrame = { dt ->
                    // Physics update per frame
                    val iter = drops.listIterator()
                    while (iter.hasNext()) {
                        val d = iter.next()
                        d.vy += GRAVITY * dt
                        d.x += d.vx * dt
                        d.y += d.vy * dt
                        d.rotation += d.rotationSpeed * dt
                        // Fade out in the lower half of the animation
                        val prog = (d.y - d.startY) / (d.startY * 0.6f)
                        d.alpha = (1f - prog * 0.6f).coerceIn(0f, 1f)
                    }
                }
            )

            // Celebration label
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(horizontal = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "🎉",
                    fontSize = 64.sp
                )
                Text(
                    text = "Let's Go!",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextPrimaryLight,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Your vocabulary journey starts now",
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextSecondaryLight,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Celebration canvas overlay
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun CelebrationOverlay(
    modifier: Modifier,
    drops: List<Drop>,
    onInit: (screenW: Float, screenH: Float) -> Unit,
    onFrame: (dt: Float) -> Unit
) {
    // Drive the animation with withFrameMillis
    var lastFrameMs by remember { mutableStateOf(0L) }
    var initialized by remember { mutableStateOf(false) }

    androidx.compose.foundation.Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        if (!initialized) {
            initialized = true
            onInit(w, h)
        }

        val nowMs = System.currentTimeMillis()
        if (lastFrameMs != 0L) {
            val dt = ((nowMs - lastFrameMs) / 16f).coerceIn(0.5f, 3f)
            onFrame(dt)
        }
        lastFrameMs = nowMs

        // Draw each particle
        drops.forEach { drop ->
            if (drop.alpha <= 0f) return@forEach
            val paint = androidx.compose.ui.graphics.Paint().apply {
                color = drop.color.copy(alpha = drop.alpha)
                isAntiAlias = true
            }
            if (drop.isCircle) {
                drawCircle(
                    color = drop.color.copy(alpha = drop.alpha),
                    radius = drop.radius,
                    center = Offset(drop.x, drop.y)
                )
            } else {
                rotate(drop.rotation, pivot = Offset(drop.x, drop.y)) {
                    drawRoundRect(
                        color = drop.color.copy(alpha = drop.alpha),
                        topLeft = Offset(drop.x - drop.rectW / 2f, drop.y - drop.rectH / 2f),
                        size = androidx.compose.ui.geometry.Size(drop.rectW, drop.rectH),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(drop.rectW / 2f)
                    )
                }
            }
        }
    }

    // Keep invalidating (recomposing) to drive the animation loop
    LaunchedEffect(Unit) {
        while (true) {
            delay(16) // ~60 fps
            // Trigger recompose by doing nothing — the Canvas reads System.currentTimeMillis() each frame
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Info slide
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun OnboardingPageContent(page: OnboardingPage) {
    val infiniteTransition = rememberInfiniteTransition(label = "icon_pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(page.gradientStart, page.gradientEnd),
                    startY = 0f, endY = 1200f
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
                .padding(bottom = 160.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(28.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .scale(scale)
                    .shadow(
                        elevation = 12.dp,
                        shape = RoundedCornerShape(32.dp),
                        ambientColor = page.accentColor.copy(alpha = 0.15f),
                        spotColor = page.accentColor.copy(alpha = 0.2f)
                    )
                    .clip(RoundedCornerShape(32.dp))
                    .background(page.iconBackground),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = page.icon,
                    contentDescription = null,
                    tint = page.iconTint,
                    modifier = Modifier.size(56.dp)
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = page.title,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextPrimaryLight,
                    textAlign = TextAlign.Center,
                    lineHeight = 34.sp
                )
                Text(
                    text = page.subtitle,
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextSecondaryLight,
                    textAlign = TextAlign.Center,
                    lineHeight = 26.sp
                )
            }

            Box(
                modifier = Modifier
                    .width(48.dp)
                    .height(3.dp)
                    .clip(CircleShape)
                    .background(page.accentColor.copy(alpha = 0.6f))
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Schedule slide
// ─────────────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun SchedulePageContent(
    selectedTimes: List<String>,
    onAddTime: (String) -> Unit,
    onRemoveTime: (String) -> Unit
) {
    var showTimePicker by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFFFFF3EE), LightBackground),
                    startY = 0f, endY = 1200f
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 28.dp)
                .padding(bottom = 160.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .shadow(
                        elevation = 10.dp,
                        shape = RoundedCornerShape(28.dp),
                        ambientColor = PrimaryTerracotta.copy(alpha = 0.15f),
                        spotColor = PrimaryTerracotta.copy(alpha = 0.2f)
                    )
                    .clip(RoundedCornerShape(28.dp))
                    .background(Color(0xFFFFEDE7)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.AccessTime,
                    contentDescription = null,
                    tint = PrimaryTerracotta,
                    modifier = Modifier.size(48.dp)
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "When Should We\nRemind You?",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextPrimaryLight,
                    textAlign = TextAlign.Center,
                    lineHeight = 34.sp
                )
                Text(
                    text = "Pick the times you want your daily wordzips. You can always change these in Settings.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextSecondaryLight,
                    textAlign = TextAlign.Center,
                    lineHeight = 24.sp
                )
            }

            Box(modifier = Modifier.fillMaxWidth()) {
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    selectedTimes.forEach { time ->
                        TimeChip(
                            time = time,
                            accentColor = PrimaryTerracotta,
                            canRemove = selectedTimes.size > 1,
                            onRemove = { onRemoveTime(time) }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    if (selectedTimes.size < 10) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(PrimaryTerracotta.copy(alpha = 0.10f))
                                .clickable { showTimePicker = true }
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Add,
                                    contentDescription = "Add time",
                                    tint = PrimaryTerracotta,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "Add time",
                                    color = PrimaryTerracotta,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }
            }

            Text(
                text = "${selectedTimes.size} reminder${if (selectedTimes.size != 1) "s" else ""} per day",
                style = MaterialTheme.typography.labelMedium,
                color = PrimaryTerracotta,
                fontWeight = FontWeight.Bold
            )
        }
    }

    if (showTimePicker) {
        val timePickerState = rememberTimePickerState(initialHour = 8, initialMinute = 0, is24Hour = true)
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            containerColor = LightSurface,
            title = { Text("Add Reminder Time", color = TextPrimaryLight, fontWeight = FontWeight.Bold) },
            text = {
                Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    TimePicker(
                        state = timePickerState,
                        colors = TimePickerDefaults.colors(
                            selectorColor = PrimaryTerracotta,
                            containerColor = LightSurfaceContainer,
                            periodSelectorBorderColor = PrimaryTerracotta,
                            periodSelectorSelectedContainerColor = PrimaryTerracotta,
                            periodSelectorUnselectedContainerColor = LightSurfaceHigh,
                            periodSelectorSelectedContentColor = Color.White,
                            periodSelectorUnselectedContentColor = TextPrimaryLight,
                            timeSelectorSelectedContainerColor = PrimaryTerracotta,
                            timeSelectorUnselectedContainerColor = LightSurfaceHigh,
                            timeSelectorSelectedContentColor = Color.White,
                            timeSelectorUnselectedContentColor = TextPrimaryLight
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val formatted = String.format(Locale.getDefault(), "%02d:%02d", timePickerState.hour, timePickerState.minute)
                        onAddTime(formatted)
                        showTimePicker = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryTerracotta,
                        contentColor = Color.White
                    )
                ) { Text("Add", fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) {
                    Text("Cancel", color = TextSecondaryLight)
                }
            }
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Time chip
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun TimeChip(time: String, accentColor: Color, canRemove: Boolean, onRemove: () -> Unit) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(LightSurface)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(
            imageVector = Icons.Rounded.AccessTime,
            contentDescription = null,
            tint = accentColor,
            modifier = Modifier.size(14.dp)
        )
        Text(text = time, color = TextPrimaryLight, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        if (canRemove) {
            Icon(
                imageVector = Icons.Rounded.Close,
                contentDescription = "Remove $time",
                tint = OutlineColorLight,
                modifier = Modifier.size(14.dp).clickable { onRemove() }
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Page indicator dots
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun PageIndicator(pageCount: Int, currentPage: Int, accentColor: Color) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(pageCount) { index ->
            val isSelected = index == currentPage
            val dotWidth by animateDpAsState(
                targetValue = if (isSelected) 28.dp else 8.dp,
                animationSpec = tween(300, easing = FastOutSlowInEasing),
                label = "dot_width"
            )
            val dotColor by animateColorAsState(
                targetValue = if (isSelected) accentColor else LightSurfaceHigh,
                animationSpec = tween(300),
                label = "dot_color"
            )
            Box(
                modifier = Modifier
                    .width(dotWidth)
                    .height(8.dp)
                    .clip(CircleShape)
                    .background(dotColor)
            )
        }
    }
}

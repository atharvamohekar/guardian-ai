package com.guardianai.app.ui.screens.dashboard

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.guardianai.app.R
import com.guardianai.app.domain.models.VitalsSample
import com.guardianai.app.ui.components.common.SOSButton
import com.guardianai.app.ui.components.glassmorphic.GlassCard
import com.guardianai.app.ui.components.glassmorphic.GlassType
import com.guardianai.app.ui.theme.*

@Composable
fun DashboardScreen(
    paddingValues: PaddingValues,
    onSOSClick: (String) -> Unit,
    onPredictionClick: (String) -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "dashboard_animation")

    // Simulated vitals data (will come from ViewModel)
    var currentVitals by remember {
        mutableStateOf(
            VitalsSample(
                id = "sample_1",
                heartRate = 72,
                spO2 = 98,
                stressScore = 25,
                steps = 8542,
                sleepHours = 7.5f,
                dataSource = com.guardianai.app.domain.models.DataSource.SIMULATED
            )
        )
    }

    val pagerState = rememberPagerState(pageCount = 5)

    // Heart rate pulse animation
    val heartScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "heart_pulse"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Header with greeting and SOS button
        HeaderSection(
            userName = "User", // Will come from UserProfile
            onSOSClick = { onSOSClick("emergency_${System.currentTimeMillis()}") }
        )

        // Vitals Grid (2x2)
        VitalsGrid(currentVitals = currentVitals, heartScale = heartScale)

        // Chart Section with tabs
        ChartSection(pagerState = pagerState)

        // Info text
        InfoSection()
    }
}

@Composable
private fun HeaderSection(
    userName: String,
    onSOSClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = stringResource(R.string.hello_user, userName),
                style = GuardianAITypography.NeonLarge,
                color = NeonAqua,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Today at ${kotlinx.datetime.Clock.System.now().toLocalDateTime().time}",
                style = GuardianAITypography.BodySmall,
                color = WhiteSecondary,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        SOSButton(
            onClick = onSOSClick,
            isHighRisk = false,
            size = 48f
        )
    }
}

@Composable
private fun VitalsGrid(
    currentVitals: VitalsSample,
    heartScale: Float
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Heart Rate Card
            VitalsCard(
                title = stringResource(R.string.heart_rate),
                value = "${currentVitals.heartRate}",
                unit = stringResource(R.string.bpm),
                color = ChartHeartRate,
                icon = Icons.Default.Favorite,
                modifier = Modifier.weight(1f),
                scale = if (currentVitals.heartRate > 100) 1.05f else heartScale
            )

            // SpO2 Card
            VitalsCard(
                title = stringResource(R.string.spo2),
                value = "${currentVitals.spO2}",
                unit = "%",
                color = ChartSpO2,
                icon = Icons.Default.Air,
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Stress Card
            VitalsCard(
                title = stringResource(R.string.stress),
                value = "${currentVitals.stressScore}",
                unit = "",
                color = ChartStress,
                icon = Icons.Default.Psychology,
                modifier = Modifier.weight(1f)
            )

            // Sleep Card
            VitalsCard(
                title = stringResource(R.string.sleep),
                value = "${currentVitals.sleepHours}",
                unit = stringResource(R.string.hours),
                color = ChartSleep,
                icon = Icons.Default.Nightlight,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun VitalsCard(
    title: String,
    value: String,
    unit: String,
    color: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier,
    scale: Float = 1f
) {
    GlassCard(
        glassType = GlassType.DEFAULT,
        padding = 20.dp,
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer { scaleX = scale; scaleY = scale }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = color,
                modifier = Modifier.size(32.dp)
            )

            Text(
                text = value,
                style = GuardianAITypography.MetricValue,
                color = White,
                textAlign = TextAlign.Center
            )

            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = unit,
                    style = GuardianAITypography.MetricLabel,
                    color = WhiteSecondary,
                    modifier = Modifier.padding(bottom = 2.dp)
                )
            }

            Text(
                text = title,
                style = GuardianAITypography.MetricLabel,
                color = WhiteSecondary,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun ChartSection(pagerState: androidx.compose.foundation.pager.PagerState) {
    val tabTitles = listOf(
        stringResource(R.string.hr_tab),
        stringResource(R.string.spo2_tab),
        stringResource(R.string.stress_tab),
        stringResource(R.string.sleep_tab),
        stringResource(R.string.steps_tab)
    )

    GlassCard(
        glassType = GlassType.DEFAULT,
        padding = 20.dp
    ) {
        Column {
            // Tab Row
            TabRow(
                selectedTabIndex = pagerState.currentPage,
                modifier = Modifier.fillMaxWidth(),
                containerColor = Color.Transparent,
                contentColor = WhiteSecondary,
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        modifier = Modifier.pagerTabIndicatorOffset(
                            pagerState = pagerState,
                            tabPositions = tabPositions
                        ),
                        color = NeonAqua,
                        height = 2.dp
                    )
                }
            ) {
                tabTitles.forEachIndexed { index, title ->
                    Tab(
                        selected = pagerState.currentPage == index,
                        onClick = {
                            // Will navigate to pager page
                        },
                        text = {
                            Text(
                                text = title,
                                style = GuardianAITypography.BodyMedium.copy(
                                    fontWeight = if (pagerState.currentPage == index) FontWeight.Bold else FontWeight.Normal
                                ),
                                color = if (pagerState.currentPage == index) NeonAqua else WhiteSecondary
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Chart Content (placeholder for now)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Analytics,
                        contentDescription = "Chart",
                        tint = NeonAqua.copy(alpha = 0.6f),
                        modifier = Modifier.size(48.dp)
                    )
                    Text(
                        text = "Chart data for ${tabTitles[pagerState.currentPage]}",
                        style = GuardianAITypography.BodyMedium,
                        color = WhiteSecondary,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
private fun InfoSection() {
    GlassCard(
        glassType = GlassType.DEFAULT,
        padding = 16.dp
    ) {
        Text(
            text = stringResource(R.string.metrics_refresh_info),
            style = GuardianAITypography.BodySmall,
            color = WhiteSecondary,
            textAlign = TextAlign.Center
        )
    }
}
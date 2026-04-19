package dev.gbenga.danfo

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex


fun createRoundedGutterNavbarPath(
    width: Float,
    height: Float,
    cornerRadius: Float,
    gutterRadius: Float,
    gutterCenterX: Float
): Path {
    return Path().apply {

        val left = 0f
        val top = 0f
        val right = width
        val bottom = height

        // Start from bottom-left corner
        moveTo(left + cornerRadius, bottom)

        // Bottom-left corner
        arcTo(
            rect = Rect(left, bottom - 2 * cornerRadius, left + 2 * cornerRadius, bottom),
            startAngleDegrees = 90f,
            sweepAngleDegrees = 90f,
            forceMoveTo = false
        )

        // Left side
        lineTo(left, top + cornerRadius)

        // Top-left corner
        arcTo(
            rect = Rect(left, top, left + 2 * cornerRadius, top + 2 * cornerRadius),
            startAngleDegrees = 180f,
            sweepAngleDegrees = 90f,
            forceMoveTo = false
        )

        // Line to gutter start
        val gutterWidth = gutterRadius * 2.5f
        lineTo(gutterCenterX - gutterWidth, top)

        // LEFT SIDE of gutter (concave arc)
        arcTo(
            rect = Rect(
                gutterCenterX - gutterRadius,
                -gutterRadius,
                gutterCenterX + gutterRadius,
                gutterRadius
            ),
            startAngleDegrees = 180f,
            sweepAngleDegrees = -180f,
            forceMoveTo = false
        )

        // Continue top edge
        lineTo(right - cornerRadius, top)

        // Top-right corner
        arcTo(
            rect = Rect(right - 2 * cornerRadius, top, right, top + 2 * cornerRadius),
            startAngleDegrees = 270f,
            sweepAngleDegrees = 90f,
            forceMoveTo = false
        )

        // Right side
        lineTo(right, bottom - cornerRadius)

        // Bottom-right corner
        arcTo(
            rect = Rect(right - 2 * cornerRadius, bottom - 2 * cornerRadius, right, bottom),
            startAngleDegrees = 0f,
            sweepAngleDegrees = 90f,
            forceMoveTo = false
        )

        // Bottom edge
        lineTo(left + cornerRadius, bottom)

        close()
    }
}

data class NavItem(
    val icon: ImageVector,
    val label: String
)

@Composable
fun CustomNavbar(
    items: List<NavItem>,
    selectedIndex: Int,
    highlightIndex: Int,
    onItemClick: (Int) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val path = createRoundedGutterNavbarPath(
                width = size.width,
                height = size.height,
                cornerRadius = 60f,
                gutterRadius = 10f,
                gutterCenterX = 10f
            )

            drawPath(path, color = Color.White)
        }

        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEachIndexed { index, item ->

                if (index == highlightIndex) {
                    // 🌟 Highlighted (bigger)
                    Box(
                        modifier = Modifier
                            .offset(y = (-20).dp)
                            .size(64.dp)
                            .background(Color.Blue, shape = CircleShape)
                            .clickable { onItemClick(index) },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(item.icon, contentDescription = item.label, tint = Color.White)
                    }
                } else {
                    Icon(
                        item.icon,
                        contentDescription = item.label,
                        modifier = Modifier
                            .size(28.dp)
                            .clickable { onItemClick(index) },
                        tint = if (index == selectedIndex) Color.Blue else Color.Gray
                    )
                }
            }
        }
    }
}



@Composable
fun FloatingGutterNavbar(
    items: List<NavItem>,
    selectedIndex: Int,
    highlightIndex: Int,
    onItemClick: (Int) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp)
            .padding(horizontal = 16.dp)
    ) {

        // 🎨 Background shape
        Canvas(modifier = Modifier.matchParentSize()) {
            val centerX = size.width * (highlightIndex + 0.5f) / items.size

            val path = createRoundedGutterNavbarPath(
                width = size.width,
                height = size.height,
                cornerRadius = 30f,
                gutterRadius = 55f,
                gutterCenterX = centerX
            )

            drawPath(path, color = Color.White)
        }

        // 🧩 Icons row
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {

            items.forEachIndexed { index, item ->

                if (index == highlightIndex) {

                    // 🌟 FLOATING BUTTON
                    Box(
                        modifier = Modifier
                            .zIndex(1f) // ensure above navbar
                            .offset(y = (-28).dp) // 👈 THIS makes it float
                            .size(64.dp)
                            .shadow(8.dp, CircleShape)
                            .background(Color.Blue, CircleShape)
                            .clickable { onItemClick(index) },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            item.icon,
                            contentDescription = item.label,
                            tint = Color.White
                        )
                    }

                } else {
                    Icon(
                        item.icon,
                        contentDescription = item.label,
                        modifier = Modifier
                            .size(26.dp)
                            .clickable { onItemClick(index) },
                        tint = if (index == selectedIndex) Color.Blue else Color.Gray
                    )
                }
            }
        }
    }
}



fun createArcGutterNavbarPath(
    width: Float,
    height: Float,
    cornerRadius: Float,
    gutterRadius: Float,
    centerX: Float,
    arcDepth: Float // 👈 how much of the arc is visible
): Path {

    return Path().apply {

        val left = 0f
        val top = 0f
        val right = width
        val bottom = height

        // Position the circle so ONLY its bottom shows
        val circleTop = top - (gutterRadius * 2 - arcDepth)

        val rect = Rect(
            left = centerX - gutterRadius,
            top = circleTop,
            right = centerX + gutterRadius,
            bottom = circleTop + 2 * gutterRadius
        )

        moveTo(left + cornerRadius, bottom)

        // Bottom-left corner
        arcTo(
            Rect(left, bottom - 2 * cornerRadius, left + 2 * cornerRadius, bottom),
            90f, 90f, false
        )

        // Left side
        lineTo(left, top + cornerRadius)

        // Top-left corner
        arcTo(
            Rect(left, top, left + 2 * cornerRadius, top + 2 * cornerRadius),
            180f, 90f, false
        )

        // Move to arc start
        val arcStartX = centerX - gutterRadius
        lineTo(arcStartX, top)

        // 👇 TRUE ARC (concave notch)
        arcTo(
            rect = rect,
            startAngleDegrees = 180f,
            sweepAngleDegrees = -180f, // negative = concave
            forceMoveTo = false
        )

        // Continue top edge
        lineTo(right - cornerRadius, top)

        // Top-right corner
        arcTo(
            Rect(right - 2 * cornerRadius, top, right, top + 2 * cornerRadius),
            270f, 90f, false
        )

        // Right side
        lineTo(right, bottom - cornerRadius)

        // Bottom-right corner
        arcTo(
            Rect(right - 2 * cornerRadius, bottom - 2 * cornerRadius, right, bottom),
            0f, 90f, false
        )

        close()
    }
}

@Composable
fun ProperFloatingNavbar(
    items: List<NavItem>,
    selectedIndex: Int,
    highlightIndex: Int,
    onItemClick: (Int) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp) // 👈 extra space for floating button
            .padding(horizontal = 16.dp)
    ) {

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp) // 👈 navbar stays LOWER
                .align(Alignment.BottomCenter)
        ) {
            val centerX = size.width * (highlightIndex + 0.5f) / items.size

            val path = createArcGutterNavbarPath(
                width = size.width,
                height = size.height,
                cornerRadius = 28f,
                gutterRadius = 60f,
                centerX = centerX,
                arcDepth = 220f // 👈 shallow but perfectly circular
            )

            drawPath(path, color = Color.White)
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
                .align(Alignment.BottomCenter),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEachIndexed { index, item ->

                if (index == highlightIndex) {

                    // 🌟 TRUE FLOATING BUTTON
                    Box(
                        modifier = Modifier
                            .zIndex(2f)
                            .offset(y = (-36).dp) // 👈 HIGHER than before
                            .size(64.dp)
                            .shadow(10.dp, CircleShape)
                            .background(Color.Blue, CircleShape)
                            .clickable { onItemClick(index) },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(item.icon, contentDescription = item.label, tint = Color.White)
                    }

                } else {
                    Icon(
                        item.icon,
                        contentDescription = item.label,
                        modifier = Modifier
                            .size(26.dp)
                            .clickable { onItemClick(index) },
                        tint = if (index == selectedIndex) Color.Blue else Color.Gray
                    )
                }
            }
        }
    }
}




@Composable
fun SyncedNavbar(
    items: List<NavItem>,
    selectedIndex: Int,
    highlightIndex: Int,
    onItemClick: (Int) -> Unit
) {
    val buttonSize = 64.dp

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp)
            .padding(horizontal = 16.dp)
    ) {

        var centerOffset by remember { mutableStateOf(Offset.Zero) }

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
                .align(Alignment.BottomCenter)
        ) {
            val centerX = size.width * (highlightIndex + 0.5f) / items.size

            val gutterRadius = 60f
            val arcDepth = 20f
            val top = 0f

            val circleTop = top - (gutterRadius * 2 - arcDepth)
            val circleCenterY = circleTop + gutterRadius

            // 👇 Save exact center for the button
            centerOffset = Offset(centerX, circleCenterY)

            val path = createArcGutterNavbarPath(
                width = size.width,
                height = size.height,
                cornerRadius = 28f,
                gutterRadius = gutterRadius,
                centerX = centerX,
                arcDepth = arcDepth
            )

            drawPath(path, color = Color.White)
        }

        // 🌟 PERFECTLY SYNCED FLOATING BUTTON
        Box(
            modifier = Modifier
                .offset {
                    IntOffset(
                        x = (centerOffset.x - buttonSize.toPx() / 2).toInt(),
                        y = (centerOffset.y - buttonSize.toPx() / 2).toInt()
                    )
                }
                .size(buttonSize)
                .zIndex(2f)
                .shadow(10.dp, CircleShape)
                .background(Color.Blue, CircleShape)
                .clickable { onItemClick(highlightIndex) },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                items[highlightIndex].icon,
                contentDescription = null,
                tint = Color.White
            )
        }

        // Other items
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
                .align(Alignment.BottomCenter),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEachIndexed { index, item ->
                if (index != highlightIndex) {
                    Icon(
                        item.icon,
                        contentDescription = item.label,
                        modifier = Modifier
                            .size(26.dp)
                            .clickable { onItemClick(index) },
                        tint = if (index == selectedIndex) Color.Blue else Color.Gray
                    )
                } else {
                    Spacer(modifier = Modifier.size(64.dp)) // keep spacing
                }
            }
        }
    }
}
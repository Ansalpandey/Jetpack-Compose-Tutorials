package com.smarttoolfactory.tutorial1_1basics.chapter3_layout

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.tutorial1_1basics.ui.Blue400
import com.smarttoolfactory.tutorial1_1basics.ui.BlueGrey400
import com.smarttoolfactory.tutorial1_1basics.ui.components.StyleableTutorialText

@Preview
@Composable
fun Tutorial3_2Screen0() {
    TutorialContent()
}

@Composable
private fun TutorialContent() {
    Column(modifier = Modifier.fillMaxSize()) {

        StyleableTutorialText(
            text = "A custom layout is created using **Layout** Composable. A **MeasurePolicy** " +
                    "is assigned to define the measure and layout behavior of a Layout.\n" +
                    "Layout and MeasurePolicy are the way\n" +
                    "Compose layouts (such as `Box`, `Column`, etc.) are built,\n" +
                    "and they can also be used to achieve custom layouts.\n\n" +
                    "During the Layout phase, the tree is traversed using the following 3 step algorithm:\n" +
                    "\n" +
                    "1-) Measure children: A node measures its children, if any.\n" +
                    "2-) Decide own size: Based on those measurements, a node decides on its own size.\n" +
                    "3-) Place children: Each child node is placed relative to a node’s own position.",
            bullets = false
        )

        /*
            Prints:
            // ! These values are on a Pixel 5 emulator
            🔥🔥 Depth-First Tree Traversal
            I  Parent Scope

            I  Child1 Scope
            I  Box Scope

            I  Child2 Outer Scope
            I  Child2 Inner Scope
            I  🍏 Child1 Measurement Scope minHeight: 138, maxHeight: 138
            I  contentHeight: 138, layoutHeight: 138

            I  🍏 Child2 Inner Measurement Scope minHeight: 0, maxHeight: 1054
            I  contentHeight: 52, layoutHeight: 52
            I  🍏 Child2 Outer Measurement Scope minHeight: 0, maxHeight: 1054
            I  contentHeight: 52, layoutHeight: 52

            I  🍏 Parent Measurement Scope minHeight: 0, maxHeight: 1054
            I  contentHeight: 190, layoutHeight: 190
            I  🍎 Parent Placement Scope
            I  🍎 Child1 Placement Scope
            I  🍎 Child2 Outer Placement Scope
            I  🍎 Child2 Inner Placement Scope
         */

        // label is for logging, they are not part of real custom
        // layouts
        MyLayout(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color.Green),
            label = "Parent"
        ) {
            println("Parent Scope")
            MyLayout(
                modifier = Modifier
                    .fillMaxWidth()
                    .size(50.dp)
                    .border(2.dp, Color.Red),
                label = "Child1"
            ) {
                println("Child1 Scope")

                // This Box is measured in range of min=50.dp, max=50.dp
                // because of parent size
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .background(Color.Red),
                    contentAlignment = Alignment.CenterStart
                ) {
                    println("Box Scope")
                    Text(text = "Box Content", color = Color.White)
                }
            }

            MyLayout(
                modifier = Modifier.border(2.dp, Blue400),
                label = "Child2 Outer"
            ) {
                println("Child2 Outer Scope")

                MyLayout(
                    modifier = Modifier.border(3.dp, BlueGrey400),
                    label = "Child2 Inner"
                ) {
                    println("Child2 Inner Scope")
                    Text("Child2 Bottom Content")
                }
            }
        }

        StyleableTutorialText(
            text = "In this example in with which Constraints content is measured is overridden." +
                    "And Composable out of bound of min=150.dp, max=300.dp is measured in min or " +
                    "max values of this range.",
            bullets = false
        )

        CustomConstrainLayout(
            modifier = Modifier.fillMaxSize()
        ) {
            BoxWithConstraints(
                modifier = Modifier
                    .width(50.dp)
                    .background(Color.Cyan)
            ) {
                Text(text = "min: $minWidth, max: $maxWidth")
            }
            BoxWithConstraints(
                modifier = Modifier
                    .width(250.dp)
                    .background(Color.Yellow)
            ) {
                Text(text = "min: $minWidth, max: $maxWidth")
            }

            BoxWithConstraints(
                modifier = Modifier
                    .width(350.dp)
                    .background(Color.Green)
            ) {
                Text(text = "min: $minWidth, max: $maxWidth")
            }
        }
    }
}


@Composable
private fun MyLayout(
    modifier: Modifier = Modifier,
    label: String,
    content: @Composable () -> Unit
) {

    // A custom layout is created using Layout Composable
    /*
       MeasurePolicy defines the measure and layout behavior of a [Layout].
       [Layout] and [MeasurePolicy] are the way
       Compose layouts (such as `Box`, `Column`, etc.) are built,
       and they can also be used to achieve custom layouts.
     */
    Layout(
        modifier = modifier,
        content = content
    ) { measurables, constraints ->

        /*
            During the Layout phase, the tree is traversed using the following 3 step algorithm:

            1-) Measure children: A node measures its children, if any.
            2-) Decide own size: Based on those measurements, a node decides on its own size.
            3-) Place children: Each child node is placed relative to a node’s own position.
         */

        // 🔥 1-) We measure Measurables Contents inside content lambda
        // with Constraints
        // ⚠️ Constraints are the range we measure them with depending on which
        // Size Modifier or Scroll Modifier this has range changes
        // You can check out this answer for to see which size modifier returns which
        // Constraints
        val placeables = measurables.map { measurable ->
            measurable.measure(
                // 🔥 This is for changing range min to 0, Modifier.width(100)
                // returns minWidth 255(dp*px) while our Composable(Text,Image) can be smaller
                constraints.copy(minWidth = 0, minHeight = 0)
            )
        }


        // 2-) After measuring each children we decide how big this Layout/Composable should be
        // Let's say we want to make a Column we need to set width to max of content Composables
        // while sum of content Composables
        val contentWidth = placeables.maxOf { it.width }
        val contentHeight = placeables.sumOf { it.height }

        // 🔥🔥 We calculated total content size however in some situations with Modifiers such as
        // Modifier.fillMaxSize we need to set Layout dimensions to match parent not
        // total dimensions of Content


        val layoutWidth = if (constraints.hasBoundedWidth && constraints.hasFixedWidth) {
            constraints.maxWidth
        } else {
            contentWidth.coerceIn(constraints.minWidth, constraints.maxWidth)
        }

        val layoutHeight = if (constraints.hasBoundedHeight && constraints.hasFixedHeight) {
            constraints.maxHeight
        } else {
            contentHeight.coerceIn(constraints.minHeight, constraints.maxHeight)
        }

        println(
            "🍏 $label Measurement Scope " +
                    "minHeight: ${constraints.minHeight}, " +
                    "maxHeight: ${constraints.maxHeight}\n" +
                    "contentHeight: $contentHeight, " +
                    "layoutHeight: $layoutHeight\n"
        )

        // 🔥 Layout dimensions should be in Constraints range we get from parent
        // otherwise this Layout is placed incorrectly
        layout(layoutWidth, layoutHeight) {

            // 3-) 🔥🔥 Place placeables or Composables inside content lambda accordingly
            // In this example we place like a Column vertically

            var y = 0

            println("🍎 $label Placement Scope")

            placeables.forEach { placeable: Placeable ->
                placeable.placeRelative(0, y)
                y += placeable.height
            }
        }
    }
}

@Composable
private fun CustomConstrainLayout(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Layout(
        modifier = modifier,
        content = content
    ) { measurables, constraints ->

        val placeables = measurables.map { measurable ->
            measurable.measure(

                // 🔥🔥 We override how Composables inside this content will be measured
                constraints.copy(
                    minWidth = 150.dp.roundToPx(),
                    maxWidth = 300.dp.roundToPx(),
                    minHeight = 0
                )
            )
        }

        val contentWidth = placeables.maxOf { it.width }
        val contentHeight = placeables.sumOf { it.height }

        val layoutWidth = if (constraints.hasBoundedWidth && constraints.hasFixedWidth) {
            constraints.maxWidth
        } else {
            contentWidth.coerceIn(constraints.minWidth, constraints.maxWidth)
        }

        val layoutHeight = if (constraints.hasBoundedHeight && constraints.hasFixedHeight) {
            constraints.maxHeight
        } else {
            contentHeight.coerceIn(constraints.minHeight, constraints.maxHeight)
        }

        println(
            "🚗 CustomConstrainLayout Measurement Scope " +
                    "minWidth: ${constraints.minWidth}, " +
                    "maxHeight: ${constraints.maxHeight}\n" +
                    "contentWidth: $contentWidth, " +
                    "layoutWidth: $layoutWidth\n"
        )

        layout(layoutWidth, layoutHeight) {

            var y = 0

            println("🚗🚗 CustomConstrainLayout Placement Scope")

            placeables.forEach { placeable: Placeable ->
                placeable.placeRelative(0, y)
                y += placeable.height
            }
        }
    }
}
package com.gedrocht.mosmena.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.gedrocht.mosmena.R
import com.gedrocht.mosmena.audio.DistanceMeasurement
import kotlin.math.min

/**
 * Custom view that visualizes the measured distance.
 *
 * The left side represents the phone. A vertical marker on the right represents
 * the nearest reflection.
 */
class DistanceRadarView @JvmOverloads constructor(
  context: Context,
  attributeSet: AttributeSet? = null
) : View(context, attributeSet) {

  private val surfacePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
    color = ContextCompat.getColor(context, R.color.mosmena_surface_variant)
    style = Paint.Style.FILL
  }

  private val outlinePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
    color = ContextCompat.getColor(context, R.color.mosmena_outline)
    style = Paint.Style.STROKE
    strokeWidth = 4f
  }

  private val directCouplingPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
    color = ContextCompat.getColor(context, R.color.mosmena_direct_coupling)
    style = Paint.Style.FILL
  }

  private val reflectionPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
    color = ContextCompat.getColor(context, R.color.mosmena_reflection)
    style = Paint.Style.FILL
  }

  private val labelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
    color = ContextCompat.getColor(context, R.color.mosmena_on_surface)
    textSize = 36f
  }

  private var latestDistanceMeasurement: DistanceMeasurement? = null

  /**
   * The visualization is intentionally capped at three meters because the
   * simple pulse-echo approach becomes unreliable on many phones beyond that.
   */
  private val maximumDisplayedDistanceInMeters = 3.0

  /**
   * Stores the latest measurement and requests a redraw.
   */
  fun showDistanceMeasurement(distanceMeasurement: DistanceMeasurement?) {
    latestDistanceMeasurement = distanceMeasurement
    invalidate()
  }

  /**
   * Redraws the radar whenever Android invalidates the view.
   */
  override fun onDraw(canvas: Canvas) {
    super.onDraw(canvas)

    val horizontalPaddingInPixels = 48f
    val verticalPaddingInPixels = 32f
    val drawingLeft = horizontalPaddingInPixels
    val drawingTop = verticalPaddingInPixels
    val drawingRight = width - horizontalPaddingInPixels
    val drawingBottom = height - verticalPaddingInPixels

    canvas.drawRoundRect(
      drawingLeft,
      drawingTop,
      drawingRight,
      drawingBottom,
      28f,
      28f,
      surfacePaint
    )
    canvas.drawRoundRect(
      drawingLeft,
      drawingTop,
      drawingRight,
      drawingBottom,
      28f,
      28f,
      outlinePaint
    )

    drawDistanceScale(
      canvas = canvas,
      drawingLeft = drawingLeft,
      drawingRight = drawingRight,
      drawingBottom = drawingBottom
    )

    val phoneLeft = drawingLeft + 28f
    val phoneRight = phoneLeft + 48f
    canvas.drawRoundRect(
      phoneLeft,
      drawingTop + 40f,
      phoneRight,
      drawingBottom - 40f,
      18f,
      18f,
      directCouplingPaint
    )

    val distanceMeasurement = latestDistanceMeasurement
    if (distanceMeasurement == null) {
      canvas.drawText(
        context.getString(R.string.distance_view_no_measurement),
        drawingLeft + 110f,
        drawingTop + 100f,
        labelPaint
      )
      return
    }

    val normalizedDistance =
      min(1.0, distanceMeasurement.measuredDistanceInMeters / maximumDisplayedDistanceInMeters)
    val reflectionMarkerLeft = (
      drawingLeft + 120f + (drawingRight - drawingLeft - 180f) * normalizedDistance
      ).toFloat()

    canvas.drawRect(
      reflectionMarkerLeft,
      drawingTop + 28f,
      reflectionMarkerLeft + 18f,
      drawingBottom - 28f,
      reflectionPaint
    )

    canvas.drawText(
      context.getString(
        R.string.distance_view_distance_label,
        distanceMeasurement.measuredDistanceInMeters
      ),
      reflectionMarkerLeft - 24f,
      drawingTop + 18f,
      labelPaint
    )

    canvas.drawText(
      context.getString(
        R.string.distance_view_confidence_label,
        distanceMeasurement.measurementConfidence * 100.0
      ),
      drawingLeft + 110f,
      drawingBottom - 20f,
      labelPaint
    )
  }

  /**
   * Draws a few simple guide labels under the visualization.
   */
  private fun drawDistanceScale(
    canvas: Canvas,
    drawingLeft: Float,
    drawingRight: Float,
    drawingBottom: Float
  ) {
    val labelValuesInMeters = listOf(0.5, 1.0, 2.0, 3.0)
    labelValuesInMeters.forEach { labelValueInMeters ->
      val normalizedDistance = labelValueInMeters / maximumDisplayedDistanceInMeters
      val labelPositionInPixels = (
        drawingLeft + 120f + (drawingRight - drawingLeft - 180f) * normalizedDistance
        ).toFloat()

      canvas.drawLine(
        labelPositionInPixels,
        drawingBottom - 60f,
        labelPositionInPixels,
        drawingBottom - 20f,
        outlinePaint
      )
      canvas.drawText(
        "${labelValueInMeters} m",
        labelPositionInPixels - 24f,
        drawingBottom + 8f,
        labelPaint
      )
    }
  }
}

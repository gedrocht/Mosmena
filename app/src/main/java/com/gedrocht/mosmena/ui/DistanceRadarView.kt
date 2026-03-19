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
    strokeWidth = OUTLINE_STROKE_WIDTH_IN_PIXELS
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
    textSize = LABEL_TEXT_SIZE_IN_PIXELS
  }

  private var latestDistanceMeasurement: DistanceMeasurement? = null

  /**
   * The visualization is intentionally capped at three meters because the
   * simple pulse-echo approach becomes unreliable on many phones beyond that.
   */
  private val maximumDisplayedDistanceInMeters = MAXIMUM_DISPLAYED_DISTANCE_IN_METERS

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

    val drawingBounds = createDrawingBounds()
    drawBackgroundPanel(canvas = canvas, drawingBounds = drawingBounds)
    drawDistanceScale(canvas = canvas, drawingBounds = drawingBounds)
    drawPhoneMarker(canvas = canvas, drawingBounds = drawingBounds)

    val distanceMeasurement = latestDistanceMeasurement
    if (distanceMeasurement == null) {
      drawEmptyState(canvas = canvas, drawingBounds = drawingBounds)
      return
    }

    drawReflectionMarker(
      canvas = canvas,
      drawingBounds = drawingBounds,
      distanceMeasurement = distanceMeasurement
    )
    drawMeasurementLabels(
      canvas = canvas,
      drawingBounds = drawingBounds,
      distanceMeasurement = distanceMeasurement
    )
  }

  /**
   * Builds the inner panel bounds that all drawing helpers share.
   */
  private fun createDrawingBounds(): DrawingBounds {
    return DrawingBounds(
      left = HORIZONTAL_PADDING_IN_PIXELS,
      top = VERTICAL_PADDING_IN_PIXELS,
      right = width - HORIZONTAL_PADDING_IN_PIXELS,
      bottom = height - VERTICAL_PADDING_IN_PIXELS
    )
  }

  /**
   * Draws the rounded background panel that contains the whole visualization.
   */
  private fun drawBackgroundPanel(
    canvas: Canvas,
    drawingBounds: DrawingBounds
  ) {
    canvas.drawRoundRect(
      drawingBounds.left,
      drawingBounds.top,
      drawingBounds.right,
      drawingBounds.bottom,
      PANEL_CORNER_RADIUS_IN_PIXELS,
      PANEL_CORNER_RADIUS_IN_PIXELS,
      surfacePaint
    )
    canvas.drawRoundRect(
      drawingBounds.left,
      drawingBounds.top,
      drawingBounds.right,
      drawingBounds.bottom,
      PANEL_CORNER_RADIUS_IN_PIXELS,
      PANEL_CORNER_RADIUS_IN_PIXELS,
      outlinePaint
    )
  }

  /**
   * Draws the left-side marker that represents the phone itself.
   */
  private fun drawPhoneMarker(
    canvas: Canvas,
    drawingBounds: DrawingBounds
  ) {
    val phoneLeft = drawingBounds.left + PHONE_LEFT_MARGIN_IN_PIXELS
    val phoneRight = phoneLeft + PHONE_WIDTH_IN_PIXELS

    canvas.drawRoundRect(
      phoneLeft,
      drawingBounds.top + PHONE_VERTICAL_MARGIN_IN_PIXELS,
      phoneRight,
      drawingBounds.bottom - PHONE_VERTICAL_MARGIN_IN_PIXELS,
      PHONE_CORNER_RADIUS_IN_PIXELS,
      PHONE_CORNER_RADIUS_IN_PIXELS,
      directCouplingPaint
    )
  }

  /**
   * Draws the placeholder text shown before the user has taken a measurement.
   */
  private fun drawEmptyState(
    canvas: Canvas,
    drawingBounds: DrawingBounds
  ) {
    canvas.drawText(
      context.getString(R.string.distance_view_no_measurement),
      drawingBounds.left + EMPTY_STATE_TEXT_LEFT_OFFSET_IN_PIXELS,
      drawingBounds.top + EMPTY_STATE_TEXT_TOP_OFFSET_IN_PIXELS,
      labelPaint
    )
  }

  /**
   * Draws the reflected-surface marker at the measured horizontal position.
   */
  private fun drawReflectionMarker(
    canvas: Canvas,
    drawingBounds: DrawingBounds,
    distanceMeasurement: DistanceMeasurement
  ) {
    val reflectionMarkerLeft = calculateHorizontalPositionForDistance(
      drawingBounds = drawingBounds,
      distanceInMeters = distanceMeasurement.measuredDistanceInMeters
    )

    canvas.drawRect(
      reflectionMarkerLeft,
      drawingBounds.top + REFLECTION_MARKER_VERTICAL_MARGIN_IN_PIXELS,
      reflectionMarkerLeft + REFLECTION_MARKER_WIDTH_IN_PIXELS,
      drawingBounds.bottom - REFLECTION_MARKER_VERTICAL_MARGIN_IN_PIXELS,
      reflectionPaint
    )
  }

  /**
   * Draws the distance and confidence labels for the most recent measurement.
   */
  private fun drawMeasurementLabels(
    canvas: Canvas,
    drawingBounds: DrawingBounds,
    distanceMeasurement: DistanceMeasurement
  ) {
    val reflectionMarkerLeft = calculateHorizontalPositionForDistance(
      drawingBounds = drawingBounds,
      distanceInMeters = distanceMeasurement.measuredDistanceInMeters
    )

    canvas.drawText(
      context.getString(
        R.string.distance_view_distance_label,
        distanceMeasurement.measuredDistanceInMeters
      ),
      reflectionMarkerLeft - DISTANCE_LABEL_HORIZONTAL_OFFSET_IN_PIXELS,
      drawingBounds.top + DISTANCE_LABEL_VERTICAL_OFFSET_IN_PIXELS,
      labelPaint
    )
    canvas.drawText(
      context.getString(
        R.string.distance_view_confidence_label,
        distanceMeasurement.measurementConfidence * CONFIDENCE_PERCENT_MULTIPLIER
      ),
      drawingBounds.left + CONFIDENCE_LABEL_LEFT_OFFSET_IN_PIXELS,
      drawingBounds.bottom - CONFIDENCE_LABEL_BOTTOM_OFFSET_IN_PIXELS,
      labelPaint
    )
  }

  /**
   * Draws the meter markers along the bottom scale.
   */
  private fun drawDistanceScale(
    canvas: Canvas,
    drawingBounds: DrawingBounds
  ) {
    SCALE_LABEL_VALUES_IN_METERS.forEach { labelValueInMeters ->
      val labelPositionInPixels = calculateHorizontalPositionForDistance(
        drawingBounds = drawingBounds,
        distanceInMeters = labelValueInMeters
      )

      canvas.drawLine(
        labelPositionInPixels,
        drawingBounds.bottom - SCALE_TICK_TOP_OFFSET_IN_PIXELS,
        labelPositionInPixels,
        drawingBounds.bottom - SCALE_TICK_BOTTOM_OFFSET_IN_PIXELS,
        outlinePaint
      )
      canvas.drawText(
        "${labelValueInMeters} m",
        labelPositionInPixels - SCALE_LABEL_HORIZONTAL_OFFSET_IN_PIXELS,
        drawingBounds.bottom + SCALE_LABEL_BASELINE_OFFSET_IN_PIXELS,
        labelPaint
      )
    }
  }

  /**
   * Converts a measured distance into the horizontal coordinate used by the
   * phone, scale, and reflection marker.
   */
  private fun calculateHorizontalPositionForDistance(
    drawingBounds: DrawingBounds,
    distanceInMeters: Double
  ): Float {
    val normalizedDistance =
      min(MAXIMUM_NORMALIZED_DISTANCE, distanceInMeters / maximumDisplayedDistanceInMeters)

    return (
      drawingBounds.left + DISTANCE_TRACK_LEFT_OFFSET_IN_PIXELS +
        (drawingBounds.right - drawingBounds.left - DISTANCE_TRACK_RIGHT_MARGIN_IN_PIXELS) *
          normalizedDistance
      ).toFloat()
  }

  /**
   * Simple container that keeps the panel edges together when passing them to
   * helper methods.
   */
  private data class DrawingBounds(
    val left: Float,
    val top: Float,
    val right: Float,
    val bottom: Float
  )

  private companion object {
    private const val CONFIDENCE_LABEL_BOTTOM_OFFSET_IN_PIXELS = 20f
    private const val CONFIDENCE_LABEL_LEFT_OFFSET_IN_PIXELS = 110f
    private const val CONFIDENCE_PERCENT_MULTIPLIER = 100.0
    private const val DISTANCE_LABEL_HORIZONTAL_OFFSET_IN_PIXELS = 24f
    private const val DISTANCE_LABEL_VERTICAL_OFFSET_IN_PIXELS = 18f
    private const val DISTANCE_TRACK_LEFT_OFFSET_IN_PIXELS = 120f
    private const val DISTANCE_TRACK_RIGHT_MARGIN_IN_PIXELS = 180f
    private const val EMPTY_STATE_TEXT_LEFT_OFFSET_IN_PIXELS = 110f
    private const val EMPTY_STATE_TEXT_TOP_OFFSET_IN_PIXELS = 100f
    private const val HORIZONTAL_PADDING_IN_PIXELS = 48f
    private const val LABEL_TEXT_SIZE_IN_PIXELS = 36f
    private const val MAXIMUM_DISPLAYED_DISTANCE_IN_METERS = 3.0
    private const val MAXIMUM_NORMALIZED_DISTANCE = 1.0
    private const val OUTLINE_STROKE_WIDTH_IN_PIXELS = 4f
    private const val PANEL_CORNER_RADIUS_IN_PIXELS = 28f
    private const val PHONE_CORNER_RADIUS_IN_PIXELS = 18f
    private const val PHONE_LEFT_MARGIN_IN_PIXELS = 28f
    private const val PHONE_VERTICAL_MARGIN_IN_PIXELS = 40f
    private const val PHONE_WIDTH_IN_PIXELS = 48f
    private const val REFLECTION_MARKER_VERTICAL_MARGIN_IN_PIXELS = 28f
    private const val REFLECTION_MARKER_WIDTH_IN_PIXELS = 18f
    private const val SCALE_LABEL_BASELINE_OFFSET_IN_PIXELS = 8f
    private const val SCALE_LABEL_HORIZONTAL_OFFSET_IN_PIXELS = 24f
    private const val SCALE_TICK_BOTTOM_OFFSET_IN_PIXELS = 20f
    private const val SCALE_TICK_TOP_OFFSET_IN_PIXELS = 60f
    private const val VERTICAL_PADDING_IN_PIXELS = 32f

    private val SCALE_LABEL_VALUES_IN_METERS = listOf(
      0.5,
      1.0,
      2.0,
      MAXIMUM_DISPLAYED_DISTANCE_IN_METERS
    )
  }
}

package com.gedrocht.mosmena.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gedrocht.mosmena.R
import com.gedrocht.mosmena.databinding.ItemApplicationLogMessageBinding
import com.gedrocht.mosmena.logging.ApplicationLogMessage

/**
 * RecyclerView adapter that shows the most recent log entries.
 */
class ApplicationLogRecyclerViewAdapter :
  ListAdapter<ApplicationLogMessage, ApplicationLogRecyclerViewAdapter.ApplicationLogViewHolder>(
    ApplicationLogMessageDiffCallback
  ) {

  /**
   * Creates a new visible list row.
   */
  override fun onCreateViewHolder(
    parent: ViewGroup,
    viewType: Int
  ): ApplicationLogViewHolder {
    val layoutInflater = LayoutInflater.from(parent.context)
    val binding = ItemApplicationLogMessageBinding.inflate(layoutInflater, parent, false)
    return ApplicationLogViewHolder(binding)
  }

  /**
   * Binds the row at the requested position.
   */
  override fun onBindViewHolder(
    holder: ApplicationLogViewHolder,
    position: Int
  ) {
    holder.bind(getItem(position))
  }

  /**
   * Binds one log entry to one visible row.
   */
  class ApplicationLogViewHolder(
    private val itemApplicationLogMessageBinding: ItemApplicationLogMessageBinding
  ) : RecyclerView.ViewHolder(itemApplicationLogMessageBinding.root) {

    /**
     * Copies one log message into the visible text views for this row.
     */
    fun bind(applicationLogMessage: ApplicationLogMessage) {
      itemApplicationLogMessageBinding.logTimestampTextView.text = applicationLogMessage.timestampText
      itemApplicationLogMessageBinding.logSeverityAndTagTextView.text =
        itemApplicationLogMessageBinding.root.context.getString(
          R.string.application_log_severity_and_tag_label,
          applicationLogMessage.severity.name,
          applicationLogMessage.tag
        )
      itemApplicationLogMessageBinding.logMessageTextView.text = applicationLogMessage.message
    }
  }

  private object ApplicationLogMessageDiffCallback : DiffUtil.ItemCallback<ApplicationLogMessage>() {
    override fun areItemsTheSame(
      oldItem: ApplicationLogMessage,
      newItem: ApplicationLogMessage
    ): Boolean {
      return oldItem.timestampText == newItem.timestampText &&
        oldItem.tag == newItem.tag &&
        oldItem.message == newItem.message
    }

    override fun areContentsTheSame(
      oldItem: ApplicationLogMessage,
      newItem: ApplicationLogMessage
    ): Boolean {
      return oldItem == newItem
    }
  }
}

package com.example.opentypeai

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class FeatureAdapter(
    private val features: List<FeatureInfo>,
    private val onFeatureStateChanged: (String, Int) -> Unit
) : RecyclerView.Adapter<FeatureAdapter.FeatureViewHolder>() {

    inner class FeatureViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val featureName: TextView = itemView.findViewById(R.id.feature_name)
        val featureTag: TextView = itemView.findViewById(R.id.feature_tag)
        val featureDesc: TextView = itemView.findViewById(R.id.feature_description)
        val btnEnable: Button = itemView.findViewById(R.id.btn_enable)
        val btnDisable: Button = itemView.findViewById(R.id.btn_disable)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeatureViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_feature, parent, false)
        return FeatureViewHolder(view)
    }

    override fun onBindViewHolder(holder: FeatureViewHolder, position: Int) {
        val feature = features[position]

        holder.featureName.text = feature.name
        holder.featureTag.text = "الوسم: ${feature.tag}"
        holder.featureDesc.text = feature.description

        holder.btnEnable.setOnClickListener {
            onFeatureStateChanged(feature.tag, 1)
            holder.btnEnable.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.primary_color))
            holder.btnEnable.setTextColor(Color.WHITE)
            holder.btnDisable.setBackgroundColor(Color.TRANSPARENT)
            holder.btnDisable.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.primary_color))
        }

        holder.btnDisable.setOnClickListener {
            onFeatureStateChanged(feature.tag, 0)
            holder.btnDisable.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.primary_color))
            holder.btnDisable.setTextColor(Color.WHITE)
            holder.btnEnable.setBackgroundColor(Color.TRANSPARENT)
            holder.btnEnable.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.primary_color))
        }
    }

    override fun getItemCount(): Int = features.size
}
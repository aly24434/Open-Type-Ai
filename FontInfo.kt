package com.example.opentypeai

data class FontInfo(
    val name: String,
    val family: String,
    val style: String,
    val version: String,
    val designer: String,
    val features: List<FeatureInfo>
)
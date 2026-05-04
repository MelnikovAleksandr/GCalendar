package ru.melnikov.gcalendar.common

import androidx.compose.ui.graphics.RenderEffect

data class AbsorptionColor(
    val r: Float = 1.0f,
    val g: Float = 1.0f,
    val b: Float = 1.0f,
) {
    companion object {
        val CLEAR = AbsorptionColor(1.0f, 1.0f, 1.0f)
        val BLUE_TINT = AbsorptionColor(0.92f, 0.96f, 1.0f)
        val SMOKY = AbsorptionColor(0.9f, 0.9f, 0.92f)
    }
}

data class ReplacementColor(
    val r: Float,
    val g: Float,
    val b: Float,
)

data class GlassShaderParams(
    val width: Float = 0.5f,
    val height: Float = 0.3f,
    val centerX: Float = 0.5f,
    val centerY: Float = 0.5f,
    val cornerRadius: Float = 0.05f,
    val ior: Float = 1.33f,
    val highlightStrength: Float = 0.8f,
    val bevelWidth: Float = 0.02f,
    val thickness: Float = 0.02f,
    val shadowIntensity: Float = 0.3f,
    val chromaticAberration: Float = 0.001f,
    val frostedBlurRadius: Float = 0.0f,
    val absorptionColor: AbsorptionColor = AbsorptionColor.CLEAR,
    val absorptionDensity: Float = 0.0f,
    val sourceColor: ReplacementColor? = null,
    val targetColor: ReplacementColor? = null,
    val colorTolerance: Float = 0.15f,
) {
    val colorReplaceEnabled: Boolean
        get() = sourceColor != null && targetColor != null

    companion object {
        fun magnifyingLens() =
            GlassShaderParams(
                width = 0.4f,
                height = 0.4f,
                centerX = 0.5f,
                centerY = 0.5f,
                cornerRadius = 0.2f,
                ior = 1.5f,
                highlightStrength = 1.5f,
                bevelWidth = 0.12f,
                thickness = 0.18f,
                shadowIntensity = 0.15f,
                chromaticAberration = 0.006f,
                frostedBlurRadius = 0.0f,
                absorptionColor = AbsorptionColor.CLEAR,
                absorptionDensity = 0.0f,
            )

        fun waterDroplet() =
            GlassShaderParams(
                width = 0.4f,
                height = 0.4f,
                centerX = 0.5f,
                centerY = 0.5f,
                cornerRadius = 0.2f,
                ior = 1.45f,
                highlightStrength = 0f,
                bevelWidth = 0.2f,
                thickness = 0.15f,
                shadowIntensity = 0f,
                chromaticAberration = 0.1f,
                frostedBlurRadius = 0.0f,
                absorptionColor = AbsorptionColor.CLEAR,
                absorptionDensity = 0.0f,
            )

        fun pill() =
            GlassShaderParams(
                width = 0.5f,
                height = 0.15f,
                centerX = 0.5f,
                centerY = 0.5f,
                cornerRadius = 0.075f,
                ior = 1.5f,
                highlightStrength = 1.2f,
                bevelWidth = 0.04f,
                thickness = 0.08f,
                shadowIntensity = 0.15f,
                chromaticAberration = 0.005f,
                frostedBlurRadius = 0.0f,
                absorptionColor = AbsorptionColor.CLEAR,
                absorptionDensity = 0.0f,
            )

        fun subtle() =
            GlassShaderParams(
                width = 0.6f,
                height = 0.3f,
                centerX = 0.5f,
                centerY = 0.5f,
                cornerRadius = 0.02f,
                ior = 1.25f,
                highlightStrength = 0.4f,
                bevelWidth = 0.015f,
                thickness = 0.015f,
                shadowIntensity = 0.1f,
                chromaticAberration = 0.001f,
                frostedBlurRadius = 0.0f,
                absorptionColor = AbsorptionColor.CLEAR,
                absorptionDensity = 0.0f,
            )

        fun frostedGlass() =
            GlassShaderParams(
                width = 0.6f,
                height = 0.4f,
                centerX = 0.5f,
                centerY = 0.5f,
                cornerRadius = 0.03f,
                ior = 1.4f,
                highlightStrength = 0.6f,
                bevelWidth = 0.02f,
                thickness = 0.03f,
                shadowIntensity = 0.15f,
                chromaticAberration = 0.0005f,
                frostedBlurRadius = 0.012f,
                absorptionColor = AbsorptionColor.SMOKY,
                absorptionDensity = 0.2f,
            )

        fun rainbowPrism() =
            GlassShaderParams(
                width = 0.45f,
                height = 0.45f,
                centerX = 0.5f,
                centerY = 0.5f,
                cornerRadius = 0.225f,
                ior = 1.8f,
                highlightStrength = 2.5f,
                bevelWidth = 0.2f,
                thickness = 0.25f,
                shadowIntensity = 0.2f,
                chromaticAberration = 0.015f,
                frostedBlurRadius = 0.0f,
                absorptionColor = AbsorptionColor.CLEAR,
                absorptionDensity = 0.0f,
            )
    }
}

expect fun createGlassRenderEffect(
    width: Float,
    height: Float,
    params: GlassShaderParams,
): RenderEffect?
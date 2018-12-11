package com.ruslan.hlushan.game.play.ui.view.calculations

internal data class CellTextSizeParams(
        val sizesByTextLength: Map<Int, Float>
) {

    companion object {
        fun createDefault(): CellTextSizeParams = CellTextSizeParams(emptyMap())
    }
}

internal fun CellTextSizeParams.getCellTextSizeForText(text: String): Float {
    val value = this.sizesByTextLength[text.length]
    return if (value != null) {
        value
    } else {
        val maxTextLength = this.sizesByTextLength.keys.maxOrNull()
        val minTextLength = this.sizesByTextLength.keys.minOrNull()
        when {
            (maxTextLength != null && text.length > maxTextLength) -> this.sizesByTextLength.getValue(maxTextLength)
            (minTextLength != null && text.length < minTextLength) -> this.sizesByTextLength.getValue(minTextLength)
            else                                                   -> 0f
        }
    }
}
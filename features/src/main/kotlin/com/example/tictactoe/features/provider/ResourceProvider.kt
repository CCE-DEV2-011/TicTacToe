package com.example.tictactoe.features.provider

import android.content.Context
import androidx.annotation.StringRes

fun interface ResourceProvider {
    /** Get string from resources
     *
     * @param resId Resource id
     *
     * @return String from resources
     */
    fun getString(@StringRes resId: Int): String
}

class AndroidResourceProvider(
    private val context: Context,
) : ResourceProvider {
    override fun getString(@StringRes resId: Int) = context.getString(resId)
}

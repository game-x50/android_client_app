package com.ruslan.hlushan.game.settings.ui.languages

import android.view.View
import androidx.annotation.AttrRes
import androidx.annotation.DrawableRes
import androidx.annotation.LayoutRes
import com.ruslan.hlushan.android.extensions.colorAttributeValue
import com.ruslan.hlushan.android.extensions.setThrottledOnClickListener
import com.ruslan.hlushan.core.api.dto.WrappedLanguage
import com.ruslan.hlushan.core.api.managers.ResourceManager
import com.ruslan.hlushan.core.api.utils.thread.UiMainThread
import com.ruslan.hlushan.core.ui.api.recycler.AdapterDelegate
import com.ruslan.hlushan.core.ui.api.recycler.BaseItemViewHolder
import com.ruslan.hlushan.core.ui.api.recycler.OnItemClickListener
import com.ruslan.hlushan.core.ui.api.recycler.RecyclerItem
import com.ruslan.hlushan.extensions.ifNotNull
import com.ruslan.hlushan.game.settings.ui.R
import com.ruslan.hlushan.game.settings.ui.databinding.GameSettingsUiItemLanguageBinding

/**
 * Created by Ruslan on 22.10.2017.
 */

internal class LanguagesAdapterDelegate(
        private val resourceManager: ResourceManager,
        private val onItemClick: OnItemClickListener<WrappedLanguage>
) : AdapterDelegate<String, LanguageRecyclerItem, LanguageRecyclerItem> {

    @get:LayoutRes
    override val layoutResId: Int
        get() = R.layout.game_settings_ui_item_language

    override fun createViewHolder(itemView: View): BaseItemViewHolder<String, LanguageRecyclerItem> =
            LanguagesViewHolder(itemView, onItemClick, resourceManager)
}

private class LanguagesViewHolder(
        itemView: View,
        private val onItemClick: OnItemClickListener<WrappedLanguage>,
        private val resourceManager: ResourceManager
) : BaseItemViewHolder<String, LanguageRecyclerItem>(itemView) {

    private val binding = GameSettingsUiItemLanguageBinding.bind(itemView)

    @UiMainThread
    override fun onViewAttachedToWindow() {
        super.onViewAttachedToWindow()
        itemView.setThrottledOnClickListener {
            ifNotNull(recyclerItem) { item ->
                onItemClick(item.wrappedLanguage)
            }
        }
    }

    @UiMainThread
    override fun onBindView(item: LanguageRecyclerItem) {
        super.onBindView(item)
        binding.itemLanguageName.text = item.wrappedLanguage.language.name
        @AttrRes val textColorAttrResId = if (item.wrappedLanguage.isAppLanguage) {
           com.google.android.material.R.attr.colorSecondary
        } else {
            com.google.android.material.R.attr.colorPrimary
        }
        binding.itemLanguageName.setTextColor(binding.itemLanguageName.context.colorAttributeValue(textColorAttrResId))

        @DrawableRes val langDrawableResourceId: Int? = item.wrappedLanguage.language.imageUrl
                ?.let { imageResName -> resourceManager.getDrawableResourceIdByName(imageResName) }

        if (langDrawableResourceId != null) {
            binding.itemLanguageImage.setImageResource(langDrawableResourceId)
        } else {
            binding.itemLanguageImage.setImageDrawable(null)
        }
    }

    @UiMainThread
    override fun onViewDetachedFromWindow() {
        super.onViewDetachedFromWindow()
        itemView.setOnClickListener(null)
    }
}

internal class LanguageRecyclerItem(
        val wrappedLanguage: WrappedLanguage
) : RecyclerItem<String> {

    override val id: String get() = wrappedLanguage.language.fullCode
}
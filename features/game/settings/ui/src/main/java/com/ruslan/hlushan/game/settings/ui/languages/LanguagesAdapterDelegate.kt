package com.ruslan.hlushan.game.settings.ui.languages

import android.view.View
import androidx.annotation.AttrRes
import androidx.annotation.DrawableRes
import androidx.annotation.LayoutRes
import com.ruslan.hlushan.android.extensions.colorAttributeValue
import com.ruslan.hlushan.android.extensions.setThrottledOnClickListener
import com.ruslan.hlushan.core.api.dto.LangFullCode
import com.ruslan.hlushan.core.extensions.ifNotNull
import com.ruslan.hlushan.core.language.api.WrappedLanguage
import com.ruslan.hlushan.core.manager.api.ResourceManager
import com.ruslan.hlushan.core.recycler.item.RecyclerItem
import com.ruslan.hlushan.core.thread.UiMainThread
import com.ruslan.hlushan.core.ui.recycler.adapter.AdapterDelegate
import com.ruslan.hlushan.core.ui.recycler.adapter.BaseItemViewHolder
import com.ruslan.hlushan.core.ui.recycler.adapter.OnItemClickListener
import com.ruslan.hlushan.game.settings.ui.R
import com.ruslan.hlushan.game.settings.ui.databinding.GameSettingsUiItemLanguageBinding

internal class LanguagesAdapterDelegate(
        private val resourceManager: ResourceManager,
        private val onItemClick: OnItemClickListener<WrappedLanguage>
) : AdapterDelegate<LangFullCode, LanguageRecyclerItem, LanguageRecyclerItem> {

    @get:LayoutRes
    override val layoutResId: Int
        get() = R.layout.game_settings_ui_item_language

    override fun createViewHolder(itemView: View): BaseItemViewHolder<LangFullCode, LanguageRecyclerItem> =
            LanguagesViewHolder(itemView, onItemClick, resourceManager)
}

private class LanguagesViewHolder(
        itemView: View,
        private val onItemClick: OnItemClickListener<WrappedLanguage>,
        private val resourceManager: ResourceManager
) : BaseItemViewHolder<LangFullCode, LanguageRecyclerItem>(itemView) {

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
) : RecyclerItem<LangFullCode> {

    override val id: LangFullCode get() = wrappedLanguage.language.code
}
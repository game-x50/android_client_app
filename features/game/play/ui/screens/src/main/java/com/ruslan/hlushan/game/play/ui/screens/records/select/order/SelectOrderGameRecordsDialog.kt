package com.ruslan.hlushan.game.play.ui.screens.records.select.order

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.ruslan.hlushan.android.extensions.setThrottledOnClickListener
import com.ruslan.hlushan.core.api.utils.thread.UiMainThread
import com.ruslan.hlushan.core.ui.dialog.BaseDialogFragment
import com.ruslan.hlushan.core.ui.dialog.DialogBackgroundColorLifecyclePluginObserver
import com.ruslan.hlushan.core.ui.dialog.command.DialogCommandsHandler
import com.ruslan.hlushan.core.ui.dialog.command.ShowDialogCommand
import com.ruslan.hlushan.core.ui.recycler.adapter.DelegatesRecyclerAdapter
import com.ruslan.hlushan.core.ui.recycler.adapter.RecyclerViewLifecyclePluginObserver
import com.ruslan.hlushan.core.ui.viewbinding.extensions.bindViewBinding
import com.ruslan.hlushan.extensions.lazyUnsafe
import com.ruslan.hlushan.game.api.play.dto.GameRecordWithSyncState
import com.ruslan.hlushan.game.api.play.dto.OrderType
import com.ruslan.hlushan.game.play.ui.screens.R
import com.ruslan.hlushan.game.play.ui.screens.databinding.GamePlayUiSelectOrderGameRecordsDialogBinding
import com.ruslan.hlushan.game.play.ui.screens.dto.GameRecordWithSyncStateOrderParamsParcelable
import com.ruslan.hlushan.game.play.ui.screens.dto.toParcelable
import com.ruslan.hlushan.third_party.androidx.fragment.extensions.dismissNowSafety
import com.ruslan.hlushan.third_party.androidx.recyclerview.extensions.setUpDefaults

private const val KEY_INIT_ORDER_PARAMS = "KEY_INIT_ORDER_PARAMS"

internal class SelectOrderGameRecordsDialog : BaseDialogFragment() {

    @get:LayoutRes
    override val layoutResId: Int
        get() = R.layout.game_play_ui_select_order_game_records_dialog

    private val parentSelectOrderGameRecordsParamsListener: SelectOrderGameRecordsParamsListener?
        get() = ((parentFragment as? SelectOrderGameRecordsParamsListener)
                 ?: (activity as? SelectOrderGameRecordsParamsListener))

    @UiMainThread
    private val initOrderParams: GameRecordWithSyncState.Order.Params
        get() =
            requireArguments()
                    .getParcelable<GameRecordWithSyncStateOrderParamsParcelable>(KEY_INIT_ORDER_PARAMS)!!
                    .toOriginal()

    private val binding by bindViewBinding(GamePlayUiSelectOrderGameRecordsDialogBinding::bind)

    @UiMainThread
    private var selectedOrderVariant: GameRecordWithSyncState.Order.Variant? = null
        set(newValue) {
            if ((field != newValue) && (newValue != null)) {
                field = newValue
                gameRecordsOrderVariantRecyclerAdapter.submitList(
                        GameRecordsOrderVariantAdapterDelegate.createRecyclerItems(newValue)
                )
            }
        }

    private val gameRecordsOrderVariantRecyclerAdapter by lazyUnsafe {
        DelegatesRecyclerAdapter(
                GameRecordsOrderVariantAdapterDelegate { selectedRecyclerItem ->
                    selectedOrderVariant = selectedRecyclerItem.orderVariant
                }
        )
    }

    @UiMainThread
    override fun initLifecyclePluginObservers() {
        super.initLifecyclePluginObservers()
        addLifecyclePluginObserver(DialogBackgroundColorLifecyclePluginObserver(
                owner = this,
                color = Color.TRANSPARENT
        ))
        addLifecyclePluginObserver(RecyclerViewLifecyclePluginObserver {
            binding?.selectOrderGameRecordsDialogRecycler
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViews()
    }

    @UiMainThread
    private fun setUpViews() {
        selectedOrderVariant = initOrderParams.variant
        binding?.selectOrderGameRecordsDialogRecycler?.setUpDefaults(
                gameRecordsOrderVariantRecyclerAdapter,
                hasFixedSize = false
        )
        binding?.selectOrderGameRecordsDialogOrderTypeRadioGroup?.check(initOrderParams.type.radioButtonViewResId)
        binding?.selectOrderGameRecordsDialogApplyBtn?.setThrottledOnClickListener {
            applySelectedVariants()
        }
    }

    @UiMainThread
    private fun applySelectedVariants() {
        val localSelectedOrderVariant = selectedOrderVariant
        val selectedOrderType: OrderType? = OrderType.values()
                .firstOrNull { type ->
                    @Suppress("MaxLineLength")
                    (type.radioButtonViewResId == binding?.selectOrderGameRecordsDialogOrderTypeRadioGroup?.checkedRadioButtonId)
                }

        if ((selectedOrderType != null) && (localSelectedOrderVariant != null)) {
            val orderParams = GameRecordWithSyncState.Order.Params(localSelectedOrderVariant, selectedOrderType)
            parentSelectOrderGameRecordsParamsListener?.onOrderGameRecordsParamsSelected(orderParams)
        }
        dismissNowSafety()
    }

    interface SelectOrderGameRecordsParamsListener {
        @UiMainThread
        fun onOrderGameRecordsParamsSelected(orderParams: GameRecordWithSyncState.Order.Params)
    }
}

@UiMainThread
internal fun <Parent> Parent.showSelectOrderGameRecordsDialog(
        initOrderParams: GameRecordWithSyncState.Order.Params
) where Parent : DialogCommandsHandler.Owner,
        Parent : SelectOrderGameRecordsDialog.SelectOrderGameRecordsParamsListener =
        this.dialogCommandsHandler.executeShowOrAddToQueue(ShowSelectOrderGameRecordsDialogCommand(initOrderParams))

private class ShowSelectOrderGameRecordsDialogCommand(
        private val initOrderParams: GameRecordWithSyncState.Order.Params
) : ShowDialogCommand() {

    override val tag: String get() = "TAG_ORDER_GAME_RECORDS_DIALOG"

    @UiMainThread
    override fun getOrCreate(fragmentManager: FragmentManager): DialogFragment =
            ((fragmentManager.findFragmentByTag(tag) as? SelectOrderGameRecordsDialog)
             ?: (createSelectOrderGameRecordsDialog(initOrderParams)))
}

private fun createSelectOrderGameRecordsDialog(
        initOrderParams: GameRecordWithSyncState.Order.Params
): SelectOrderGameRecordsDialog =
        SelectOrderGameRecordsDialog().apply {
            val args = Bundle(1)
            args.putParcelable(KEY_INIT_ORDER_PARAMS, initOrderParams.toParcelable())
            arguments = args
        }

@get:IdRes
private val OrderType.radioButtonViewResId: Int
    get() = when (this) {
        OrderType.ASC  -> R.id.select_order_game_records_dialog_order_type_asc
        OrderType.DESC -> R.id.select_order_game_records_dialog_order_type_desc
    }
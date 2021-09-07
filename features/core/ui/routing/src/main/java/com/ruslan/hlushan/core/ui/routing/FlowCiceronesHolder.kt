package com.ruslan.hlushan.core.ui.routing

import com.github.terrakok.cicerone.Cicerone
import com.github.terrakok.cicerone.Router
import com.ruslan.hlushan.core.api.utils.thread.SingleThreadSafety

//TODO: #write_unit_tests
@SingleThreadSafety
class FlowCiceronesHolder {

    private val flowCicerones: MutableMap<String, Cicerone<FlowRouter>> = mutableMapOf()

    fun getOrCreate(name: String, parentRouter: Router): Cicerone<FlowRouter> {
        val storedValue = flowCicerones[name]

        return if (storedValue != null) {
            storedValue
        } else {
            val newCicerone = Cicerone.create(FlowRouter(parentRouter))
            flowCicerones[name] = newCicerone
            return newCicerone
        }
    }

    fun clear(name: String) = flowCicerones.remove(name)
}
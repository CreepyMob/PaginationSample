package com.creepymob.mobile.pagginationsample.presentation.paginator

/**
 * User: andrey
 * Date: 05.05.2018
 * Time: 18:28
 *
 */
class StateApplier<T>(
        private val loader: PageContentLoader<T>,
        private val stateStore: StateStore<T>,
        private val stateInvoker: StateInvoker<T>) {


    fun apply(newState: State<T>) {
        val previousState = stateStore.state
        stateStore.state = newState
        stateInvoker(previousState, newState, loader)
    }

}
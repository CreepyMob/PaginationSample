package com.creepymob.mobile.pagginationsample.app

import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * User: andrey
 * Date: 28.04.2018
 * Time: 1:28
 *
 */
class SchedulersProviderImpl : SchedulersProvider {

    override fun single() = Schedulers.single()

    override fun computation() = Schedulers.computation()

    override fun io() = Schedulers.io()

    override fun trampoline() = Schedulers.trampoline()

    override fun newThread() = Schedulers.newThread()

    override fun main() = AndroidSchedulers.mainThread()
}

interface SchedulersProvider {

    fun single(): Scheduler

    fun computation(): Scheduler

    fun io(): Scheduler

    fun trampoline(): Scheduler

    fun newThread(): Scheduler

    fun main(): Scheduler
}

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

    override fun main(): Scheduler = AndroidSchedulers.mainThread()
}

class SchedulersProviderTest(private val scheduler: Scheduler = Schedulers.trampoline()) : SchedulersProvider {

    override fun single() = scheduler

    override fun computation() = scheduler

    override fun io() = scheduler

    override fun trampoline() = scheduler

    override fun newThread() = scheduler

    override fun main() = scheduler
}

interface SchedulersProvider {

    fun single(): Scheduler

    fun computation(): Scheduler

    fun io(): Scheduler

    fun trampoline(): Scheduler

    fun newThread(): Scheduler

    fun main(): Scheduler
}

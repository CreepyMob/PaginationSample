package com.creepymob.mobile.pagginationsample.data

import com.creepymob.mobile.pagginationsample.domain.DataRepository
import com.creepymob.mobile.pagginationsample.entity.DataLoadFilter
import com.creepymob.mobile.pagginationsample.entity.LoadItem
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.subjects.BehaviorSubject
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * User: andrey
 * Date: 28.04.2018
 * Time: 2:05
 *
 */
class DataRepositoryImpl : DataRepository {

    private val subject: BehaviorSubject<List<LoadItem>> = BehaviorSubject.createDefault(emptyList())
    override val observable: Observable<List<LoadItem>> = subject.hide()

    override fun update(filter: DataLoadFilter, offset: Int): Completable = /*Completable.never()*/
            Single.fromCallable { generate2(offset) }
                    .delay(2000, TimeUnit.MILLISECONDS)
                    .onErrorResumeNext { Single.timer(2000, TimeUnit.MILLISECONDS).flatMap { _-> Single.error<List<LoadItem>>(it) } }
                    .map {
                        mutableListOf<LoadItem>().apply {

                            if (offset != 0) {
                                subject.value?.let {
                                    addAll(it)
                                }
                            }

                            addAll(it)
                        }
                    }
                    .doOnSuccess { subject.onNext(it) }
                    .toCompletable()

    private var counter = 0;

    private fun generate2(page: Int): List<LoadItem> {

        System.out.println("DataRepositoryImpl generate2 $page ")
        val list = mutableListOf<LoadItem>()

        counter++

        when {
            counter == 1 -> return list
            counter % 2 != 0 -> throw RuntimeException("no item exception")
            else -> {
                val size = 20
                for (i in 0 until size) {
                    list.add(LoadItem("title ${size * page + i}", "message ${size * page + i}"))
                }
            }
        }


        return list
    }

    private fun generate(page: Int): List<LoadItem> {

        val random = Random()

        val generateType = random.nextInt(3)

        if (generateType == 0) {
            throw RuntimeException("no item exception")
        }

        val list = mutableListOf<LoadItem>()
        if (generateType == 1) {
            val size = 20
            for (i in 0 until size) {
                list.add(LoadItem("title ${size * page + i}", "message ${size * page + i}"))
            }
        }

        return list
    }
}
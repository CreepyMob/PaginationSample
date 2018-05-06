package com.creepymob.mobile.pagginationsample.data

import com.creepymob.mobile.pagginationsample.domain.DataRepository
import com.creepymob.mobile.pagginationsample.entity.DataLoadFilter
import com.creepymob.mobile.pagginationsample.entity.LoadItem
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.subjects.BehaviorSubject
import java.util.concurrent.TimeUnit

/**
 * User: andrey
 * Date: 28.04.2018
 * Time: 2:05
 *
 */
class DataRepositoryImpl : DataRepository {

    private val subject: BehaviorSubject<List<LoadItem>> = BehaviorSubject.createDefault(generateDefault())

    override val observable: Observable<List<LoadItem>> = subject.hide()

    override fun update(filter: DataLoadFilter, offset: Int): Single<List<LoadItem>> = /*Completable.never()*/
            Single.fromCallable { generateNext(offset) }
                    .delay(5000, TimeUnit.MILLISECONDS)
                    .onErrorResumeNext { Single.timer(5000, TimeUnit.MILLISECONDS).flatMap { _ -> Single.error<List<LoadItem>>(it) } }
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
                    .map { it.toList() }
                    .doOnSuccess { subject.onNext(it) }

    private var counter = 0;

    private fun generateDefault(): List<LoadItem> {

        System.out.println("DataRepositoryImpl generateDefault")

        val list = mutableListOf<LoadItem>()

        for (i in 0 until 1000) {
            list.add(LoadItem("CACHED ITEM $i", "message $i"))
        }

        return list
    }

    private fun generateNext(page: Int): List<LoadItem> {

        System.out.println("DataRepositoryImpl generate2 $page ")


        counter++

        val list = mutableListOf<LoadItem>()
        when {
            counter == 1 -> return list
            counter % 2 == 0 -> throw RuntimeException("no item exception")
            else -> {
                val size = 20
                for (i in 0 until size) {
                    list.add(LoadItem("title ${size * page + i}", "message ${size * page + i}"))
                }
            }
        }


        return list
    }

}
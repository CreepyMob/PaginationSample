package com.creepymob.mobile.pagginationsample.presentation.paginator

import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Function
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers

/**
 * User: andrey
 * Date: 29.04.2018
 * Time: 21:59
 *
 */
val dataBaseAllContentCollectionDataEmptyChecker: (oldCollection: Collection<*>, newCollection: Collection<*>) -> Boolean =
        { oldCollection, newCollection ->
            oldCollection.containsAll(newCollection)
        }

class ContentToCheckedContentPairMapper<T>(private val collector: ContentCollector<T>, private val collectionChecker: (oldCollection: Collection<*>, newCollection: Collection<*>) -> Boolean = dataBaseAllContentCollectionDataEmptyChecker) :
        Function<Collection<T>, Pair<Collection<T>, Boolean>> {

    override fun apply(it: Collection<T>): Pair<Collection<T>, Boolean> {
        val isNewContentEmpty = collectionChecker(collector.content, it)
        return Pair(it, it.isEmpty())
    }
}

class PageContentLoader<T>(private val subscribeScheduler: Scheduler = Schedulers.io(),
                           private val observeScheduler: Scheduler = AndroidSchedulers.mainThread(),
                           private val collector: ContentCollector<T> = ContentCollector(),
                           private val contentPairMapper: ContentToCheckedContentPairMapper<T> = ContentToCheckedContentPairMapper(collector, dataBaseAllContentCollectionDataEmptyChecker),
                           private val disposable: CompositeDisposable = CompositeDisposable(),
                           private val pageCounter: PageCounter = PageCounter(0)) {

    val content: Collection<T>
        get() = collector.content

    private lateinit var request: (Int) -> Single<out Collection<T>>
    private lateinit var stateMachine: PaginationStateMachine<T>

    fun init(request: (Int) -> Single<out Collection<T>>, stateMachine: PaginationStateMachine<T>) {
        this.request = request;
        this.stateMachine = stateMachine;
    }

    fun loadFirstPage(currentState: State<T>) {
        pageCounter.reset()
        loadPage(pageCounter.currentPage, currentState, true)
    }

    fun loadNextPage(currentState: State<T>) {
        loadPage(pageCounter.currentPage, currentState, false)
    }

    private fun loadPage(page: Int, currentState: State<T>, clearCache: Boolean) {
        disposable.clear()
        request.invoke(page)
                .map(contentPairMapper)
                .subscribeOn(subscribeScheduler)
                .observeOn(observeScheduler)
                .subscribe(
                        {

                            if (clearCache) {
                                collector.clear()
                            }
                            pageCounter.increment()
                            collector.add(it.first)
                            stateMachine.apply(currentState.newPage(it.second))
                        },
                        { stateMachine.apply(currentState.fail(it)) }
                ).addTo(disposable)
    }

    fun release() {
        disposable.dispose()
    }
}
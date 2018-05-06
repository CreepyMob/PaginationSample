package com.creepymob.mobile.pagginationsample.presentation.paginator

import com.creepymob.mobile.pagginationsample.app.SchedulersProvider
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Function
import io.reactivex.rxkotlin.addTo

/**
 * User: andrey
 * Date: 29.04.2018
 * Time: 21:59
 *
 */
class WaitUntilCollectorReceiveNewContent<T> {

    operator fun invoke(content: Collection<T>, collector: ContentCollector<T>): Single<Collection<T>> =
            if (content.isEmpty()) {
                Single.just(content)
            } else {
                collector.contentObservable
                        .filter { it.containsAll(content) }
                        .firstOrError()
                        .flatMap { Single.just(content) }
            }
}


class ContentToIsEmptyContentMapper<T> :
        Function<Collection<T>, Pair<Collection<T>, Boolean>> {

    override fun apply(it: Collection<T>): Pair<Collection<T>, Boolean> {
        return Pair(it, it.isEmpty())
    }
}

class PageContentLoader<T>(
        private val collector: ContentCollector<T>,
        private val schedulersProvider: SchedulersProvider,
        private val waitUntilCollectorReceiveNewContent: WaitUntilCollectorReceiveNewContent<T> = WaitUntilCollectorReceiveNewContent(),
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

    fun loadFirstPage() {
        pageCounter.reset()
        loadPage(pageCounter.currentPage)
    }

    fun loadNextPage() {
        loadPage(pageCounter.currentPage)
    }

    private fun loadPage(page: Int) {
        disposable.clear()

        request.invoke(page)
                //.map(contentPairMapper)
                .flatMap {
                    waitUntilCollectorReceiveNewContent(it, collector)
                }
                /* .flatMap { contentToIsEmpty ->

                     if (contentToIsEmpty.second) {
                         Single.just(contentToIsEmpty)
                     } else {
                         collector.contentObservable
                                 .filter { it.containsAll(contentToIsEmpty.first) }
                                 .firstOrError()
                                 .flatMap { Single.just(contentToIsEmpty) }
                     }
                 }*/
                .subscribeOn(schedulersProvider.io())
                .observeOn(schedulersProvider.main())
                .subscribe({
                    pageCounter.increment()
                    //collector.set(content)
                    stateMachine.newPage(it.isEmpty())
                }, {
                    stateMachine.fail(it)
                }).addTo(disposable)


        /*  collector.contentObservable.firstOrError()
                  .flatMap {

                          .map { Triple(it, ) }
                  }*/

        /*  request.invoke(page)
                  .map(contentPairMapper)
                  .subscribeOn(schedulersProvider.io())
                  .observeOn(schedulersProvider.main())
                  .subscribe(
                          {
                              *//* if (clearCache) {
                                 collector.clear()
                             }*//*
                            pageCounter.increment()
                            collector.set(it.first)
                            stateMachine.newPage(it.second)
                        },
                        { stateMachine.fail(it) }
                ).addTo(disposable)*/
    }

    fun release() {
        disposable.dispose()
    }
}
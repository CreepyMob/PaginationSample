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
class WaitUntilCollectorReceiveNewContent<T>(private val schedulersProvider: SchedulersProvider) {

    operator fun invoke(content: Collection<T>, collector: ContentCollector<T>): Single<Collection<T>> =
            if (content.isEmpty()) {
                Single.just(content)
            } else {
                collector.contentObservable
                        .observeOn(schedulersProvider.io())
                        .doOnNext {
                            //  System.out.println("SUBJECT isMain Thread: ${Looper.myLooper() == Looper.getMainLooper()}")
                        }
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
        private val waitUntilCollectorReceiveNewContent: WaitUntilCollectorReceiveNewContent<T> = WaitUntilCollectorReceiveNewContent(schedulersProvider),
        private val disposable: CompositeDisposable = CompositeDisposable(),
        private val pageCounter: PageCounter = PageCounter(0)) {

    private lateinit var request: (Int) -> Single<out Collection<T>>
    private lateinit var stateMachine: PaginationStateMachine<T>

    fun init(request: (Int) -> Single<out Collection<T>>, stateMachine: PaginationStateMachine<T>) {
        this.request = request
        this.stateMachine = stateMachine
    }

    fun loadFirstPage() {
        loadPage(0)
    }

    fun loadNextPage() {
        loadPage(pageCounter.currentPage)
    }

    private fun loadPage(page: Int) {
        disposable.clear()

        request.invoke(page)
                .flatMap {
                    waitUntilCollectorReceiveNewContent(it, collector)
                }
                .subscribeOn(schedulersProvider.io())
                .observeOn(schedulersProvider.main())
                .subscribe({
                    pageCounter.incrementAndSet(page)
                    stateMachine.newPage(it.isEmpty())
                }, {
                    stateMachine.fail(it)
                }).addTo(disposable)
    }

    fun release() {
        disposable.dispose()
    }
}
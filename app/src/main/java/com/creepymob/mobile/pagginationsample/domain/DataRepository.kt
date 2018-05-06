package com.creepymob.mobile.pagginationsample.domain

import com.creepymob.mobile.pagginationsample.entity.DataLoadFilter
import com.creepymob.mobile.pagginationsample.entity.LoadItem
import io.reactivex.Observable
import io.reactivex.Single

/**
 * User: andrey
 * Date: 28.04.2018
 * Time: 1:13
 *
 */
interface DataRepository{

    val observable: Observable<List<LoadItem>>

    fun update(filter: DataLoadFilter, offset: Int): Single<List<LoadItem>>

}
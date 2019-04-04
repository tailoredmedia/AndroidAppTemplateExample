/*
 * Copyright 2019 Florian Schuster.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tailoredapps.countriesexample.all

import com.tailoredapps.countriesexample.BuildConfig
import com.tailoredapps.countriesexample.core.remote.BaseUrl
import com.tailoredapps.countriesexample.detail.detailModule
import com.tailoredapps.countriesexample.favorites.favoritesModule
import com.tailoredapps.countriesexample.overview.overviewModule
import org.koin.dsl.module

internal val appModule = module {
    single { BaseUrl(BuildConfig.BASE_URL) }
    factory { CountryAdapter() }
}

val appModules = listOf(appModule, overviewModule, favoritesModule, detailModule)
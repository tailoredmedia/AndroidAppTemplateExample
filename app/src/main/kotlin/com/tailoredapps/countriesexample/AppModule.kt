/*
 * Copyright 2020 Tailored Media GmbH.
 * Created by Florian Schuster.
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

package com.tailoredapps.countriesexample

import android.content.Context
import android.content.pm.ApplicationInfo
import com.tailoredapps.countriesexample.all.CountryAdapter
import com.tailoredapps.countriesexample.core.model.AppBuildInfo
import com.tailoredapps.countriesexample.detail.detailModule
import com.tailoredapps.countriesexample.favorites.favoritesModule
import com.tailoredapps.countriesexample.overview.overviewModule
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

internal val appModule = module {
    single { provideAppBuildInfo(context = androidContext()) }
    factory { CountryAdapter() }
}

private fun provideAppBuildInfo(context: Context): AppBuildInfo = AppBuildInfo(
    debug = (context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0,
    buildType = BuildConfig.BUILD_TYPE,
    flavor = BuildConfig.FLAVOR,
    versionCode = BuildConfig.VERSION_CODE,
    versionName = BuildConfig.VERSION_NAME,
    baseUrl = BuildConfig.BASE_URL
)

val appModules = listOf(appModule, overviewModule, favoritesModule, detailModule)
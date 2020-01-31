/* Copyright 2018 Florian Schuster
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. */

package com.tailoredapps.countriesexample.favorites

import at.florianschuster.control.Controller
import com.tailoredapps.androidapptemplate.base.ui.DelegateViewModel
import com.tailoredapps.countriesexample.core.CountriesProvider
import com.tailoredapps.countriesexample.core.model.Country
import kotlinx.coroutines.flow.flattenMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

class FavoritesViewModel(
    private val countriesProvider: CountriesProvider
) : DelegateViewModel<FavoritesViewModel.Action, FavoritesViewModel.State>() {

    sealed class Action {
        data class RemoveFavorite(val country: Country) : Action()
    }

    sealed class Mutation {
        data class SetCountries(val countries: List<Country>) : Mutation()
    }

    data class State(
        val countries: List<Country> = emptyList()
    )

    override val controller: Controller<Action, Mutation, State> = Controller(
        initialState = State(),
        mutationsTransformer = { mutations ->
            val favorites = countriesProvider
                .getFavoriteCountries()
                .map { Mutation.SetCountries(it) }
            flowOf(mutations, favorites).flattenMerge()
        },
        mutator = { action ->
            when (action) {
                is Action.RemoveFavorite -> flow { countriesProvider.toggleFavorite(action.country) }
            }
        },
        reducer = { previousState, mutation ->
            when (mutation) {
                is Mutation.SetCountries -> previousState.copy(countries = mutation.countries)
            }
        }
    )
}
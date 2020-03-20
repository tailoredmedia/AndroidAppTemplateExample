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

package com.tailoredapps.countriesexample.overview

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import at.florianschuster.control.bind
import com.google.android.material.snackbar.Snackbar
import com.tailoredapps.androidapptemplate.base.ui.Async
import com.tailoredapps.androidutil.ui.extensions.snack
import com.tailoredapps.countriesexample.R
import com.tailoredapps.countriesexample.all.CountryAdapter
import com.tailoredapps.countriesexample.all.CountryAdapterInteractionType
import com.tailoredapps.countriesexample.main.liftsAppBarWith
import com.tailoredapps.countriesexample.main.removeLiftsAppBarWith
import com.tailoredapps.countriesexample.util.asCause
import kotlinx.android.synthetic.main.fragment_overview.*
import kotlinx.android.synthetic.main.fragment_overview_empty.view.*
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flattenMerge
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import reactivecircus.flowbinding.android.view.clicks
import reactivecircus.flowbinding.swiperefreshlayout.refreshes
import timber.log.Timber

class OverviewView : Fragment(R.layout.fragment_overview) {

    private val viewModel: OverviewViewModel by viewModel()
    private val navController: NavController by lazy(::findNavController)
    private val adapter: CountryAdapter by inject()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvOverView.adapter = adapter
        liftsAppBarWith(rvOverView)

        adapter.interaction.filterIsInstance<CountryAdapterInteractionType.DetailClick>()
            .map { OverviewViewDirections.actionOverviewToDetail(it.id) }
            .bind(to = navController::navigate)
            .launchIn(viewLifecycleOwner.lifecycleScope)

        // action
        flowOf(srlOverView.refreshes(), emptyLayout.btnLoad.clicks())
            .flattenMerge()
            .map { OverviewViewModel.Action.Reload }
            .bind(to = viewModel::dispatch)
            .launchIn(viewLifecycleOwner.lifecycleScope)

        adapter.interaction.filterIsInstance<CountryAdapterInteractionType.FavoriteClick>()
            .map { it.country }
            .map { OverviewViewModel.Action.ToggleFavorite(it) }
            .bind(to = viewModel::dispatch)
            .launchIn(viewLifecycleOwner.lifecycleScope)

        // state
        viewModel.state.map { it.displayCountriesEmpty }
            .distinctUntilChanged()
            .bind(to = emptyLayout::isVisible::set)
            .launchIn(viewLifecycleOwner.lifecycleScope)

        viewModel.state.map { it.countriesLoad }
            .distinctUntilChanged()
            .bind { countriesLoad ->
                srlOverView.isRefreshing = countriesLoad is Async.Loading
                if (countriesLoad is Async.Error) errorSnack(countriesLoad.error)
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        viewModel.state.map { it.countries }
            .distinctUntilChanged()
            .bind(adapter::submitList)
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun errorSnack(throwable: Throwable) {
        Timber.e(throwable)
        val message = throwable.asCause(R.string.overview_error_message).translation(resources)
        root.snack(message, Snackbar.LENGTH_LONG, getString(R.string.overview_error_retry)) {
            viewModel.dispatch(OverviewViewModel.Action.Reload)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        removeLiftsAppBarWith(rvOverView)
    }
}
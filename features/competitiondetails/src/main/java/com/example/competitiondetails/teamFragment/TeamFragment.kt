package com.example.competitiondetails.teamFragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.common.base.BaseFragment
import com.example.presentation.utils.Utilities
import com.example.competitiondetails.R
import com.example.competitiondetails.databinding.TeamFragmentBinding
import com.example.competitiondetails.di.DaggerCompetitionDetailsComponent
import com.example.core.coreComponent
import com.example.presentation.models.PlayerResponse
import com.example.presentation.models.Resource
import com.example.presentation.models.Team
import com.example.presentation.utils.Utilities.hasInternetConnection
import com.example.presentation.viewmodels.CompetitionDetailsViewModel

import io.reactivex.disposables.Disposable
import javax.inject.Inject

class TeamFragment : BaseFragment() {

    lateinit var binding: TeamFragmentBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TeamAdapter
    private var listener: OnFragmentInteractionListener? = null
    @Inject
    lateinit var factory: ViewModelProvider.Factory
    private val viewModel: CompetitionDetailsViewModel by viewModels { factory }
    var disposable: Disposable? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        DaggerCompetitionDetailsComponent.factory().create(coreComponent()).inject(this)
//        if (context is OnFragmentInteractionListener)
//            listener = context
//        else
//            throw IllegalArgumentException("${context.toString()} must implement OnFragmentInteractionListener")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.team_fragment, container, false)
        val view = binding.root
        binding.click = MyHandler()
        getIntents()
        initRecyclerView()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getTeams(competitionId)
    }

    private fun getIntents() {
        competitionId = arguments?.getLong("id")!!
    }

    private fun initRecyclerView() {
        adapter = TeamAdapter(ArrayList(), clickListener)
        recyclerView = binding.teamRecyclerview
        recyclerView.adapter = adapter
        recyclerView.layoutManager = GridLayoutManager(context, 3)
    }

    private fun getTeams(id: Long) {
        disposable = hasInternetConnection().doOnSuccess {
            if (it)
                viewModel.getTeams(id).observe(viewLifecycleOwner, Observer { result ->
                    when (result.status) {
                        Resource.Status.LOADING -> {
                            println("Loading")
                        }
                        Resource.Status.ERROR -> {
                            println("Error")
                        }
                        Resource.Status.SUCCESS -> {
                            result.data?.let { data ->
                                Log.e("TAG", data.teams.toString())
                                if (data.teams.isNullOrEmpty()) {
                                    binding.progressBar.visibility = View.GONE
                                    binding.noData.visibility = View.VISIBLE
                                } else {
                                    binding.progressBar.visibility = View.GONE
                                    binding.teamRecyclerview.visibility = View.VISIBLE
                                    adapter.updateAdapter(data.teams)
                                }
                            }
                        }
                    }
                })
            else {
                showNoInternet()
            }
        }.doOnError {
            showNoInternet()
        }.subscribe()
    }

    private fun getPlayers(id: Long) {
        binding.progressBar.visibility = View.VISIBLE
        viewModel.getPlayers(id).observe(viewLifecycleOwner, Observer { result ->
            binding.progressBar.visibility = View.GONE
            when (result.status) {
                Resource.Status.LOADING -> {
                    println("Loading")
                }
                Resource.Status.ERROR -> {
                    println("Error")
                }
                Resource.Status.SUCCESS -> {
                    result.data?.let { data ->
                        if (data.squad.isNotEmpty()) {
                            //listener?.sendTeam(data)
                        }
                    }
                }
            }
        })
    }

    private fun showNoInternet() {
        binding.teamRecyclerview.visibility = View.GONE
        binding.progressBar.visibility = View.GONE
        binding.noInternet.visibility = View.VISIBLE
    }

    private val clickListener = View.OnClickListener {
        val team = it.tag as Team
        getPlayers(team.id)
    }

    inner class MyHandler {
        fun onTapToRetry(view: View) {
            binding.progressBar.visibility = View.VISIBLE
            binding.noInternet.visibility = View.GONE
            getTeams(competitionId)
        }
    }

    interface OnFragmentInteractionListener {
        fun sendTeam(playerResponse: PlayerResponse)
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        disposable?.dispose()
    }

    companion object {
        fun newInstance() = TeamFragment()
        var competitionId: Long = 0L
    }

}

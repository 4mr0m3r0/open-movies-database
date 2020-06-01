package com.tzion.openmoviesdatabase.movies.ui.movieDetail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.view.Menu
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.tzion.openmoviesdatabase.R
import com.tzion.openmoviesdatabase.databinding.ActivityFindMoviesByNameBinding
import com.tzion.openmoviesdatabase.databinding.ActivityMovieDetailBinding
import com.tzion.openmoviesdatabase.di.ViewModelFactory
import com.tzion.openmoviesdatabase.movies.presentation.MovieDetailViewModel
import com.tzion.openmoviesdatabase.movies.presentation.model.UiMovieDetail
import com.tzion.openmoviesdatabase.movies.presentation.uistates.MovieDetailUiState
import dagger.android.AndroidInjection
import javax.inject.Inject

class MovieDetailActivity: AppCompatActivity() {

    @Inject lateinit var viewModelFactory: ViewModelFactory
    private val movieDetailViewModel: MovieDetailViewModel? by lazy {
        ViewModelProviders
            .of(this, viewModelFactory)
            .get(MovieDetailViewModel::class.java)
    }
    private lateinit var binding: ActivityMovieDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_movie_detail)
        setupDependencyInjection()
        setupViewModel()
        loadMovieDetailById()
    }

    private fun setupDependencyInjection() {
        AndroidInjection.inject(this)
    }

    private fun setupViewModel() {
        movieDetailViewModel?.getLiveData()?.observe(this, Observer { renderUiState(it) })
    }

    private fun renderUiState(uiState: MovieDetailUiState) {
        updateScreenForLoading(uiState.loading)
        updateScreenForSuccess(uiState.movieDetail)
        updateScreenForError(uiState.error)
    }

    private fun updateScreenForLoading(loading: Boolean) {
        if (loading) {
            binding.pbMovieDetail.visibility = View.VISIBLE
        } else {
            binding.pbMovieDetail.visibility = View.GONE
        }
    }

    private fun updateScreenForSuccess(movieDetail: UiMovieDetail) {
        if (movieDetail.title.isNotEmpty()) {
            Glide.with(this).load(movieDetail.poster).into(binding.ivPoster)
            binding.movieDetail = movieDetail
        }
    }

    private fun updateScreenForError(error: Throwable?) {
        error?.let {
            Toast.makeText(this, it.localizedMessage, Toast.LENGTH_LONG).show()
        }
    }

    private fun loadMovieDetailById() {
        val movieId = intent.extras?.getString(MOVIE_ID_KEY)
        movieDetailViewModel?.loadMovieDetailById(movieId)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.movie_detail_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    companion object {
        const val MOVIE_ID_KEY = "movieIdKey"

        fun makeIntent(context: Context?, movieId: String): Intent? = context?.let {
            Intent(context, MovieDetailActivity::class.java).apply {
                putExtra(MOVIE_ID_KEY, movieId)
            }
        }

    }

}
package com.heyzeusv.yourlists.add

import androidx.lifecycle.ViewModel
import com.heyzeusv.yourlists.database.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AddViewModel @Inject constructor(
    private val repo: Repository
) : ViewModel() {

}
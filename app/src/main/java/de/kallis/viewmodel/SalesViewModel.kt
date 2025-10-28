package de.kallis.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import de.kallis.model.*
import kotlinx.coroutines.launch

class SalesViewModel(private val repo: SalesRepository) : ViewModel() {
    init {
        viewModelScope.launch(Dispatchers.IO) {
            repo.syncPendingSales()
        }
    }
    val allSales = repo.allSales

    fun addSale(sale: SaleRecordEntity) {
        viewModelScope.launch {
            repo.addSale(sale)
        }
    }

    fun syncPendingSales() {
        viewModelScope.launch {
            repo.syncPendingSales()
        }
    }
}

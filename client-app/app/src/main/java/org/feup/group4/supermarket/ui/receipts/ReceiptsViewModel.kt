package org.feup.group4.supermarket.ui.receipts

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ReceiptsViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Receipts Fragment"
    }
    val text: LiveData<String> = _text
}
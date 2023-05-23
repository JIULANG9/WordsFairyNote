package com.wordsfairy.note.utils.file

import android.annotation.SuppressLint
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultCaller
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract

open class XResultLauncher<I, O>(caller: ActivityResultCaller, contract: ActivityResultContract<I, O>) {

    private var launcher: ActivityResultLauncher<I>? = null
    private var callback: ActivityResultCallback<O>? = null

    init {
        launcher = caller.registerForActivityResult(contract) { result: O ->
            if (callback != null) {
                callback!!.onActivityResult(result)
                callback = null
            }
        }
    }
    fun launch(@SuppressLint("UnknownNullness") input: I, callback: ActivityResultCallback<O>) {
        this.callback = callback
        launcher!!.launch(input)
    }
}

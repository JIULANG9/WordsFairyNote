package com.wordsfairy.note.utils.file

import android.net.Uri
import androidx.activity.result.ActivityResultCaller
import androidx.activity.result.contract.ActivityResultContracts

open class XActivityLauncher(caller: ActivityResultCaller) :
    XResultLauncher<String, Uri?>(caller, ActivityResultContracts.GetContent())

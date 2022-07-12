package com.skysam.hchirinos.digitalforce.ui.init

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.skysam.hchirinos.digitalforce.repositories.InitSession

class LoginViewModel: ViewModel() {
    private val _messageSession = MutableLiveData<String>()
    val messageSession: LiveData<String> get() = _messageSession

    fun initSession(email: String, password: String) {
        InitSession.initSession(email, password)
            .addOnCompleteListener {task->
                if (task.isSuccessful) {
                    _messageSession.value = "ok"
                } else {
                    Log.w(ContentValues.TAG, "signInWithEmail:failure", task.exception)
                    _messageSession.value = task.exception?.localizedMessage
                }
            }
    }
}

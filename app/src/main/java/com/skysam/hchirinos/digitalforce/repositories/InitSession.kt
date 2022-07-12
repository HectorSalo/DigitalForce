package com.skysam.hchirinos.digitalforce.repositories

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

/**
 * Created by Hector Chirinos on 12/07/2022.
 */

object InitSession {
 private fun getInstance(): FirebaseAuth {
  return FirebaseAuth.getInstance()
 }

 fun getCurrentUser(): FirebaseUser? {
  return getInstance().currentUser
 }

 fun initSession(email: String, password: String): Task<AuthResult> {
  return getInstance().signInWithEmailAndPassword(email, password)
 }
}
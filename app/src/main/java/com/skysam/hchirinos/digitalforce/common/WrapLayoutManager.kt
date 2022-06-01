package com.skysam.hchirinos.digitalforce.common

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * Created by Hector Chirinos on 31/05/2022.
 */

class WrapLayoutManager(context: Context?, orientation: Int, reverseLayout: Boolean) :
 LinearLayoutManager(context, orientation, reverseLayout) {

 override fun supportsPredictiveItemAnimations(): Boolean {
  return false
 }

 override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State?) {
  try {
   super.onLayoutChildren(recycler, state)
  } catch (ex: Exception) {
   ex.printStackTrace()
  }
 }
}
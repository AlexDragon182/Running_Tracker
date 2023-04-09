package com.example.runningtracker.UI.fragments

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentOnAttachListener
import com.example.runningtracker.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class CancelTrackingDialog:DialogFragment() {

    private var yesListener:(()-> Unit)? = null

    fun setYesListemer(listener: ()-> Unit) {
        yesListener = listener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return         return MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogTheme)
            .setTitle("Cancel the Run")
            .setMessage("Are you sure to cancel the current run and delete its data?")
            .setIcon(R.drawable.ic_delete)
            .setPositiveButton("Yes") {_,_, ->
                yesListener?.let { yes ->
                    yes()
                }
            }
            .setNegativeButton("No") {dialogInterface , _ ->
                dialogInterface.cancel()
            }
            .create()

    }



}
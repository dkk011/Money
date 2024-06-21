package com.tukorea.Fearow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment

class Application : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_application, container, false)

        val buttonMyApplications = view.findViewById<Button>(R.id.buttonMyApplications)
        val buttonReceivedApplications = view.findViewById<Button>(R.id.buttonReceivedApplications)
        val buttonMyApplicationStatus = view.findViewById<Button>(R.id.buttonMyApplicationStatus)

        buttonMyApplications.setOnClickListener {
            replaceFragment(MyApplicationsFragment())
        }

        buttonReceivedApplications.setOnClickListener {
            replaceFragment(ReceivedApplicationsFragment())
        }

        buttonMyApplicationStatus.setOnClickListener {
            replaceFragment(MyApplicationStatusFragment())
        }

        return view
    }

    private fun replaceFragment(fragment: Fragment) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .addToBackStack(null)
            .commit()
    }
}

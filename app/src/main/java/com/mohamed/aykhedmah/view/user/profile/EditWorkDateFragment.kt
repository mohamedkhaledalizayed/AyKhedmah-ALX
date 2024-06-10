package com.mohamed.aykhedmah.view.user.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.mohamed.aykhedmah.R
import com.mohamed.aykhedmah.view.provider.profile.ProviderProfileScreen
import kotlinx.android.synthetic.main.fragment_edit_work_date.*


class EditWorkDateFragment : DialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_work_date, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        edit.setOnClickListener {

            if(work_days.text.isNullOrEmpty()){
                Toast.makeText(context,"من فضلك ادخل مواعيد العمل اولا", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            if(work_times.text.isNullOrEmpty()){
                Toast.makeText(context,"من فضلك ادخل ساعات العمل اولا", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val action = activity as ProviderProfileScreen
            action.onEditClickListener(work_days.text.toString(), work_times.text.toString())
            dismiss()
        }
    }

}
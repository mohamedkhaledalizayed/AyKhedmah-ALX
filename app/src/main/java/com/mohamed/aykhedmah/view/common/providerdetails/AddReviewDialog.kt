package com.mohamed.aykhedmah.view.common.providerdetails

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.mohamed.aykhedmah.R
import kotlinx.android.synthetic.main.fragment_add_review_dialog.*


class AddReviewDialog : DialogFragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_review_dialog, container, false)
    }

    private var rate: Float = 0.0f
    private var reviewContent: String = ""
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        animation.playAnimation()

        review.setOnClickListener {
            rate = rate_bar.rating
            reviewContent = comment.text.toString()

            if (rate == 0.0f){
                Toast.makeText(context,"من فضلك قيم مقدم الخدمة اولا", Toast.LENGTH_LONG).show()
            }else{
                val recipeDetailsActivity = activity as ProviderDetailsScreen
                recipeDetailsActivity.onAddClickListener(rate,reviewContent)
                dismiss()
            }
        }
    }

}
package com.wujie.scanall

import android.animation.ObjectAnimator
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ProgressBar

class TestActivity : AppCompatActivity() , View.OnClickListener{
    lateinit var mProgressBar: ProgressBar
    lateinit var mView: View
    lateinit var mGreenView: View
    var pro = 100
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
        initView()
    }
    private fun initView() {
        mProgressBar = findViewById(R.id.progresBar)
        mView = findViewById(R.id.bottom_view)
        mGreenView = findViewById(R.id.green_view)
        mGreenView.setOnClickListener(this)

    }


    override fun onClick(v: View?) {
        val height = mView.height - mGreenView.height

        val animator = ObjectAnimator.ofFloat(mView, "translationY", height.toFloat(), 0f)
        animator.duration = 2000
        animator.start()
    }
}

package io.meld.example

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.meld.example.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.textViewInstitutionConnection.setOnClickListener {
            setFragmentView(1)
        }

        binding.textViewSearchInstitutionConnection.setOnClickListener {
            setFragmentView(2)
        }

        binding.textViewFinancialAccount.setOnClickListener {
            setFragmentView(3)
        }

        binding.textViewSearchFinancialAccount.setOnClickListener {
            setFragmentView(4)
        }

        binding.textViewFinancialTransaction.setOnClickListener {
            setFragmentView(5)
        }
        binding.textViewSearchFinancialTransaction.setOnClickListener {
            setFragmentView(6)
        }
        binding.textViewInstitution.setOnClickListener {
            setFragmentView(7)
        }
        binding.textViewSearchInstitution.setOnClickListener {
            setFragmentView(8)
        }
    }

    private fun setFragmentView(case: Int) {
        Intent(this@HomeActivity, ConfigActivity::class.java)?.apply {
            this.putExtra("case", case)
            startActivity(this)
        }
    }
}
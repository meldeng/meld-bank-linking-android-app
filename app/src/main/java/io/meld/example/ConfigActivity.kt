package io.meld.example

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import io.meld.example.databinding.ActivityContentBinding

class ConfigActivity : AppCompatActivity() {
    private lateinit var binding: ActivityContentBinding
    private lateinit var mNavController: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContentBinding.inflate(layoutInflater)
        setContentView(binding.root)


//        val navHostFragment =
//            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
//
//        mNavController = navHostFragment.navController
//
//        val graphInflater = navHostFragment.navController.navInflater
//        val navGraph = graphInflater.inflate(R.navigation.nav_graph)
//        val case = intent.extras?.getInt("case")
//        when (intent.extras?.getInt("case")) {
//            2 -> {
//                navGraph.setStartDestination(R.id.searchInstitutionConnectionsFragment)
//            }
//            4 -> {
//                navGraph.setStartDestination(R.id.searchFinancialAccountFragment)
//            }
//            8 -> {
//                mNavController.navigate(R.id.searchInstitutionFragment)
//            }
//            9 -> {
//                navGraph.setStartDestination(R.id.configFragment)
//            }
//        }
//        mNavController.graph = navGraph
    }
}
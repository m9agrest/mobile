package com.example.test

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Menu
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import com.example.test.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.test.pojo.User
import loadBackGroundFromUrl
import loadImageFromUrl

object GlobalMe {
    var  user: User? = null
    var loadUserId: Int = -1
}

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    @SuppressLint("CutPasteId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        /*binding.appBarMain.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null)
                .setAnchorView(R.id.fab).show()
        }*/

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment


        navController = navHostFragment.navController


        val headerView = navView.getHeaderView(0)

        // Находим TextView элементы из заголовка
        val headerName = headerView.findViewById<TextView>(R.id.textName) // ID из `nav_header_main.xml`
        val headerStatus = headerView.findViewById<TextView>(R.id.textStatus) // ID из `nav_header_main.xml`

        // Устанавливаем текст для заголовка
        headerName.text = GlobalMe.user?.name
        headerStatus.text = GlobalMe.user?.status
        GlobalMe.user?.icon?.let { loadImageFromUrl(headerView.findViewById(R.id.imageIcon), it) }
        loadBackGroundFromUrl(headerView, GlobalMe.user?.cover)



        // Passing each menu ID as a set of IDs because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_profile, R.id.nav_news, R.id.nav_news_all
            ), drawerLayout
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}

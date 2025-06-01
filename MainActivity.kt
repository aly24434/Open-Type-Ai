package com.example.opentypeai

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.example.opentypeai.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        binding.toolbar.setNavigationOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.content_frame) as NavHostFragment
        navController = navHostFragment.navController

        NavigationUI.setupWithNavController(binding.navView, navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.toolbar.title = when(destination.id) {
                R.id.editorFragment -> "المحرر"
                R.id.fontInspectorFragment -> "مفتش الخطوط"
                R.id.fontRecognitionFragment -> "التعرف على الخط"
                R.id.fontLibraryFragment -> "مكتبة الخطوط"
                R.id.savedDesignsFragment -> "تصاميمي"
                R.id.settingsFragment -> "الإعدادات"
                else -> "الأوبن تايب"
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_sync -> {
                syncWithDrive()
                true
            }
            R.id.action_notifications -> {
                showNotifications()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun syncWithDrive() {
        Toast.makeText(this, "جارٍ المزامنة مع Google Drive...", Toast.LENGTH_SHORT).show()
    }

    private fun showNotifications() {
        // كود عرض الإشعارات
    }
}
package ru.mirea.mvolobueva.mireaproject

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import ru.mirea.mvolobueva.mireaproject.databinding.ActivityMainBinding
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import android.widget.Toast
/*

КРАТКОЕ ОПИСАНИЕ:
Главная активность приложения, которая организует навигацию между фрагментами
через Navigation Component, используя боковое меню (DrawerLayout) и нижнюю
панель навигации (BottomNavigationView).

ОСОБЕННОСТИ:

- Реализована поддержка двух типов навигационных меню одновременно
- FAB (плавающая кнопка) скрыта, так как не используется в главной активности
- При отсутствии бокового меню показывается overflow-меню (три точки)
*/

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    // onCreate Вызывается при создании активности Здесь происходит вся начальная настройка


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.appBarMain.toolbar)

        binding.appBarMain.fab?.hide()
// ПОЛУЧЕНИЕ СИСТЕМЫ НАВИГАЦИИ
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment
        val navController = navHostFragment.navController

        binding.navView?.let { navigationView ->
            appBarConfiguration = AppBarConfiguration(
                setOf(
                    R.id.nav_transform,
                    R.id.nav_reflow,
                    R.id.nav_slideshow,
                    R.id.nav_settings,
                    R.id.nav_data,
                    R.id.nav_webview,
                    R.id.nav_worker,
                    R.id.nav_sensor,
                    R.id.nav_camera,
                    R.id.nav_audio,
                    R.id.nav_profile,
                    R.id.nav_files,
                    R.id.nav_network,
                    R.id.nav_places
                ),
                binding.drawerLayout
            )
            setupActionBarWithNavController(navController, appBarConfiguration)
            navigationView.setupWithNavController(navController)
        }

        binding.appBarMain.contentMain.bottomNavView?.let { bottomNavView ->
            appBarConfiguration = AppBarConfiguration(
                setOf(
                    R.id.nav_transform,
                    R.id.nav_reflow,
                    R.id.nav_slideshow
                )
            )
            setupActionBarWithNavController(navController, appBarConfiguration)
            bottomNavView.setupWithNavController(navController)
        }
    }
    // onCreateOptionsMenu - СОЗДАНИЕ МЕНЮ
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val result = super.onCreateOptionsMenu(menu)
        val navView: NavigationView? = findViewById(R.id.nav_view)

        if (navView == null) {
            menuInflater.inflate(R.menu.overflow, menu)
        }

        return result
    }
    // onOptionsItemSelected - ОБРАБОТКА НАЖАТИЙ НА ПУНКТЫ МЕНЮ
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == R.id.nav_worker) {
            // Запускаем Worker
            val workRequest = OneTimeWorkRequest.Builder(MyWorker::class.java).build()
            WorkManager.getInstance(this).enqueue(workRequest)


            android.widget.Toast.makeText(
                this,
                "Фоновая задача запущена! Через 5 секунд проверьте Logcat",
                android.widget.Toast.LENGTH_LONG
            ).show()


            android.util.Log.d("MireaProject", "=== Worker запущен из меню ===")

            return true
        }

       //Находим NavController Выполняем навигацию к соответствующему фрагменту
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }
// onSupportNavigateUp - ОБРАБОТКА КНОПКИ "НАЗАД"
    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}
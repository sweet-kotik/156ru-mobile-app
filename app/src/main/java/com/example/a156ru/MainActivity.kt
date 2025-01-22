package com.example.a156ru

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request

class MainActivity : AppCompatActivity(), AdvertAdapter.OnItemClickListener {

    private lateinit var tabLayout: TabLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var addButton: MaterialButton
    private lateinit var progressBar: ProgressBar
    private lateinit var adapter: AdvertAdapter

    private val categories = mapOf(
        "Недвижимость" to "nedvizhimost",
        "Работа" to "rabota",
        "Транспорт" to "avto-moto",
        "Услуги" to "uslugi",
        "Животные" to "zhivotnye",
        "Электроника" to "electronika"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
        setupRecyclerView()
        setupTabs()
        loadInitialData()
    }

    private fun initViews() {
        tabLayout = findViewById(R.id.tabLayout)
        recyclerView = findViewById(R.id.recyclerView)
        addButton = findViewById(R.id.addButton)
        progressBar = findViewById(R.id.progressBar)

        addButton.setOnClickListener {
            openWebPage("https://156.ru/addnew")
        }
    }

    private fun setupRecyclerView() {
        adapter = AdvertAdapter(this)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun setupTabs() {
        categories.keys.forEach { tabName ->
            tabLayout.addTab(
                tabLayout.newTab().apply {
                    text = tabName
                    contentDescription = "Категория: $tabName"
                }
            )
        }

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                val category = categories[tab?.text?.toString()] ?: "nedvizhimost"
                loadDataForCategory(category)
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun loadInitialData() {
        loadDataForCategory("all")
    }

    private fun loadDataForCategory(category: String) {
        progressBar.visibility = View.VISIBLE // Показываем перед запуском корутины

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val ads = RssParser().parseFromUrl(category)
                withContext(Dispatchers.Main) {
                    if (ads.isEmpty()) {
                        showEmptyState() // Показываем состояние "нет данных"
                    } else {
                        recyclerView.visibility = View.VISIBLE
                        adapter.submitList(ads)
                    }
                }
            }
            finally {
                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE // Скрываем в любом случае
                }
            }
        }
    }

    private fun showEmptyState() {
        recyclerView.visibility = View.GONE
        // Покажите здесь ваш View с сообщением "Нет данных"
    }

    override fun onItemClick(advert: Advert) {
        openWebPage(advert.link)
    }

    private fun openWebPage(url: String) {
        println("🔗 Открываем URL: $url")
        Intent(this, WebActivity::class.java).apply {
            putExtra("URL", url)
            startActivity(this)
        }
    }
}
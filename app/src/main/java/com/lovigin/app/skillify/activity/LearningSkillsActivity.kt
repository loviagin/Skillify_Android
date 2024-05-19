package com.lovigin.app.skillify.activity

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.lovigin.app.skillify.App
import com.lovigin.app.skillify.Const
import com.lovigin.app.skillify.R
import com.lovigin.app.skillify.activity.ui.theme.SkillifyTheme
import com.lovigin.app.skillify.`object`.Skill

class LearningSkillsActivity : ComponentActivity() {

    @OptIn(
        ExperimentalMaterial3Api::class
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            val skills = remember {
                mutableStateListOf<Skill>().apply {
                    Const.icons.keys.forEach { item ->
                        if (App.userViewModel.user.value?.learningSkills?.find { it.name == item } != null) {
                            add(
                                Skill(
                                    name = item,
                                    level = App.userViewModel.user.value?.learningSkills?.find { it.name == item }!!.level
                                )
                            )
                        } else {
                            add(Skill(name = item))
                        }
                    }

                }
            }
            var searchQuery by remember { mutableStateOf("") }

            SkillifyTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        TopAppBar(
                            title = { Text(text = stringResource(R.string.learning_skills_str)) },
                            navigationIcon = {
                                IconButton(onClick = {
                                    saveData(skills.filter { it.level != null })
                                    finish()
                                }) {
                                    Icon(
                                        Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Back"
                                    )
                                }
                            }
                        )

                    }
                ) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .padding(innerPadding)
                            .background(Color.White),
                    ) {
                        SearchBar(
                            query = searchQuery,
                            onQueryChange = { item ->
                                searchQuery = item
                                skills.clear()
                                Const.icons.keys.filter {
                                    it.contains(
                                        searchQuery,
                                        ignoreCase = true
                                    )
                                }.forEach { sk ->
                                    if (App.userViewModel.user.value?.selfSkills?.find { it.name == sk } != null) {
                                        skills.add(
                                            Skill(
                                                name = sk,
                                                level = App.userViewModel.user.value?.selfSkills?.find { it.name == sk }!!.level
                                            )
                                        )
                                    } else {
                                        skills.add(Skill(name = sk))
                                    }
                                }
                            },
                            onSearch = {},
                            active = true,
                            onActiveChange = {},
                            placeholder = { Text(stringResource(R.string.search_str)) },
                            trailingIcon = {
                                Icon(
                                    Icons.Filled.Search,
                                    contentDescription = "Back"
                                )
                            },
                            colors = SearchBarDefaults.colors(
                                containerColor = Color.White
                            )
                        ) {
                            LazyColumn {
                                skills.forEach {
                                    item {
                                        SkillView(it.name, it.level)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun saveData(skills: List<Skill>) {
        if (skills != App.userViewModel.user.value?.selfSkills) {
        }
    }
}

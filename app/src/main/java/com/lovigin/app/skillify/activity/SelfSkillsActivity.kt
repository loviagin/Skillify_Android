package com.lovigin.app.skillify.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Chip
import androidx.compose.material.ChipDefaults
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.RadioButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.lovigin.app.skillify.App
import com.lovigin.app.skillify.Const
import com.lovigin.app.skillify.R
import com.lovigin.app.skillify.activity.ui.theme.SkillifyTheme
import com.lovigin.app.skillify.`object`.Skill

class SelfSkillsActivity : ComponentActivity() {

    @OptIn(
        ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class,
        ExperimentalMaterialApi::class
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            val skills = remember {
                mutableStateListOf<Skill>().apply {
                    Const.icons.keys.forEach { item ->
                        if (App.userViewModel.user.value?.selfSkills?.find { it.name == item } != null) {
                            add(
                                Skill(
                                    name = item,
                                    level = App.userViewModel.user.value?.selfSkills?.find { it.name == item }!!.level
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
                            title = { Text("My skills") },
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
                        FlowRow(
                            modifier = Modifier.fillMaxWidth().padding(top = 5.dp),
                            verticalArrangement = Arrangement.Center
                        ) {
                            Chip(
                                onClick = { /*TODO*/ },
                                colors = ChipDefaults.chipColors(backgroundColor = Color.White)
                            ) {
                                Text(text = "Learning skills:")
                            }
                            if (App.userViewModel.user.value!!.learningSkills.isNotEmpty()) {
                                App.userViewModel.user.value?.learningSkills?.forEach {
                                    Chip(onClick = { /*TODO*/ }) {
                                        Text(text = it.name)
                                    }
                                }
                            } else {
                                Chip(onClick = {
                                    finish()
                                    startActivity(Intent(this@SelfSkillsActivity, LearningSkillsActivity::class.java))
                                }) {
                                    Text(text = "Set now")
                                }
                            }
                        }
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
                            placeholder = { Text("Search ...") },
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
            Log.d("info", "saving data")
        }
    }
}

@Composable
fun SkillView(text: String, level: String? = "") {
    var isOpen by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .padding(16.dp)
            .clickable {
                isOpen = !isOpen
            }
    ) {
        Text(text = Const.icons.getValue(text), modifier = Modifier.padding(end = 10.dp))
        Text(text = text, modifier = Modifier.padding(end = 10.dp))
        Spacer(modifier = Modifier.weight(1f))
        if (level != null) {
            Text(text = level)
        }
    }

    if (isOpen) {
        var selectedOption by remember { mutableStateOf("") }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            if (level != null) {
                when (level) {
                    "Beginner" -> {
                        LevelSkillView("Intermediate", selectedOption) { option ->
                            selectedOption = option
                        }
                        LevelSkillView("Advanced", selectedOption) { option ->
                            selectedOption = option
                        }
                    }

                    "Intermediate" -> {
                        LevelSkillView("Advanced", selectedOption) { option ->
                            selectedOption = option
                        }
                    }

                    else -> {
                        Text(text = stringResource(R.string.high_level_skill_text))
                    }
                }
            } else {
                LevelSkillView("Beginner", selectedOption) { option ->
                    selectedOption = option
                }
                LevelSkillView("Intermediate", selectedOption) { option ->
                    selectedOption = option
                }
                LevelSkillView("Advanced", selectedOption) { option ->
                    selectedOption = option
                }
            }
        }
    }

    HorizontalDivider(
        modifier =
        Modifier.padding(horizontal = 16.dp),
        thickness = 0.5.dp
    )
}

@Composable
fun LevelSkillView(text: String, selectedOption: String, onOptionSelected: (String) -> Unit) {
    Card(
        modifier = Modifier.clickable {
            onOptionSelected(text)
        }
    ) {
        Row {
            RadioButton(
                selected = selectedOption == text,
                onClick = { onOptionSelected(text) }
            )
            Text(text = text)
        }
    }
}
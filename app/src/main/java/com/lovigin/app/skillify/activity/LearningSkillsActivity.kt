package com.lovigin.app.skillify.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lovigin.app.skillify.App
import com.lovigin.app.skillify.Const
import com.lovigin.app.skillify.R
import com.lovigin.app.skillify.activity.ui.theme.SkillifyTheme
import com.lovigin.app.skillify.`object`.Skill

class LearningSkillsActivity : ComponentActivity() {

    var skills = mutableStateListOf<Skill>().apply {
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

    @OptIn(
        ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class,
        ExperimentalMaterialApi::class
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var searchQuery by remember { mutableStateOf("") }

            SkillifyTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        TopAppBar(
                            title = { Text(getString(R.string.learning_skills_str)) },
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
                                    if (App.userViewModel.user.value?.learningSkills?.find { it.name == sk } != null) {
                                        skills.add(
                                            Skill(
                                                name = sk,
                                                level = App.userViewModel.user.value?.learningSkills?.find { it.name == sk }!!.level
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
                            placeholder = { Text(getString(R.string.search_str)) },
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
                                skills.forEach { skill ->
                                    item {
                                        SkillView(skill.name, skill.level) { selectedLevel ->
                                            skill.level = selectedLevel
                                        }
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
        if (skills != App.userViewModel.user.value?.learningSkills) {
            val updatedData = skills.filter { it.level != null }
            App.userViewModel.updateData(
                "users",
                App.userViewModel.user.value!!.id,
                mapOf("learningSkills" to updatedData)
            )
        }
    }

    @Composable
    fun SkillView(text: String, level: String?, onLevelSelected: (String?) -> Unit) {
        var isOpen by remember { mutableStateOf(false) }
        var selectedOption by remember { mutableStateOf(level) }

        Row(
            modifier = Modifier
                .padding(16.dp)
                .clickable {
                    if (skills.filter { it.level != null }.size > 5) {
                        Toast
                            .makeText(this, getString(R.string.max_skills_txt), Toast.LENGTH_SHORT)
                            .show()
                    } else if (!isOpen && selectedOption != null) {
                        onLevelSelected(null)
                        selectedOption = null
                        Log.d("TAG", "info")
                    } else {
                        isOpen = !isOpen
                    }
                }
        ) {
            Text(text = Const.icons.getValue(text), modifier = Modifier.padding(end = 10.dp))
            Text(text = text, modifier = Modifier.padding(end = 10.dp))
            Spacer(modifier = Modifier.weight(1f))
            selectedOption?.let {
                Text(text = it)
            }
        }

        if (isOpen) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                LevelSkillView(
                    stringResource(R.string.beginner_txt),
                    selectedOption
                ) { option ->
                    selectedOption = option
                    onLevelSelected(option)
                    isOpen = false
                }
                LevelSkillView(
                    stringResource(R.string.intermediate_txt),
                    selectedOption
                ) { option ->
                    selectedOption = option
                    onLevelSelected(option)
                    isOpen = false
                }
                LevelSkillView(
                    stringResource(R.string.advanced_txt),
                    selectedOption
                ) { option ->
                    selectedOption = option
                    onLevelSelected(option)
                    isOpen = false
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
    fun LevelSkillView(text: String, selectedOption: String?, onOptionSelected: (String) -> Unit) {
        Row(
            modifier = Modifier.clickable {
                onOptionSelected(text)
            },
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = selectedOption == text,
                onClick = { onOptionSelected(text) }
            )
            Text(text = text)
        }
    }
}

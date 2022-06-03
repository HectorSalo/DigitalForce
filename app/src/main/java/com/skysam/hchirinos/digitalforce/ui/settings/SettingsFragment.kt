package com.skysam.hchirinos.digitalforce.ui.settings

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.navigation.fragment.NavHostFragment
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceScreen
import com.skysam.hchirinos.digitalforce.BuildConfig
import com.skysam.hchirinos.digitalforce.R

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val aboutPreference: PreferenceScreen = findPreference("about")!!
        aboutPreference.setOnPreferenceClickListener {
            NavHostFragment.findNavController(this)
                .navigate(R.id.action_settingsFragment_to_aboutFragment)
            true
        }

        val versionPreferenceScreen = findPreference<PreferenceScreen>("name_version")
        versionPreferenceScreen?.title = getString(R.string.version_name, BuildConfig.VERSION_NAME)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            requireActivity().finish()
        }
        return super.onOptionsItemSelected(item)
    }
}
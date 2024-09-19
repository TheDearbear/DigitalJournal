package com.thedearbear.nnov.tabs

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import com.thedearbear.nnov.R
import com.thedearbear.nnov.ui.viewmodel.MainViewModel
import com.thedearbear.nnov.utils.materialTextLinkStyles

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExtendedInformation(
    onBack: () -> Unit,
    viewModel: MainViewModel
) {
    val rules by viewModel.rules.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(R.string.account_extended_information))
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Default.KeyboardArrowLeft, "Go Back")
                    }
                }
            )
        }
    ) { padding ->
        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .verticalScroll(scrollState)
                .padding(padding)
                .padding(12.dp)
        ) {
            RulesEntry(R.string.ext_info_roles, rules.roles.joinToString(", "))
            RulesEntry(R.string.ext_info_allowed_ads, rules.allowedAds.joinToString(", "))
            RulesEntry(R.string.ext_info_allowed_sections, rules.allowedSections.joinToString(", "))
            RulesEntry(R.string.ext_info_message_signature, rules.messageSignature)
            RulesEntry(R.string.ext_info_upload_file_url, buildAnnotatedString {
                append(rules.uploadFileUrl)
                addLink(LinkAnnotation.Url(rules.uploadFileUrl, materialTextLinkStyles()), 0, rules.uploadFileUrl.length)
                toAnnotatedString()
            })
            RulesEntry(R.string.ext_info_support_phone, rules.supportPhone)
            RulesEntry(R.string.ext_info_support_email, rules.supportEmail)
            RulesEntry(R.string.ext_info_support_link, buildAnnotatedString {
                append(rules.supportHelpdesk)
                addLink(LinkAnnotation.Url(rules.supportHelpdesk, materialTextLinkStyles()), 0, rules.supportHelpdesk.length)
                toAnnotatedString()
            })
            RulesEntry(R.string.ext_info_support_description, rules.supportDescription)
            RulesEntry(R.string.ext_info_name, rules.name)
            RulesEntry(R.string.ext_info_age, rules.age.toString())
            RulesEntry(R.string.ext_info_id, rules.id)
            RulesEntry(R.string.ext_info_vuid, rules.vuid)
            RulesEntry(R.string.ext_info_id_hash, rules.idHash)
            RulesEntry(R.string.ext_info_title, rules.title)
            RulesEntry(R.string.ext_info_vendor, rules.vendor)
            RulesEntry(R.string.ext_info_vendor_id, rules.vendorId.toString())
            RulesEntry(R.string.ext_info_guid, rules.guid)
            RulesEntry(R.string.ext_info_lastname, rules.lastname)
            RulesEntry(R.string.ext_info_firstname, rules.firstname)
            RulesEntry(R.string.ext_info_middlename, rules.middlename)
            RulesEntry(R.string.ext_info_gender, rules.gender)
            RulesEntry(R.string.ext_info_email, rules.email)
            RulesEntry(R.string.ext_info_email_confirmed, rules.emailConfirmed.toString())
            RulesEntry(R.string.ext_info_region, rules.region)
            RulesEntry(R.string.ext_info_region_code, rules.regionCode)
            RulesEntry(R.string.ext_info_city, rules.city)
            RulesEntry(R.string.ext_info_licey, rules.rtLiceySchoolEndDate.toString())
        }
    }
}

@Composable
private fun RulesEntry(
    @StringRes key: Int,
    value: String
) {
    Row {
        Text(
            text = "${stringResource(key)}: ",
            style = MaterialTheme.typography.labelLarge
        )

        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun RulesEntry(
    @StringRes key: Int,
    value: AnnotatedString
) {
    Row {
        Text(
            text = "${stringResource(key)}: ",
            style = MaterialTheme.typography.labelLarge
        )

        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

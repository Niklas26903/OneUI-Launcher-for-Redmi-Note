package com.oneui.launcher.models

import com.oneui.launcher.models.AppInfo

data class FolderInfo(
    val id: String,
    val name: String,
    val apps: List<AppInfo>
)

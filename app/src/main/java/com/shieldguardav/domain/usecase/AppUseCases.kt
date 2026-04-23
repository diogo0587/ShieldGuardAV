package com.shieldguardav.domain.usecase

import com.shieldguardav.domain.model.AppInfo
import com.shieldguardav.domain.repository.AppRepository
import javax.inject.Inject

class GetInstalledAppsUseCase @Inject constructor(
    private val appRepository: AppRepository
) {
    suspend operator fun invoke(): List<AppInfo> {
        return appRepository.getInstalledApps()
    }
}

class GetAppInfoUseCase @Inject constructor(
    private val appRepository: AppRepository
) {
    suspend operator fun invoke(packageName: String): AppInfo? {
        return appRepository.getAppInfo(packageName)
    }
}

class AnalyzeAppRisksUseCase @Inject constructor(
    private val appRepository: AppRepository
) {
    suspend operator fun invoke(appInfo: AppInfo): AppInfo {
        return appRepository.analyzeAppRisks(appInfo)
    }
}

class UninstallAppUseCase @Inject constructor(
    private val appRepository: AppRepository
) {
    suspend operator fun invoke(packageName: String) {
        appRepository.uninstallApp(packageName)
    }
}
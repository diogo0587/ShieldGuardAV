package com.shieldguardav.domain.usecase

import com.shieldguardav.domain.model.*
import com.shieldguardav.domain.repository.SecurityRepository
import javax.inject.Inject

class GetSecurityScoreUseCase @Inject constructor(
    private val securityRepository: SecurityRepository
) {
    suspend operator fun invoke(): SecurityScore {
        return securityRepository.getSecurityScore()
    }
}

class CheckWifiSecurityUseCase @Inject constructor(
    private val securityRepository: SecurityRepository
) {
    suspend operator fun invoke(): List<WifiSecurity> {
        return securityRepository.checkWifiSecurity()
    }
}

class GetActiveConnectionsUseCase @Inject constructor(
    private val securityRepository: SecurityRepository
) {
    suspend operator fun invoke(): List<NetworkConnection> {
        return securityRepository.getActiveConnections()
    }
}

class ScanJunkFilesUseCase @Inject constructor(
    private val securityRepository: SecurityRepository
) {
    suspend operator fun invoke(): List<JunkFile> {
        return securityRepository.scanJunkFiles()
    }
}

class CleanJunkFilesUseCase @Inject constructor(
    private val securityRepository: SecurityRepository
) {
    suspend operator fun invoke(files: List<JunkFile>): Long {
        return securityRepository.cleanJunkFiles(files)
    }
}
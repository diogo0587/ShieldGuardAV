package com.shieldguardav.domain.usecase

import com.shieldguardav.domain.model.*
import com.shieldguardav.domain.repository.AntivirusRepository
import com.shieldguardav.domain.repository.MalwareDatabaseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class ScanFileUseCase @Inject constructor(
    private val antivirusRepository: AntivirusRepository
) {
    suspend operator fun invoke(filePath: String): ScanResult? {
        return antivirusRepository.scanFile(filePath)
    }
}

class QuickScanUseCase @Inject constructor(
    private val antivirusRepository: AntivirusRepository
) {
    suspend operator fun invoke(): Flow<ScanProgress> {
        return antivirusRepository.quickScan()
    }
}

class FullScanUseCase @Inject constructor(
    private val antivirusRepository: AntivirusRepository
) {
    suspend operator fun invoke(): Flow<ScanProgress> {
        return antivirusRepository.fullScan()
    }
}

class CustomScanUseCase @Inject constructor(
    private val antivirusRepository: AntivirusRepository
) {
    suspend operator fun invoke(paths: List<String>): Flow<ScanProgress> {
        return antivirusRepository.scanCustom(paths)
    }
}

class ScanPackageUseCase @Inject constructor(
    private val antivirusRepository: AntivirusRepository
) {
    suspend operator fun invoke(packageName: String): ScanResult? {
        return antivirusRepository.scanPackage(packageName)
    }
}

class GetScanResultsUseCase @Inject constructor(
    private val antivirusRepository: AntivirusRepository
) {
    operator fun invoke(): Flow<List<ScanResult>> {
        return antivirusRepository.getScanResults()
    }
}

class GetThreatsCountUseCase @Inject constructor(
    private val antivirusRepository: AntivirusRepository
) {
    operator fun invoke(): Flow<Int> {
        return antivirusRepository.getThreatsCount()
    }
}

class QuarantineThreatUseCase @Inject constructor(
    private val antivirusRepository: AntivirusRepository
) {
    suspend operator fun invoke(scanResult: ScanResult) {
        antivirusRepository.quarantineThreat(scanResult)
    }
}

class DeleteThreatUseCase @Inject constructor(
    private val antivirusRepository: AntivirusRepository
) {
    suspend operator fun invoke(scanResult: ScanResult) {
        antivirusRepository.deleteThreat(scanResult)
    }
}

class GetQuarantinedItemsUseCase @Inject constructor(
    private val antivirusRepository: AntivirusRepository
) {
    operator fun invoke(): Flow<List<ScanResult>> {
        return antivirusRepository.getQuarantinedItems()
    }
}

class CheckMalwareSignatureUseCase @Inject constructor(
    private val malwareDatabaseRepository: MalwareDatabaseRepository
) {
    suspend operator fun invoke(hash: String): MalwareSignature? {
        return malwareDatabaseRepository.checkSignature(hash)
    }
}

class LoadSignaturesUseCase @Inject constructor(
    private val malwareDatabaseRepository: MalwareDatabaseRepository
) {
    suspend operator fun invoke() {
        malwareDatabaseRepository.loadLocalSignatures()
    }
}

class UpdateSignaturesUseCase @Inject constructor(
    private val malwareDatabaseRepository: MalwareDatabaseRepository
) {
    suspend operator fun invoke(): Boolean {
        return malwareDatabaseRepository.updateSignaturesFromCloud()
    }
}
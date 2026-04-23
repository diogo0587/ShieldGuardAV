package com.shieldguardav.domain.model

data class ScanResult(
    val id: Long = 0,
    val packageName: String,
    val appName: String,
    val threatType: ThreatType,
    val threatName: String,
    val severity: ThreatSeverity,
    val filePath: String,
    val scanDate: Long = System.currentTimeMillis(),
    val status: ThreatStatus = ThreatStatus.FOUND
)

enum class ThreatType {
    VIRUS,
    TROJAN,
    MALWARE,
    ADWARE,
    SPYWARE,
    RANSOMWARE,
    WORM,
    BACKDOOR,
    UNKNOWN
}

enum class ThreatSeverity {
    CRITICAL,
    HIGH,
    MEDIUM,
    LOW,
    NONE
}

enum class ThreatStatus {
    FOUND,
    QUARANTINED,
    CLEANED,
    DELETED,
    IGNORED
}
package no.nordicsemi.android.toolbox.profile.data.directionFinder

import no.nordicsemi.android.toolbox.profile.data.DFSServiceData
import no.nordicsemi.android.toolbox.profile.data.SensorData
import no.nordicsemi.android.toolbox.profile.parser.directionFinder.PeripheralBluetoothAddress
import no.nordicsemi.android.toolbox.profile.parser.directionFinder.QualityIndicator
import java.time.Clock
import java.time.Instant
import java.time.format.DateTimeFormatter

const val DFS_CSV_HEADER =
    "timestamp,address,quality,ifft,phaseSlope,rssi,best,rtt,azimuth,elevation"

fun DFSServiceData.toDfsCsvReport(clock: Clock = Clock.systemUTC()): String = buildString {
    append(DFS_CSV_HEADER)
    data.entries
        .sortedBy { it.key.address }
        .forEach { (address, sensorData) ->
            appendLine()
            append(sensorData.toDfsCsvRow(clock.instant(), address))
        }
}

fun SensorData.toDfsCsvRow(
    timestamp: Instant,
    address: PeripheralBluetoothAddress? = null,
): String {
    val rowAddress = address?.address ?: latestAddress()?.address
    val quality = latestQuality()

    return listOf(
        DateTimeFormatter.ISO_INSTANT.format(timestamp),
        rowAddress.orEmpty(),
        quality?.name.orEmpty(),
        ifftValue()?.toString().orEmpty(),
        phaseSlopeValue()?.toString().orEmpty(),
        rssiValue()?.toString().orEmpty(),
        bestEffortValue()?.toString().orEmpty(),
        rttValue()?.toString().orEmpty(),
        azimuthValue()?.toString().orEmpty(),
        elevationValue()?.toString().orEmpty(),
    ).joinToString(",")
}

private fun SensorData.latestAddress(): PeripheralBluetoothAddress? =
    mcpdDistance?.values?.lastOrNull()?.address
        ?: rttDistance?.values?.lastOrNull()?.address
        ?: azimuth?.values?.lastOrNull()?.address
        ?: elevation?.values?.lastOrNull()?.address

private fun SensorData.latestQuality(): QualityIndicator? =
    mcpdDistance?.values?.lastOrNull()?.quality
        ?: rttDistance?.values?.lastOrNull()?.quality
        ?: azimuth?.values?.lastOrNull()?.quality
        ?: elevation?.values?.lastOrNull()?.quality

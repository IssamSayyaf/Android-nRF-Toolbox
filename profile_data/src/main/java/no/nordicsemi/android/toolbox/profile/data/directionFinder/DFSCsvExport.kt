package no.nordicsemi.android.toolbox.profile.data.directionFinder

import no.nordicsemi.android.toolbox.profile.data.SensorData
import no.nordicsemi.android.toolbox.profile.parser.directionFinder.PeripheralBluetoothAddress

data class DFSRangingSample(
    val timestampEpochMillis: Long,
    val deviceAddress: String,
    val distanceDm: Int?,
    val rttDm: Int?,
    val rttQuality: String?,
    val mcpdBestDm: Int?,
    val mcpdIfftDm: Int?,
    val mcpdPhaseSlopeDm: Int?,
    val mcpdRssi: Int?,
    val mcpdQuality: String?,
)

fun SensorData.toRangingSample(
    selectedDevice: PeripheralBluetoothAddress,
    timestampEpochMillis: Long,
): DFSRangingSample? {
    val latestRtt = rttDistance?.values?.lastOrNull()
    val latestMcpd = mcpdDistance?.values?.lastOrNull()
    val distanceDm = latestMcpd?.mcpd?.best ?: latestRtt?.rtt?.value

    if (distanceDm == null &&
        latestRtt == null &&
        latestMcpd == null
    ) {
        return null
    }

    return DFSRangingSample(
        timestampEpochMillis = timestampEpochMillis,
        deviceAddress = selectedDevice.address,
        distanceDm = distanceDm,
        rttDm = latestRtt?.rtt?.value,
        rttQuality = latestRtt?.quality?.name,
        mcpdBestDm = latestMcpd?.mcpd?.best,
        mcpdIfftDm = latestMcpd?.mcpd?.ifft,
        mcpdPhaseSlopeDm = latestMcpd?.mcpd?.phaseSlope,
        mcpdRssi = latestMcpd?.mcpd?.rssi,
        mcpdQuality = latestMcpd?.quality?.name,
    )
}

object DFSCsvFormatter {
    private const val HEADER = "timestamp_epoch_ms,device_address,distance_dm,rtt_dm,rtt_quality,mcpd_best_dm,mcpd_ifft_dm,mcpd_phase_slope_dm,mcpd_rssi,mcpd_quality"

    fun write(samples: List<DFSRangingSample>, appendable: Appendable) {
        appendable.appendLine(HEADER)
        samples.forEach { sample ->
            appendable.appendLine(
                listOf(
                    sample.timestampEpochMillis.toString(),
                    sample.deviceAddress,
                    sample.distanceDm?.toString().orEmpty(),
                    sample.rttDm?.toString().orEmpty(),
                    sample.rttQuality.orEmpty(),
                    sample.mcpdBestDm?.toString().orEmpty(),
                    sample.mcpdIfftDm?.toString().orEmpty(),
                    sample.mcpdPhaseSlopeDm?.toString().orEmpty(),
                    sample.mcpdRssi?.toString().orEmpty(),
                    sample.mcpdQuality.orEmpty(),
                ).joinToString(",") { csvEscape(it) }
            )
        }
    }

    fun format(samples: List<DFSRangingSample>): String {
        return buildString {
            write(samples, this)
        }
    }

    private fun csvEscape(value: String): String {
        if (!value.any { it == ',' || it == '"' || it == '\n' || it == '\r' }) {
            return value
        }
        return "\"${value.replace("\"", "\"\"")}\""
    }
}

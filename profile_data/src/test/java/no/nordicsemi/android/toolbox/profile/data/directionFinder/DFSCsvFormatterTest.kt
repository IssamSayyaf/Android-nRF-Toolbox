package no.nordicsemi.android.toolbox.profile.data.directionFinder

import org.junit.Assert.assertEquals
import org.junit.Test

class DFSCsvFormatterTest {

    @Test
    fun `format returns header when there are no samples`() {
        val csv = DFSCsvFormatter.format(emptyList())

        assertEquals(
            "timestamp_epoch_ms,device_address,distance_dm,rtt_dm,rtt_quality,mcpd_best_dm,mcpd_ifft_dm,mcpd_phase_slope_dm,mcpd_rssi,mcpd_quality\n",
            csv
        )
    }

    @Test
    fun `format returns rows with escaped values`() {
        val csv = DFSCsvFormatter.format(
            listOf(
                DFSRangingSample(
                    timestampEpochMillis = 1700000000000,
                    deviceAddress = "AA:BB:CC:DD:EE:FF,Random",
                    distanceDm = 20,
                    rttDm = 19,
                    rttQuality = "GOOD",
                    mcpdBestDm = 20,
                    mcpdIfftDm = 21,
                    mcpdPhaseSlopeDm = 22,
                    mcpdRssi = -60,
                    mcpdQuality = "NOT\"SPECIFIED",
                )
            )
        )

        assertEquals(
            "timestamp_epoch_ms,device_address,distance_dm,rtt_dm,rtt_quality,mcpd_best_dm,mcpd_ifft_dm,mcpd_phase_slope_dm,mcpd_rssi,mcpd_quality\n" +
                "1700000000000,\"AA:BB:CC:DD:EE:FF,Random\",20,19,GOOD,20,21,22,-60,\"NOT\"\"SPECIFIED\"\n",
            csv
        )
    }
}

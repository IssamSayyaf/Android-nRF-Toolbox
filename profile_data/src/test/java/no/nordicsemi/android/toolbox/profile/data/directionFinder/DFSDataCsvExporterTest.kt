package no.nordicsemi.android.toolbox.profile.data.directionFinder

import no.nordicsemi.android.toolbox.profile.data.DFSServiceData
import no.nordicsemi.android.toolbox.profile.data.SensorData
import no.nordicsemi.android.toolbox.profile.data.SensorValue
import no.nordicsemi.android.toolbox.profile.parser.directionFinder.AddressType
import no.nordicsemi.android.toolbox.profile.parser.directionFinder.PeripheralBluetoothAddress
import no.nordicsemi.android.toolbox.profile.parser.directionFinder.QualityIndicator
import no.nordicsemi.android.toolbox.profile.parser.directionFinder.azimuthal.AzimuthMeasurementData
import no.nordicsemi.android.toolbox.profile.parser.directionFinder.distance.MCPDEstimate
import no.nordicsemi.android.toolbox.profile.parser.directionFinder.distance.McpdMeasurementData
import no.nordicsemi.android.toolbox.profile.parser.directionFinder.distance.RTTEstimate
import no.nordicsemi.android.toolbox.profile.parser.directionFinder.distance.RttMeasurementData
import no.nordicsemi.android.toolbox.profile.parser.directionFinder.elevation.ElevationMeasurementData
import org.junit.Test
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset
import kotlin.test.assertEquals

class DFSDataCsvExporterTest {

    @Test
    fun `creates csv with exact header and ordered dfs columns`() {
        val address = PeripheralBluetoothAddress(AddressType.RANDOM, "AA:BB:CC:DD:EE:FF")
        val serviceData = DFSServiceData(
            data = mapOf(
                address to SensorData(
                    azimuth = SensorValue(
                        values = listOf(
                            AzimuthMeasurementData(
                                quality = QualityIndicator.GOOD,
                                address = address,
                                azimuth = 101
                            )
                        )
                    ),
                    elevation = SensorValue(
                        values = listOf(
                            ElevationMeasurementData(
                                quality = QualityIndicator.GOOD,
                                address = address,
                                elevation = -12
                            )
                        )
                    ),
                    mcpdDistance = SensorValue(
                        values = listOf(
                            McpdMeasurementData(
                                quality = QualityIndicator.GOOD,
                                address = address,
                                mcpd = MCPDEstimate(
                                    ifft = 11,
                                    phaseSlope = 22,
                                    rssi = 33,
                                    best = 44,
                                )
                            )
                        )
                    ),
                    rttDistance = SensorValue(
                        values = listOf(
                            RttMeasurementData(
                                quality = QualityIndicator.POOR,
                                address = address,
                                rtt = RTTEstimate(55)
                            )
                        )
                    )
                )
            )
        )
        val clock = Clock.fixed(Instant.parse("2026-04-21T10:00:00Z"), ZoneOffset.UTC)

        val csv = serviceData.toDfsCsvReport(clock)
        val lines = csv.lines()

        assertEquals(DFS_CSV_HEADER, lines[0])
        assertEquals(
            "2026-04-21T10:00:00Z,AA:BB:CC:DD:EE:FF,GOOD,11,22,33,44,55,101,-12",
            lines[1]
        )
    }

    @Test
    fun `leaves blank fields when values are unavailable`() {
        val address = PeripheralBluetoothAddress(AddressType.RANDOM, "AA:BB:CC:DD:EE:FF")
        val serviceData = DFSServiceData(
            data = mapOf(
                address to SensorData()
            )
        )
        val clock = Clock.fixed(Instant.parse("2026-04-21T10:00:00Z"), ZoneOffset.UTC)

        val row = serviceData.toDfsCsvReport(clock).lines()[1]
        val columns = row.split(',', ignoreCase = false, limit = 10)

        assertEquals(10, columns.size)
        assertEquals("2026-04-21T10:00:00Z", columns[0])
        assertEquals("AA:BB:CC:DD:EE:FF", columns[1])
        assertEquals(listOf("", "", "", "", "", "", "", ""), columns.drop(2))
    }
}

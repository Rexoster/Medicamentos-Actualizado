package com.luisangel.calculadoramedicamentos.obstetrics

import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class UltrasoundDatingTest {
    @Test fun crl50mmProducesApproximatelyElevenWeeksFiveDays() {
        val result = UltrasoundDatingCalculator.fromCrl(LocalDate.of(2026, 6, 16), 50.0).getOrThrow()
        assertEquals(82, result.gestationalAgeDays)
        assertEquals(11, result.weeks)
        assertEquals(5, result.days)
    }

    @Test fun fourParameterHadlockProducesCompositeSecondTrimesterAge() {
        val result = UltrasoundDatingCalculator.fromBiometry(
            scanDate = LocalDate.of(2026, 6, 16),
            trimester = UltrasoundTrimester.SECOND,
            bpdMm = 50.0, hcMm = 180.0, acMm = 160.0, flMm = 35.0
        ).getOrThrow()
        assertEquals(145, result.gestationalAgeDays)
        assertEquals(20, result.weeks)
        assertEquals(5, result.days)
        assertTrue(result.method.contains("DBP + CC + CA + LF"))
    }

    @Test fun lmpComparisonUsesAcogThresholdForEarlySecondTrimester() {
        val scan = LocalDate.of(2026, 6, 16)
        val result = UltrasoundDatingCalculator.fromBiometry(
            scanDate = scan,
            trimester = UltrasoundTrimester.SECOND,
            bpdMm = 50.0, hcMm = 180.0, acMm = 160.0, flMm = 35.0,
            lmpDate = scan.minusDays(150)
        ).getOrThrow()
        val comparison = requireNotNull(result.lmpComparison)
        assertEquals(10, comparison.redatingThresholdDays)
        assertFalse(comparison.exceedsThreshold)
    }

    @Test fun thirdTrimesterSelectionWarnsWhenBiometryCalculatesSecondTrimester() {
        val result = UltrasoundDatingCalculator.fromBiometry(
            scanDate = LocalDate.of(2026, 6, 16),
            trimester = UltrasoundTrimester.THIRD,
            bpdMm = 50.0, hcMm = 180.0, acMm = 160.0, flMm = 35.0
        ).getOrThrow()
        assertTrue(result.warnings.any { it.contains("menor de 28 semanas") })
    }
}

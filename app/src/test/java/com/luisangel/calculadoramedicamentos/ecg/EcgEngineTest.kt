package com.luisangel.calculadoramedicamentos.ecg

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class EcgEngineTest {
    @Test fun paperSpeed25ConvertsSquaresCorrectly() {
        assertEquals(40.0, EcgCalculator.smallSquareMs(EcgPaperSpeed.SPEED_25), 0.001)
        assertEquals(200.0, EcgCalculator.largeSquareMs(EcgPaperSpeed.SPEED_25), 0.001)
    }

    @Test fun heartRateRulesMatchStandardPaperSpeed() {
        val large = EcgCalculator.rateFromLargeSquares(4.0).getOrThrow()
        val small = EcgCalculator.rateFromSmallSquares(20.0).getOrThrow()
        assertEquals(75.0, large.bpm, 0.001)
        assertEquals(75.0, small.bpm, 0.001)
        assertEquals(800.0, large.rrMs, 0.001)
    }

    @Test fun qtcFormulasMatchReferenceCalculation() {
        val qtc = EcgCalculator.qtcFromHeartRate(qtMs = 360.0, heartRateBpm = 75.0).getOrThrow()
        assertEquals(402.49, qtc.bazettMs, 0.02)
        assertEquals(387.80, qtc.fridericiaMs, 0.02)
        assertEquals(390.80, qtc.framinghamMs, 0.02)
        assertEquals(386.25, qtc.hodgesMs, 0.02)
    }

    @Test fun axisFromLeadIAndAvfClassifiesQuadrants() {
        val normal = EcgCalculator.calculateAxis(EcgAxisMethod.LEAD_I_AVF, 10.0, 10.0)
        val left = EcgCalculator.calculateAxis(EcgAxisMethod.LEAD_I_AVF, 10.0, -10.0)
        val right = EcgCalculator.calculateAxis(EcgAxisMethod.LEAD_I_AVF, -10.0, 10.0)
        assertEquals(45.0, normal.degrees, 0.01)
        assertEquals("Eje normal", normal.category)
        assertEquals("Desviación izquierda", left.category)
        assertEquals("Desviación derecha", right.category)
    }

    @Test fun lvhCriteriaDetectSokolowAndCornell() {
        val result = EcgCalculator.lvh(
            sex = EcgSex.MALE,
            sV1Mm = 20.0,
            rV5Mm = 18.0,
            rV6Mm = 10.0,
            rAvlMm = 13.0,
            sV3Mm = 18.0,
            qrsDurationMs = 100.0
        )
        assertEquals(38.0, result.sokolowLyonMm ?: 0.0, 0.001)
        assertTrue(result.sokolowPositive == true)
        assertEquals(31.0, result.cornellVoltageMm ?: 0.0, 0.001)
        assertTrue(result.cornellVoltagePositive == true)
        assertTrue(result.cornellProductPositive == true)
    }

    @Test fun stElevationUsesSexAgeAndLeadGroupThresholds() {
        val youngMan = EcgCalculator.stElevationCriteria(
            ageYears = 35,
            sex = EcgSex.MALE,
            leadGroup = EcgLeadGroup.V2_V3,
            elevationMm = 2.4,
            contiguousLeads = true
        ).getOrThrow()
        assertEquals(2.5, youngMan.thresholdMm, 0.001)
        assertFalse(youngMan.meetsCriteria)

        val woman = EcgCalculator.stElevationCriteria(
            ageYears = 35,
            sex = EcgSex.FEMALE,
            leadGroup = EcgLeadGroup.V2_V3,
            elevationMm = 1.5,
            contiguousLeads = true
        ).getOrThrow()
        assertEquals(1.5, woman.thresholdMm, 0.001)
        assertTrue(woman.meetsCriteria)
    }
}

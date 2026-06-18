package com.luisangel.calculadoramedicamentos.renal

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RenalEngineTest {
    @Test fun ckdEpi2021CreatinineMatchesReferenceExample() {
        val value = RenalCalculator.ckdEpi2021Creatinine(
            age = 60,
            sex = RenalSex.MALE,
            creatinineMgDl = 1.0
        )
        assertEquals(86.16, value, 0.02)
    }

    @Test fun combinedEquationUsesCreatinineAndCystatin() {
        val value = RenalCalculator.ckdEpi2021CreatinineCystatin(
            age = 60,
            sex = RenalSex.MALE,
            creatinineMgDl = 1.0,
            cystatinCmgL = 1.0
        )
        assertEquals(84.77, value, 0.02)
    }

    @Test fun cockcroftGaultProducesRawAndIndexedValues() {
        val result = RenalCalculator.calculate(
            RenalInput(
                method = RenalMethod.COCKCROFT_GAULT,
                ageYears = 60,
                sex = RenalSex.MALE,
                creatinine = 1.0,
                weightKg = 70.0,
                heightCm = 170.0
            )
        ).getOrThrow()
        assertEquals(77.78, result.primaryValue, 0.02)
        assertEquals(74.01, result.indexedValue ?: 0.0, 0.03)
        assertEquals(GfrCategory.G2, result.gfrCategory)
    }

    @Test fun kdigoCategoriesAndRiskMatrixAreClassified() {
        assertEquals(GfrCategory.G3A, RenalCalculator.classifyGfr(52.0))
        assertEquals(
            AlbuminuriaCategory.A2,
            RenalCalculator.classifyAlbuminuria(85.0, AcrUnit.MG_G)
        )
        assertEquals(
            CkdRiskLevel.HIGH,
            RenalCalculator.combinedRisk(GfrCategory.G3A, AlbuminuriaCategory.A2)
        )
    }

    @Test fun albuminuriaCanBeOmitted() {
        val result = RenalCalculator.calculate(
            RenalInput(
                method = RenalMethod.CKD_EPI_2021_CREATININE,
                ageYears = 50,
                sex = RenalSex.FEMALE,
                creatinine = 1.2
            )
        ).getOrThrow()
        assertEquals(GfrCategory.G3A, result.gfrCategory)
        assertEquals(null, result.albuminuriaCategory)
        assertEquals(null, result.riskLevel)
        assertTrue(result.warnings.any { it.contains("Sin ACR") })
    }
}

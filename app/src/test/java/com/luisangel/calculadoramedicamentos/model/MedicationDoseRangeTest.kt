package com.luisangel.calculadoramedicamentos.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class MedicationDoseRangeTest {

    private fun interactiveInsulin() = MedicationRecord(
        id = "test-insulin",
        type = MedicationType.ADULT,
        isSpecialAdult = true,
        name = "Insulina",
        presentation = "100 UI/mL",
        dose = "Rango",
        dosePerKg = 0.3,
        isInteractiveDose = true,
        dosePerKgMin = 0.1,
        dosePerKgMax = 0.7,
        dosePerKgStep = 0.1,
        doseUnit = "UI",
        frequencyPerDay = "Diaria",
        durationDays = 30,
        family = "Antidiabético",
        specialties = listOf("Endocrinología")
    )

    @Test
    fun validInteractiveRangePassesValidation() {
        assertNull(
            interactiveInsulin().validationError()
        )
    }

    @Test
    fun selectedDoseIsUsedForCalculation() {
        assertEquals(
            "49 UI",
            interactiveInsulin().calculatedDose(
                weight = 70.0,
                selectedDosePerKg = 0.7
            )
        )
    }

    @Test
    fun maximumMustBeGreaterThanMinimum() {
        val invalid = interactiveInsulin().copy(
            dosePerKgMax = 0.1
        )

        assertTrue(
            invalid.validationError()
                ?.contains("máxima") == true
        )
    }

    @Test
    fun interactiveStartIsClampedToRange() {
        val record = interactiveInsulin().copy(
            dosePerKg = 0.9
        )

        assertEquals(
            0.7,
            record.interactiveDoseStart()
                ?: 0.0,
            0.0001
        )
    }
}

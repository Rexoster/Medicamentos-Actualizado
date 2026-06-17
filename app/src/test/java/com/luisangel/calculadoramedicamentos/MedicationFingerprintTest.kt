package com.luisangel.calculadoramedicamentos

import com.luisangel.calculadoramedicamentos.model.MedicationRecord
import com.luisangel.calculadoramedicamentos.model.MedicationType
import com.luisangel.calculadoramedicamentos.model.fingerprint
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class MedicationFingerprintTest {
    private val base = MedicationRecord(
        type = MedicationType.ADULT,
        name = "Paracetamol",
        presentation = "Tableta 500 mg",
        dose = "1 tableta",
        doseUnit = "mg",
        frequencyPerDay = "Cada 8 horas",
        durationDays = 3,
        family = "Analgésico",
        specialties = listOf("Medicina Familiar", "Medicina Interna")
    )

    @Test
    fun ignoresCaseWhitespaceAndSpecialtyOrder() {
        val second = base.copy(
            id = "different-id",
            name = "  PARACETAMOL  ",
            specialties = base.specialties.reversed()
        )
        assertEquals(base.fingerprint(), second.fingerprint())
    }

    @Test
    fun differentPresentationIsNotDuplicate() {
        assertNotEquals(
            base.fingerprint(),
            base.copy(presentation = "Suspensión 160 mg/5 mL").fingerprint()
        )
    }

    @Test
    fun differentDoseIsNotDuplicate() {
        assertNotEquals(
            base.fingerprint(),
            base.copy(dose = "2 tabletas").fingerprint()
        )
    }
}

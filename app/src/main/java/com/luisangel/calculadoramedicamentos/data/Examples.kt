package com.luisangel.calculadoramedicamentos.data

import com.luisangel.calculadoramedicamentos.model.MedicationRecord
import com.luisangel.calculadoramedicamentos.model.MedicationType

fun exampleMedications(now: Long = System.currentTimeMillis()) = listOf(
    MedicationRecord(
        id = "example-paracetamol-adult",
        type = MedicationType.ADULT,
        name = "Paracetamol",
        presentation = "Tableta 1 g",
        dose = "1 tableta (1 g)",
        doseUnit = "mg",
        frequencyPerDay = "Cada 6 horas",
        durationDays = 1,
        family = "Analgésico y antipirético",
        specialties = listOf(
            "Anestesiología",
            "Medicina Familiar",
            "Medicina Interna",
            "Medicina de Urgencias"
        ),
        notes = "Ejemplo educativo. Máximo 4 g en 24 horas; no combinar con otros productos que contengan paracetamol. La duración real depende de la valoración clínica. Fuente: PLM.",
        createdAt = now,
        updatedAt = now
    ),
    MedicationRecord(
        id = "example-paracetamol-pediatric",
        type = MedicationType.PEDIATRIC,
        name = "Paracetamol",
        presentation = "Suspensión infantil 160 mg/5 mL",
        dose = "Dosis calculada por peso",
        dosePerKg = 10.0,
        isInteractiveDose = true,
        dosePerKgMin = 10.0,
        dosePerKgMax = 15.0,
        dosePerKgStep = 2.5,
        doseUnit = "mg",
        frequencyPerDay = "Cada 4 a 6 horas; máximo 5 dosis al día",
        durationDays = 5,
        family = "Analgésico y antipirético",
        specialties = listOf(
            "Medicina Familiar",
            "Medicina de Urgencias",
            "Pediatría"
        ),
        notes = "Ejemplo educativo para niños de 2 a 12 años. PLM indica 10 a 15 mg/kg por toma; el cálculo usa 10 mg/kg como ejemplo. No administrar por más de 5 días sin valoración médica. Fuente: PLM.",
        createdAt = now,
        updatedAt = now
    ),
    MedicationRecord(
        id = "example-insulin-basal-adult",
        type = MedicationType.ADULT,
        isSpecialAdult = true,
        name = "Insulina basal",
        presentation = "NPH o análoga de larga duración, 100 UI/mL",
        dose = "Rango interactivo de 0.1 a 0.7 UI/kg",
        dosePerKg = 0.3,
        isInteractiveDose = true,
        dosePerKgMin = 0.1,
        dosePerKgMax = 0.7,
        dosePerKgStep = 0.1,
        doseUnit = "UI",
        frequencyPerDay = "Aplicación diaria; horario y titulación individualizados",
        durationDays = 30,
        family = "Antidiabético",
        subgroup = "Insulina basal",
        specialties = listOf(
            "Endocrinología",
            "Medicina Familiar",
            "Medicina Interna"
        ),
        notes = "Ejemplo educativo basado en la PRONAM de diabetes tipo 2. La selección del tipo de insulina, horario, titulación, metas y vigilancia de hipoglucemia requieren valoración médica. Los 30 días son ilustrativos; el tratamiento suele ser continuo e individualizado.",
        createdAt = now,
        updatedAt = now
    )
)

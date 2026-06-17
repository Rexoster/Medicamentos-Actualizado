# Fuentes y alcance del módulo gineco-obstétrico

La sección Gineco-OB se diseñó como módulo nativo inspirado en la estructura pública
de calculadoras de Medicina Fetal Barcelona.

Calculadoras incluidas en esta primera versión:

- Edad gestacional y fecha probable de parto por FUM.
- Peso fetal estimado Hadlock 4 parámetros.
- Discordancia gemelar.
- Relación cerebroplacentaria simple.
- Doppler rápido: IP uterina media e índice TEI.
- LHR en hernia diafragmática por método de diámetros.

La página de Fetal Medicine Barcelona lista calculadoras para crecimiento fetal,
preeclampsia 1T, hernia diafragmática, anemia, Doppler, restricción del crecimiento
fetal y gemelos. Algunas dependen de modelos, tablas o algoritmos propietarios no
publicados como una fórmula simple en la página; por eso no se reproducen como
riesgo diagnóstico automático.


## Edad gestacional por ultrasonido

Primer trimestre:
- LCC/CRL mediante Robinson-Fleming corregida:
  GA días = 8.052 × raíz(1.037 × CRL mm) + 23.73.

Segundo y tercer trimestre:
- Ecuaciones de edad menstrual Hadlock 1984.
- Las medidas se capturan en mm y se convierten internamente a cm.
- Se selecciona automáticamente la ecuación para DBP, CC, CA y LF disponibles.

Precisión orientativa:
- Primer trimestre: ±5–7 días.
- 14+0 a 21+6: ±7–10 días.
- 22+0 a 27+6: ±10–14 días.
- 28+0 en adelante: ±21–30 días.

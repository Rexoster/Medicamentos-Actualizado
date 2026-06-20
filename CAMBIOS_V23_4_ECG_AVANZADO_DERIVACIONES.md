# Cambios v23.4 · ECG avanzado con derivaciones y territorios

Esta actualización corrige y amplía el módulo **ECG**.

## Corrección principal

- Se corrigió la sincronización visual del apartado **QT/QTc** en la vista previa del ECG.
- La vista previa ahora distingue entre:
  - **QT medido** capturado por el usuario.
  - **QTc calculado** por FC o por RR, según la configuración del apartado QTc.
- Al modificar FC/RR/QT en las calculadoras, la vista previa actualiza sus cifras y el trazo generado.

## Nuevas funciones visuales

- El ECG de muestra ahora es interactivo: al tocarlo se abre un menú de visualización.
- Se agregaron modos:
  - Una derivación.
  - ECG completo de 12 derivaciones.
  - Comparación por áreas cardíacas.
  - Comparación por territorio coronario / irrigación.
- Se agregó zoom del trazo.
- Se agregó desplazamiento horizontal para ver mejor el ECG completo.
- La rejilla ahora muestra cuadros pequeños y cuadros grandes.
- En áreas e irrigación se agregan sombreados tenues para distinguir derivaciones relacionadas.

## Derivaciones disponibles

DI, DII, DIII, aVR, aVL, aVF, V1, V2, V3, V4, V5 y V6.

## Áreas cardíacas incluidas

- Inferior: DII, DIII, aVF.
- Septal: V1, V2.
- Anterior: V3, V4.
- Lateral alta: DI, aVL.
- Lateral baja: V5, V6.
- Anteroseptal: V1 a V4.
- Lateral: DI, aVL, V5, V6.
- Posterior indirecta: V1 a V3 como espejo; V7-V9 no están en el ECG estándar de 12 derivaciones.

## Territorios coronarios incluidos

- DA / LAD: V1-V4, DI/aVL según extensión.
- CD / RCA: DII, DIII, aVF; V1 como orientación de VD.
- Cx / LCx: DI, aVL, V5-V6; inferior según dominancia.

## Limitación clínica

El trazo sigue siendo una **simulación didáctica generada por parámetros**. No sustituye un ECG real, lectura especializada, correlación clínica, biomarcadores ni manejo urgente cuando aplique.

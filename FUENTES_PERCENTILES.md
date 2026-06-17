# Fuentes del módulo de percentiles

El módulo reproduce en forma nativa los indicadores descritos por la calculadora de IHAN:

- Peso para la edad.
- Longitud/talla para la edad.
- Peso para la longitud/talla.
- Índice de masa corporal para la edad.

Los cálculos utilizan el método LMS de los estándares y referencias de crecimiento de la Organización Mundial de la Salud:

- WHO Child Growth Standards, nacimiento a 5 años, 2006.
- WHO Growth Reference, 5 a 19 años, 2007.

El peso para la edad se limita a los 10 años, conforme a la referencia OMS 2007. Peso para longitud/talla se limita a 5 años. La app presenta IMC y talla para la edad hasta los 19 años.

Para mediciones en postura distinta a la requerida por la edad, se aplica el ajuste de 0.7 cm indicado por la metodología OMS:

- Antes de 24 meses: longitud acostado.
- Desde 24 meses: talla de pie.

Los resultados son orientativos. Un percentil aislado no sustituye la valoración clínica ni el seguimiento de la trayectoria de crecimiento.


## Clasificación nutricional al estilo IHAN

La tarjeta de situación nutricional utiliza el percentil de IMC para la edad:

- Menor de P3: bajo peso.
- P3 a menor de P85: intervalo esperado.
- P85 a menor de P97: sobrepeso.
- P97 o superior: obesidad.

Los pesos de inicio de sobrepeso y obesidad se obtienen calculando el IMC
correspondiente a P85 y P97 para sexo y edad, y convirtiéndolo a peso con
la talla ajustada capturada.

Este criterio reproduce el tipo de aviso mostrado por la calculadora IHAN.


# Cotejo de percentiles OMS y CDC

## Qué usa la app

La app conserva **OMS/WHO como referencia principal**:

- OMS 2006 para 0 a 60 meses.
- OMS 2007 para 5 a 19 años.
- LMS para z-score y percentil.
- IMC para la edad como base de la situación nutricional.
- Sobrepeso/obesidad en escolares/adolescentes se interpreta con puntos de corte OMS: > +1 DE y > +2 DE.

## Revisión CDC

CDC 2000 publica archivos LMS para peso, talla, IMC y peso/talla. Los archivos incluyen parámetros L, M y S, percentiles seleccionados y z-scores. CDC también documenta que las curvas 2000 no cambiaron desde su lanzamiento original y que los archivos incluyen P85 para BMI-for-age y weight-for-stature.

CDC publicó curvas extendidas de IMC para edad en 2022, revisadas en 2024, para monitorizar IMC muy alto en niños y adolescentes de 2 a 20 años.

## Decisión técnica

No se mezclan OMS y CDC dentro de un mismo resultado porque sus poblaciones, edades y puntos de corte no son equivalentes. Para uso general en esta app se deja OMS como estándar primario. CDC queda documentado como referencia alternativa, especialmente cuando se desee comparar población de Estados Unidos o IMC extremadamente alto.

## Pendiente seguro

Si se desea una pestaña CDC real, debe incorporarse una segunda base local con los CSV oficiales CDC LMS y un selector claro de estándar: OMS o CDC. No debe cambiarse automáticamente sin avisar al usuario.

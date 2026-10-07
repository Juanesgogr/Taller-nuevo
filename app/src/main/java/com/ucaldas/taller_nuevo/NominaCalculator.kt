package com.ucaldas.taller_nuevo

const val SMMLV_2026 = 1750905.0
const val AUXILIO_TRANSPORTE_2026 = 249095.0
const val HORAS_MES = 210.0
const val PORCENTAJE_SALUD = 0.04
const val PORCENTAJE_PENSION = 0.04
const val PORCENTAJE_FONDO = 0.01

data class ResultadoNomina(
    val valorHora: Double,
    val totalHorasExtra: Double,
    val auxilioTransporte: Double,
    val totalDevengado: Double,
    val aporteSalud: Double,
    val aportePension: Double,
    val fondoSolidaridad: Double,
    val totalDeducciones: Double,
    val salarioNeto: Double
)

enum class RangoSalarial { RANGO_1, RANGO_2, RANGO_3 }

fun calcularNomina(
    salarioBasico: Double,
    horasDiurnas: Double,
    horasNocturnas: Double,
    esDominical: Boolean,
    transporteEmpresa: Boolean
): ResultadoNomina {
    val valorHora = salarioBasico / HORAS_MES
    val factorDiurno = if (esDominical) 2.15 else 1.25
    val factorNocturno = if (esDominical) 2.65 else 1.75
    val pagoExtrasDiurnas = horasDiurnas * valorHora * factorDiurno
    val pagoExtrasNocturnas = horasNocturnas * valorHora * factorNocturno
    val totalHorasExtra = pagoExtrasDiurnas + pagoExtrasNocturnas
    val ibc = salarioBasico + totalHorasExtra

    val auxilioTransporte = if (salarioBasico <= (SMMLV_2026 * 2) && !transporteEmpresa) AUXILIO_TRANSPORTE_2026 else 0.0

    val totalDevengado = ibc + auxilioTransporte
    val aporteSalud = ibc * PORCENTAJE_SALUD
    val aportePension = ibc * PORCENTAJE_PENSION
    val fondoSolidaridad = if (ibc >= (SMMLV_2026 * 4)) ibc * PORCENTAJE_FONDO else 0.0

    val totalDeducciones = aporteSalud + aportePension + fondoSolidaridad
    val salarioNeto = totalDevengado - totalDeducciones

    return ResultadoNomina(
        valorHora, totalHorasExtra, auxilioTransporte, totalDevengado,
        aporteSalud, aportePension, fondoSolidaridad, totalDeducciones, salarioNeto
    )
}

fun clasificarRango(salarioBasico: Double): RangoSalarial {
    return if (salarioBasico <= SMMLV_2026 * 2) RangoSalarial.RANGO_1
    else if (salarioBasico < SMMLV_2026 * 4) RangoSalarial.RANGO_2
    else RangoSalarial.RANGO_3
}
package com.ucaldas.taller_nuevo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ucaldas.taller_nuevo.ui.theme.TallernuevoTheme
import java.text.NumberFormat
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TallernuevoTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFFF4F6F8)
                ) {
                    CalculadoraNominaApp()
                }
            }
        }
    }
}

@Composable
fun CalculadoraNominaApp() {
    var salarioInput by rememberSaveable { mutableStateOf("") }
    var horasDiurnasInput by rememberSaveable { mutableStateOf("") }
    var horasNocturnasInput by rememberSaveable { mutableStateOf("") }

    var esDominical by rememberSaveable { mutableStateOf(false) }
    var transporteEmpresa by rememberSaveable { mutableStateOf(false) }

    var resultadoNomina by remember { mutableStateOf<ResultadoNomina?>(null) }
    var rangoSalarialActual by remember { mutableStateOf<RangoSalarial?>(null) }
    var mensajeError by rememberSaveable { mutableStateOf("") }

    val colorPrimario = Color(0xFF3F51B5)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Text(
                text = stringResource(R.string.estudiante_nombre),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E293B)
            )
            Text(
                text = "Calculadora de Nómina",
                fontSize = 14.sp,
                color = Color.Gray
            )
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                CampoEntradaSimple(
                    etiqueta = stringResource(R.string.salario_basico),
                    valor = salarioInput,
                    onValueChange = { salarioInput = it },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    ),
                    isError = mensajeError.isNotEmpty()
                )
                CampoEntradaSimple(
                    etiqueta = stringResource(R.string.horas_diurnas),
                    valor = horasDiurnasInput,
                    onValueChange = { horasDiurnasInput = it }
                )
                CampoEntradaSimple(
                    etiqueta = stringResource(R.string.horas_nocturnas),
                    valor = horasNocturnasInput,
                    onValueChange = { horasNocturnasInput = it },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    )
                )
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                FilaSwitchSimple(
                    etiqueta = stringResource(R.string.switch_dominical),
                    checked = esDominical,
                    colorActivo = colorPrimario,
                    onCheckedChange = { nuevoEstado ->
                        esDominical = nuevoEstado
                        if (resultadoNomina != null) {
                            val salario = salarioInput.toDoubleOrNull() ?: 0.0
                            val hDiurnas = horasDiurnasInput.toDoubleOrNull() ?: 0.0
                            val hNocturnas = horasNocturnasInput.toDoubleOrNull() ?: 0.0
                            resultadoNomina = calcularNomina(salario, hDiurnas, hNocturnas, esDominical, transporteEmpresa)
                        }
                    }
                )
                HorizontalDivider(color = Color(0xFFF1F5F9))
                FilaSwitchSimple(
                    etiqueta = stringResource(R.string.switch_transporte),
                    checked = transporteEmpresa,
                    colorActivo = colorPrimario,
                    onCheckedChange = { nuevoEstado ->
                        transporteEmpresa = nuevoEstado
                        if (resultadoNomina != null) {
                            val salario = salarioInput.toDoubleOrNull() ?: 0.0
                            val hDiurnas = horasDiurnasInput.toDoubleOrNull() ?: 0.0
                            val hNocturnas = horasNocturnasInput.toDoubleOrNull() ?: 0.0
                            resultadoNomina = calcularNomina(salario, hDiurnas, hNocturnas, esDominical, transporteEmpresa)
                        }
                    }
                )
            }
        }

        if (mensajeError.isNotEmpty()) {
            Text(
                text = mensajeError,
                color = MaterialTheme.colorScheme.error,
                fontSize = 14.sp,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = {
                    salarioInput = ""
                    horasDiurnasInput = ""
                    horasNocturnasInput = ""
                    esDominical = false
                    transporteEmpresa = false
                    resultadoNomina = null
                    rangoSalarialActual = null
                    mensajeError = ""
                },
                modifier = Modifier.weight(1f).height(48.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(text = stringResource(R.string.boton_limpiar), color = Color.Gray)
            }

            Button(
                onClick = {
                    val salario = salarioInput.toDoubleOrNull()
                    val hDiurnas = horasDiurnasInput.toDoubleOrNull() ?: 0.0
                    val hNocturnas = horasNocturnasInput.toDoubleOrNull() ?: 0.0
                    val totalHorasExtra = hDiurnas + hNocturnas

                    if (salario == null) {
                        mensajeError = "Ingrese un salario básico válido"
                        resultadoNomina = null
                    } else if (salario < SMMLV_2026) {
                        mensajeError = "El salario no puede ser inferior al mínimo ($ 1.750.905)"
                        resultadoNomina = null
                    } else if (hDiurnas < 0 || hNocturnas < 0) {
                        mensajeError = "Las horas extra no pueden ser negativas"
                        resultadoNomina = null
                    } else if (totalHorasExtra > 48) {
                        mensajeError = "El total de horas extra no supera 48 en el mes"
                        resultadoNomina = null
                    } else {
                        mensajeError = ""
                        rangoSalarialActual = clasificarRango(salario)
                        resultadoNomina = calcularNomina(salario, hDiurnas, hNocturnas, esDominical, transporteEmpresa)
                    }
                },
                modifier = Modifier.weight(1f).height(48.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = colorPrimario)
            ) {
                Text(text = stringResource(R.string.boton_calcular))
            }
        }

        resultadoNomina?.let { nomina ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    rangoSalarialActual?.let { rango ->
                        val imagenRes = when (rango) {
                            RangoSalarial.RANGO_1 -> R.drawable.ic_rango1
                            RangoSalarial.RANGO_2 -> R.drawable.ic_rango2
                            RangoSalarial.RANGO_3 -> R.drawable.ic_rango3
                        }
                        val descRes = when (rango) {
                            RangoSalarial.RANGO_1 -> R.string.rango_1_desc
                            RangoSalarial.RANGO_2 -> R.string.rango_2_desc
                            RangoSalarial.RANGO_3 -> R.string.rango_3_desc
                        }
                        Image(
                            painter = painterResource(imagenRes),
                            contentDescription = stringResource(descRes),
                            modifier = Modifier.size(80.dp).padding(bottom = 8.dp)
                        )
                        Text(
                            text = stringResource(descRes),
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp,
                            color = Color.DarkGray
                        )
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = Color(0xFFF1F5F9))

                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text("DEVENGADO", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = colorPrimario)
                        FilaDesglose("Valor hora ordinaria", nomina.valorHora)
                        FilaDesglose("Salario básico", salarioInput.toDoubleOrNull() ?: 0.0)
                        FilaDesglose("Total horas extra", nomina.totalHorasExtra)
                        FilaDesglose("Auxilio de transporte", nomina.auxilioTransporte)
                        FilaDesglose("Total Devengado", nomina.totalDevengado, esTotal = true)

                        Spacer(modifier = Modifier.height(12.dp))

                        Text("DEDUCCIONES", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = colorPrimario)
                        FilaDesglose("Aporte Salud (4%)", nomina.aporteSalud)
                        FilaDesglose("Aporte Pensión (4%)", nomina.aportePension)
                        FilaDesglose("Fondo de Solidaridad", nomina.fondoSolidaridad)
                        FilaDesglose("Total Deducciones", nomina.totalDeducciones, esTotal = true)
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                            .background(Color(0xFFEEF2FF), RoundedCornerShape(8.dp))
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Neto a Pagar", fontSize = 14.sp, color = colorPrimario)
                            Text(
                                text = formatoMoneda(nomina.salarioNeto),
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E293B)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CampoEntradaSimple(
    etiqueta: String,
    valor: String,
    onValueChange: (String) -> Unit,
    keyboardOptions: KeyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
    isError: Boolean = false
) {
    OutlinedTextField(
        value = valor,
        onValueChange = onValueChange,
        label = { Text(etiqueta) },
        singleLine = true,
        keyboardOptions = keyboardOptions,
        isError = isError,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color(0xFF1E293B),
            unfocusedTextColor = Color(0xFF1E293B),
            unfocusedBorderColor = Color(0xFFE2E8F0),
            focusedBorderColor = Color(0xFF3F51B5)
        )
    )
}

@Composable
fun FilaSwitchSimple(
    etiqueta: String,
    checked: Boolean,
    colorActivo: Color,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = etiqueta, fontSize = 15.sp, color = Color(0xFF334155))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(checkedTrackColor = colorActivo)
        )
    }
}

@Composable
fun FilaDesglose(
    concepto: String,
    valor: Double,
    esTotal: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = concepto,
            fontWeight = if (esTotal) FontWeight.Bold else FontWeight.Normal,
            fontSize = 14.sp,
            color = if (esTotal) Color.Black else Color.DarkGray
        )
        Text(
            text = formatoMoneda(valor),
            fontWeight = if (esTotal) FontWeight.Bold else FontWeight.Normal,
            fontSize = 14.sp,
            color = if (esTotal) Color.Black else Color.DarkGray
        )
    }
}

fun formatoMoneda(valor: Double): String {
    val formato = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("es-CO"))
    formato.maximumFractionDigits = 0
    return formato.format(valor)
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    TallernuevoTheme {
        CalculadoraNominaApp()
    }
}

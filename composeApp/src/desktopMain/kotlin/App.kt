import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    MaterialTheme {
        var sourceCode by remember { mutableStateOf("escreva('Olá mundo!')") }
        var fileName by remember { mutableStateOf("Principal.nhe") }

        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.Start) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                TextField(
                    value = fileName,
                    onValueChange = { fileName = it },
                    label = { Text("Nome do Arquivo") },
                    modifier = Modifier.padding(16.dp)
                )
                Button(onClick = { }, modifier = Modifier.padding(16.dp)) {
                    Text("Executar")
                }
            }

            TextField(
                modifier = Modifier.fillMaxSize(),

                value = sourceCode,
                onValueChange = { sourceCode = it },
                label = { Text("Código Fonte") }
            )
        }
    }
}
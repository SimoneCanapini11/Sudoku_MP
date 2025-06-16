package com.canapini_grasselli.app_sudoku

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.canapini_grasselli.app_sudoku.ui.theme.App_SudokuTheme
import com.canapini_grasselli.app_sudoku.views.SudokuScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            App_SudokuTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SudokuScreen()
                }
            }
        }
    }
}

//Cambiare i font
//landscape
//mantieni stato partita in background
//Inserire la possibilità di mettere in pausa e quando si riapre l'applicazione l'ultima partita messa in pausa viene ricaricata
//Due modalità di gioco: 1)Libera il timer conta semplicemente il tempo a finire la partita 2)Sfida allo scadere del timer la partita termina con sconfitta
//Appunti
/*
1.      Una interfaccia utente ben fatta!
2.      Un database dei risultati precedenti
·       Partire giocate
·       Partite vinte (terminate)
·       Miglior Tempo
L'interfaccia dovrà inoltre implementare il massimo degli aiuti possibili all'utente tra cui almeno uno:
·       Appunti utente per ognuna delle 81 celle
*/
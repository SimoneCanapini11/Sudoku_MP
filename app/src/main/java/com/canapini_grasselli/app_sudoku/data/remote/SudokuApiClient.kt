import com.canapini_grasselli.app_sudoku.data.remote.SudokuAPI
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object SudokuApiClient {
    val service: SudokuAPI = Retrofit.Builder()
        .baseUrl("https://sudoku-api.vercel.app/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(SudokuAPI::class.java)
}
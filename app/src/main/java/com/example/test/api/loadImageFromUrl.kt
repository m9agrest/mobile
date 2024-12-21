import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.drawable.BitmapDrawable
import android.widget.ImageView
import android.util.Log
import android.util.LruCache
import android.view.View
import com.example.test.api.AddCookiesInterceptor
import com.example.test.pojo.Photo
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.InputStream


val MemoryCache: LruCache<String, Bitmap> = object : LruCache<String, Bitmap>(4 * 1024 * 1024) {
    override fun sizeOf(key: String, value: Bitmap): Int {
        // Размер кэшируемого объекта в байтах
        return value.byteCount
    }
}


@OptIn(DelicateCoroutinesApi::class)
fun loadImageFromUrl(imageView: ImageView, photo: Photo?) {
    if(photo == null){
        return
    }
    val bitmap = MemoryCache.get(photo.file)
    if(bitmap != null){
        imageView.setImageBitmap(bitmap)
        return
    }
    Log.d("Debug", "load image")



    // Формируем запрос с добавлением заголовков и параметров
    val client = OkHttpClient.Builder()
        .addInterceptor(AddCookiesInterceptor(imageView.context))
        .build()
    val request = Request.Builder()
        .url("https://adapt-key.com/content/${photo.file}?key=${photo.key}")
        .build()


    GlobalScope.launch(Dispatchers.IO) {
        try {
            // Выполняем запрос
            val response = client.newCall(request).execute()

            // Проверяем успешность ответа
            if (response.isSuccessful) {
                val inputStream: InputStream = response.body?.byteStream() ?: return@launch
                val bitmap: Bitmap = BitmapFactory.decodeStream(inputStream)
                MemoryCache.put(photo.file, bitmap)
                // Переходим на основной поток для обновления UI
                withContext(Dispatchers.Main) {
                    imageView.setImageBitmap(bitmap)
                }
            } else {
                Log.d("Debug", "http not 200")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

@OptIn(DelicateCoroutinesApi::class)
fun loadBackGroundFromUrl(linearLayout: View, photo: Photo?){
    if(photo == null){
        return
    }
    fun __update(linearLayout: View, bitmap: Bitmap){
        // Получаем размеры LinearLayout
        val width = linearLayout.width
        val height = linearLayout.height

        // Рассчитываем коэффициенты масштаба для ширины и высоты
        val scaleX = width.toFloat() / bitmap.width
        val scaleY = height.toFloat() / bitmap.height

        // Используем максимальный коэффициент масштабирования для сохранения пропорций
        val scale = Math.max(scaleX, scaleY)

        // Масштабируем изображение, сохраняя пропорции
        val matrix = Matrix()
        matrix.setScale(scale, scale)

        // Применяем матрицу к изображению
        val scaledBitmap = Bitmap.createBitmap(
            bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true
        )

        // Находим центральную часть изображения, которую нужно обрезать
        val cropX = (scaledBitmap.width - width) / 2
        val cropY = (scaledBitmap.height - height) / 2

        // Обрезаем центральную часть
        val croppedBitmap = Bitmap.createBitmap(
            scaledBitmap, cropX, cropY, width, height
        )

        // Создаем BitmapDrawable и устанавливаем как фон
        val drawable = BitmapDrawable(linearLayout.context.resources, croppedBitmap)
        linearLayout.background = drawable
    }
    val bitmap = MemoryCache.get(photo.file)
    if(bitmap != null){
        __update(linearLayout, bitmap)
        return
    }
    Log.d("Debug", "load image")

    // Формируем запрос с добавлением заголовков и параметров
    val client = OkHttpClient.Builder()
        .addInterceptor(AddCookiesInterceptor(linearLayout.context))
        .build()
    val request = Request.Builder()
        .url("https://adapt-key.com/content/${photo.file}?key=${photo.key}")
        .build()


    GlobalScope.launch(Dispatchers.IO) {
        try {
            // Выполняем запрос
            val response = client.newCall(request).execute()

            // Проверяем успешность ответа
            if (response.isSuccessful) {
                val inputStream: InputStream = response.body?.byteStream() ?: return@launch
                val bitmap: Bitmap = BitmapFactory.decodeStream(inputStream)

                // Переходим на основной поток для обновления UI
                withContext(Dispatchers.Main) {
                    __update(linearLayout, bitmap)
                }
            } else {
                Log.d("Debug", "http not 200")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}

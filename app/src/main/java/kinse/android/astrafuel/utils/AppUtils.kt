package kinse.android.astrafuel.utils

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.util.TypedValue
import android.view.ViewGroup
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kiinse.programs.kiinseapi.fuel.fueldata.Fuel
import kiinse.programs.kiinseapi.fuel.fueldata.NewsData
import kinse.android.astrafuel.MainActivity
import org.json.JSONObject
import java.io.*
import java.text.SimpleDateFormat

object AppUtils {

    fun writeToStream(outputStream: OutputStream, jsonObject: JSONObject) {
        val osw = OutputStreamWriter(outputStream, "UTF-8")
        osw.write(jsonObject.toString())
        osw.flush()
        osw.close()
        outputStream.close()
    }

    fun inputStreamToString(inputStream: InputStream): String {
        val reader = BufferedReader(InputStreamReader(inputStream))
        var inputLine: String?
        val content = StringBuffer()
        while (reader.readLine().also { inputLine = it } != null) {
            content.append(inputLine)
        }
        reader.close()
        return content.toString()
    }

    fun createButton(context: Context, text: String, layout: LinearLayout, background: Drawable?, color: Int) {
        val btnTag = Button(context)
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT)
        params.height = 150
        params.setMargins(10, 10, 15, 0)
        btnTag.layoutParams = params
        btnTag.background = background
        btnTag.setTextColor(color)
        btnTag.text = text
        btnTag.textSize = 13F
        layout.addView(btnTag)
    }

    fun createFuelLayout(context: Context, fuel: Fuel, layout: LinearLayout, background: Drawable?, color: Int) {
        val fuelLayout = RelativeLayout(context)
        val params = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.MATCH_PARENT,
            50)
        params.height = 150
        params.setMargins(10, 10, 15, 0)
        fuelLayout.layoutParams = params
        fuelLayout.background = background

        var textParams = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT)
        textParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE)
        textParams.topMargin = 42
        textParams.marginStart = 50
        setText(context, fuelLayout, fuel.name, textParams, color)

        textParams = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT)
        textParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE)
        textParams.topMargin = 42
        textParams.marginEnd = 50
        setText(context, fuelLayout, "${fuel.price}₽", textParams, color)

        fuelLayout.id = fuel.id
        layout.addView(fuelLayout)
    }

    fun createNewsLayout(context: Context, news: NewsData, layout: LinearLayout, background: Drawable?, color: Int) {
        val newsLayout = RelativeLayout(context)
        val params = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.MATCH_PARENT,
            900)
        params.height = 900
        params.setMargins(10, 10, 15, 0)
        newsLayout.layoutParams = params
        newsLayout.background = background

        var textParams = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT)
        textParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE)
        textParams.topMargin = 30
        textParams.marginStart = 70
        setText(context, newsLayout, news.title, textParams, color)



        textParams = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT)
        textParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE)
        textParams.topMargin = 30
        textParams.marginEnd = 70

        val sdf = SimpleDateFormat("dd.MM.yyyy")
        setText(context, newsLayout, sdf.format(news.date), textParams, color)

        textParams = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT)
        textParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE)
        textParams.topMargin = 115
        textParams.bottomMargin = 30
        textParams.marginStart = 70
        textParams.marginEnd = 70

        setTextBlock(context, newsLayout, news.text, textParams, color)

        newsLayout.id = news.id
        layout.addView(newsLayout)
    }

    fun getThemeTextColor(context: Context): Int {
        val typedValue = TypedValue()
        context.theme.resolveAttribute(com.google.android.material.R.attr.colorSecondaryVariant, typedValue, true)
        return typedValue.data
    }

    private fun setTextBlock(context: Context, layout: ViewGroup, textStr: String, params: RelativeLayout.LayoutParams, color: Int) {
        val text = MultiAutoCompleteTextView(context)
        text.setText(textStr)
        text.isEnabled = false
        text.textSize = 15F
        text.setTextColor(color)
        text.layoutParams = params
        layout.addView(text)
    }

    private fun setText(context: Context, layout: ViewGroup, textStr: String, params: RelativeLayout.LayoutParams, color: Int) {
        val text = TextView(context)
        text.text = textStr
        text.textSize = 15F
        text.setTextColor(color)
        text.layoutParams = params
        layout.addView(text)
    }

    fun sendErrorDialog(context: Context, message: String?) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }


    fun checkPermission(permission: String, requestCode: Int) {
        if (ContextCompat.checkSelfPermission(MainActivity.context!!, permission) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(MainActivity.instance!!, arrayOf(permission), requestCode)
        }
    }

}
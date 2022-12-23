package kinse.android.astrafuel

import android.accounts.Account
import android.app.AlertDialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.commit
import com.yandex.mapkit.Animation
import kiinse.programs.kiinseapi.fuel.fueldata.Station
import kinse.android.astrafuel.listeners.MapTapListener
import kinse.android.astrafuel.utils.AppUtils
import kinse.android.astrafuel.utils.RESTUtils
import org.json.JSONObject
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AccountLoginFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AccountLoginFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_account_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val loginButton = view.findViewById<AppCompatButton>(R.id.login_button)
        loginButton.setOnClickListener {
            loginButton.startAnimation(AnimationUtils.loadAnimation(MainActivity.context, R.anim.alpha_animator))
            val credentials = JSONObject()
            credentials.put("login", view.findViewById<EditText>(R.id.login_text).text)
            credentials.put("password", view.findViewById<EditText>(R.id.password_text).text)

            try {
                MainActivity.account = RESTUtils.login(credentials)
            } catch (e: Exception) {
                e.printStackTrace()
                if (e.message == "Account with this login already registered!") {
                    try {
                        RESTUtils.removeAccount(credentials)
                        RESTUtils.login(credentials)
                    } catch (e: Exception) {
                        AppUtils.sendErrorDialog(view.context, e.message)
                    }
                } else {
                    AppUtils.sendErrorDialog(view.context, e.message)
                }
            }

            val account = MainActivity.account
            if (account != null) {
                MainActivity.instance?.supportFragmentManager?.commit{
                    setCustomAnimations(R.anim.open_animator, R.anim.close_animator)
                    replace(R.id.window_fragment, AccountFragment(), "account")
                    addToBackStack(null)
                }
            }
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AccountFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AccountLoginFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
package com.example.tallerpracticoi_dsm.users

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.tallerpracticoi_dsm.R
import com.example.tallerpracticoi_dsm.databinding.ActivityCreateUserBinding
import com.example.tallerpracticoi_dsm.dto.UserDTO
import com.example.tallerpracticoi_dsm.interfaces.UserPayload
import com.example.tallerpracticoi_dsm.interfaces.UsersApi
import com.example.tallerpracticoi_dsm.schedules.CitesList
import com.example.tallerpracticoi_dsm.utils.AppLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class CreateUser : AppLayout() {
    private val dateFormatter = SimpleDateFormat("dd/MM/yyyy")
    private lateinit var binding: ActivityCreateUserBinding
    private lateinit var usersApi: UsersApi

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityCreateUserBinding.inflate(layoutInflater)
        setContentView(binding.root)
        super.onCreate(savedInstanceState)

        usersApi = this.getApi(UsersApi::class.java)

        createUser()
    }

    private fun createUser() {
        binding.txtBirthdate.setOnClickListener{
            initDatePicker(binding.txtBirthdate)
        }

        binding.txtBirthdate.setOnFocusChangeListener{_, hasFocus ->
            if (hasFocus)
                initDatePicker(binding.txtBirthdate)
        }

        onButtonClick()
    }

    private fun onButtonClick() {
        val duiRexeg = Regex("^[0-9]{8}-[0-9]{1}$")
        val phoneRexeg = Regex("^[762][0-9]{3}[0-9]{4}$")

        binding.btnCreateUser.setOnClickListener{
            val email = getSharedPreferences(getString(R.string.preps_file), Context.MODE_PRIVATE).getString("email", "")
            val name = binding.txtName.text.toString().trim()
            val lastname = binding.txtLastname.text.toString().trim()
            val dui = binding.txtDUI.text.toString().trim()
            val phone = binding.txtPhone.text.toString().trim()
            val birthdate = binding.txtBirthdate.text.toString().trim()
            val address = binding.txtAddress.text.toString().trim()
            var error = 0

            if(name.length < 3) {
                error++
                binding.txtName.error = getString(R.string.error_input_required)
            }

            if(lastname.length < 3) {
                error++
                binding.txtLastname.error = getString(R.string.error_input_required)
            }

            if(!duiRexeg.matches(dui)) {
                error++
                binding.txtDUI.error = getString(R.string.error_input_dui)
            }

            if(!phoneRexeg.matches(phone)) {
                error++
                binding.txtPhone.error = getString(R.string.error_input_phone)
            }

            if(birthdate.length === 0) {
                error++
                binding.txtBirthdate.error = getString(R.string.error_input_birthdate_required)
            }

            if(address.length < 3) {
                error++
                binding.txtAddress.error = getString(R.string.error_input_required)
            }

            if(error === 0) {
                val birthdateSplit = birthdate.split('/')
                val dateFormatted = "${birthdateSplit[2]}/${birthdateSplit[1]}/${birthdateSplit[0]}"
                val newUser = UserPayload(dui, name, lastname, email!!, address, dateFormatted, phone)
                val call = this.usersApi.newUser(newUser)
                call.enqueue(object: Callback<UserDTO> {
                    override fun onResponse(call: Call<UserDTO>, response: Response<UserDTO>) {
                        println("***** response ******8")
                        println(newUser)
                        println("***** response ******8")
                        if(response.isSuccessful) {
                            val preps = getSharedPreferences(getString(R.string.preps_file),Context.MODE_PRIVATE).edit()
                            preps.putString("dui",dui)
                            preps.apply()
                            Toast.makeText(this@CreateUser, R.string.success_create_user, Toast.LENGTH_SHORT).show()
                            val intent = Intent(this@CreateUser, CitesList::class.java)
                            intent.putExtra("itemMenuSelected", R.id.cites)
                            startActivity(intent)
                        }
                        else {
                            val error = response.errorBody()?.string()
                            Log.e("API", "Error al crear el usuario: $error")
                            Toast.makeText(this@CreateUser, "Error al crear el usuario", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<UserDTO>, t: Throwable) {
                        Toast.makeText(this@CreateUser, R.string.failed_create_user, Toast.LENGTH_SHORT).show()
                    }
                })
            }

            error = 0

        }
    }

    private fun initDatePicker(editText: EditText) {
        if(editText.error != null && editText.error.isNotEmpty()) editText.error = null
        val calendar = Calendar.getInstance()
        if(editText.text.isNotEmpty()) calendar.time = dateFormatter.parse(editText.text.toString())
        val year = calendar.get(Calendar.YEAR)-18
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val datePickerDialog = DatePickerDialog(this, {
                _, year, month , dayOfMonth ->

            val selectedDate = dateFormatter.parse("$dayOfMonth/${month + 1}/$year")
            if(selectedDate.after(Date())) {
                editText.error = getString(R.string.error_input_birthdate)
                return@DatePickerDialog
            }
            val actualMonth = month +1;
            editText.setText("$dayOfMonth/$actualMonth/$year")
        }, year, month, day)
        datePickerDialog.show()
    }
}
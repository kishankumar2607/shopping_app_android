package com.bibintomj.firebasegroupapp1

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bibintomj.firebasegroupapp1.databinding.ActivityOrderConfirmationBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class OrderConfirmationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOrderConfirmationBinding
    private lateinit var continueShopping: Button
    private lateinit var orderTotal: TextView
    private lateinit var orderDate: TextView
    private lateinit var orderNumber: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderConfirmationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        continueShopping = findViewById(R.id.continueShoppingButton)
        orderDate = findViewById(R.id.orderDate)
        orderTotal = findViewById(R.id.orderTotal)
        orderNumber = findViewById(R.id.orderNumber)

        val orderId = (100000..999999).random()
        orderNumber.text = "Order Number: #$orderId"

        orderDate.text = "Delivery Date: ${getDeliveryDate()}"

        val totalAmount = intent.getDoubleExtra("totalAmount", 0.0)
        orderTotal.text = "Total: $${String.format("%.2f", totalAmount)}"

        continueShopping.setOnClickListener {
            val intent = Intent(this, ProductActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun getDeliveryDate(): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DATE, 1)
        val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }
}
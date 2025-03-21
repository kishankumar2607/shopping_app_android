package com.bibintomj.firebasegroupapp1

import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bibintomj.firebasegroupapp1.databinding.ActivityCheckoutBinding
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Locale

class CheckoutActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCheckoutBinding
    private var adapter: CartAdapter? = null
    private lateinit var paymentButton: Button
    private lateinit var rView: RecyclerView
    private lateinit var emptyCartLayout: View
    private lateinit var shopNowButton: Button
    private lateinit var footerLayout: View

    private var currentTotal = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCheckoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DATE, 1)
        val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        val tomorrowDate = dateFormat.format(calendar.time)
        val deliveryDate: TextView = findViewById(R.id.deliveryDetails)
        deliveryDate.text = "Delivery by: $tomorrowDate"

        val backButton: ImageButton = findViewById(R.id.backButton)
        backButton.setOnClickListener({
            finish()
        })

        paymentButton = findViewById(R.id.payment_button)
        rView = findViewById(R.id.rView)
        emptyCartLayout = findViewById(R.id.emptyCartLayout)
        shopNowButton = findViewById(R.id.shopNowButton)
        footerLayout = findViewById(R.id.footerLayout)

        paymentButton.setOnClickListener {
            val intent = Intent(this, PaymentActivity::class.java)
            intent.putExtra("totalAmount", currentTotal)
            startActivity(intent)
            finish()
        }

        shopNowButton.setOnClickListener {
            val intent = Intent(this, ProductActivity::class.java)
            startActivity(intent)
            finish()
        }

        rView.layoutManager = LinearLayoutManager(this)

        loadCartProducts()
    }

    override fun onStart() {
        super.onStart()
        adapter?.startListening()
    }

    private fun loadCartProducts() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val cartRef = FirebaseDatabase.getInstance().reference.child("cart/$userId")

        cartRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists() && snapshot.childrenCount > 0) {
                    showCartWithItems()
                } else {
                    showEmptyCartMessage()
                }

                var subTotal = 0.0
                for (child in snapshot.children) {
                    val cartItem = child.getValue(CartItem::class.java)
                    if (cartItem?.product != null) {
                        subTotal += cartItem.product.price * cartItem.count
                    }
                }

                val deliveryFee = if (subTotal >= 1000) 0.0 else 30.0

                val deliveryMessage: TextView = findViewById(R.id.freeDeliveryMessage)
                deliveryMessage.visibility = if (subTotal >= 1000) View.GONE else View.VISIBLE

                val tax = subTotal * 0.13
                val total = subTotal + tax + deliveryFee

                currentTotal = total

                val subTotalAmount: TextView = findViewById(R.id.subTotalAmount)
                val deliveryAmount: TextView = findViewById(R.id.deliveryAmount)
                val taxAmount: TextView = findViewById(R.id.taxAmount)
                val totalAmount: TextView = findViewById(R.id.totalAmount)

                subTotalAmount.text = "$${String.format("%.2f", subTotal)}"
                deliveryAmount.text = "$${String.format("%.2f", deliveryFee)}"
                taxAmount.text = "$${String.format("%.2f", tax)}"
                totalAmount.text = "$${String.format("%.2f", total)}"
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("CheckoutActivity", "loadCartProducts:error", error.toException())
            }
        })

        val options = FirebaseRecyclerOptions.Builder<CartItem>()
            .setQuery(cartRef, CartItem::class.java)
            .build()

        adapter = CartAdapter(options)
        rView.adapter = adapter
        adapter?.startListening()
    }

    private fun showEmptyCartMessage() {
        rView.visibility = View.GONE
        footerLayout.visibility = View.GONE
        emptyCartLayout.visibility = View.VISIBLE
    }

    private fun showCartWithItems() {
        rView.visibility = View.VISIBLE
        footerLayout.visibility = View.VISIBLE
        emptyCartLayout.visibility = View.GONE
    }
}



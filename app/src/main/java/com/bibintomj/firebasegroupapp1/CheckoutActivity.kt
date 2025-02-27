package com.bibintomj.firebasegroupapp1

import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bibintomj.firebasegroupapp1.databinding.ActivityCheckoutBinding
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener

class CheckoutActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCheckoutBinding
    private var adapter: CartAdapter? = null
    private lateinit var paymentButton: Button
    private lateinit var rView: RecyclerView
    private lateinit var emptyCartLayout: View
    private lateinit var shopNowButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCheckoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val backButton: ImageButton = findViewById(R.id.backButton)
        backButton.setOnClickListener({
            finish()
        })

        paymentButton = findViewById(R.id.payment_button)
        rView = findViewById(R.id.rView)
        emptyCartLayout = findViewById(R.id.emptyCartLayout)
        shopNowButton = findViewById(R.id.shopNowButton)

        paymentButton.setOnClickListener {
            val intent = Intent(this, PaymentActivity::class.java)
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

        cartRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists() && snapshot.childrenCount > 0) {
                    showCartWithItems()
                } else {
                    showEmptyCartMessage()
                }
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
        paymentButton.visibility = View.GONE
        emptyCartLayout.visibility = View.VISIBLE
    }

    private fun showCartWithItems() {
        rView.visibility = View.VISIBLE
        paymentButton.visibility = View.VISIBLE
        emptyCartLayout.visibility = View.GONE
    }

}



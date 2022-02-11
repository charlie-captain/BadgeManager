package com.charlie.badge

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.charlie.badgelibrary.BadgeManager
import com.charlie.badgelibrary.widget.BadgeView
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val node1 = "Badge"
        BadgeManager.instance.createNewNode(node1)

        val node1Child = "BadgeChild1"
        BadgeManager.instance.createNewNode(node1Child, parentKey = node1)
        BadgeManager.instance.createNewNode("BadgeChild2", parentKey = node1)
        BadgeManager.instance.createNewNode("BadgeChild3", parentKey = node1)
        BadgeManager.instance.createNewNode("BadgeChild4", parentKey = node1)

        val badgeView = findViewById<BadgeView>(R.id.badge)
        badgeView.bindNode(node1)

        findViewById<Button>(R.id.button).setOnClickListener {
            val random = Random.nextInt(0, 100)
            BadgeManager.instance.edit(node1).setBadgeNumber(random).commit()
        }

        findViewById<Button>(R.id.buttonChild).setOnClickListener {
//            val random = Random.nextInt(0, 10)
//            BadgeManager.instance.edit("BadgeChild1").setBadgeNumber(random).commit()
//            BadgeManager.instance.edit("BadgeChild2").setBadgeNumber(random).commit()
//            BadgeManager.instance.edit("BadgeChild3").setBadgeNumber(random).commit()
//            BadgeManager.instance.edit("BadgeChild4").setBadgeNumber(random).commit()

            repeat(10) {
                Thread(Runnable {
                    val random = Random.nextInt(0, 10)
                    Log.d("test", "Thread = ${Thread.currentThread().name}  $random")
                    BadgeManager.instance.edit("BadgeChild1").setBadgeNumber(random).commit()
                }).start()
            }
        }

        findViewById<Button>(R.id.buttonJump).setOnClickListener {
            startActivity(Intent(this, ChildActivity::class.java))
        }
    }
}
package com.charlie.badge

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.charlie.badgelibrary.BadgeManager
import com.charlie.badgelibrary.widget.BadgeView

class ChildActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_child)


        val key = "BadgeChild1"
        findViewById<BadgeView>(R.id.badge).bindNode(key)

        findViewById<BadgeView>(R.id.badge2).bindNode("BadgeChild2")
        findViewById<BadgeView>(R.id.badge3).bindNode("BadgeChild3")
        findViewById<BadgeView>(R.id.badge4).bindNode("BadgeChild4")


        findViewById<Button>(R.id.button).setOnClickListener {
            BadgeManager.instance.edit(key).dismissAndCommit()
        }
    }
}
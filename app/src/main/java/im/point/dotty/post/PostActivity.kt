package im.point.dotty.post

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import im.point.dotty.R
import im.point.dotty.databinding.ActivityPostBinding

class PostActivity : AppCompatActivity() {
    protected lateinit var binding: ActivityPostBinding

    companion object {
        const val POST_ID = "post-id"
        const val POST_FROM = "post-from"

        fun getIntent(context: Context, from: From, id: String): Intent {
            val intent = Intent(context, PostActivity::class.java)
            intent.putExtra(POST_ID, id)
            intent.putExtra(POST_FROM, from)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (supportFragmentManager.backStackEntryCount == 0) {
            val fragment = PostFragment.newInstance(intent.getSerializableExtra(POST_FROM) as From,
                    intent.getStringExtra(POST_ID)!!)
            supportFragmentManager.beginTransaction()
                    .replace(R.id.post_container, fragment)
                    .addToBackStack(fragment::class.simpleName)
                    .commit()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (supportFragmentManager.backStackEntryCount == 0) {
            finish()
        }
    }
}

enum class From {
    FROM_RECENT,
    FROM_COMMENTED,
    FROM_ALL
}
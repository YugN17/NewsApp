import android.os.Bundle
import android.view.View
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.navigation.NavArgs
import androidx.navigation.fragment.navArgs
import com.example.newsapp.R
import com.example.newsapp.ui.MainActivity
import com.example.newsapp.ui.NewsViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_art.*

class ArtFragment : Fragment(R.layout.fragment_art) {

     val args: ArtFragmentArgs by navArgs()
    lateinit var fab:FloatingActionButton
    lateinit var viewModel: NewsViewModel
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel=(activity as MainActivity).viewModel
        val article=args.article
        fab=view.findViewById(R.id.fab)
webView.apply {
    webViewClient= WebViewClient()
    loadUrl(article.url)
    fab.setOnClickListener { 
        viewModel.saveArticle(article)
        Snackbar.make(this,"Article saved successfully",Snackbar.LENGTH_SHORT).show()
    }
}

    }
}
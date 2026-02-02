package net.bloople.manga

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import coil3.load

class PageFragment : Fragment() {
    private lateinit var url: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        url = requireArguments().getString("url")!!
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.page_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val imageView: ImageView = view.findViewById(R.id.image)

        imageView.load(url)
    }

    companion object {
        fun newInstance(url: MangosUrl): PageFragment {
            return PageFragment().apply {
                arguments = Bundle().apply {
                    putString("url", url.toString())
                }
            }
        }
    }
}
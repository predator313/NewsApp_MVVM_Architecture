import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.aamirashraf.mvvmnewsapp.R
import com.aamirashraf.mvvmnewsapp.adapter.NewsAdapter
import com.aamirashraf.mvvmnewsapp.db.ArticleDatabase
import com.aamirashraf.mvvmnewsapp.repository.NewsRepository
import com.aamirashraf.mvvmnewsapp.ui.NewsActivity
import com.aamirashraf.mvvmnewsapp.ui.NewsViewModel
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.aamirashraf.mvvmnewsapp.ui.NewsViewModelProviderFactory
import com.google.android.material.snackbar.Snackbar

class SavedNewsFragment: Fragment(R.layout.saved_news_fragment) {
    lateinit var viewModel: NewsViewModel
    lateinit var rvSavedNews: RecyclerView
    lateinit var newsAdapter: NewsAdapter
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel=(activity as NewsActivity).viewModel
        val newsRepository= NewsRepository(ArticleDatabase.createDatabase(requireContext()))
        val viewModelProviderFactory= NewsViewModelProviderFactory(newsRepository)
        viewModel= ViewModelProvider(requireActivity(),viewModelProviderFactory)[NewsViewModel::class.java]
//        viewModel=(activity as NewsActivity).viewModel
        rvSavedNews=view.findViewById(R.id.rvSavedNews)
        setupRecyclerView()
        newsAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("article", it)
            }
            findNavController().navigate(
                R.id.action_savedNewsFragment_to_articleFragment,
                bundle
            )
        }
        val itemTouchHelperCallback=object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or  ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true

            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position=viewHolder.adapterPosition
                val article=newsAdapter.differ.currentList[position]
                viewModel.deleteArticle(article)
                Snackbar.make(view,"successfully deleted",Snackbar.LENGTH_LONG).apply {
                    setAction("Undo"){
                        viewModel.savedArticle(article)
                    }
                    show()
                }
            }

        }
        ItemTouchHelper(itemTouchHelperCallback).apply {
            attachToRecyclerView(rvSavedNews)
        }

        viewModel.savedNews().observe(viewLifecycleOwner, Observer {article->
            newsAdapter.differ.submitList(article)

        })
        return
    }
    private fun setupRecyclerView(){
        newsAdapter= NewsAdapter()
        rvSavedNews.apply {
            adapter=newsAdapter
            //some issue with the layout manager that i will solved tomorrow
            //we can use required activity or activity as it reference to the lifecycle of the activity
            //which host this fragment
            layoutManager= LinearLayoutManager(requireActivity())
        }

    }
}
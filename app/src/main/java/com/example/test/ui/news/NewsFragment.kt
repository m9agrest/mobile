package com.example.test.ui.news

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.test.api.RetrofitClient
import com.example.test.databinding.FragmentNewsBinding
import com.example.test.pojo.Post
import com.example.test.pojo.PostList
import com.example.test.ui.PostAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

open class NewsFragment : Fragment()  {
    var _binding: FragmentNewsBinding? = null
    val binding get() = _binding!!
    lateinit var recyclerView: RecyclerView
    lateinit var postAdapter: PostAdapter
    val postList: MutableList<Post> = mutableListOf() // Для хранения данных
    private var isLoading = false // Флаг загрузки

    protected var _all = false;

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        list = 0;
        _binding = FragmentNewsBinding.inflate(inflater, container, false)
        val root: View = binding.root
        recyclerView = binding.postsRecyclerView
        postAdapter = PostAdapter(requireContext(), postList, this)
        setupRecyclerView()
        loadPost()
        return root
    }




    fun setupRecyclerView() {
        binding.postsRecyclerView.adapter = postAdapter
        // Добавляем скролл-слушатель для подгрузки данных
        binding.postsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.postsRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                // Проверяем, достигнут ли конец списка
                val layoutManager = recyclerView.layoutManager as? LinearLayoutManager
                val lastVisibleItemPosition = layoutManager?.findLastVisibleItemPosition() ?: 0
                if (!isLoading && lastVisibleItemPosition == postList.size - 1) {
                    loadPost() // Загружаем следующую страницу
                }
            }
        })
    }


    var list:Int = 0;
    fun loadPost(){
        val fragment = this;
        list++;
        Log.d("Debug", "load post list $list news all = ${_all}")
        val apiService = RetrofitClient.create(binding.root.context)
        val call = apiService.news(list, _all)
        call.enqueue(object : Callback<PostList> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<PostList>, response: Response<PostList>) {
                Log.d("Debug", "api request: session")
                if (response.isSuccessful) {
                    Log.d("Debug", "response: successful")
                    val list = response.body()
                    Log.d("Debug", "response json ${list?.response}")
                    if (list?.response == 0) {

                        val startPos = postList.size
                        list.list?.let { postList.addAll(it) }
                        list.list?.size?.let { postAdapter.notifyItemRangeInserted(startPos, it) }
                    }else{
                        Log.d("Debug", "response: don`t list")
                    }
                }else{
                    Log.d("Debug", "response: error")
                }
            }
            override fun onFailure(call: Call<PostList>, t: Throwable) {
                // Ошибка сети или сервера, перенаправляем на экран входа
                Log.d("Debug", "request is error")
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
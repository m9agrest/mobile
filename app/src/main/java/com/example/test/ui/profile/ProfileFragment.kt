package com.example.test.ui.profile

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.test.GlobalMe
import com.example.test.R
import com.example.test.api.RetrofitClient
import com.example.test.databinding.FragmentProfileBinding
import com.example.test.pojo.Post
import com.example.test.pojo.PostList
import com.example.test.pojo.User
import com.example.test.ui.PostAdapter
import loadBackGroundFromUrl
import loadImageFromUrl
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private lateinit var postAdapter: PostAdapter
    private lateinit var recyclerView: RecyclerView
    private val binding get() = _binding!!
    private var user: User? = null;

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d("Debug", "Start profile view")
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        recyclerView = binding.postsRecyclerView
        val root: View = binding.root
        recyclerView.layoutManager = LinearLayoutManager(context)

        if(GlobalMe.loadUserId > 0){
            load(GlobalMe.loadUserId)
            GlobalMe.loadUserId = -1;
        }else{
            GlobalMe.user?.let { setData(it) }
        }

        return root;
    }

    fun setData(user: User){
        this.user = user;

        Log.d("Debug", "set user data")
        binding.userName.text = user.name
        binding.userStatus.text = user.status
        user?.icon?.let { loadImageFromUrl(binding.userIcon, it) }
        loadBackGroundFromUrl(binding.headerLayout, user.cover)
        list = 0;
        loadPost();
    }

    var list: Int = 0;
    fun loadPost(){
        val fragment = this;
        list++
        Log.d("Debug", "load post list $list user ${user?.id}")
        val apiService = RetrofitClient.create(binding.root.context)
        val call = user?.id?.let { apiService.listPost(list, it) }
        call?.enqueue(object : Callback<PostList> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<PostList>, response: Response<PostList>) {
                Log.d("Debug", "api request: session")
                if (response.isSuccessful) {
                    Log.d("Debug", "response: successful")
                    val list = response.body()
                    Log.d("Debug", "response json ${list?.response}")
                    if (list?.response == 0) {
                        list.list?.forEach { post -> post.user = user }
                        val l :MutableList<Post> =  mutableListOf()
                        list.list?.let { l.addAll(it) }
                        postAdapter = PostAdapter(requireContext(), l, fragment)
                        recyclerView.adapter = postAdapter
                        postAdapter.notifyDataSetChanged()
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





    fun load(id:Int?){
        if(id == null){
            return
        }

        Log.d("Debug", "load user")
        val apiService = RetrofitClient.create(binding.userIcon.context) // Инициализация API-сервиса
        val call = apiService.getUser(id) // Вызываем метод
        call.enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                Log.d("Debug", "api request: get user")
                if (response.isSuccessful) {
                    Log.d("Debug", "response: successful")
                    val user = response.body()
                    Log.d("Debug", "response json ${user?.response}")
                    if (user?.response == 0) {
                        // Успешно, продолжаем загрузку активности
                        setData(user);
                    }else{
                        Log.d("Debug", "response: error")
                        binding.userName.text = "Error"
                        binding.userStatus.text = "Not found"
                    }
                }else{
                    Log.d("Debug", "response: error")
                    binding.userName.text = "Error"
                    binding.userStatus.text = "Not found"
                }
            }
            override fun onFailure(call: Call<User>, t: Throwable) {
                // Ошибка сети или сервера, перенаправляем на экран входа
                Log.d("Debug", "request is error")
                binding.userName.text = "Error"
                binding.userStatus.text = "Internet connection"
            }
        })
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
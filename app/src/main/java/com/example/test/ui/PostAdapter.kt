package com.example.test.ui

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.test.GlobalMe
import com.example.test.R
import com.example.test.api.RetrofitClient
import com.example.test.databinding.ItemPostBinding
import com.example.test.pojo.Post
import com.example.test.ui.profile.ProfileFragment
import loadImageFromUrl
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date


class PostAdapter(
    private val context: Context, private val postList: MutableList<Post>?,
    private val fragment: Fragment
) :
    RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    private var selectedPost: Post? = null // Для обработки действий с выбранным постом

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = ItemPostBinding.inflate(LayoutInflater.from(context), parent, false)
        return PostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = postList?.get(position)
        if (post != null ) {
            holder.bind(post)

            if(post.user?.id == GlobalMe.user?.id){
                // Регистрация для контекстного меню
                holder.itemView.setOnCreateContextMenuListener { menu, _, _ ->
                    fragment.requireActivity().menuInflater.inflate(R.menu.post_item_menu, menu)
                    menu.findItem(R.id.action_delete_post)?.setOnMenuItemClickListener {
                        handleDeletePost(position, post)
                        true
                    }
                }

                // Устанавливаем текущий пост как выбранный при долгом нажатии
                holder.itemView.setOnLongClickListener {
                    selectedPost = post
                    holder.itemView.showContextMenu()
                    true
                }
            }
        }
    }

    private fun handleDeletePost(position: Int, post: Post) {
        postList?.let {
            println("Attempting to remove item at position $position from list of size ${it.size}")
            if (position in 0 until it.size) {
                val apiService = fragment.context?.let { it1 -> RetrofitClient.create(it1) }
                if(apiService != null){
                    val call = post.id?.let { it1 -> apiService.geletePost(it1) }
                    call?.enqueue(object : Callback<Post> {
                        @SuppressLint("NotifyDataSetChanged")
                        override fun onResponse(call: Call<Post>, response: Response<Post>) {
                            if (response.isSuccessful) {
                                Log.d("Debug", "response: successful")
                                val list = response.body()
                                Log.d("Debug", "response json ${list?.response}")
                                if (list?.response == 0) {

                                    it.removeAt(position) // Удаление из списка
                                    notifyItemRemoved(position) // Уведомление об удалении
                                    notifyItemRangeChanged(position, it.size - position) // Обновление оставшихся элементов
                                }else{
                                    Log.d("Debug", "response: don`t list")
                                }
                            }else{
                                Log.d("Debug", "response: error")
                            }
                        }
                                override fun onFailure(call: Call<Post>, t: Throwable) {
                            Log.d("Debug", "request is error")
                        }
                    })
                }


            } else {
                println("Error: Position $position is out of bounds for list size ${it.size}")
            }
        } ?: run {
            println("Error: postList is null")
        }

    }

    override fun getItemCount(): Int {
        return postList?.size ?: 0
    }

    inner class PostViewHolder(private val binding: ItemPostBinding) : RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SimpleDateFormat", "SetTextI18n")
        fun bind(post: Post) {
            // Set user info
            binding.userName.text = post.user?.name

            binding.userName.setOnClickListener{
                GlobalMe.loadUserId = post.user?.id!!
                val profileFragment = ProfileFragment()
                fragment.requireActivity().supportFragmentManager.beginTransaction()
                    .replace(fragment.id, profileFragment) // Используем ID контейнера
                    .addToBackStack(null) // Если нужно, чтобы фрагмент оказался в back stack
                    .commit()

            }
            post.user?.icon?.let { loadImageFromUrl(binding.userIcon, it) }

            // Set publication date
            binding.publicationDate.text = post.date?.toLong()
                ?.let { Date(it  * 1000) }?.let { SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(it) }

            // Set post content
            binding.postText.text = post.text

            // Set images
            if (post.photo?.isNotEmpty() == true) {
                binding.imageGallery.visibility = View.VISIBLE
                /*val adapter = ImageAdapter(binding.root.context, post.photo)
                val gridView: GridView = binding.imageGallery
                gridView.adapter = adapter
                adapter.notifyDataSetChanged()*/
            } else {
                binding.imageGallery.visibility = View.GONE
            }

            // Set likes and dislikes
            if(post.rating != null){
                if(post.rating.like != null){
                    binding.likesCount.text = "${post.rating.like} Likes"
                }
                if(post.rating.dislike != null){
                    binding.dislikesCount.text = "${post.rating.dislike} Likes"
                }
            }
        }
    }
}

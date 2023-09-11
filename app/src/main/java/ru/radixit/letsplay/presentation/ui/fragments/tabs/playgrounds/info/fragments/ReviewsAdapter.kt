package ru.radixit.letsplay.presentation.ui.fragments.tabs.playgrounds.info.fragments

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.radixit.letsplay.data.network.response.CommentResponse
import ru.radixit.letsplay.databinding.ItemReviewRedesBinding
import ru.radixit.letsplay.utils.gone
import ru.radixit.letsplay.utils.inVisible
import ru.radixit.letsplay.utils.visible

private typealias SelectItemOnClickListener = ((String) -> Unit)

class ReviewsAdapter : RecyclerView.Adapter<ReviewsAdapter.ReviewsHolder>() {

    private var selectItemOnClickListener: SelectItemOnClickListener? = null
    private var dataList = emptyList<CommentResponse.Comment>()
    companion object {
        val ratingList = emptyList<Int>()
    }


    class ReviewsHolder(private val binding: ItemReviewRedesBinding) :
        RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun from(parent: ViewGroup): ReviewsHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemReviewRedesBinding.inflate(layoutInflater, parent, false)
                return ReviewsHolder(binding)
            }
        }

        fun bind(
            comment: CommentResponse.Comment,
            selectItemOnClickListener: SelectItemOnClickListener
        ) {
            ReviewsAdapter.ratingList

            comment.createdBy.surname?.let {
                binding.tvCreatedBy.text = "${comment.createdBy.name} ${comment.createdBy.surname}"
            }?:run {
                binding.tvCreatedBy.text = comment.createdBy.name
            }

            binding.tvCreatedAt.text = comment.createdAt.timeSummary

//            val ratingString = comment.rating
//            val ratingArray = ratingString.split("/")
//            val rating1 = ratingArray[0]
//            val rating2 = ratingArray[1].toInt()
//            binding.rbRatingBar.numStars = rating2
            if(comment.rating == 0){
                binding.rbRatingBar.inVisible()
                binding.nullStartsImg.visible()
            }else{
                binding.rbRatingBar.visible()
                binding.nullStartsImg.inVisible()
                binding.rbRatingBar.rating = comment.rating.toFloat()
            }
            val url = comment.createdBy.photo?.url
            url?.let{
                binding.nameOnAvatar.gone()
                Glide.with(binding.root).load(it).into(binding.userAvatarReviewImg)
            }?:run{
                binding.nameOnAvatar.visible()
                binding.nameOnAvatar.text = "${comment.createdBy.name[0]}"
            }
            binding.tvText.text = comment.text
            binding.itemReport.setOnClickListener {
                selectItemOnClickListener.invoke(comment.id.toString())
            }
        }

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ReviewsHolder {
        return ReviewsHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ReviewsHolder, position: Int) {
        holder.bind(dataList[position], selectItemOnClickListener!!)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    fun selectItem(listener: SelectItemOnClickListener?) {
        selectItemOnClickListener = listener
    }

    fun setData(list: List<CommentResponse.Comment>) {
        Log.v("setData", list.toString())
        dataList = list
        notifyDataSetChanged()
    }

}

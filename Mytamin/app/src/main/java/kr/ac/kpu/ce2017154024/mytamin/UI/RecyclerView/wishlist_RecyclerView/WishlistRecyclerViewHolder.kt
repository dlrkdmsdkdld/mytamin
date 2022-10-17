package kr.ac.kpu.ce2017154024.mytamin.UI.RecyclerView.wishlist_RecyclerView

import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_wishlist.view.*
import kotlinx.android.synthetic.main.wishlist_item.view.*
import kr.ac.kpu.ce2017154024.mytamin.R
import kr.ac.kpu.ce2017154024.mytamin.UI.ViewPager2.RecyclerView.home_RecyclerView.IHomeRecyclerView
import kr.ac.kpu.ce2017154024.mytamin.utils.Constant

class WishlistRecyclerViewHolder(itemView: View, HomeRecylcerViewInterface: IWishRecyclerAdapter)
    : RecyclerView.ViewHolder(itemView),
    View.OnClickListener {
    private var RecyclerViewInterface:IWishRecyclerAdapter
    private var title= itemView.wishlist_title_item
    private var count = itemView.wishlist_count_item
    private var layout = itemView.wishlist_layout_item


    init {
        this.RecyclerViewInterface = HomeRecylcerViewInterface
        layout.setOnClickListener(this)
    }
    fun bindWithView(titledata:String,countdata:Int){
        title.text=titledata
        count.text=countdata.toString()
        Log.d(Constant.TAG,"바인드함  wishlistViewHolder")
    }
    override fun onClick(p0: View?) {
        when(p0){
            layout ->{
                this.RecyclerViewInterface.onSearchItemClicked(adapterPosition,title.text.toString())

            }
        }
    }
}
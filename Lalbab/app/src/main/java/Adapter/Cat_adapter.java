package Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lalbab.app.Config.BaseURL;
import com.lalbab.app.Model.Category_model;
import com.lalbab.app.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class Cat_adapter extends RecyclerView.Adapter<Cat_adapter.MyViewHolder> {
    private List<Category_model> modelList;
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public ImageView image;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.tv_cat_title);
            image = (ImageView) view.findViewById(R.id.iv_cat_img);
        }
    }

    public Cat_adapter(List<Category_model> modelList) {
        this.modelList = modelList;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rv_cats, parent, false);

        context = parent.getContext();

        return new Cat_adapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Category_model mList = modelList.get(position);

        /*
        Glide.with(context)
                .load(BaseURL.IMG_CATEGORY_URL+mList.getImage())
                .placeholder(R.mipmap.ic_launcher)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .dontAnimate()
                .into(holder.image);
        */

        Picasso.with(context).load(BaseURL.IMG_PRODUCT_URL +  mList.getImage()).into(holder.image);


        holder.title.setText(mList.getTitle());

    }



    @Override
    public int getItemCount() {
        return modelList.size();
    }
}

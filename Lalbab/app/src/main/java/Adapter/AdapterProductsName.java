package Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import com.lalbab.app.Config.BaseURL;
import com.lalbab.app.Model.List_Product_model;
import com.lalbab.app.R;

public class AdapterProductsName extends ArrayAdapter<List_Product_model> {

    LayoutInflater flater;
    Context context ;

    public AdapterProductsName(Activity context, int resouceId, int textviewId, List<List_Product_model> list){

        super(context,resouceId,textviewId, list);
        this.context = context;
        flater = context.getLayoutInflater();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {



        return rowview(convertView,position);
    }
    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return rowview(convertView,position);
    }
    private class viewHolder{
        TextView txtTitle;
        ImageView imageView;
    }
    private View rowview(View convertView , int position){

        List_Product_model product = getItem(position);

        viewHolder holder ;
        View rowview = convertView;
        if (rowview==null) {

            holder = new viewHolder();
            flater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowview = flater.inflate(R.layout.item_pruduct_name, null, false);

            holder.txtTitle = (TextView) rowview.findViewById(R.id.title_pp);
            holder.imageView = (ImageView) rowview.findViewById(R.id.img_p);
            rowview.setTag(holder);
        }else{
            holder = (viewHolder) rowview.getTag();
        }
        //holder.imageView.setImageResource(rowItem.getImageId());

        String tittle = "";
            tittle = ""+product.getName_ar();




        holder.txtTitle.setText(tittle);
        try {
            if (product.getImg().toString().length() > 0)
                Picasso.with(context).load(BaseURL.IMG_PRODUCT_URL + product.getImg()).into(holder.imageView);
        }catch (Exception e){e.printStackTrace();}

        return rowview;
    }



}
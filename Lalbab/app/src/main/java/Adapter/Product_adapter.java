package Adapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.lalbab.app.Config.BaseURL;
import com.lalbab.app.Model.Product_model;
import com.lalbab.app.MainActivity;
import com.lalbab.app.R;
import com.lalbab.app.util.DatabaseHandler;

/**
 * Created by Rajesh Dabhi on 26/6/2017.
 */

public class Product_adapter extends RecyclerView.Adapter<Product_adapter.MyViewHolder>
        implements Filterable {

    private List<Product_model> modelList;
    private List<Product_model> mFilteredList;
    private Context context;
    private DatabaseHandler dbcart;

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView tv_title, tv_price, tv_total, tv_contetiy;
        public ImageView iv_logo, iv_plus, iv_minus, iv_remove;

        public MyViewHolder(View view) {
            super(view);
            tv_title = (TextView) view.findViewById(R.id.tv_subcat_title);
            tv_price = (TextView) view.findViewById(R.id.tv_subcat_price);
            tv_total = (TextView) view.findViewById(R.id.tv_subcat_total);
            tv_contetiy = (TextView) view.findViewById(R.id.tv_subcat_contetiy);
           // tv_add = (TextView) view.findViewById(R.id.tv_subcat_add);
            iv_logo = (ImageView) view.findViewById(R.id.iv_subcat_img);
            iv_plus = (ImageView) view.findViewById(R.id.iv_subcat_plus);
            iv_minus = (ImageView) view.findViewById(R.id.iv_subcat_minus);
            iv_remove = (ImageView) view.findViewById(R.id.iv_subcat_remove);

            iv_remove.setVisibility(View.GONE);

            iv_minus.setOnClickListener(this);
            iv_plus.setOnClickListener(this);
            //tv_add.setOnClickListener(this);
            iv_logo.setOnClickListener(this);

            CardView cardView = (CardView) view.findViewById(R.id.card_view);
            cardView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            int id = view.getId();
            int position = getAdapterPosition();

            if (id == R.id.iv_subcat_plus) {

               /* if (!check(position)){
                    showClearDialog(position, tv_contetiy);
                    return;
                }*/

                int qty = Integer.valueOf(tv_contetiy.getText().toString());
                qty = qty + 1;

                tv_contetiy.setText(String.valueOf(qty));
                savedata(position, tv_contetiy);

            } else if (id == R.id.iv_subcat_minus) {


                int qty = 0;
                if (!tv_contetiy.getText().toString().equalsIgnoreCase(""))
                    qty = Integer.valueOf(tv_contetiy.getText().toString());

                if (qty > 0) {
                    qty = qty - 1;
                    tv_contetiy.setText(String.valueOf(qty));
                }
                savedata(position, tv_contetiy);

            } else if (id == R.id.iv_subcat_img) {
                showImage(modelList.get(position).getProduct_image());
            } else if (id == R.id.card_view) {
                showProductDetail(modelList.get(position).getProduct_image(),
                        modelList.get(position).getTitle(),
                        modelList.get(position).getProduct_description(),
                        modelList.get(position).getProduct_name(),
                        position, tv_contetiy.getText().toString());
            }

        }
    }
    private void showClearDialog(final int position , final TextView tv_contetiy) {
        final DatabaseHandler db = new DatabaseHandler(context);

        ArrayList<HashMap<String, String>> map = db.getCartAll();

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setMessage("Do you want to remove all pruducts ?");
        alertDialog.setNegativeButton("Cancle", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // clear cart data
                db.clearCart();
                savedata(position, tv_contetiy);


                dialogInterface.dismiss();
            }
        });

        alertDialog.show();
    }




    private boolean check (int position) {
        if ( BaseURL.CurentIDCatagore .equals("0") ) {
            BaseURL.CurentIDCatagore = BaseURL.category_modelList.get(BaseURL.CURENT_CATAGORY_POSTION ).getId();
            return true;
        }

        if (BaseURL.category_modelList.get(BaseURL.CURENT_CATAGORY_POSTION ).getId().equals(BaseURL.CurentIDCatagore)) {
            return true;
        }else {
            return false;
        }
    }

    public Product_adapter(List<Product_model> modelList, Context context) {
        this.modelList = modelList;
        this.mFilteredList = modelList;

        dbcart = new DatabaseHandler(context);
    }

    @Override
    public Product_adapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_product_rv, parent, false);

        context = parent.getContext();

        return new Product_adapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(Product_adapter.MyViewHolder holder, int position) {
        Product_model mList = modelList.get(position);

        /*
        Glide.with(context)
                .load( BaseURL.IMG_PRODUCT_URL +  mList.getProduct_image())
              //  .centerCrop()
                .placeholder(R.mipmap.ic_launcher)
               // .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                //.dontAnimate()
                .into(holder.iv_logo);
        */

        Picasso.with(context).load(BaseURL.IMG_PRODUCT_URL +  mList.getProduct_image()).into(holder.iv_logo);


        holder.tv_title.setText(mList.getProduct_name());
        holder.tv_price.setText(context.getResources().getString(R.string.tv_pro_price) + mList.getUnit_value() + " " +
                mList.getUnit() + " " + context.getResources().getString(R.string.currency) + " " + mList.getPrice());

        if (dbcart.isInCart(mList.getProduct_id())) {
           // holder.tv_add.setText(context.getResources().getString(R.string.tv_pro_update));
            holder.tv_contetiy.setText(dbcart.getCartItemQty(mList.getProduct_id()));
        } else {
          //  holder.tv_add.setText(context.getResources().getString(R.string.tv_pro_add));
        }

        Double items = Double.parseDouble(dbcart.getInCartItemQty(mList.getProduct_id()));
        Double price = Double.parseDouble(mList.getPrice());

        holder.tv_total.setText("" + price * items);

    }

    @Override
    public int getItemCount() {
        return mFilteredList.size();
    }

    @Override
    public Filter getFilter() {

        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {

                String charString = charSequence.toString();

                if (charString.isEmpty()) {

                    mFilteredList = modelList;
                } else {

                    ArrayList<Product_model> filteredList = new ArrayList<>();

                    for (Product_model androidVersion : modelList) {

                        if (androidVersion.getProduct_name().toLowerCase().contains(charString)) {

                            filteredList.add(androidVersion);
                        }
                    }

                    mFilteredList = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = mFilteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                mFilteredList = (ArrayList<Product_model>) filterResults.values;
                notifyDataSetChanged();

            }
        };
    }

    private void showImage(String image) {

        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.product_image_dialog);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        dialog.show();

        ImageView iv_image_cancle = (ImageView) dialog.findViewById(R.id.iv_dialog_cancle);
        ImageView iv_image = (ImageView) dialog.findViewById(R.id.iv_dialog_img);

        /*
        Glide.with(context)
                .load(BaseURL.IMG_PRODUCT_URL + image)
            //    .centerCrop()
                .placeholder(R.mipmap.ic_launcher)
              //  .crossFade()
                .into(iv_image);

        */
        Picasso.with(context).load(BaseURL.IMG_PRODUCT_URL + image).into(iv_image);


        iv_image_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

    }

    private void showProductDetail(String image, String title, String description, String detail, final int position, String qty) {

        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_product_detail);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        dialog.show();

        ImageView iv_image = (ImageView) dialog.findViewById(R.id.iv_product_detail_img);
        ImageView iv_minus = (ImageView) dialog.findViewById(R.id.iv_subcat_minus);
        ImageView iv_plus = (ImageView) dialog.findViewById(R.id.iv_subcat_plus);
        TextView tv_title = (TextView) dialog.findViewById(R.id.tv_product_detail_title);
        final TextView tv_contetiy = (TextView) dialog.findViewById(R.id.tv_subcat_contetiy);

        tv_title.setText(modelList.get(position).getProduct_name());
        tv_contetiy.setText(qty);


        /*
        Glide.with(context)
                .load(BaseURL.IMG_PRODUCT_URL + image)
             //   .centerCrop()
                .placeholder(R.mipmap.ic_launcher)
              //  .crossFade()
                .into(iv_image);
        */
        Picasso.with(context).load(BaseURL.IMG_PRODUCT_URL + image).into(iv_image);


        if (dbcart.isInCart(modelList.get(position).getProduct_id())) {
            tv_contetiy.setText(dbcart.getCartItemQty(modelList.get(position).getProduct_id()));
        }

        iv_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!check(position)){
                    showClearDialog(position, tv_contetiy);
                    return;
                }

                int qty = Integer.valueOf(tv_contetiy.getText().toString());
                qty = qty + 1;

                tv_contetiy.setText(String.valueOf(qty));

                savedata(position ,tv_contetiy );

            }
        });

        iv_minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int qty = 0;
                if (!tv_contetiy.getText().toString().equalsIgnoreCase(""))
                    qty = Integer.valueOf(tv_contetiy.getText().toString());

                if (qty > 0) {
                    qty = qty - 1;
                    tv_contetiy.setText(String.valueOf(qty));
                }
                savedata(position ,tv_contetiy  );
            }
        });

    }

    private void savedata(int position , TextView tv_contetiy ){

        HashMap<String, String> map = new HashMap<>();

            map.put("product_id", modelList.get(position).getProduct_id());
            map.put("category_id", modelList.get(position).getCategory_id());
            map.put("product_image", modelList.get(position).getProduct_image());
            map.put("increament", modelList.get(position).getIncreament());
            map.put("product_name", modelList.get(position).getProduct_name());

            map.put("price", modelList.get(position).getPrice());
            map.put("stock", modelList.get(position).getIn_stock());
            map.put("title", modelList.get(position).getTitle());
            map.put("unit", modelList.get(position).getUnit());

            map.put("unit_value", modelList.get(position).getUnit_value());

            if (!tv_contetiy.getText().toString().equalsIgnoreCase("0")) {

                if (dbcart.isInCart(map.get("product_id"))) {
                    dbcart.setCart(map, Float.valueOf(tv_contetiy.getText().toString()));
                    //   tv_add.setText(context.getResources().getString(R.string.tv_pro_update));
                } else {
                    dbcart.setCart(map, Float.valueOf(tv_contetiy.getText().toString()));
                    //  tv_add.setText(context.getResources().getString(R.string.tv_pro_update));
                }
            } else {
                dbcart.removeItemFromCart(map.get("product_id"));
                // tv_add.setText(context.getResources().getString(R.string.tv_pro_add));
            }

            Double items = Double.parseDouble(dbcart.getInCartItemQty(map.get("product_id")));
            Double price = Double.parseDouble(map.get("price"));

            ((MainActivity) context).setCartCounter("" + dbcart.getCartCount());

            notifyItemChanged(position);

    }

}
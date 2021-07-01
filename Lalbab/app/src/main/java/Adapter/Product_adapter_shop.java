package Adapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.lalbab.app.Config.BaseURL;
import com.lalbab.app.Model.Product_model;
import com.lalbab.app.AppController;
import com.lalbab.app.MainActivity;
import com.lalbab.app.R;
import com.lalbab.app.util.CustomVolleyJsonRequest;
import com.lalbab.app.util.DatabaseHandler;

/**
 * Created by Rajesh Dabhi on 26/6/2017.
 */

public class Product_adapter_shop extends RecyclerView.Adapter<Product_adapter_shop.MyViewHolder>
        implements Filterable {

    private List<Product_model> modelList;
    private List<Product_model> mFilteredList;
    private Context context;
    private DatabaseHandler dbcart;

    int curent_catagore;
    int pos_type;

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

             if (id == R.id.iv_subcat_img) {
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

    public Product_adapter_shop(List<Product_model> modelList, Context context , int curent_catagore) {
        this.modelList = modelList;
        this.mFilteredList = modelList;
        this.curent_catagore = curent_catagore;

        dbcart = new DatabaseHandler(context);
    }


    @Override
    public Product_adapter_shop.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_product_shop_rv, parent, false);

        context = parent.getContext();

        return new Product_adapter_shop.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(Product_adapter_shop.MyViewHolder holder, int position) {
        Product_model mList = modelList.get(position);

        /*
        Glide.with(context)
                .load(BaseURL.IMG_PRODUCT_URL + mList.getProduct_image())
               // .centerCrop()
                .placeholder(R.mipmap.ic_launcher)
               // .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
               // .dontAnimate()
                .into(holder.iv_logo);
        */

        Picasso.with(context).load(BaseURL.IMG_PRODUCT_URL +  mList.getProduct_image()).into(holder.iv_logo);

        holder.tv_title.setText(mList.getProduct_name());
        holder.tv_price.setText(context.getResources().getString(R.string.tv_pro_price) + mList.getUnit_value() + " " +
                mList.getUnit() + " " + context.getResources().getString(R.string.currency) + " " + mList.getPrice());
        int in_stock = Integer.parseInt(mList.getIn_stock());
        if (in_stock == 0) {
            holder.tv_title.setTextColor(Color.parseColor("#ff0023"));
        }else {
            holder.tv_title.setTextColor(Color.parseColor("#000000"));
        }
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
                .load(BaseURL.IMG_PRODUCT_URL +   image)
              //  .centerCrop()
                .placeholder(R.mipmap.ic_launcher)
              //  .crossFade()
                .into(iv_image);
        */
        Picasso.with(context).load(BaseURL.IMG_PRODUCT_URL +   image).into(iv_image);


        iv_image_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

    }

    int status_product = 0;
    private void showProductDetail(String image, String title, String description, String detail, final int position, String qty) {

        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_product_shop_detail);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        dialog.show();

        ImageView iv_image = (ImageView) dialog.findViewById(R.id.iv_product_detail_img);
        ImageView iv_minus = (ImageView) dialog.findViewById(R.id.iv_subcat_minus);
        final Switch sw_is_live = (Switch) dialog.findViewById(R.id.sw_is_live);
        ImageView iv_plus = (ImageView) dialog.findViewById(R.id.iv_subcat_plus);
        TextView tv_title = (TextView) dialog.findViewById(R.id.tv_product_detail_title);
        Button bt_edit = (Button) dialog.findViewById(R.id.bt_edit);
        final EditText tv_contetiy = (EditText) dialog.findViewById(R.id.tv_subcat_contetiy);

        sw_is_live.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    String is_live = "" + context.getResources().getString(R.string.is_live);;

                    sw_is_live.setText(""+is_live);
                    status_product = 1;
                }else {
                    String is_no_live = "" + context.getResources().getString(R.string.is_no_live);
                    sw_is_live.setText(""+is_no_live);
                    status_product = 0;
                }
            }
        });

        int In_stock = Integer.parseInt(modelList.get(position).getIn_stock());

        if (In_stock == 1){
            String is_live = "" + context.getResources().getString(R.string.is_live);;

            sw_is_live.setText(""+is_live);
            sw_is_live.setChecked(true);
            status_product = 1;
        }else {
            String is_no_live = "" + context.getResources().getString(R.string.is_no_live);
            sw_is_live.setText(""+is_no_live);
            sw_is_live.setChecked(false);
            status_product = 0;
        }

        tv_title.setText(modelList.get(position).getProduct_name());
        //tv_detail.setText(detail);
        tv_contetiy.setText(modelList.get(position).getPrice());
       // tv_detail.setText(description);

        /*
        Glide.with(context)
                .load(BaseURL.IMG_PRODUCT_URL +   image)
                //.centerCrop()
                .placeholder(R.mipmap.ic_launcher)
              //  .crossFade()
                .into(iv_image);
        */
        Picasso.with(context).load(BaseURL.IMG_PRODUCT_URL +   image).into(iv_image);




        bt_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int price = Integer.parseInt(tv_contetiy.getText().toString());
                editProduct(position,price, curent_catagore , status_product);
            }
        });


        /*

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
        */

    }

    private void editProduct(int position , int price , int curent_catagore , int status_product) {
      //  Toast.makeText(context, ""+position +" "+price  +" "+ curent_catagore, Toast.LENGTH_SHORT).show();

        makeAddProductRequest(position , price , curent_catagore , status_product);

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

    public int getCurent_catagore() {
        return curent_catagore;
    }

    public void setCurent_catagore(int curent_catagore) {
        this.curent_catagore = curent_catagore;
    }


    private void makeAddProductRequest(int position ,int price ,int curent_catagore , int status_product) {

        // Tag used to cancel the request
        String tag_json_obj = "json_edit_product_req";

        String user_phone = "";
        String user_uid = "";
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            user_phone = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber().toString();
            user_uid = FirebaseAuth.getInstance().getCurrentUser().getUid().toString();
        }

        String parent = ""+curent_catagore;
        String prod_status = "1";

        Map<String, String> params = new HashMap<String, String>();
        params.put("parent", parent);
        params.put("parent_id", modelList.get(position).getProduct_id());
        params.put("prod_status", prod_status);
        params.put("price", ""+price);
        params.put("in_stock", ""+status_product);
        params.put("user_phone", user_phone);
        params.put("user_uid", user_uid);


        CustomVolleyJsonRequest jsonObjReq = new CustomVolleyJsonRequest(Request.Method.POST,
                BaseURL.EDIT_PRODUCT_URL, params, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d("TAG", response.toString());

                try {
                    Boolean status = response.getBoolean("responce");
                    if (status) {
                        String msg = response.getString("data");
                        Log.e("msg", ""+msg);
                        Toast.makeText(context, ""+msg, Toast.LENGTH_SHORT).show();

                    }else {
                        String msg = response.getString("error");
                        Log.e("msg", ""+msg);
                        Toast.makeText(context, ""+msg, Toast.LENGTH_SHORT).show();

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("TAG", "Error: " + error.getMessage());
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(context,context.getString(R.string.connection_time_out), Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

}
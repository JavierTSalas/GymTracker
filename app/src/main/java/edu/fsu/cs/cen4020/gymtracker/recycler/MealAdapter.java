package edu.fsu.cs.cen4020.gymtracker.recycler;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import edu.fsu.cs.cen4020.gymtracker.R;
/*
 See https://medium.com/@droidbyme/android-recyclerview-fca74609725e for how this works
 */

public class MealAdapter extends RecyclerView.Adapter<MealAdapter.GymHolder> {
    @NonNull
    @Override
    /*
     Inflate item layout in onCreateViewHolder() method and inflate item_row for recyclerview
     */
    public GymHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_row_gym_select, parent, false);
        return new GymHolder(view);
    }

    /*
    Call this method from onBindViewHolder() method to bind item of recyclerview
     */
    @Override
    public void onBindViewHolder(@NonNull GymHolder holder, int position) {
        Meal_POJO planet = mealPojoArrayList.get(position);
        holder.setMealInfo(planet);
    }

    private Context context;

    private ArrayList<Meal_POJO> mealPojoArrayList;

    public MealAdapter(Context context, ArrayList<Meal_POJO> meals) {

        this.context = context;
        this.mealPojoArrayList = meals;

    }

    @Override
    public int getItemCount() {
        return mealPojoArrayList.size();
    }

    private static ClickListener clickListener;

        public void setOnItemClickListener(ClickListener clickListener) {
            MealAdapter.clickListener = clickListener;
        }

        public interface ClickListener {
            void onItemClick(int position, View v);
        }

   public static class GymHolder extends RecyclerView.ViewHolder  implements View.OnClickListener {
        private TextView tv_title, tv_subtitle;

        public GymHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            tv_title = itemView.findViewById(R.id.tv_row_GymTitle);
            tv_subtitle = itemView.findViewById(R.id.tv_row_GymSubtext);
        }

        public void setMealInfo(Meal_POJO gymInfo) {
            tv_title.setText(gymInfo.getTitle());
            tv_subtitle.setText("\n"+gymInfo.getDescription());
        }
        @Override
        public void onClick(View v) {
            clickListener.onItemClick(getAdapterPosition(), v);
        }


    }
}